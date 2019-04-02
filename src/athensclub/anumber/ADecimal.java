package athensclub.anumber;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * A class that represent a single decimal number
 * 
 * @author Athensclub
 *
 */
public class ADecimal extends ANumber {

    public static final ADecimal TWO = new ADecimal(BigDecimal.valueOf(2));

    public static final ADecimal ONE = new ADecimal(BigDecimal.valueOf(1));

    public static final ADecimal ZERO = new ADecimal(BigDecimal.valueOf(0));

    public static final ADecimal NEGATIVE_ONE = new ADecimal(BigDecimal.valueOf(-1));

    /**
     * Note that this class implemented operation with ADecimal only
     */

    private BigDecimal value;

    public ADecimal(String value) {
	this.value = new BigDecimal(value);
    }

    public ADecimal(BigDecimal value) {
	if (value == null) {
	    throw new NullPointerException();
	}
	this.value = value;
    }

    @Override
    public ANumber add(ANumber other) {
	if (other instanceof ADecimal) {
	    return new ADecimal(value.add(((ADecimal) other).value));
	}
	return other.add(this);
    }

    @Override
    public ANumber multiply(ANumber other) {
	if (other instanceof ADecimal) {
	    return new ADecimal(value.multiply(((ADecimal) other).value));
	}
	return other.multiply(this);
    }

    @Override
    public boolean isNegative() {
	return value.signum() == -1;
    }

    @Override
    public boolean isZero() {
	return value.signum() == 0;
    }

    @Override
    public ANumber pow(ANumber other) {
	if (other instanceof AComplex) {
	    AComplex o = (AComplex) other;
	    if (o.isZero()) {
		return pow(o.getReal());
	    }
	    return AComplex.fromDecimal(this).pow(other);
	} else if (isNegative()) {
	    if(other.isInteger()) {
		if(other.isEven()) {
		    return abs().pow(other);
		}else {
		    return abs().pow(other).negate();
		}
	    }
	    return AComplex.fromDecimal(this).pow(other);
	} else if (other instanceof AFraction) {
	    AFraction o = (AFraction) other;
	    return new ADecimal(BigDecimalMath.root(
		    BigDecimalMath.pow(value, o.getNumerator().bigDecimalValue(), MathContext.DECIMAL128),
		    ((AFraction) other).getDenominator().bigDecimalValue(), MathContext.DECIMAL128));
	} else {
	    // assume ADecimal
	    return new ADecimal(BigDecimalMath.pow(value, other.bigDecimalValue(), MathContext.DECIMAL128));
	}
    }

    @Override
    protected BigDecimal calculateBigDecimalValue() {
	return value;
    }

    @Override
    public ANumber exp() {
	return new ADecimal(BigDecimalMath.exp(value, MathContext.DECIMAL128));
    }

    @Override
    public ANumber ln() {
	return new ADecimal(BigDecimalMath.log(value, MathContext.DECIMAL128));
    }

}

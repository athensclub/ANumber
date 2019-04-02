package athensclub.anumber;

import java.math.BigDecimal;
import java.math.MathContext;

import a10lib.compiler.provider.ComplexableNumberProvider;
import a10lib.compiler.provider.DecimalNumberProvider;
import a10lib.compiler.provider.FractionableNumberProvider;
import a10lib.compiler.token.StringTokenizer;
import a10lib.compiler.token.Token;
import a10lib.math.Maths;
import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * A base class of all number in anumber package
 * 
 * @author Athensclub
 *
 */
public abstract class ANumber extends Number implements Comparable<ANumber> {

    /**
     * ORDER OF IMPLEMENTATION: ADecimal -> AFraction
     */

    /**
     * 
     */
    private static final long serialVersionUID = 4078167201408747426L;

    private static final StringTokenizer tokenizer = new StringTokenizer("");

    static {
	tokenizer.addProvider(ComplexableNumberProvider.INSTANCE);
    }

    private BigDecimal value;

    private boolean even;

    private boolean initEven;

    /**
     * Create racket number according to its string(any CharSequence)
     * representation.Number string rules can be found at
     * {@link a10lib.compiler.Regex} for pattern of numbers.This support all number
     * in that class.
     * 
     * @param str
     * @return
     */
    public static ANumber valueOf(String str) {
	tokenizer.reset(str);
	try {
	    Token token = tokenizer.nextToken();
	    if (tokenizer.nextToken() != null) {
		throw new NumberFormatException(str);
	    }
	    if (token instanceof ComplexableNumberProvider.Token) {
		return valueOf((ComplexableNumberProvider.Token) token);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	throw new NumberFormatException(str);
    }

    /**
     * Get the value of a10lib token of complexable number
     * 
     * @param token
     * @return
     */
    public static ANumber valueOf(ComplexableNumberProvider.Token token) {
	if (token.getImaginary() == null) {
	    return valueOf(token.getReal());
	}
	return new AComplex(valueOf(token.getReal()), valueOf(token.getImaginary()));
    }

    /**
     * Get the value of a10lib token of fractionable number
     * 
     * @param token
     * @return
     */
    public static ANumber valueOf(FractionableNumberProvider.Token token) {
	if (token.getDenominator() == null) {
	    return valueOf(token.getNumerator());
	}
	return new AFraction(valueOf(token.getNumerator()), valueOf(token.getDenominator()));
    }

    /**
     * Get the value of a10lib token of decimal number
     * 
     * @param token
     * @return
     */
    public static ADecimal valueOf(DecimalNumberProvider.Token token) {
	return new ADecimal(token.getString());
    }

    /**
     * Check if this value is even.This value is cached, so it should be able to be
     * called any of times without performance drops.
     * 
     * @return
     */
    public boolean isEven() {
	if (!initEven) {
	    even = remainder(ADecimal.TWO).equals(ADecimal.ZERO);
	}
	return even;
    }

    /**
     * 
     * Check if this value is odd.This value is cached, so it should be able to be
     * called any of times without performance drops.
     * 
     * @return
     */
    public boolean isOdd() {
	return !isEven();
    }

    /**
     * Return the remainder from dividing this number by other number.
     * 
     * @param other
     * @return
     */
    public ADecimal remainder(ANumber other) {
	if (isInteger() && other.isInteger()) {
	    ANumber fraction = divide(other);
	    if (fraction instanceof AFraction) {
		return ((AFraction) fraction).remainder();
	    } else if (fraction instanceof AComplex) {
		AComplex com = (AComplex) fraction;
		if (com.getImaginary().isZero()) {
		    fraction = com.getReal();
		    if (fraction instanceof AFraction) {
			return ((AFraction) fraction).remainder();
		    } else if (fraction.isInteger()) {
			return ADecimal.ZERO;
		    } else {
			throw new IllegalArgumentException("Unable to find remainder of complex number: " + com);
		    }
		} else {
		    throw new IllegalArgumentException(
			    "Calculate remainder from dividing complex number: " + this + "/" + other);
		}
	    } else if (fraction.isInteger()) {
		return ADecimal.ZERO;
	    }
	}
	throw new IllegalArgumentException(
		"Calculate remainder from division of non integer value: " + this + "/" + other);
    }

    /**
     * Return natural log of this number
     * 
     * @return
     */
    public abstract ANumber ln();
    
    public ANumber tanh() {
	return new ADecimal(BigDecimalMath.tanh(bigDecimalValue(), MathContext.DECIMAL128));
    }
    
    /**
     * Return cosh of this number in radians.
     * @return
     */
    public ANumber cosh() {
	return new ADecimal(BigDecimalMath.cosh(bigDecimalValue(), MathContext.DECIMAL128));
    }

    /**
     * Return sinh of this number in radians.
     * @return
     */
    public ANumber sinh() {
	return new ADecimal(BigDecimalMath.sinh(bigDecimalValue(), MathContext.DECIMAL128));
    }

    /**
     * Return cos of this number in radians.
     * 
     * @return
     */
    public ANumber cos() {
	return new ADecimal(BigDecimalMath.cos(bigDecimalValue(), MathContext.DECIMAL128));
    }
    
    /**
     * Return sin of this number in radians.
     * 
     * @return
     */
    public ANumber sin() {
	return new ADecimal(BigDecimalMath.sin(bigDecimalValue(), MathContext.DECIMAL128));
    }

    /**
     * Return tan of this number in radians.
     * 
     * @return
     */
    public ANumber tan() {
	return new ADecimal(BigDecimalMath.tan(bigDecimalValue(), MathContext.DECIMAL128));
    }

    /**
     * Return if this < 0
     * 
     * @return
     */
    public abstract boolean isNegative();

    /**
     * return if this == 0
     * 
     * @return
     */
    public abstract boolean isZero();

    /**
     * Return if this >= 0
     * 
     * @return
     */
    public boolean isPositive() {
	return !isNegative();
    }

    /**
     * Add this number by other number
     * 
     * @param other
     */
    public abstract ANumber add(ANumber other);

    /**
     * Subtract this number by other number
     * 
     * @param other
     */
    public ANumber subtract(ANumber other) {
	return other.negate().add(this);
    }

    /**
     * Multiply this number by other number
     * 
     * @param other
     */
    public abstract ANumber multiply(ANumber other);

    /**
     * Divide this number by other number
     * 
     * @param other
     */
    public ANumber divide(ANumber other) {
	return other.reciprocal().multiply(this);
    }

    /**
     * Return the other base root of this number
     * 
     * @param other
     * @return
     */
    public ANumber root(ANumber other) {
	return pow(other.reciprocal());
    }

    /**
     * Return the inverse multiplication of this number. Aka. 1/this number
     * 
     * @return
     */
    public ANumber reciprocal() {
	return new AFraction(ADecimal.ONE, this);
    }

    /**
     * Return the negated version of this value(this value multiplied by -1)
     * 
     * @return
     */
    public ANumber negate() {
	return multiply(ADecimal.NEGATIVE_ONE);
    }

    /**
     * Return the e raise to the power of this number
     * 
     * @return
     */
    public abstract ANumber exp();

    /**
     * Raise this number by the other number.returned as the result
     * 
     * @param other
     * @return
     */
    public abstract ANumber pow(ANumber other);

    /**
     * Return the square root of this number
     * 
     * @return
     */
    public ANumber sqrt() {
	return pow(AFraction.ONE_HALF);
    }

    /**
     * Return absolute value of this number
     * 
     * @return
     */
    public ANumber abs() {
	return isNegative() ? negate() : this;
    }

    public boolean isInteger() {
	bigDecimalValue();
	return value.signum() == 0 || value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0;
    }

    /**
     * Calculate the approximate big decimal value of this number.the value will
     * then be cached by ANumber for later bigDecimalValue() uses.
     * 
     * @return
     */
    protected abstract BigDecimal calculateBigDecimalValue();

    /**
     * Return BigDecimal value that is or approximately equal to this number.
     * 
     * <p>
     * This method will store the cache of BIgDecimal value of this number.Users
     * should be able to use method multiple times without performance drops.
     * </p>
     * 
     * @return
     */
    public BigDecimal bigDecimalValue() {
	if (value == null) {
	    value = calculateBigDecimalValue();
	}
	return value;
    }

    @Override
    public double doubleValue() {
	return bigDecimalValue().doubleValue();
    }

    @Override
    public float floatValue() {
	return (float) doubleValue();
    }

    @Override
    public int intValue() {
	return (int) doubleValue();
    }

    @Override
    public long longValue() {
	return (long) doubleValue();
    }

    @Override
    public byte byteValue() {
	return (byte) doubleValue();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof ANumber) {
	    return compareTo((ANumber) obj) == 0;
	} else if (obj instanceof Number) {
	    return doubleValue() == ((Number) obj).doubleValue();
	}
	return false;
    }

    @Override
    public int compareTo(ANumber o) {
	ANumber sub = subtract(o);
	if (sub.isZero()) {
	    return 0;
	} else if (sub.isNegative()) {
	    return -1;
	} else {
	    return 1;
	}
    }

    @Override
    public String toString() {
	return bigDecimalValue().toPlainString();
    }

}

package athensclub.anumber;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * A class that represent one number divided by other
 * 
 * @author Athensclub
 *
 */
public class AFraction extends ANumber {

    /**
     * Note this class only implemented for AFraction and ADecimal
     */

    /**
     * Represent Fraction 1/2
     */
    public static final AFraction ONE_HALF = new AFraction(ADecimal.ONE, ADecimal.TWO);

    /**
     * 
     */
    private static final long serialVersionUID = -5834860691871099314L;

    private ANumber numerator, denominator;

    /**
     * Convert ADecimal value to AFraction value
     * 
     * @param val
     * @return
     */
    public static AFraction fromDecimal(ADecimal val) {
	return new AFraction(val, ADecimal.ONE);
    }

    /**
     * Get the remainder from division of this fraction.
     * 
     * @return
     */
    public ADecimal remainder() {
	if (numerator.isInteger() && denominator.isInteger()) {
	    return new ADecimal(numerator.bigDecimalValue().remainder(denominator.bigDecimalValue()).abs());
	}
	throw new IllegalArgumentException("Find the remainder of division beween non integer number: " + this);
    }

    public AFraction(ANumber numerator, ANumber denominator) {
	if (numerator == null || denominator == null) {
	    throw new NullPointerException();
	}
	if (numerator instanceof AComplex) {
	    AComplex num = (AComplex) numerator;
	    if (!num.getImaginary().isZero()) {
		throw new IllegalArgumentException(
			"Complex Fraction: " + numerator + ", Try using Complex Number with Fraction instead.");
	    }
	    numerator = num.getReal();
	}
	if (denominator instanceof AComplex) {
	    AComplex num = (AComplex) denominator;
	    if (!num.getImaginary().isZero()) {
		throw new IllegalArgumentException(
			"Complex Fraction: " + denominator + ", Try using Complex Number with Fraction instead.");
	    }
	    denominator = num.getReal();
	}
	ANumber toMultNum = null, toMultDenom = null;
	ANumber aNum = numerator, aDenom = denominator;
	if (numerator instanceof AFraction) {
	    aNum = ((AFraction) numerator).numerator;
	    toMultDenom = ((AFraction) numerator).denominator;
	}
	if (denominator instanceof AFraction) {
	    aDenom = ((AFraction) denominator).numerator;
	    toMultNum = ((AFraction) denominator).denominator;
	}
	this.numerator = toMultNum == null ? aNum : aNum.multiply(toMultNum);
	this.denominator = toMultDenom == null ? aDenom : aDenom.multiply(toMultDenom);
	if (this.numerator.isNegative() && this.denominator.isNegative()) {
	    this.numerator = numerator.negate();
	    this.denominator = denominator.negate();
	}
    }

    /**
     * Getter for this fraction denominator
     * 
     * @return
     */
    public ANumber getDenominator() {
	return denominator;
    }

    /**
     * Getter for this fraction numerator
     * 
     * @return
     */
    public ANumber getNumerator() {
	return numerator;
    }

    @Override
    public ANumber add(ANumber other) {
	if (other instanceof AFraction) {
	    AFraction o = (AFraction) other;
	    // (a/b) + (c/d) = (ad+bc)/bd
	    return new AFraction(numerator.multiply(o.denominator).add(o.numerator.multiply(denominator)),
		    denominator.multiply(o.denominator));
	}
	if (other instanceof ADecimal) {
	    return add(fromDecimal((ADecimal) other));
	}
	return other.add(this);
    }

    @Override
    public ANumber multiply(ANumber other) {
	if (other instanceof AFraction) {
	    AFraction o = (AFraction) other;
	    // (a/b) * (c/d) = (ab/cd)
	    return new AFraction(numerator.multiply(o.numerator), denominator.multiply(o.denominator));
	}
	if (other instanceof ADecimal) {
	    return multiply(fromDecimal((ADecimal) other));
	}
	return other.add(this);
    }

    @Override
    public boolean isNegative() {
	return numerator.isNegative() ^ denominator.isNegative(); // either but not both
    }

    @Override
    public boolean isZero() {
	return numerator.isZero();
    }

    @Override
    public ANumber reciprocal() {
	return new AFraction(denominator, numerator);
    }

    @Override
    public String toString() {
	return numerator + "/" + denominator;
    }

    @Override
    public ANumber pow(ANumber other) {
	return new AFraction(numerator.pow(other), denominator.pow(denominator));
    }

    @Override
    protected BigDecimal calculateBigDecimalValue() {
	BigDecimal num = numerator.bigDecimalValue();
	BigDecimal denom = denominator.bigDecimalValue();
	try {
	    return num.divide(denom);
	} catch (ArithmeticException e) {
	    return num.divide(denom, 20 + num.scale() + denom.scale(), RoundingMode.HALF_UP);
	}
    }

    @Override
    public ANumber exp() {
	return numerator.exp().root(denominator);
    }

    @Override
    public ANumber ln() {
	// ln(a/b) = ln(a)-ln(b)
	return numerator.ln().subtract(denominator.ln());
    }

}

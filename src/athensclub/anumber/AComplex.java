package athensclub.anumber;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;

/**
 * Represent a complex number consist of real number added by imaginary number
 * 
 * @author Athensclub
 *
 */
public class AComplex extends ANumber {

    /**
     * Implement only for AComplex,AFraction,ADecimal (and real-number based
     * ANumber)
     */

    /**
     * 
     */
    private static final long serialVersionUID = -6889921093493258982L;

    private ANumber real, imaginary;

    public AComplex(ANumber real, ANumber imaginary) {
	if (real instanceof AComplex) {
	    AComplex r = (AComplex) real;
	    // take real part of complex number as real part of this number if it does not
	    // contain imaginary number
	    if (r.imaginary.isZero()) {
		this.real = r.real;
	    } else {
		throw new IllegalArgumentException("Real number parts contains imaginary number: " + r);
	    }
	} else {
	    this.real = real;
	}
	if (imaginary instanceof AComplex) {
	    AComplex i = (AComplex) imaginary;
	    // take real part of complex number as imaginary part of this number if it does
	    // not
	    // contain imaginary number
	    if (i.imaginary.isZero()) {
		this.imaginary = i.real;
	    } else {
		throw new IllegalArgumentException("Imaginary number parts contains imaginary number: " + i);
	    }
	} else {
	    this.imaginary = imaginary;
	}
    }

    /**
     * Create complex number from given fraction value
     * 
     * @param fraction
     * @return
     */
    public static AComplex fromFraction(AFraction fraction) {
	return new AComplex(fraction, ADecimal.ZERO);
    }

    /**
     * Create complex number from given decimal value
     * 
     * @param decimal
     * @return
     */
    public static AComplex fromDecimal(ADecimal decimal) {
	return new AComplex(decimal, ADecimal.ZERO);
    }

    @Override
    public boolean isNegative() {
	if (imaginary.isZero()) {
	    return real.isNegative();
	}
	throw new IllegalArgumentException("Check for negative of imaginary number: " + this);
    }

    @Override
    public boolean isZero() {
	return real.isZero() && imaginary.isZero();
    }

    @Override
    public ANumber add(ANumber other) {
	if (other instanceof AComplex) {
	    return new AComplex(real.add(((AComplex) other).real), imaginary.add(((AComplex) other).imaginary));
	} else {
	    // assume the rest of class are real numbers
	    return new AComplex(real.add(other), imaginary);
	}
    }

    @Override
    public ANumber multiply(ANumber other) {
	if (other instanceof AComplex) {
	    ANumber c = ((AComplex) other).real;
	    ANumber d = ((AComplex) other).imaginary;
	    // (a+bi)(c+di) = ac - bd + (ad + bc)i
	    return new AComplex(real.multiply(c).subtract(imaginary.multiply(d)),
		    real.multiply(d).add(imaginary.multiply(c)));
	} else {
	    // assume the rest of class are real numbers
	    // (a+bi) * c = ac +bci
	    return new AComplex(real.multiply(other), imaginary.multiply(other));
	}
    }

    /**
     * Get the real number part of this complex number
     * 
     * @return
     */
    public ANumber getReal() {
	return real;
    }

    /**
     * Get the imaginary part of this complex number
     * 
     * @return
     */
    public ANumber getImaginary() {
	return imaginary;
    }

    @Override
    public ANumber reciprocal() {
	// 1 / (a+bi) = (a-bi)/(a^2+b^2)
	ANumber denom = imaginary.pow(ADecimal.TWO).add(real.pow(ADecimal.TWO));
	return new AComplex(new AFraction(real, denom), new AFraction(imaginary.negate(), denom));
    }

    @Override
    public ANumber pow(ANumber other) {
	return other.multiply(ln()).exp();
    }

    @Override
    public String toString() {
	return imaginary.isZero() ? real.toString()
		: imaginary.isNegative() ? real + "-" + imaginary.toString().replaceFirst("-", "") + "i"
			: real + "+" + imaginary + "i";
    }

    @Override
    protected BigDecimal calculateBigDecimalValue() {
	if (imaginary.isZero()) {
	    return real.bigDecimalValue();
	}
	throw new IllegalArgumentException("Calculate decimal value for complex number: " + this);
    }

    @Override
    public ANumber exp() {
	ANumber expRe = real.exp();
	return new AComplex(expRe.multiply(imaginary.cos()), expRe.multiply(imaginary.sin()));
    }

    /**
     * Calculate angle in radians of this complex nubmer
     * 
     * @return
     */
    public ADecimal angle() {
	return new ADecimal(
		BigDecimalMath.atan2(imaginary.bigDecimalValue(), real.bigDecimalValue(), MathContext.DECIMAL128));
    }

    @Override
    public ANumber abs() {
	// |a+bi|=sqrt(a^2+b^2)
	return real.pow(ADecimal.TWO).add(imaginary.pow(ADecimal.TWO)).sqrt();
    }

    @Override
    public ANumber ln() {
	return new AComplex(abs().ln(), angle());
    }

    @Override
    public ANumber cosh() {
	// cosh(x+yi) =sinh(x) cos(y) + i cosh(x) sin(y)
	return new AComplex(real.sinh().multiply(imaginary.cos()), real.cosh().multiply(imaginary.sin()));
    }

    @Override
    public ANumber sinh() {
	// sinh(a+bi)=sinh(a)cos(b)+icosh(a)sin(b)
	return new AComplex(real.sinh().multiply(imaginary.cos()), real.cosh().multiply(imaginary.sin()));
    }

    @Override
    public ANumber tanh() {
	// tanh(a+bi)=sinh(2a)+isin(2b)/cosh(2a)+cos(2b)
	ANumber a2 = real.multiply(ADecimal.TWO);
	ANumber b2 = imaginary.multiply(ADecimal.TWO);
	ANumber denom = a2.cosh().add(b2.cos());
	return new AComplex(new AFraction(a2.sinh(),denom),new AFraction(b2.sin(),denom));
    }

    @Override
    public ANumber cos() {
	// sin(a+bi)=sin(a)cosh(b)+icos(a)sinh(b)
	return new AComplex(real.sin().multiply(imaginary.cosh()), real.cos().multiply(imaginary.sinh()));
    }

    @Override
    public ANumber sin() {
	// cos(a+bi)=cos(a)cosh(b)-isin(a)sinh(b)
	return new AComplex(real.cos().multiply(imaginary.cosh()), real.sin().multiply(imaginary.sinh()).negate());
    }

    @Override
    public ANumber tan() {
	// tan(a+bi)=(sin(2a)+sinh(2b)i)/(cos(2a)+cosh(2b))
	ANumber a2 = real.multiply(ADecimal.TWO);
	ANumber b2 = imaginary.multiply(ADecimal.TWO);
	ANumber denom = a2.cos().add(b2.cosh());
	return new AComplex(new AFraction(a2.sin(), denom), new AFraction(b2.sinh(), denom));
    }

}

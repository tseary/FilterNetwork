package filter;

/**
 * A complex constant value.
 * @author Thomas
 *
 */
public class Complex implements IComplex {
	
	// Was private final to make Complex immutable
	protected double re, im;
	
	public Complex() {
		this(0d);
	}
	
	public Complex(double re) {
		this(re, 0d);
	}
	
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}
	
	public Complex(IComplex c) {
		this.re = c.getRe();
		this.im = c.getIm();
	}
	
	public Complex product(IComplex a) {
		return new Complex(this.getRe() * a.getRe() - this.getIm() * a.getIm(),
				this.getRe() * a.getIm() + this.getIm() * a.getRe());
	}
	
	public Complex quotient(IComplex d) {
		// Multiply this (numerator) and d (denominator) by the conjugate of d.
		// This results in a real number on the bottom.
		// The components of the top are divided by this real number.
		
		Complex newNum = this.product(conjugate(d));
		double realDen = magnitudeSqr(d);
		
		return new Complex(newNum.getRe() / realDen, newNum.getIm() / realDen);
	}
	
	public Complex conjugate() {
		return new Complex(this.getRe(), -this.getIm());
	}
	
	public double angle() {
		return Math.atan2(getIm(), getRe());
	}
	
	public double magnitude() {
		return Math.sqrt(magnitudeSqr());
	}
	
	public double magnitudeSqr() {
		return Math.pow(getRe(), 2d) + Math.pow(getIm(), 2d);
	}
	
	public Complex sum(IComplex a) {
		return new Complex(this.getRe() + a.getRe(), this.getIm() + a.getIm());
	}
	
	//
	// Static versions of unary operations
	//
	
	private static Complex conjugate(IComplex c) {
		return new Complex(c.getRe(), -c.getIm());
	}
	
	private static double magnitude(IComplex c) {
		return Math.sqrt(magnitudeSqr(c));
	}
	
	private static double magnitudeSqr(IComplex c) {
		return Math.pow(c.getRe(), 2d) + Math.pow(c.getIm(), 2d);
	}
	
	//
	// Overrides
	//
	
	@Override
	public double getRe() {
		return re;
	}
	
	@Override
	public double getIm() {
		return im;
	}
	
	@Override
	public String toString() {
		final double myRe = getRe(),
				myIm = getIm();
		
		boolean reExists = myRe != 0d,
				imExists = myIm != 0d;
		boolean imSign = myIm >= 0d;
		
		String reStr = String.valueOf(re);
		
		String imPrefix = (imSign ? (reExists ? " + " : "") : (reExists ? " - " : "-")) + "j";
		String imAbs = String.valueOf(Math.abs(myIm));
		
		String imStr = imExists ? (imPrefix + imAbs) : "";
		
		return reStr + imStr;
	}
}

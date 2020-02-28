package circuits;

import filter.Complex;
import filter.IComplex;

/**
 * A wrapper around Complex for circuit values.
 * @author Thomas
 *
 */
public class Impedance extends Complex {
	public Impedance() {
		super();
	}
	
	public Impedance(double r, double x) {
		super(r, x);
	}
	
	public Impedance(IComplex c) {
		super(c);
	}
	
	public void tweak(double factor) {
		re *= factor;
		im *= factor;
	}
	
	public static Impedance createOpen() {
		return new Impedance(Double.POSITIVE_INFINITY, 0d);
	}
	
	public static Impedance series(Impedance a, Impedance b) {
		return new Impedance(a.getRe() + b.getRe(), a.getIm() + b.getIm());
	}
	
	public static Impedance parallel(Impedance a, Impedance b) {
		Complex prod = a.product(b);
		Complex sum = a.sum(b);
		return new Impedance(prod.quotient(sum));
	}
}

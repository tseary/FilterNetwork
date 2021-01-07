package circuits;

import filter.Complex;
import filter.IComplex;
import filter.ITweakable;

/**
 * A wrapper around Complex for circuit values.
 * @author Thomas
 *
 */
public class Impedance extends Complex implements ITweakable {
	
	protected double lastTweak = 1d;
	
	private static int nextDesignator = 1;
	protected int designator = 0;
	
	public Impedance() {
		super();
		designator = nextDesignator++;
	}
	
	public Impedance(double r, double x) {
		super(r, x);
		designator = nextDesignator++;
	}
	
	public Impedance(IComplex c) {
		super(c);
		designator = nextDesignator++;
	}
	
	public void tweak(double factor) {}
	
	public void unTweak() {}
	
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
	
	/**
	 * Returns a component value formatted as a string, e.g. given 1000d, returns "1.00 k".
	 * @param value
	 * @return
	 */
	public static String niceUnitString(double value) {
		// Find order of magnitude
		String unitStr = "";
		if (value >= 1e9) {
			value /= 1e9;
			unitStr = "G";
		} else if (value >= 1e6) {
			value /= 1e6;
			unitStr = "Meg";	// distinct from "m"
		} else if (value >= 1e3) {
			value /= 1e3;
			unitStr = "k";
		} else if (value < 1e-12) {
			value *= 1e15;
			unitStr = "f";
		} else if (value < 1e-9) {
			value *= 1e12;
			unitStr = "p";
		} else if (value < 1e-6) {
			value *= 1e9;
			unitStr = "n";
		} else if (value < 1e-3) {
			value *= 1e6;
			unitStr = "u";
		} else if (value < 1d) {
			value *= 1e3;
			unitStr = "m";
		}
		
		// Choose number of decimal places
		int sigFigs;
		if (value >= 100d) {
			sigFigs = 1;
		} else if (value >= 10d) {
			sigFigs = 2;
		} else {
			sigFigs = 3;
		}
		
		return String.format("%." + sigFigs + "f", value) + " " + unitStr;
	}
}

package circuits;

public abstract class Reactance extends Impedance {
	
	private static double omega = Double.NaN;
	
	public static void setGlobalFrequency(double f) {
		omega = 2d * Math.PI * f;
	}
	
	protected static double getOmega() {
		return omega;
	}
}

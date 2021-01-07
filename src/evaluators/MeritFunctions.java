package evaluators;

public final class MeritFunctions {
	private MeritFunctions() {}
	
	/**
	 * 
	 * @param value The measured value.
	 * @param target The ideal value, resulting in a merit of 1.0.
	 * @param tolerance The difference between value and target that results in a merit of 0.5.
	 * @return
	 */
	public static double target(double value, double target, double tolerance) {
		final double scale = Math.sqrt(-Math.log(0.5)) / tolerance;
		return Math.exp(-Math.pow((value - target) * scale, 2));
	}
	
	/**
	 * 
	 * @param value The measured value.
	 * @param target The ideal value, resulting in a merit of 1.0.
	 * @param tolerance The difference between value and target that results in a merit of e^-1.
	 * @return
	 */
	public static double targetSharp(double value, double target, double tolerance) {
		double error = Math.abs(value - target);
		return Math.exp(-error / tolerance);
	}
	
	/**
	 * Returns a merit value that saturates at 0 and 1.
	 * Assuming hi > lo, if value <= lo, the result is <= 0.1, and if value >= hi the result is >= 0.9.
	 * @param value
	 * @param lo
	 * @param hi
	 * @return
	 */
	public static double sigmoid(double value, double lo, double hi) {
		// The value of x that produces 0.9 from the unscaled sigmoid function
		final double X90 = Math.log(9d);
		
		double stretch = 2d * X90 / (hi - lo);
		double xScaled = (value - lo) * stretch - X90;
		
		return 1d / (1d + Math.exp(-xScaled));
	}
}

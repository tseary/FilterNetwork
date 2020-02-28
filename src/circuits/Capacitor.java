package circuits;

public class Capacitor extends Reactance {
	
	private double c;
	
	public Capacitor(double c) {
		this.c = c;
	}
	
	public double getCapacitance() {
		return c;
	}
	
	@Override
	public void tweak(double factor) {
		c *= factor;
		lastTweak = factor;
	}
	
	@Override
	public void unTweak() {
		c /= lastTweak;
	}
	
	@Override
	public double getIm() {
		return -1d / (getOmega() * c);
	}
	
	@Override
	public String toString() {
		String desStr = String.format("C%1$d ", designator);
		String valStr;
		if (c >= 1d) {
			valStr = String.format("%1$.3f F", c);
		} else if (c >= 1e-3) {
			valStr = String.format("%1$.3f mF", c / 1e-3);
		} else if (c >= 1e-6) {
			valStr = String.format("%1$.3f uF", c / 1e-6);
		} else if (c >= 1e-9) {
			valStr = String.format("%1$.3f nF", c / 1e-9);
		} else {
			valStr = String.format("%1$.3f pF", c / 1e-12);
		}
		return desStr + valStr + " capacitor";
	}
}

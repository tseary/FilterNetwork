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
	}
	
	@Override
	public double getIm() {
		return -1d / (getOmega() * c);
	}
	
	@Override
	public String toString() {
		String str = "";
		if (c >= 1d) {
			str = String.format("%1$.3f F", c);
		} else if (c >= 1e-3) {
			str = String.format("%1$.3f mF", c / 1e-3);
		} else if (c >= 1e-6) {
			str = String.format("%1$.3f uF", c / 1e-6);
		} else if (c >= 1e-9) {
			str = String.format("%1$.3f nF", c / 1e-9);
		} else {
			str = String.format("%1$.3f pF", c / 1e-12);
		}
		return str + " capacitor";
	}
}

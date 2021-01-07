package circuits;

public class Capacitor extends Reactance {
	
	private double cap;
	
	public Capacitor(double c) {
		this.cap = c;
	}
	
	public double getCapacitance() {
		return cap;
	}
	
	@Override
	public void tweak(double factor) {
		cap *= factor;
		lastTweak = factor;
	}
	
	@Override
	public void unTweak() {
		cap /= lastTweak;
	}
	
	@Override
	public double getIm() {
		return -1d / (getOmega() * cap);
	}
	
	@Override
	public String toString() {
		String desStr = String.format("C%1$d ", designator);
		return desStr + niceUnitString(cap) + "F capacitor";
	}
}

package circuits;

public class Resistor extends Impedance {
	public Resistor(double r) {
		super(r, 0d);
	}
	
	public double getResistance() {
		return re;
	}
	
	public void tweak(double factor) {
		re *= factor;
		lastTweak = factor;
	}
	
	public void unTweak() {
		re /= lastTweak;
	}
	
	@Override
	public String toString() {
		String desStr = String.format("R%1$d ", designator);
		return desStr + getRe() + " Ohm resistor";
	}
}

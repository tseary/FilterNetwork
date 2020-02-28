package circuits;

public class Resistor extends Impedance {
	public Resistor(double r) {
		super(r, 0d);
	}
	
	public double getResistance() {
		return re;
	}
	
	@Override
	public void tweak(double factor) {
		re *= factor;
	}
	
	@Override
	public String toString() {
		return getRe() + " Ohm resistor";
	}
}

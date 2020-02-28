package circuits;

public class Inductor extends Reactance {
	
	private double l;
	
	public Inductor(double l) {
		this.l = l;
	}
	
	public double getInductance() {
		return l;
	}
	
	@Override
	public void tweak(double factor) {
		l *= factor;
		lastTweak = factor;
	}
	
	@Override
	public void unTweak() {
		l /= lastTweak;
	}
	
	@Override
	public double getIm() {
		return getOmega() * l;
	}
	
	@Override
	public String toString() {
		String desStr = String.format("L%1$d ", designator);
		String valStr = "";
		if (l >= 1d) {
			valStr = String.format("%1$.3f H", l);
		} else if (l >= 1e-3) {
			valStr = String.format("%1$.3f mH", l / 1e-3);
		} else if (l >= 1e-6) {
			valStr = String.format("%1$.3f uH", l / 1e-6);
		} else if (l >= 1e-9) {
			valStr = String.format("%1$.3f nH", l / 1e-9);
		} else {
			valStr = String.format("%1$.3f pH", l / 1e-12);
		}
		return desStr + valStr + " inductor";
	}
}

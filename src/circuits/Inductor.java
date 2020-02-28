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
	}
	
	@Override
	public double getIm() {
		return getOmega() * l;
	}
	
	@Override
	public String toString() {
		String str = "";
		if (l >= 1d) {
			str = String.format("%1$.3f H", l);
		} else if (l >= 1e-3) {
			str = String.format("%1$.3f mH", l / 1e-3);
		} else if (l >= 1e-6) {
			str = String.format("%1$.3f uH", l / 1e-6);
		} else if (l >= 1e-9) {
			str = String.format("%1$.3f nH", l / 1e-9);
		} else {
			str = String.format("%1$.3f pH", l / 1e-12);
		}
		return str + " inductor";
	}
}

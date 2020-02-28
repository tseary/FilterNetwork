package circuits;

public class SeriesBranch extends Branch {
	public SeriesBranch(Impedance z1, Impedance z2) {
		super(z1, z2);
	}
	
	@Override
	public double getRe() {
		return series(z1, z2).getRe();
	}
	
	@Override
	public double getIm() {
		return series(z1, z2).getIm();
	}
	
	@Override
	public String toString() {
		return z1.toString() + ", " + z2.toString();
	}
}

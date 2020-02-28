package circuits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import filter.ITweakable;

public abstract class Branch extends Impedance {
	protected Impedance z1, z2;
	
	public Branch(Impedance z1, Impedance z2) {
		this.z1 = z1;
		this.z2 = z2;
	}
	
	public Collection<ITweakable> getTweakables() {
		List<ITweakable> tweakables = new ArrayList<ITweakable>();
		tweakables.add(z1);
		tweakables.add(z2);
		return tweakables;
	}
	
	public Collection<Impedance> getImpedances() {
		List<Impedance> impedances = new ArrayList<Impedance>();
		impedances.add(z1);
		impedances.add(z2);
		return impedances;
	}
}

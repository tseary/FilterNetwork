package filter;

import circuits.Capacitor;
import circuits.Impedance;
import circuits.Inductor;
import circuits.Resistor;

/**
 * A single component of a two-port network, representing a series- or shunt-impedance.
 * This class wraps a pssive component and has a flag for series/shunt.
 * @author Thomas
 *
 */
public class NetImpedance {
	
	public Impedance z;
	private boolean shunt;
	
	public NetImpedance(Impedance z, boolean shunt) {
		this.z = z;
		this.shunt = shunt;
	}
	
	public boolean isShunt() {
		return shunt;
	}
	
	/**
	 * Gets the impedance seen at the input when the given load is connected to this network.
	 * @param load
	 * @return
	 */
	public Impedance upstreamCombo(Impedance load) {
		return shunt ? Impedance.parallel(z, load) : Impedance.series(z, load);
	}
	
	@Override
	public String toString() {
		return getDiagram() + (shunt ? "shunt  " : "series ") + z.toString();
	}
	
	public String getDiagram() {
		if (z instanceof Inductor) {
			return shunt ? " |-L--| " : " |    L ";
		} else if (z instanceof Capacitor) {
			return shunt ? " |-C--| " : " |    C ";
		} else if (z instanceof Resistor) {
			return shunt ? " |-R--| " : " |    R ";
		} else {
			return shunt ? " |-Z--| " : " |    Z ";
		}
	}
}

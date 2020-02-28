package filter;

import java.util.ArrayList;
import java.util.List;

import circuits.Impedance;
import circuits.Reactance;

public class Network {
	
	private List<NetImpedance> components;
	private List<ITweakable> tweakables;
	
	private List<TweakGroup> tweakgroups;
	
	public Network() {
		components = new ArrayList<NetImpedance>();
		tweakables = new ArrayList<ITweakable>();
	}
	
	public void addComponent(Impedance z, boolean shunt) {
		addComponent(z, shunt, true);
	}
	
	public void addComponent(Impedance z, boolean shunt, boolean tweakable) {
		components.add(new NetImpedance(z, shunt));
		if (tweakable) {
			tweakables.add(z);
		}
	}
	
	public AnalysisResult analyse(TestCondition testCondition) {
		// Set the operating conditions
		Reactance.setGlobalFrequency(testCondition.getFrequency());
		Impedance load = testCondition.getLoad();
		
		// Assemble the impedances working backward from the load
		Impedance total = load;
		
		// Assume a test voltage of 1 V, 0 deg at the load
		Complex loadVoltage = new Complex(1d);
		Complex voltage = new Complex(loadVoltage);
		Complex current = voltage.quotient(load);
		
		// Append the network components to the load
		for (int i = components.size() - 1; i >= 0; i--) {
			NetImpedance comp = components.get(i);
			
			// Update the total impedance
			total = comp.upstreamCombo(total);
			
			if (comp.isShunt()) {
				// The voltage is the same, so update the current
				current = voltage.quotient(total);
			} else {
				// The current is the same, so update the voltage
				voltage = current.product(total);
			}
		}
		
		// Apply the line impedance
		Impedance line = testCondition.getLine();
		if (line != null) {
			total = Impedance.series(line, total);
			voltage = current.product(total);
		}
		
		return new AnalysisResult(total, loadVoltage.quotient(voltage));
	}
	
	public List<Impedance> getComponents() {
		ArrayList<Impedance> zs = new ArrayList<>(components.size());
		for (NetImpedance comp : components) {
			zs.add(comp.z);
		}
		return zs;
	}
	
	public List<ITweakable> getTweakables() {
		return tweakables;
	}
	
	public void print() {
		System.out.println("Network of " + components.size() + " components:");
		for (NetImpedance comp : components) {
			System.out.println(comp);
		}
	}
}

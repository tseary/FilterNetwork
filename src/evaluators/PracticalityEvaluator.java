package evaluators;

import java.util.List;

import circuits.Capacitor;
import circuits.Impedance;
import circuits.Inductor;
import filter.Network;

/**
 * A merit evaluator that prefers realistic inductor and capacitor values.
 * @author Thomas
 *
 */
public class PracticalityEvaluator implements IMeritEvaluator {
	
	// Preferred component values
	// If a components is within these bounds, its merit is 1.0.
	private static final double hiL = 250e-9,
			loL = 10e-9,
			hiC = 660e-12,
			loC = 10e-12;
	
	// Limiting component values
	// A component's merit decays exponentially outside the preferred bounds
	// such that it equals e^-1 at these values.
	private static final double maxL = 260e-9,
			minL = 1e-9,
			maxC = 1000e-12,
			minC = 1e-12;
	
	@Override
	public double getMerit(Network network) {
		List<Impedance> components = network.getComponents();
		
		double merit = 1d;
		
		for (Impedance comp : components) {
			if (comp instanceof Inductor) {
				merit *= evaluateInductor((Inductor)comp);
			} else if (comp instanceof Capacitor) {
				merit *= evaluateCapacitor((Capacitor)comp);
			}
		}
		
		return merit;
	}
	
	/**
	 * Gets the merit of an inductor.
	 * @param ind
	 * @return
	 */
	private static double evaluateInductor(Inductor ind) {
		double L = ind.getInductance();
		
		// Too high
		if (L > hiL) {
			return Math.exp(-(L - hiL) / (maxL - hiL));
		}
		
		// Too low
		if (L < loL) {
			return Math.exp(-(loL - L) / (loL - minL));
		}
		
		// Preferred value
		return 1d;
	}
	
	/**
	 * Gets the merit of a capacitor.
	 * @param cap
	 * @return
	 */
	private static double evaluateCapacitor(Capacitor cap) {
		double C = cap.getCapacitance();
		
		// Too high
		if (C > hiC) {
			return Math.exp(-(C - hiC) / (maxC - hiC));
		}
		
		// Too low
		if (C < loC) {
			return Math.exp(-(loC - C) / (loC - minC));
		}
		
		// Preferred value
		return 1d;
	}
}

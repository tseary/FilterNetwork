package filter;

import java.util.ArrayList;
import java.util.List;

import circuits.Capacitor;
import circuits.Impedance;
import circuits.Inductor;
import circuits.Reactance;
import circuits.Resistor;
import merit.BandPassEvaluator;
import merit.CenterFrequencyEvaluator;
import merit.IMeritEvaluator;
import merit.PracticalityEvaluator;
import merit.TransformerEvaluator;

public class FilterNetworkMain {
	
	public static void main(String[] args) {
		
		final double f0 = 27.12e6;
		final double span = 2d;
		
		Impedance line = new Resistor(100d);
		
		// Create an pi-CLC + pi-LCL network
		Network network = new Network();
		network.addComponent(new Capacitor(100e-12d), true);
		network.addComponent(new Inductor(100e-9d), false);
		network.addComponent(new Capacitor(100e-12d), true);
		network.addComponent(new Inductor(100e-9d), true);
		network.addComponent(new Capacitor(100e-12d), false);
		network.addComponent(new Inductor(100e-9d), true);
		
		// Create a load
		Impedance load = new Resistor(20d);
		TestCondition nominalTestCondition = new TestCondition(line, load, f0);
		System.out.println("Z_load = " + load.toString());
		
		network.print();
		
		double center = CenterFrequencyEvaluator.findCenterFrequency(network, nominalTestCondition);
		System.out.println("center = " + center);
		
		// Test the impedance at multiple frequencies
		for (double f = f0 / span; f <= f0 * span; f *= Math.pow(span, 1 / 10d)) {
			Reactance.setGlobalFrequency(f);
			AnalysisResult result = network.analyse(new TestCondition(nominalTestCondition, f));
			
			System.out.print((int)f + "\t");
			System.out.println(result.getGainDecibels());
		}
		System.out.println();
		
		// Iterate
		List<IMeritEvaluator> evaluators = new ArrayList<IMeritEvaluator>();
		evaluators.add(new BandPassEvaluator(nominalTestCondition, 0.125d));
		evaluators.add(new CenterFrequencyEvaluator(nominalTestCondition, 0.3d));
		evaluators.add(new PracticalityEvaluator());
		evaluators.add(new TransformerEvaluator(nominalTestCondition, new Resistor(200d)));
		
		iterateDesign(network, evaluators);
		
		network.print();
	}
	
	public static void iterateDesign(Network network, List<IMeritEvaluator> evaluators) {
		List<Impedance> components = network.getComponents();
		
		// Tweak each component up and down by a small amount.
		// After each adjustment, check the merit.
		// If the merit improves, keep the change, otherwise discard the change.
		// If tweaking every component produces no improvement, reduce the tweak size and continue.
		
		double bestMerit = getTotalMerit(network, evaluators);
		double tweakProportion = 0.10d;
		final double tweakProportionDrop = 0.5d;
		
		final int MAX_ITERATIONS = 100000;
		int iterations = 0;
		
		// Keep iterating until the tweak factor is sufficiently small
		do {
			double tweakFactor = 1d + tweakProportion;
			
			System.out.println("tweakPercent = " + tweakProportion);
			
			// Keep iterating until no further improvements can be made
			boolean improvement;
			do {
				// Clear the flag
				improvement = false;
				
				for (int i = 0; i < components.size(); i++) {
					// Get the component
					Impedance comp = components.get(i);
					double newMerit;
					
					// Tweak down
					comp.tweak(1d / tweakFactor);
					newMerit = getTotalMerit(network, evaluators);
					if (newMerit > bestMerit) {
						// Keep the change
						bestMerit = newMerit;
						improvement = true;
						
						System.out.println("Improved " + comp + "\tmerit = " + bestMerit);
						
						continue;	// Go to the next component
					} else {
						// Discard the change
						comp.tweak(tweakFactor);
					}
					
					// Tweak up
					comp.tweak(tweakFactor);
					
					newMerit = getTotalMerit(network, evaluators);
					if (newMerit > bestMerit) {
						// Keep the change
						bestMerit = newMerit;
						improvement = true;
						
						System.out.println("Improved " + comp + "\tmerit = " + bestMerit);
						
						continue;	// Go to the next component
					} else {
						// Discard the change
						comp.tweak(1d / tweakFactor);
					}
				}
			} while (improvement && ++iterations < MAX_ITERATIONS);
			
			// Reduce the tweak size
			tweakProportion *= tweakProportionDrop;
			
		} while (tweakProportion > 0.0000009d && iterations < MAX_ITERATIONS);
		
		System.out.println("Stopped after " + iterations + " iterations.");
	}
	
	public static double getTotalMerit(Network network, List<IMeritEvaluator> evaluators) {
		double totalMerit = 1d;
		
		for (IMeritEvaluator evaluator : evaluators) {
			double merit = evaluator.getMerit(network);
			totalMerit *= merit;
			
			System.out.println(evaluator.getClass().getName() + " \tmerit = " + merit);
		}
		System.out.println();
		
		return totalMerit;
	}
}

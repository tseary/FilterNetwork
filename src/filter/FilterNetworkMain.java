package filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import circuits.Capacitor;
import circuits.Impedance;
import circuits.Inductor;
import circuits.Reactance;
import circuits.Resistor;
import evaluators.IMeritEvaluator;
import evaluators.ImpedanceEvaluator;

public class FilterNetworkMain {
	
	public static void main(String[] args) {
		new FilterNetworkMain().doTheThing();
	}
	
	private Network network;
	
	List<IMeritEvaluator> evaluators;
	private Map<IMeritEvaluator, Double> merits;
	
	public FilterNetworkMain() {
		evaluators = new ArrayList<IMeritEvaluator>();
		merits = new HashMap<IMeritEvaluator, Double>();
	}
	
	public void doTheThing() {
		
		final double f0 = 1.695e6;
		
		// Create the line
		// Impedance line50 = new Resistor(50d);
		
		// Create a lossy 1:1 transformer
		network = new Network();
		Capacitor Cself = new Capacitor(10e-12d);
		Inductor Lleak = new Inductor(132e-9d);
		Resistor Rwinding = new Resistor(0.102d);
		Inductor Lmutual = new Inductor(985e-9d);
		Resistor Rcore = new Resistor(500d);
		network.addComponent(Cself, true);
		network.addComponent(Rwinding, false);
		network.addComponent(Lleak, false);
		network.addComponent(Lmutual, true);
		network.addComponent(Rcore, true);
		network.addComponent(Lleak, false);
		network.addComponent(Rwinding, false);
		network.addComponent(Cself, true);
		
		network.print();
		
		// Create test loads
		Impedance loadShort = new Resistor(1e-3d);
		Impedance loadOpen = new Resistor(1e6d);
		
		// Create evaluators
		TestCondition testShort = new TestCondition(null, loadShort, f0);
		TestCondition testOpen = new TestCondition(null, loadOpen, f0);
		final double fHi = f0 + 0.1e6d,
				fLo = f0 - 0.1e6d;
		TestCondition testOpenHif = new TestCondition(null, loadOpen, fHi);
		TestCondition testOpenLof = new TestCondition(null, loadOpen, fLo);
		
		Reactance.setGlobalFrequency(f0);
		Impedance observedZinShort = Impedance.series(new Resistor(0.1023d), new Inductor(248.5e-9d));
		Impedance observedZinOpen = Impedance.series(new Resistor(11.95d), new Inductor(1117e-9d));
		Reactance.setGlobalFrequency(fHi);
		Impedance observedZinOpenHif = Impedance.series(new Resistor(11.95d + 0.7), new Inductor(1117e-9d));
		Reactance.setGlobalFrequency(fLo);
		Impedance observedZinOpenLof = Impedance.series(new Resistor(11.95d - 0.7), new Inductor(1117e-9d));
		
		evaluators = new ArrayList<IMeritEvaluator>();
		evaluators.add(new ImpedanceEvaluator(testShort, observedZinShort));
		evaluators.add(new ImpedanceEvaluator(testOpen, observedZinOpen));
		evaluators.add(new ImpedanceEvaluator(testOpenHif, observedZinOpenHif));
		evaluators.add(new ImpedanceEvaluator(testOpenLof, observedZinOpenLof));
		
		// Iterate
		iterateDesignMulti(100000);
		
		network.print();
		
		// Extra prints
		AnalysisResult result;
		
		System.out.println();
		result = network.analyse(testShort);
		System.out.println("Input Z (shorted secondary) = " + result.getInputImpedance());
		System.out.println("observedZinShort = " + observedZinShort);
		
		System.out.println();
		result = network.analyse(testOpen);
		System.out.println("Input Z (open secondary) = " + result.getInputImpedance());
		System.out.println("observedZinOpen = " + observedZinOpen);
	}
	
	public void iterateDesign(int maxIterations) {
		
		List<ITweakable> tweakables = new ArrayList<ITweakable>();
		tweakables.addAll(network.getTweakables());
		
		// Tweak each component up and down by a small amount.
		// After each adjustment, check the merit.
		// If the merit improves, keep the change, otherwise discard the change.
		// If tweaking every component produces no improvement, reduce the tweak size and continue.
		
		double bestMerit = getTotalMerit();
		
		printMerits();
		
		double tweakProportion = 0.10d;
		final double tweakProportionDrop = 0.5d;
		
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
				
				for (int i = 0; i < tweakables.size(); i++) {
					// Get the component
					ITweakable comp = tweakables.get(i);
					double newMerit;
					
					// Tweak down
					comp.tweak(1d / tweakFactor);
					newMerit = getTotalMerit();
					if (newMerit > bestMerit) {
						// Keep the change
						bestMerit = newMerit;
						improvement = true;
						
						System.out.println("Improved " + comp + "\tmerit = " + bestMerit);
						printMerits();
						
						continue;	// Go to the next component
					} else {
						// Discard the change
						comp.unTweak();
					}
					
					// Tweak up
					comp.tweak(tweakFactor);
					
					newMerit = getTotalMerit();
					if (newMerit > bestMerit) {
						// Keep the change
						bestMerit = newMerit;
						improvement = true;
						
						System.out.println("Improved " + comp + "\tmerit = " + bestMerit);
						printMerits();
						
						continue;	// Go to the next component
					} else {
						// Discard the change
						comp.unTweak();
					}
				}
			} while (improvement && ++iterations < maxIterations);
			
			// Reduce the tweak size
			tweakProportion *= tweakProportionDrop;
			
		} while (tweakProportion > 0.0000009d && iterations < maxIterations);
		
		System.out.println("Stopped after " + iterations + " iterations.");
	}
	
	public void iterateDesignMulti(int maxIterations) {
		
		TweakGroup tweakGroup = new TweakGroup();
		tweakGroup.addAll(network.getTweakables());
		
		// Tweak each component up and down by a small amount.
		// After each adjustment, check the merit.
		// If the merit improves, keep the change, otherwise discard the change.
		// If tweaking every component produces no improvement, reduce the tweak size and continue.
		
		double bestMerit = getTotalMerit();
		
		printMerits();
		
		double tweakProportion = 0.10d;
		final double tweakProportionDrop = 0.5d;
		
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
				
				boolean moreCombos;
				do {
					moreCombos = tweakGroup.nextTweakCombo(tweakFactor);
					
					// Test merit
					double newMerit = getTotalMerit();
					if (newMerit > bestMerit) {
						// Keep the change
						bestMerit = newMerit;
						improvement = true;
						
						printMerits();
						
						continue;	// Go to the next combo
					} else {
						// Discard the change(s)
						tweakGroup.unTweakCombo();
					}
				} while (moreCombos && ++iterations < maxIterations);
				
			} while (improvement && iterations < maxIterations);
			
			// Reduce the tweak size
			tweakProportion *= tweakProportionDrop;
			
		} while (tweakProportion > 0.0000009d && iterations < maxIterations);
		
		System.out.println("Stopped after " + iterations + " iterations.");
	}
	
	public double getTotalMerit() {
		double totalMerit = 1d;
		
		for (IMeritEvaluator evaluator : evaluators) {
			double merit = evaluator.getMerit(network);
			merits.put(evaluator, Double.valueOf(merit));
			
			// Multiplicative
			totalMerit *= merit;
		}
		
		return totalMerit;
	}
	
	public void printMerits() {
		for (IMeritEvaluator evaluator : evaluators) {
			System.out.println(evaluator.toString());
			System.out.println("\tmerit = " + merits.get(evaluator));
		}
		System.out.println();
	}
}

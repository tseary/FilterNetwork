package filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import circuits.Branch;
import circuits.Capacitor;
import circuits.Impedance;
import circuits.Inductor;
import circuits.Reactance;
import circuits.Resistor;
import circuits.SeriesBranch;
import merit.BandPassEvaluator;
import merit.BandStopEvaluator;
import merit.CenterFrequencyEvaluator;
import merit.IMeritEvaluator;
import merit.PracticalityEvaluator;

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
		
		final double f0 = 27.12e6;
		final double span = 2d;
		
		Impedance line = new Resistor(100d);
		
		// Create an pi-CLC + pi-LCL network
		network = new Network();
		/*Capacitor C1 = new Capacitor(100e-12d);
		Inductor L1 = new Inductor(100e-9d);
		network.addComponent(C1, true);
		network.addComponent(new Inductor(100e-9d), false);
		network.addComponent(C1, true);
		network.addComponent(L1, true);
		network.addComponent(new Capacitor(100e-12d), false);
		network.addComponent(L1, true);*/
		/*Inductor L1 = new Inductor(250e-9d);
		Capacitor C1 = new Capacitor(131e-12d);
		network.addComponent(L1, false);
		network.addComponent(new Capacitor(100e-12d), true);
		network.addComponent(L1, false);
		network.addComponent(C1, false);
		network.addComponent(new Inductor(100e-9d), true);
		network.addComponent(C1, false);
		network.addComponent(new Inductor(42e-9d), true);
		network.addComponent(new Capacitor(820e-12d), true);*/
		Branch seriesBranch = new SeriesBranch(new Capacitor(100e-12), new Inductor(100e-9));
		network.addComponent(seriesBranch, true);
		network.addComponent(new Inductor(100e-9d), true);
		network.addComponent(new Capacitor(100e-12d), true);
		
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
		evaluators = new ArrayList<IMeritEvaluator>();
		evaluators.add(new BandPassEvaluator(nominalTestCondition));
		// evaluators.add(new CenterFrequencyEvaluator(nominalTestCondition, 0.3d));
		evaluators.add(new PracticalityEvaluator());
		// evaluators.add(new TransformerEvaluator(nominalTestCondition, new Resistor(200d)));
		evaluators.add(new BandStopEvaluator(new TestCondition(nominalTestCondition, 32.7e6)));
		
		iterateDesignMulti(100000);
		
		network.print();
	}
	
	public void iterateDesign(int maxIterations) {
		
		List<ITweakable> tweakables = network.getTweakables();
		
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
			totalMerit *= merit;
		}
		
		return totalMerit;
	}
	
	public void printMerits() {
		for (IMeritEvaluator evaluator : evaluators) {
			System.out.println(evaluator.getClass().getName() + " \tmerit = " + merits.get(evaluator));
		}
		System.out.println();
	}
}

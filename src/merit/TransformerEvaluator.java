package merit;

import circuits.Impedance;
import filter.AnalysisResult;
import filter.Complex;
import filter.Network;
import filter.TestCondition;

/**
 * @author Thomas
 *
 */
public class TransformerEvaluator implements IMeritEvaluator {
	
	private Impedance zInIdeal;
	private TestCondition testCondition;
	
	public TransformerEvaluator(TestCondition testCondition, Impedance inputImpedance) {
		zInIdeal = inputImpedance;
		this.testCondition = testCondition;
	}
	
	@Override
	public double getMerit(Network network) {
		AnalysisResult result = network.analyse(testCondition);
		Impedance zIn = result.getInputImpedance();
		
		Complex ratio = zIn.quotient(zInIdeal);
		
		double magnitude = ratio.magnitude();
		double magnitudeMerit = MeritFunctions.target(magnitude, 1d, 0.10d);
		
		double phase = ratio.angle();
		double phaseMerit = MeritFunctions.target(phase, 0d, Math.PI / 2d);
		
		return magnitudeMerit * phaseMerit;
	}
	
}

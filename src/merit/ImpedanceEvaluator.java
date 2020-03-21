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
public class ImpedanceEvaluator implements IMeritEvaluator {
	
	private TestCondition testCondition;
	private Impedance zInIdeal;
	
	public ImpedanceEvaluator(TestCondition testCondition, Impedance inputImpedance) {
		this.testCondition = testCondition;
		this.zInIdeal = inputImpedance;
	}
	
	@Override
	public double getMerit(Network network) {
		AnalysisResult result = network.analyse(testCondition);
		Impedance zIn = result.getInputImpedance();
		
		Complex ratio = zIn.quotient(zInIdeal);
		
		double magnitude = ratio.magnitude();
		double magnitudeMerit = MeritFunctions.targetSharp(magnitude, 1d, 0.10d);
		
		double phase = ratio.angle();
		double phaseMerit = MeritFunctions.targetSharp(phase, 0d, Math.PI / 2d);
		
		return magnitudeMerit * phaseMerit;
	}
	
}

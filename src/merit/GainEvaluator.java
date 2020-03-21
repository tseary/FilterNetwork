package merit;

import filter.AnalysisResult;
import filter.Network;
import filter.TestCondition;

/**
 * @author Thomas
 *
 */
public class GainEvaluator implements IMeritEvaluator {
	
	private TestCondition testCondition;
	private double gainIdeal;
	private double tolerance;
	
	public GainEvaluator(TestCondition testCondition, double gainIdealDecibels, double toleranceDecibels) {
		this.testCondition = testCondition;
		this.gainIdeal = gainIdealDecibels;
		this.tolerance = toleranceDecibels;
	}
	
	@Override
	public double getMerit(Network network) {
		AnalysisResult result = network.analyse(testCondition);
		return MeritFunctions.targetSharp(result.getGainDecibels(), gainIdeal, tolerance);
	}
	
}

package evaluators;

import filter.AnalysisResult;
import filter.Network;
import filter.TestCondition;

public class ResistiveEvaluator implements IMeritEvaluator {
	
	private TestCondition testCondition;
	private double tolerance;
	
	public ResistiveEvaluator(TestCondition testCondition, double toleranceRad) {
		this.testCondition = testCondition;
		this.tolerance = toleranceRad;
	}
	
	@Override
	public double getMerit(Network network) {
		AnalysisResult result = network.analyse(testCondition);
		double inputPhase = result.getInputImpedance().angle();
		return MeritFunctions.targetSharp(inputPhase, 0, tolerance);
	}
}

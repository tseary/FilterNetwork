package merit;

import circuits.Impedance;
import filter.AnalysisResult;
import filter.Network;
import filter.TestCondition;

public class CenterFrequencyEvaluator implements IMeritEvaluator {
	
	private TestCondition testCondition;
	private double tolerance;
	
	public CenterFrequencyEvaluator(TestCondition testCondition, double tolerance) {
		this.testCondition = testCondition;
		this.tolerance = tolerance;
	}
	
	@Override
	public double getMerit(Network network) {
		double fcIdeal = testCondition.getFrequency();
		return MeritFunctions.targetSharp(
				findCenterFrequency(network, testCondition), fcIdeal, fcIdeal * tolerance);
	}
	
	/**
	 * Finds the center frequency by successive approximation, to within 1 Hz.
	 * @param network
	 * @param startFreq
	 * @return
	 */
	public static double findCenterFrequency(Network network, TestCondition testCondition) {
		Impedance load = testCondition.getLoad();
		
		double testFreq = testCondition.getFrequency();
		double change = testFreq / 2d;
		
		// The maximum gain in dB
		double maxGain = -1000d;
		
		do {
			AnalysisResult result;
			
			double loFreq = testFreq - change;
			result = network.analyse(new TestCondition(testCondition, loFreq));
			double loGain = result.getGainDecibels();
			
			double hiFreq = testFreq + change;
			result = network.analyse(new TestCondition(testCondition, hiFreq));
			double hiGain = result.getGainDecibels();
			
			if (loGain > maxGain || hiGain > maxGain) {
				// One of the new frequencies is better
				if (loGain > hiGain) {
					testFreq = loFreq;
					maxGain = loGain;
				} else {
					testFreq = hiFreq;
					maxGain = hiGain;
				}
			} else {
				// Use a smaller change if neither high or low was better
				change /= 2d;
			}
			
		} while (change > 1d);
		
		return testFreq;
	}
}

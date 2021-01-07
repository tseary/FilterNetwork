package evaluators;

import filter.AnalysisResult;
import filter.Network;
import filter.TestCondition;

public class BandPassEvaluator implements IMeritEvaluator {
	
	private TestCondition testCondition;
	
	/**
	 * Creates a bandpass evaluator with a bandwidth of +/-10%.
	 * @param centerFrequency
	 * @param load
	 */
	public BandPassEvaluator(TestCondition testCondition) {
		this.testCondition = testCondition;
	}
	
	@Override
	public double getMerit(Network network) {
		// The merit is defined as passband gain / stopband gain
		AnalysisResult result;
		
		result = network.analyse(testCondition);
		double passGain = result.getGainDecibels();
		
		return MeritFunctions.sigmoid(passGain, -20d, 0d);	// 0 dB nominal, -20 dB is bad.
	}
}

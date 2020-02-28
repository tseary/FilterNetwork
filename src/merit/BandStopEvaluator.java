package merit;

import filter.AnalysisResult;
import filter.Network;
import filter.TestCondition;

public class BandStopEvaluator implements IMeritEvaluator {
	
	private TestCondition testCondition;
	
	/**
	 * Creates a bandpass evaluator with a bandwidth of +/-10%.
	 * @param centerFrequency
	 * @param load
	 */
	public BandStopEvaluator(TestCondition testCondition) {
		this.testCondition = testCondition;
	}
	
	@Override
	public double getMerit(Network network) {
		// The merit is defined as passband gain / stopband gain
		AnalysisResult result;
		
		result = network.analyse(testCondition);
		double stopGain = result.getGainDecibels();
		
		return MeritFunctions.sigmoid(stopGain, 0d, -20d);	// -20 dB difference nominal
	}
}

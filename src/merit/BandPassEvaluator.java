package merit;

import filter.AnalysisResult;
import filter.Network;
import filter.TestCondition;

public class BandPassEvaluator implements IMeritEvaluator {
	
	private TestCondition testConditionLo, testConditionHi, testConditionCenter;
	
	/**
	 * Creates a bandpass evaluator with a bandwidth of +/-10%.
	 * @param centerFrequency
	 * @param load
	 */
	public BandPassEvaluator(TestCondition testCondition, double bandWidth) {
		this.testConditionCenter = testCondition;
		double fc = testCondition.getFrequency();
		testConditionLo = new TestCondition(testCondition, fc * (1d - bandWidth));
		testConditionHi = new TestCondition(testCondition, fc * (1d + bandWidth));
	}
	
	@Override
	public double getMerit(Network network) {
		// The merit is defined as passband gain / stopband gain
		AnalysisResult result;
		
		result = network.analyse(testConditionLo);
		double loGain = result.getGainDecibels();
		
		result = network.analyse(testConditionCenter);
		double passGain = result.getGainDecibels();
		
		result = network.analyse(testConditionHi);
		double hiGain = result.getGainDecibels();
		
		double loStopMerit = MeritFunctions.sigmoid(passGain - loGain, 0d, 20d);	// +20 dB difference nominal
		double hiStopMerit = MeritFunctions.sigmoid(passGain - hiGain, 0d, 20d);	// +20 dB difference nominal
		double passMerit = MeritFunctions.sigmoid(passGain, -20d, 0d);	// 0 dB nominal, -20 dB is bad.
		
		return loStopMerit * hiStopMerit * passMerit;
	}
}

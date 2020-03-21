package filter;

import circuits.Impedance;

public class AnalysisResult {
	
	private Impedance inputImpedance;
	private Complex transfer;
	
	public AnalysisResult(Impedance inputImpedance, Complex transfer) {
		this.inputImpedance = inputImpedance;
		this.transfer = transfer;
	}
	
	public Impedance getInputImpedance() {
		return inputImpedance;
	}
	
	public Complex transfer() {
		return transfer;
	}
	
	public double getGainDecibels() {
		return decibels(transfer.magnitude());
	}
	
	/** Converts voltage gain to decibels. */
	public static double decibels(double gain) {
		return 20d * Math.log10(gain);
	}
}

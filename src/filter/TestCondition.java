package filter;

import circuits.Impedance;

public class TestCondition {
	private Impedance line, load;
	private double frequency;
	
	public TestCondition(Impedance line, Impedance load, double frequency) {
		this.line = line;
		this.load = load;
		this.frequency = frequency;
	}
	
	public TestCondition(TestCondition impedances, double frequency) {
		this.line = impedances.getLine();
		this.load = impedances.getLoad();
		this.frequency = frequency;
	}
	
	public Impedance getLine() {
		return line;
	}
	
	public Impedance getLoad() {
		return load;
	}
	
	public double getFrequency() {
		return frequency;
	}
}

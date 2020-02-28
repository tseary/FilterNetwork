package merit;

import filter.Network;

/**
 * Merit is a unitless number that describes a network's performance according to some specification.
 * Merit of 1.0 means that the network meets the specification exactly.
 * Merit of 0.0 means that the network is no better than a straight wire.
 * Negative merit is not permitted.
 * 
 * Merits are multiplicative - if two evaluators are applied to a network and one has 0 merit,
 * then the total merit of the network is zero.
 * 
 * Evaluators should be written with this in mind:
 * If exceeding the specification does not add value, the merit should be clamped at 1.
 * 
 * @author Thomas
 *
 */
public interface IMeritEvaluator {
	public double getMerit(Network network);
}

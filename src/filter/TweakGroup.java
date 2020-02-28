package filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TweakGroup provides an ordered way to tweak multiple components.
 * @author Thomas
 *
 */
public class TweakGroup {
	
	private List<ITweakable> tweakables;
	
	/** The number of possible combinations (including combo 0). */
	private int combinations = 0;
	/** The currently applied combination. */
	private int combo = 0;
	
	public TweakGroup() {
		tweakables = new ArrayList<ITweakable>();
	}
	
	public void add(ITweakable tweakable) {
		if (tweakables.contains(tweakable)) return;
		tweakables.add(tweakable);
		combinations = (int)Math.round(Math.pow(3d, tweakables.size()));
	}
	
	public void addAll(Collection<ITweakable> toAdd) {
		for (ITweakable tweakable : toAdd) {
			this.add(tweakable);
		}
	}
	
	/**
	 * 
	 * @param factor
	 * @return True if there are more combinations to try.
	 */
	public boolean nextTweakCombo(double factor) {
		// Skip combo 0 because that means no tweaks to any component
		if (++combo >= combinations) combo = 1;
		
		// Apply tweaks to all components
		int comboCopy = combo;
		for (ITweakable tweakable : tweakables) {
			int action = comboCopy % 3;
			comboCopy /= 3;
			
			if (action == 1) {
				// Tweak up
				tweakable.tweak(factor);
			} else if (action == 2) {
				// Tweak down
				tweakable.tweak(1d / factor);
			}
		}
		
		return combo < combinations - 1;
	}
	
	public void unTweakCombo() {
		// Undo tweaks to all components
		int comboCopy = combo;
		for (ITweakable tweakable : tweakables) {
			int action = comboCopy % 3;
			comboCopy /= 3;
			
			if (action != 0) tweakable.unTweak();
			
		}
	}
}

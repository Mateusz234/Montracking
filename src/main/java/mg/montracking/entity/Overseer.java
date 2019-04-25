package mg.montracking.entity;

/**
 * Used to monitor searcher and tracker and allow communication between them
 * 
 * @author mateusz
 *
 */
public class Overseer {

	private boolean isPersonFound = false;
	private boolean isRunning = false;

	private Overseer() {
	}

	public static Overseer getInstance() {
		return OverseerHolder.INSTANCE;
	}

	private static class OverseerHolder {
		private static final Overseer INSTANCE = new Overseer();
	}

		public boolean isPersonFound() {
		return isPersonFound;
	}

	public void setPersonFound(boolean isPersonFound) {
		// add timer - if isPersonFound is false then count to 2 secs then set false
		this.isPersonFound = isPersonFound;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}

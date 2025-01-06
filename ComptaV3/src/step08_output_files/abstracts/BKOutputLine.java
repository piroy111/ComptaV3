package step08_output_files.abstracts;

class BKOutputLine<L extends BKOutputLine<L>> implements Comparable<L> {

	protected BKOutputLine(String _sLine) {
		pLine = _sLine;
	}

	/*
	 * Data
	 */
	private String pLine;
	
	@Override public int compareTo(L _sL) {
		return 0;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpLine() {
		return pLine;
	}

	

	
	
}

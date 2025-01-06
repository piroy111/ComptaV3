package step08_output_files.abstracts;

class BKOutputLineSortString extends BKOutputLine<BKOutputLineSortString> implements Comparable<BKOutputLineSortString> {

	protected BKOutputLineSortString(String _sLine, String _sKeySort) {
		super(_sLine);
		pKeySort = _sKeySort;
	}

	/*
	 * Data
	 */
	private String pKeySort;
	
	@Override public int compareTo(BKOutputLineSortString _sBKOutputLine) {
		return pKeySort.compareTo(_sBKOutputLine.pKeySort);
	}

	
}

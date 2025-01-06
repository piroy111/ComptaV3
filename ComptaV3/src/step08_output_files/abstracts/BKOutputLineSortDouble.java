package step08_output_files.abstracts;

class BKOutputLineSortDouble extends BKOutputLine<BKOutputLineSortDouble> implements Comparable<BKOutputLineSortDouble> {

	protected BKOutputLineSortDouble(String _sLine, double _sKeySort) {
		super(_sLine);
		pKeySort = _sKeySort;
	}

	/*
	 * Data
	 */
	private double pKeySort;
	
	@Override public int compareTo(BKOutputLineSortDouble _sBKOutputLine) {
		return Double.compare(pKeySort, _sBKOutputLine.pKeySort);
	}

	
	
	
}

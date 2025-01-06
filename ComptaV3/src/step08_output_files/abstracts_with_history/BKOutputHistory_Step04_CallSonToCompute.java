package step08_output_files.abstracts_with_history;

import java.util.List;

import basicmethods.BasicPrintMsg;

class BKOutputHistory_Step04_CallSonToCompute {

	
	protected BKOutputHistory_Step04_CallSonToCompute(BKOutputHistoryAbstract _sBKOutputHistoryAbstract) {
		pBKOutputHistoryAbstract = _sBKOutputHistoryAbstract;
	}
	
	/*
	 * Data
	 */
	private BKOutputHistoryAbstract pBKOutputHistoryAbstract;
	
	/**
	 * 
	 */
	public final void CallSonToCompute() {
		BasicPrintMsg.displayTitle(this, "Compute the new data to add to the file");
		for (int lDateToDo : pBKOutputHistoryAbstract.getpListDateToDo()) {
			List<String> lListLineToWriteNew = pBKOutputHistoryAbstract.getpListLineToWrite(lDateToDo);
			pBKOutputHistoryAbstract.addpListLineToWriteDifferential(lListLineToWriteNew);
			pBKOutputHistoryAbstract.addpListLineToWriteGlobal(lListLineToWriteNew);			
			BasicPrintMsg.display(this, "New data computed for date " + lDateToDo);
		}
		/*
		 * Pass to Parent
		 */
	}
	
}

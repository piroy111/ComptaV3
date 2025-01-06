package step08_output_files.abstracts_with_history;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import java.util.List;

import basicmethods.BasicPrintMsg;

class BKOutputHistory_Step05_WriteNewFile {

	protected BKOutputHistory_Step05_WriteNewFile(BKOutputHistoryAbstract _sBKOutputHistoryAbstract) {
		pBKOutputHistoryAbstract = _sBKOutputHistoryAbstract;
	}
	
	/*
	 * Data
	 */
	private BKOutputHistoryAbstract pBKOutputHistoryAbstract;
	
	/**
	 * 
	 */
	public final void WriteNewFile() {
		BasicPrintMsg.displayTitle(this, "Write new files");
		if (pBKOutputHistoryAbstract.getpListDateToDo().size() == 0) {
			BasicPrintMsg.display(this, "Nothing to do. I do nothing");
			return;
		}
		int lDateToDo = pBKOutputHistoryAbstract.getpListDateToDo().get(pBKOutputHistoryAbstract.getpListDateToDo().size() - 1);
		/*
		 * Write file for the new data only
		 */
		BasicPrintMsg.display(this, "I am writting the file with the differential data for the month ending " + lDateToDo);
		String lDir = pBKOutputHistoryAbstract.getpDirDifferential();
		String lNameFile = lDateToDo + pBKOutputHistoryAbstract.getpSuffixFile();
		String lHeader = pBKOutputHistoryAbstract.getpHeader();
		List<String> lListLineToWriteDifferential = pBKOutputHistoryAbstract.getpListLineToWriteDifferential();
		BasicFichiers.writeFile(this, lDir, lNameFile, lHeader, lListLineToWriteDifferential);
		BasicPrintMsg.display(this, "File written= " + lDir + lNameFile);
		/*
		 * Write file aggregate
		 */
		BasicPrintMsg.display(this, null);
		BasicPrintMsg.display(this, "Now I am writing the file for the aggregated data");
		String lDirGlobal = pBKOutputHistoryAbstract.getpDirGlobal();
		String lNameFileGlobal = BasicDateInt.getmToday() + pBKOutputHistoryAbstract.getpSuffixFile();
		List<String> lListLineToWriteGlobal = pBKOutputHistoryAbstract.getpListLineToWriteGlobal();
		BasicFichiers.writeFile(this, lDirGlobal, lNameFileGlobal, lHeader, lListLineToWriteGlobal);
		BasicPrintMsg.display(this, "File written= " + lDirGlobal + lNameFileGlobal);
	}
	
	
}

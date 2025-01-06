package step08_output_files.abstracts_with_history;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;

class BKOutputHistory_Step03_ReadFiles {

	protected BKOutputHistory_Step03_ReadFiles(BKOutputHistoryAbstract _sBKOutputHistoryAbstract) {
		pBKOutputHistoryAbstract = _sBKOutputHistoryAbstract;
	}
	
	/*
	 * Data
	 */
	private BKOutputHistoryAbstract pBKOutputHistoryAbstract;
	
	/**
	 * 
	 */
	public final void readFiles() {
		BasicPrintMsg.display(this, "Read files of history");
		List<String> pListLineToWriteGlobal = new ArrayList<>();
		for (BasicFile lBasicFile : pBKOutputHistoryAbstract.getpBasicDir().getmTreeMapDateToBasicFile().values()) {
			/*
			 * Skip the file if it is part of the differential because it needs to be computed
			 */
			if (pBKOutputHistoryAbstract.getpListDateToDo().contains(lBasicFile.getmDate())) {
				continue;			
			}
			/*
			 * Add file content to the list of line to write in the global file
			 */
			pListLineToWriteGlobal.addAll(lBasicFile.getmReadFile().getmContent());
			BasicPrintMsg.display(this, "File read and added; File= " + lBasicFile.getmNameFile());
		}
		/*
		 * Pass to parent
		 */
		pBKOutputHistoryAbstract.setpListLineToWriteGlobal(pListLineToWriteGlobal);
		pBKOutputHistoryAbstract.setpListLineToWriteDifferential(new ArrayList<>());
	}
	
}

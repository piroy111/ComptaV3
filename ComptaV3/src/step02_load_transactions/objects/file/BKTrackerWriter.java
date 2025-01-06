package step02_load_transactions.objects.file;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;

class BKTrackerWriter<F extends BKFile<F, M>, M extends BKFileManager<F, M>> {

	protected BKTrackerWriter(BKFileManager<F, M> _sBKFileManager) {
		pBKFileManager = _sBKFileManager;
	}
	
	/*
	 * Data
	 */
	private BKFileManager<F, M> pBKFileManager;

	/**
	 * 
	 */
	public final void write() {
		BasicPrintMsg.display(this, "Writing file tracker '" + pBKFileManager.getpNameReadFileTracker() + "'");
		String lDir = BKStaticDir.getFREEZE_TRACK_FILES_BKTRANSACTIONS();
		/*
		 * Write file content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (F lBKFile : pBKFileManager.getpMapKeyToBKFile().values()) {
			String lLine = lBKFile.getpDirLessRoot()
					+ "," + lBKFile.getpNameFile()
					+ "," + lBKFile.getpTimeStamp()
					+ "," + lBKFile.getpTimeStampDate()
					+ "," + lBKFile.getpNumberBKTransactions()
					+ "," + lBKFile.getpDateBKTransactionEarliest()
					+ "," + lBKFile.getpDateBKTransactionLatest()
					+ "," + lBKFile.getpDayFYAssociated();
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write file
		 */
		String lHeader = "Dir less root,Name file"
				+ ",Time stamp (long),Time stamp (Date)"
				+ ",Number of " + pBKFileManager.getpNameEvent() + " in the file"
				+ ",Date of the earliest " + pBKFileManager.getpNameEvent()
				+ ",Date of the latest " + pBKFileManager.getpNameEvent()
				+ ",FY associated";
		BasicFichiers.writeFile(lDir, pBKFileManager.getpNameReadFileTracker(), lHeader, lListLineToWrite);
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKFileManager<F, M> getpBKFileManager() {
		return pBKFileManager;
	}
	

}

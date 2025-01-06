package step02_load_transactions.file_tracker;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import step00_freeze_transactions.BKFrozenManager;
import step02_load_transactions.objects.file.BKFile;
import step02_load_transactions.objects.file.BKFileManager;

class BKTrackerWriterManager {

	protected BKTrackerWriterManager(BKTrackerManager _sBKTrackerManager) {
		pBKTrackerManager = _sBKTrackerManager;
	}
	
	/*
	 * Data
	 */
	private BKTrackerManager pBKTrackerManager;
	
	/**
	 * 
	 */
	public final void writeFile() {
		BasicPrintMsg.displaySuperTitle(this, "Write file tracker");
		
		writeFile(pBKTrackerManager.getpBKTransactionFileManagerFromComputation(), pBKTrackerManager.getpBKTransactionFileManagerFromTacker());
		writeFile(pBKTrackerManager.getpBKDeliveryFileManagerFromComputation(), pBKTrackerManager.getpBKDeliveryFileManagerFromTacker());
	}
	
	/**
	 * 
	 * @param _sBKFileManagerFromComputation
	 * @param _sBKFileManagerFromTracker
	 */
	private void writeFile(BKFileManager<?,?> _sBKFileManagerFromComputation, BKFileManager<?,?> _sBKFileManagerFromTracker) {
		/*
		 * Initiate file 
		 */
		String lDir = BKStaticDir.getFREEZE_TRACK_FILES_BKTRANSACTIONS();
		String lNameFile = _sBKFileManagerFromTracker.getpNameReadFileTracker();
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Case of the computation -> We update the files within the computation area
		 */
		for (BKFile<?,?> lBKFile : _sBKFileManagerFromComputation.getpMapKeyToBKFile().values()) {
			int lDate = lBKFile.getpDateFile();
			if (!BKFrozenManager.IS_EXPELL_BKTRANSACTION(lDate)) {
				lListLineToWrite.add(getpLineToWrite(lBKFile));
			}
		}
		/*
		 * Case of the tracker --> We keep the last status for outside of the computation area
		 */
		for (BKFile<?,?> lBKFile : _sBKFileManagerFromTracker.getpMapKeyToBKFile().values()) {
			int lDate = lBKFile.getpDateFile();
			if (BKFrozenManager.IS_EXPELL_BKTRANSACTION(lDate)) {
				lListLineToWrite.add(getpLineToWrite(lBKFile));
			}
		}
		/*
		 * Write file
		 */
		String lHeader = "Dir less root,Name file"
				+ ",Time stamp (long),Time stamp (Date)"
				+ ",Number of " + _sBKFileManagerFromTracker.getpNameEvent() + " in the file"
				+ ",Date of the earliest " + _sBKFileManagerFromTracker.getpNameEvent()
				+ ",Date of the latest " + _sBKFileManagerFromTracker.getpNameEvent()
				+ ",FY associated";
		BasicFichiers.writeFile(this, lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	/**
	 * 
	 * @param _sBKFile
	 * @return
	 */
	private String getpLineToWrite(BKFile<?,?> _sBKFile) {
		return _sBKFile.getpDirLessRoot()
				+ "," + _sBKFile.getpNameFile()
				+ "," + _sBKFile.getpTimeStamp()
				+ "," + _sBKFile.getpTimeStampDate()
				+ "," + _sBKFile.getpNumberBKTransactions()
				+ "," + _sBKFile.getpDateBKTransactionEarliest()
				+ "," + _sBKFile.getpDateBKTransactionLatest()
				+ "," + _sBKFile.getpDayFYAssociated();
	}
	
	
}

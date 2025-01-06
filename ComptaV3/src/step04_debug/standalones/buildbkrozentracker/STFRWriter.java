package step04_debug.standalones.buildbkrozentracker;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import step02_load_transactions.objects.file.BKFile;
import step02_load_transactions.objects.file.BKFileManager;

public class STFRWriter {

	public STFRWriter(BKFileManager<?,?> _sBKFileManager) {
		pBKFileManager = _sBKFileManager;
	}

	/*
	 * Data
	 */
	private BKFileManager<?,?> pBKFileManager;


	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Write file output for " + pBKFileManager.getpNameEvent());
		BasicPrintMsg.display(this, "Number of files in " + pBKFileManager.getpNameEvent() + "= " + pBKFileManager.getpMapKeyToBKFile().size());
		/*
		 * Loop on files
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKFile<?, ?> lBKFile : pBKFileManager.getpMapKeyToBKFile().values()) {
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
		String lNameFile = BasicDateInt.getmToday() + "_" + pBKFileManager.getpNameReadFileTracker();
		String lDir = BKStaticDir.getFREEZE_TRACK_FILES_BKTRANSACTIONS();
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}

}

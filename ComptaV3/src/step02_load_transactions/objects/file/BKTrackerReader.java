package step02_load_transactions.objects.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;

class BKTrackerReader<F extends BKFile<F, M>, M extends BKFileManager<F, M>> {

	protected BKTrackerReader(BKFileManager<F, M> _sBKFileManager) {
		pBKFileManager = _sBKFileManager;
	}
	
	/*
	 * Data
	 */
	private BKFileManager<F, M> pBKFileManager;


	/**
	 * 
	 */
	public final void read() {
		/*
		 * Initiate
		 */
		BasicPrintMsg.display(this, "Reading file tracker '" + pBKFileManager.getpNameReadFileTracker() + "'");
		String lDirTracker = BKStaticDir.getFREEZE_TRACK_FILES_BKTRANSACTIONS();
		Path lPath = Paths.get(lDirTracker + pBKFileManager.getpNameReadFileTracker());
		/*
		 * Read file tracker and create the BKFile
		 */
		if (BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
			ReadFile lReadFile = new ReadFile(lPath, comReadFile.FULL_COM);
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				String lDirLessRoot = lLineStr.get(++lIdx);
				String lNameFile = lLineStr.get(++lIdx);
				Long lTimeStamp = BasicString.getLong(lLineStr.get(++lIdx));
				int lTimeStampDate = BasicString.getInt(lLineStr.get(++lIdx));
				int lNbBKTransactions = BasicString.getInt(lLineStr.get(++lIdx));
				int lDateBKTransactionEarliest = BasicString.getInt(lLineStr.get(++lIdx));
				int lDateBKTransactionLatest = BasicString.getInt(lLineStr.get(++lIdx));
				int lDateFYAssociated = BasicString.getInt(lLineStr.get(++lIdx));
				/*
				 * Get or create
				 */
				String lKey = BKFile.getKey(lDirLessRoot, lNameFile);
				if (pBKFileManager.getpMapKeyToBKFile().containsKey(lKey)) {
					BKCom.error("The BKFile is present twice in the file Tracker"
							+ "\nFileTracker= '" + pBKFileManager.getpNameReadFileTracker() + "'"
							+ "\nBKFile present twice= '" + lDirLessRoot + lNameFile + "'");
				}
				F lBKFile = pBKFileManager.getpOrCreateBKFile(lKey);
				/*
				 * Fill
				 */
				lBKFile.setpDirLessRoot(lDirLessRoot);
				lBKFile.setpNameFile(lNameFile);
				lBKFile.setpDateFile(BasicString.getInt(BasicFichiers.getDateStr(lNameFile)));
				lBKFile.setpTimeStamp(lTimeStamp);
				lBKFile.setpTimeStampDate(lTimeStampDate);
				lBKFile.setpNumberBKTransactions(lNbBKTransactions);
				lBKFile.setpDateBKTransactionEarliest(lDateBKTransactionEarliest);
				lBKFile.setpDateBKTransactionLatest(lDateBKTransactionLatest);
				lBKFile.setpDateFYAssociated(lDateFYAssociated);
				/*
				 * Check consistency
				 */
				lBKFile.checkDatesConsistency();
			}
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKFileManager<F, M> getpBKFileManager() {
		return pBKFileManager;
	}
	
}

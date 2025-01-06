package step02_load_transactions.objects.file;

import java.nio.file.Path;

import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.BasicTime;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.BKFrozenManager;

public abstract class BKFile<F extends BKFile<F, M>, M extends BKFileManager<F, M>> {

	protected BKFile(String _sKey, BKFileManager<F, M> _sBKFileManager) {
		pKey = _sKey;
		pBKFileManager = _sBKFileManager;
	}

	/*
	 * Data
	 */
	private String pKey;
	private BKFileManager<F, M> pBKFileManager;
	private Path pPath;
	private String pNameFile;
	private int pDateFile;
	private String pDir;
	private long pTimeStamp;
	private int pTimeStampDate;
	private int pNumberBKTransactions;
	private int pDateBKTransactionEarliest;
	private int pDateBKTransactionLatest;
	private String pDirLessRoot;
	private int pDateFYAssociated;

	/**
	 * 
	 * @param _sPath
	 */
	public final void initiateData(Path _sPath) {
		pPath = _sPath;
		if (pPath != null) {
			pNameFile = BasicFichiersNioRaw.getFileNameStr(pPath);
			pDateFile = BasicString.getInt(BasicFichiers.getDateStr(pNameFile));
			pDir = BasicFichiersNioRaw.getDirStr(pPath);
			pDirLessRoot = getDirLessRoot(pPath);
		}
		updateTimeStamp();
		pDateBKTransactionEarliest = -1;
		pDateBKTransactionLatest = -1;
		pDateFYAssociated = BKFrozenManager.getpDateFYFrozenToAllocate(pDateFile);
	}

	/**
	 * 
	 * @param _sPath
	 */
	public final void updateTimeStamp() {
		pTimeStamp = BasicFichiersNioRaw.getLastModifiedTime(pPath);
		pTimeStampDate = BasicTime.getDateFromTimeStamp(pTimeStamp);
	}

	/**
	 * 
	 * @param _sPath
	 * @return
	 */
	public static String getKey(Path _sPath) {
		if (_sPath == null) {
			return "null";
		} else {
			String lNameFileStr = BasicFichiersNioRaw.getFileNameStr(_sPath);
			String lDirLessRoot = getDirLessRoot(_sPath);
			return getKey(lDirLessRoot, lNameFileStr);
		}
	}

	/**
	 * 
	 * @param _sPath
	 * @return
	 */
	private static String getDirLessRoot(Path _sPath) {
		String lDir = BasicFichiersNioRaw.getDirStr(_sPath);
		int lIdx = lDir.indexOf("Compta_bunker_v3");
		return lDir.substring(lIdx, lDir.length());
	}

	/**
	 * 
	 * @param _sDirLessRoot
	 * @param _sFileName
	 * @return
	 */
	public static String getKey(String _sDirLessRoot, String _sFileName) {
		return _sDirLessRoot + _sFileName;
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void declareNewEvent(int _sDate) {
		pNumberBKTransactions++;
		if (pDateBKTransactionEarliest == -1) {
			pDateBKTransactionEarliest = _sDate;
		} else {
			pDateBKTransactionEarliest = Math.min(pDateBKTransactionEarliest, _sDate);
		}
		pDateBKTransactionLatest = Math.max(pDateBKTransactionLatest, _sDate);
		/*
		 * Check that all BKTransactions are on the same FY
		 */
		checkDatesConsistency();
	}

	/**
	 * Check the date of the file (from the file names) and the date of all events are in the same FY as decided by BKFrozenManager
	 */
	public final void checkDatesConsistency() {
		/*
		 * Check that all BKTransactions are on the same FY
		 */
		int lDateFYEarliest = BKFrozenManager.getpDateFYFrozenToAllocate(pDateBKTransactionEarliest);
		int lDateFYLatest =  BKFrozenManager.getpDateFYFrozenToAllocate(pDateBKTransactionLatest);
		if ((pDateBKTransactionEarliest != -1 && pDateBKTransactionLatest != -1)
				&& (lDateFYEarliest != lDateFYLatest)) {
			BKCom.error("We cannot have BKTransactions on 2 different FY in the same file. You must create 2 different files"
					+ "\npDateBKTransactionEarliest= " + pDateBKTransactionEarliest + " belongs to FY " + lDateFYEarliest
					+ "\npDateBKTransactionLatest= " + pDateBKTransactionLatest + " belongs to FY " + lDateFYLatest
					+ "\n"
					+ "\nDir= " + pDirLessRoot
					+ "\nFileName= " + pNameFile);
		}
		/*
		 * Check that the date in the name of the file is consistent with the FY date
		 */
		if (BKFrozenManager.getpDateFYFrozenToAllocate(pDateFile) != pDateFYAssociated) {
			BKCom.error("the date in the name of the file is not consistent with the Date FY allocated to the file"
					+ "\nDate from the name of the file= " + pDateFile
					+ "\nDate FY allocated by BKFrozenManager= " + pDateFYAssociated
					+ "\n"
					+ "\nDir= " + pDirLessRoot
					+ "\nFileName= " + pNameFile);
		}
	}


	/**
	 * 
	 * @param _sBKFile
	 * @return
	 */
	public final String getpCompareTimeStamp(F _sBKFile) {
		try {
			String lMsgError = "";
			long lDifference = Math.abs(_sBKFile.getpTimeStamp() - pTimeStamp);
			if (lDifference > BKStaticConst.getERROR_ACCEPTABLE_TIME_STAMP_FILES()) {
				String lNewMsg = "TimeStamp " 
						+ getpBKFileManager().getpSource() + "= " 
						+ BasicTime.getTimeStampStrInUTC(pTimeStamp) 
						+ "(" + pTimeStamp + ")"
						+ " -> TimeStamp " + _sBKFile.getpBKFileManager().getpSource() + "= " 
						+ BasicTime.getTimeStampStrInUTC(_sBKFile.getpTimeStamp())
						+ "(" + _sBKFile.getpTimeStamp() + ")"
						+ "; Difference= " + BasicTime.getTimeStampStrInUTC(lDifference)
						+ "(" + lDifference + " milli)";
				lMsgError = BasicPrintMsg.addErrorMessage(lMsgError, lNewMsg, true);
			}
			return lMsgError;
		} catch (Exception lException) {
			/////////////////////////////////////////////////////////////////////////////////////////////////
			System.err.println("!!!! ERROR @ " + this.getClass().getSimpleName() + " -!-!-!");
			System.exit(-1);
			return null;
			/////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	/**
	 * 
	 * @param _sBKFile
	 * @return Error message with the things that changed. We suppose that the file has been read from computation. Hence, we know the date of first transaction, last transaction, etc.
	 */
	public final String getpCompareFull(F _sBKFile) {
		String lMsgError = "";
		if (!_sBKFile.getpNameFile().equals(pNameFile)) {
			BasicPrintMsg.errorCodeLogic();
		}
		if (_sBKFile.getpDateBKTransactionEarliest() != pDateBKTransactionEarliest) {
			lMsgError = BasicPrintMsg.addErrorMessage(lMsgError, "pDateBKTransactionEarliest " + getpBKFileManager().getpSource() + "= " + pDateBKTransactionEarliest + " -> pDateBKTransactionEarliest " + _sBKFile.getpBKFileManager().getpSource() + "= " + _sBKFile.getpDateBKTransactionEarliest(), true);
		}
		if (_sBKFile.getpDateBKTransactionLatest() != pDateBKTransactionLatest) {
			lMsgError = BasicPrintMsg.addErrorMessage(lMsgError, "pDateBKTransactionLatest " + getpBKFileManager().getpSource() + "= " + pDateBKTransactionLatest + " -> pDateBKTransactionLatest " + _sBKFile.getpBKFileManager().getpSource() + "= " + _sBKFile.getpDateBKTransactionLatest(), true);
		}
		if (_sBKFile.getpDayFYAssociated() != pDateFYAssociated) {
			lMsgError = BasicPrintMsg.addErrorMessage(lMsgError, "pDateFYAssociated " + getpBKFileManager().getpSource() + "= " + pDateFYAssociated + " -> pDateFYAssociated " + _sBKFile.getpBKFileManager().getpSource() + "= " + _sBKFile.getpDayFYAssociated(), true);
		}
		if (_sBKFile.getpNumberBKTransactions() != pNumberBKTransactions) {
			lMsgError = BasicPrintMsg.addErrorMessage(lMsgError, "pNumberBKTransactions " + getpBKFileManager().getpSource() + "= " + pNumberBKTransactions + " -> pNumberBKTransactions " + _sBKFile.getpBKFileManager().getpSource() + "= " + _sBKFile.getpNumberBKTransactions(), true);
		}
		if (_sBKFile.getpTimeStamp() != pTimeStamp) {
			lMsgError = BasicPrintMsg.addErrorMessage(lMsgError, "pTimeStamp " + getpBKFileManager().getpSource() + "= " + pTimeStamp + " -> pTimeStamp " + _sBKFile.getpBKFileManager().getpSource() + "= " + _sBKFile.getpTimeStamp(), true);
		}
		return lMsgError;
	}

	/**
	 * 
	 */
	public String toString() {
		return pNameFile;
	}

	/*
	 * Getters & Setters
	 */
	public final BKFileManager<F, M> getpBKFileManager() {
		return pBKFileManager;
	}
	public final Path getpPath() {
		return pPath;
	}
	public final String getpNameFile() {
		return pNameFile;
	}
	public final long getpTimeStamp() {
		return pTimeStamp;
	}
	public final String getpDir() {
		return pDir;
	}
	public final int getpNumberBKTransactions() {
		return pNumberBKTransactions;
	}
	public final int getpDateBKTransactionEarliest() {
		return pDateBKTransactionEarliest;
	}
	public final int getpDateBKTransactionLatest() {
		return pDateBKTransactionLatest;
	}
	public final String getpKey() {
		return pKey;
	}
	public final String getpDirLessRoot() {
		return pDirLessRoot;
	}
	public final void setpKey(String pKey) {
		this.pKey = pKey;
	}
	public final void setpPath(Path pPath) {
		this.pPath = pPath;
	}
	public final void setpNameFile(String pNameFile) {
		this.pNameFile = pNameFile;
	}
	public final void setpDir(String pDir) {
		this.pDir = pDir;
	}
	public final void setpTimeStamp(long pTimeStamp) {
		this.pTimeStamp = pTimeStamp;
	}
	public final void setpNumberBKTransactions(int pNumberBKTransactions) {
		this.pNumberBKTransactions = pNumberBKTransactions;
	}
	public final void setpDateBKTransactionEarliest(int pDateBKTransactionEarliest) {
		this.pDateBKTransactionEarliest = pDateBKTransactionEarliest;
	}
	public final void setpDateBKTransactionLatest(int pDateBKTransactionLatest) {
		this.pDateBKTransactionLatest = pDateBKTransactionLatest;
	}
	public final void setpDirLessRoot(String pDirLessRoot) {
		this.pDirLessRoot = pDirLessRoot;
	}
	public final int getpDayFYAssociated() {
		return pDateFYAssociated;
	}
	public final void setpDateFYAssociated(int pDayFYAssociated) {
		this.pDateFYAssociated = pDayFYAssociated;
	}
	public final int getpDateFile() {
		return pDateFile;
	}
	public final void setpDateFile(int pDateFile) {
		this.pDateFile = pDateFile;
	}
	public final int getpTimeStampDate() {
		return pTimeStampDate;
	}
	public final void setpTimeStampDate(int pTimeStampDate) {
		this.pTimeStampDate = pTimeStampDate;
	}
}

package step05_transactions_computed_by_compta.abstracts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.com.BKCom;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.income.BKIncome;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step10_launchme.BKLaunchMe;

public abstract class BKComptaComputorAbstract {

	public BKComptaComputorAbstract(BKComptaComputorManager _sBKComptaComputorManager) {
		pBKComptaComputorManager = _sBKComptaComputorManager;
		/*
		 * 
		 */
		pBKLaunchMe = pBKComptaComputorManager.getpBKLaunchMe();
		pBKComptaComputorManager.declareNewBKComptaComputorAbstract(this);
		/*
		 * 
		 */
		pName = this.getClass().getSimpleName();
		if (!pName.startsWith("BKComptaComputor")) {
			BKCom.errorCodeLogic();
		}
		pName = BasicString.insertSeparatorBeforeUpperCase(pName, "BKComptaComputor", "_");
	}

	/*
	 * Abstract
	 */
	public abstract void initiateGlobal();
	public abstract void initiateMonth();
	/**
	 * We generate one file per month.<br>
	 * Only the file of the current month (the month of the COMPTA) will be re-done each time the program is launched<br>
	 * @param _sListDateInOneFile
	 */
	public abstract void computeNewTransactionsDaily(int _sDate);
	/**
	 * After all the days have been called with 'computeNewTransactionsDaily(..)', we call a global method<br>
	 * This is used in case we want only one transaction per month (example: cost of storage)<br>
	 */
	public abstract void computeNewTransactionsMonthly(int _sLastDateOfMonth);
	/*
	 * Data
	 */
	protected BKComptaComputorManager pBKComptaComputorManager;
	protected BKLaunchMe pBKLaunchMe;
	protected String pName;
	protected String pDir;
	protected String pSuffixNameFile;
	protected String pNameFileOfTheMonth;
	protected int pDatePreviousFile;
	protected BKEntity pBKEntityTransfer;
	private boolean pIsPreviousFileHasBeenRemoved;
	private List<BKTransaction> pListBKTransactionNew;
	private TreeMap<Integer, List<Integer>> pTreeMapDateFileToListDateInFile;
	private Integer pDateFreeze;

	/**
	 * 
	 * @param _sBkTransaction
	 */
	public void addNewBKTransaction(BKTransaction _sBkTransaction) {
		if (_sBkTransaction != null) {
			pListBKTransactionNew.add(_sBkTransaction);
		}
	}

	/**
	 * Create a BKTransaction through BKTransactionManager + declare it to all the BKPartition + compute all the BKPartition so they are up to date
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sQuantity
	 * @param _sPrice
	 * @param _sBKAccount
	 * @param _sComment
	 * @param _sFileNameOrigin
	 * @param _sBKIncome
	 * @return
	 */
	public final BKTransaction createAndComputeNewBKTransactionByCompta(int _sDate, 
			BKAsset _sBKAsset, 
			double _sQuantity, 
			double _sPrice, 
			BKAccount _sBKAccount,
			String _sComment,
			BKIncome _sBKIncome,
			BKEntity _sBKEntity) {
		/*
		 * Create the new BKTransaction
		 */
		BKTransaction lBKTransaction = BKTransactionManager.createBKTransaction(_sDate, _sBKAsset, _sQuantity, _sPrice, _sBKAccount, _sComment, 
				_sBKIncome, _sBKEntity, null, this.getClass().getSimpleName());
		/*
		 * Case of error
		 */
		if (lBKTransaction != null) {
			pBKLaunchMe.getpBKPartitionManager().declareAndComputeNewBKTransaction(lBKTransaction);
			BasicPrintMsg.display(this, BKStaticConst.getTAB() + "New BKTransaction created= " + lBKTransaction);
		}
		/*
		 * Return
		 */
		return lBKTransaction;
	}

	
	/**
	 * 
	 */
	public final void removeFileOfTheMonth() {
		/*
		 * Create Directory
		 */
		pBKEntityTransfer = BKEntityManager.getBKEntityTransfer();
		pDir = BKStaticDir.getLOAD_TRANSACTIONS_COMPTA() 
				+ pName + "/" + BKStaticDir.getCOMPTA_SUBFOLDER_TRANSACTIONS();
		BasicFichiers.getOrCreateDirectory(pDir);
		pSuffixNameFile = "_" + pName 
				+ "_" + pBKLaunchMe.getpBKTransactionsLoader().getpBKComptaManager().getpMidFix()
				+ BKStaticNameFile.getSUFFIX_TRANSACTIONS();
		pNameFileOfTheMonth = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()
				+ pSuffixNameFile;
		/*
		 * Initiate
		 */
		pTreeMapDateFileToListDateInFile = new TreeMap<>();
		/*
		 * Return if the COMPTA is frozen
		 */
		if (getpIsFrozen(BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS())) {
			return;
		}
		/*
		 * Check the file of the date of the COMPTA + move it if it exist to the zz_old
		 */
		if (BKComOnFilesWritten.deleteFileIfExists(com_file_written.TransactionsComputedByCompta, pDir, pNameFileOfTheMonth)) {
			pIsPreviousFileHasBeenRemoved = true;
		}
		/*
		 * Check there is no file posterior of the current file
		 */
		BasicDir lBasicDir = new BasicDir(pDir, pSuffixNameFile);
		if (lBasicDir.getmLastDate() > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
			String lMsg = "There should not be files of a posterior date because the BKTransactions are computed in an accrual manner. You must remove those files."
					+ "\n   Dir= '" + pDir + "'"
					+ "\n   Date of compta= " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
			for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
				if (lBasicFile.getmDate() > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
					lMsg += "\n   '" + lBasicFile.getmNameFile() + "'";
				}
			}
			BKCom.error(lMsg);
		}
		/*
		 * Store the date of the previous file
		 */
		BasicFile lBasicFilePrevious = lBasicDir.getmBasicFile(BasicDateInt.getmPlusDay(BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS(), -1));
		if (lBasicFilePrevious != null) {
			pDatePreviousFile = lBasicFilePrevious.getmDate();
		} else {
			pDatePreviousFile = BKStaticConst.getDATE_START_COMPTA_V3();
		}
		/*
		 * Check errors
		 */
		if (lBasicDir.getmTreeMapDateToBasicFile().size() > 0 
				&& lBasicDir.getmTreeMapDateToBasicFile().lastKey() > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
			String lMsg = "There are some files which have a date posterior to the last date of compta"
					+ "\nYou must remove those files before launching the compta because we are not supposed to know the future"
					+ "\nDate to stop compta= " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
			for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
				if (lBasicFile.getmDate() > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
					lMsg += "\n" + BKStaticConst.getTAB() + lBasicFile.getmReadFile().getmDirPlusNameFile();
				}
			}
			BKCom.error(lMsg);
		}
		/*
		 * Compute the list of dates for each month which needs to be computed
		 * We split the work, in order to have one file per month. We do this in order to delete only the last file each time that we compute COMPTA
		 */
		int lDateFinal = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		int lDateStart = BasicDateInt.getmPlusDay(pDatePreviousFile, 1);
		lDateStart = Math.max(lDateStart, BasicDateInt.getmPlusDay(pDateFreeze, 1));
		int lDateStop;
		do {
			lDateStop = BasicDateInt.getmEndOfMonth(lDateStart);
			List<Integer> lListDate = new ArrayList<>();
			for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
				lListDate.add(lDate);
			}
			pTreeMapDateFileToListDateInFile.put(lDateStop, lListDate);
			lDateStart = BasicDateInt.getmPlusDay(lDateStop, 1);
		} while (lDateStop < lDateFinal);
	}

	/**
	 * 
	 */
	protected final void resetpListBKTransactionNew() {
		pListBKTransactionNew = new ArrayList<>();
	}	
	
	/**
	 * Compute and write a file for each month
	 */
	public final void checkErrorsAndWriteFile(int _sDateFile) {
		/*
		 * Initiate
		 */
		Collections.sort(pListBKTransactionNew);
		int lDateFirstDayOfMonth = BasicDateInt.getmFirstDayOfMonth(_sDateFile);
		/*
		 * Check for errors
		 */
		for (BKTransaction lBKTransaction : pListBKTransactionNew) {
			if (lBKTransaction.getpDate() <= BKStaticConst.getDATE_START_COMPTA_V3()) {
				BKCom.error("ComptaV3 cannot create a BKTransaction of a date before then end of ComptaV2"
						+ "\nDate end of ComptaV2= " + BKStaticConst.getDATE_START_COMPTA_V3()
						+ lBKTransaction.getpLineForErrorMsg());
			}
			if (lBKTransaction.getpDate() < lDateFirstDayOfMonth) {
				BKCom.error("There is one BKTransaction computed by Compta which is before the first date of the month."
						+ "\nThis is an error because we should not change the past once it is written"
						+ "\nYou can delete the previous file or look for an other error"
						+ "\n"
						+ "\nFirst date of the month= " + lDateFirstDayOfMonth
						+ "\nDir= '" + pDir + "'"
						+ "\nSuffix of file names= '" + pSuffixNameFile + "'"
						+ "\npNameFileOfTheMonth= '" + pNameFileOfTheMonth + "'"
						+ "\n"
						+ lBKTransaction.getpLineForErrorMsg());
			}
		}
		/*
		 * Build the body of the File
		 */
		int lIdxBKTransaction = 0;
		int lCount;
		lCount = 0;
		List<String> lListLineToWrite = new ArrayList<>();
		while (lIdxBKTransaction < pListBKTransactionNew.size()
				&& pListBKTransactionNew.get(lIdxBKTransaction).getpDate() <= _sDateFile) {
			BKTransaction lBKTransaction = pListBKTransactionNew.get(lIdxBKTransaction);
			String lLine = lBKTransaction.getpDate()
					+ "," + lBKTransaction.getpBKAsset().getpName()
					+ "," + lBKTransaction.getpComment()
					+ "," + lBKTransaction.getpQuantity()
					+ "," + lBKTransaction.getpPrice()
					+ "," + lBKTransaction.getpBKAccount().getpEmail()
					+ "," + lBKTransaction.getpBKIncome().getpName();
			lListLineToWrite.add(lLine);
			lIdxBKTransaction++;
			lCount++;
		}
		/*
		 * Write file
		 */
		String lHeader = BKStaticConst.getHEADER_FILE_TRANSACTIONS();
		String lNameFile = _sDateFile + pSuffixNameFile;
		BasicPrintMsg.display(this, null);
		BKComOnFilesWritten.writeFile(com_file_written.TransactionsComputedByCompta, pDir, lNameFile, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "Number of new transactions created and written in file= " + lCount
				+ " / " + pListBKTransactionNew.size());
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return pName;
	}
	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final boolean getpIsFrozen(int _sDate) {
		return _sDate <= pDateFreeze;
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final int getpDatePreviousFile() {
		return pDatePreviousFile;
	}
	public final BKComptaComputorManager getpBKComptaComputorManager() {
		return pBKComptaComputorManager;
	}
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	public final String getpName() {
		return pName;
	}
	public final String getpDir() {
		return pDir;
	}
	public final String getpSuffixNameFile() {
		return pSuffixNameFile;
	}
	public final String getpNameFileOfTheMonth() {
		return pNameFileOfTheMonth;
	}
	public final boolean getpIsPreviousFileHasBeenRemoved() {
		return pIsPreviousFileHasBeenRemoved;
	}
	public final TreeMap<Integer, List<Integer>> getpTreeMapDateFileToListDateInFile() {
		return pTreeMapDateFileToListDateInFile;
	}
	public final BKEntity getpBKEntity() {
		return pBKEntityTransfer;
	}
	public final Integer getpDateFreeze() {
		return pDateFreeze;
	}
	public final void setpDateFreeze(Integer _sPDateFreeze) {
		pDateFreeze = _sPDateFreeze;
	}





}

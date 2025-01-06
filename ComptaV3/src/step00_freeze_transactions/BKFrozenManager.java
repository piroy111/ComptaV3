package step00_freeze_transactions;

import java.util.List;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.objects.frozen_fiscal_year.BKFrozenFiscalYearManager;
import step00_freeze_transactions.step01_check_file_missing_or_for_change.BKFrozenDateChooser;
import step00_freeze_transactions.step02_compute_frozen_transactions.BKFrozenTransactionComputor;
import step00_freeze_transactions.step03_merge_frozen_transactions.BKFrozenMerger;
import step00_freeze_transactions.step04_write_file_missing.BKFrozenCapitalWriter;
import step00_freeze_transactions.step04_write_file_missing.BKFrozenWriter;
import step10_launchme.BKLaunchMe;

public class BKFrozenManager {

	public BKFrozenManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		pBKFrozenFiscalYearManager = new BKFrozenFiscalYearManager(this);
		pBKFrozenTransactionComputor = new BKFrozenTransactionComputor(this);
		pBKFrozenWriter = new BKFrozenWriter(this);
		pBKFrozenDateChooser = new BKFrozenDateChooser(this);
		pBKFrozenMerger = new BKFrozenMerger(this);
		pBKFrozenCapitalWriter = new BKFrozenCapitalWriter(this);
	}

	/*
	 * Static
	 */
	private static int DATE_FY_MIN_TO_CREATE = -1;
	private static int DATE_FY_MAX_TO_CREATE = Integer.MAX_VALUE;
	private static List<Integer> LIST_FY_FROZEN;
	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private boolean pIsAllFilesInOrder;
	private BKFrozenFiscalYearManager pBKFrozenFiscalYearManager;
	private BKFrozenTransactionComputor pBKFrozenTransactionComputor;
	private BKFrozenDateChooser pBKFrozenDateChooser;
	private BKFrozenWriter pBKFrozenWriter;
	private BKFrozenMerger pBKFrozenMerger;
	private BKFrozenCapitalWriter pBKFrozenCapitalWriter;
	private int pDateLastBKTransactionToCountInAFYFrozen;
	
	/**
	 * 
	 */
	public final void computeListDateFYFrozen() {
		pBKFrozenDateChooser.computeListDateFYFrozen();
	}
	
	
	/**
	 * Check if there is a file of BKFrozenTransaction which is missing or if there is a file which needs to be updated (if there are new BKTransaction) + choose dates of FY reports to download and to do (if needed) + download BKFrozenTransaction from file + create BKTransaction from the BKFrozenTransaction
	 */
	public final void checkForMissingFile() {
		pBKFrozenDateChooser.run();
	}

	/**
	 * Compute the BKTransactionFrozen and write them in a CSV file
	 */
	public final void computeAndWriteBKFrozenTransaction() {
		/*
		 * Get the FY to do
		 */
		int lDateFYToDo = pBKFrozenDateChooser.getpDateFYFrozenToDo();
		/*
		 * Use the BKPartition to create the BKFrozenTransaction at the date of the FYFrozen to be done
		 */
		pBKFrozenTransactionComputor.run(lDateFYToDo);
		/*
		 * Merge the BKFrozenTransactions when we have too many transfers which offset each other
		 */
//		pBKFrozenMerger.run(lDateFYToDo);
		/*
		 * Write the newly created BKFrozenTransaction
		 */
		pBKFrozenWriter.run(lDateFYToDo);
		pBKFrozenCapitalWriter.run(lDateFYToDo);
	}
	
	/**
	 * Message to write at the end of the computation
	 */
	public final String getpMsgKill() {
		return "This is not an error."
				+ "\nI had to issue a report of FY frozen transaction. This process happens once a year."
				+ "\nI needed to stop the program because of this maintenance process"
				+ "\n"
				+ "\nPlease restart the COMPTA untill all FY frozen reports are written"
				+ "n"
				+ "\nNumber of reports still to do= " + (pBKFrozenDateChooser.getpListDateFYFrozenToDo().size())
				+ "\nList of reports FY frozen still to do= " + pBKFrozenDateChooser.getpListDateFYFrozenToDo().toString();
	}

	/**
	 * @param _sDateBKTransaction : date reference to find the  FY frozen date
	 * @return the date of the FY frozen in which we should put the BKTransaction<br>
	 * It should be in the form of YYYY0331 and be more than 6 months before _sDateToday<br>
	 */
	public final static int getpDateFYFrozenToAllocate(int _sDateBKTransaction) {
		if (_sDateBKTransaction <= BKStaticConst.getFREEZE_TRANSACTION_START()) {
			return BKStaticConst.getFREEZE_TRANSACTION_START();
		}
		if (BasicDateInt.getmMonth(_sDateBKTransaction) == 3 && BasicDateInt.getmDay(_sDateBKTransaction) == 31) {
			return _sDateBKTransaction;
		} else {
			return BasicDateInt.getmNextDate(_sDateBKTransaction, 3, 31);
		}
	}

	/**
	 * @param _sDateToday : date reference to find the  FY frozen date
	 * @return the date of the last FY frozen which we should use if _sDateToday is today<br>
	 * We take margin error of 6 months before freezing in case we want to back-change some transactions<br>
	 * It should be in the form of YYYY0331 and be more than 6 months before _sDateToday<br>
	 */
	public final int getpDateFYFrozenToUse(int _sDateToday) {
		int lDateFYNext = BasicDateInt.getmNextDate(_sDateToday, 3, 31);
		int lDateFYFrozen = BasicDateInt.getmPlusYear(lDateFYNext, -1);
		while (BasicDateInt.getmNumberMonths(lDateFYFrozen, _sDateToday) < BKStaticConst.getFREEZE_START_NB_MONTHS_AFTER_LAST_FY()) {
			lDateFYFrozen = BasicDateInt.getmPlusYear(lDateFYFrozen, -1);
		}
		if (lDateFYFrozen < BKStaticConst.getFREEZE_TRANSACTION_START()) {
			return -1;
		}
		return lDateFYFrozen;
	}

	/**
	 * @param _sDate : date of the BKTransaction or date of the file of BKTransactions
	 * @return Expel the BKTransaction if its date is not in the range of fiscal year that we retained<br>
	 * the range of accepted dates are ]DATE_FY_MIN_TO_CREATE, DATE_FY_MAX_TO_CREATE]<br><br>
	 * Note that it works also with files of BKTransaction because we impose that all BKTransaction in a file belong to the same fiscal year.<br>
	 * So basically to expel a file, it is enough to expel its date (no need to expel all BKTransactions inside the file)<br>
	 */
	public static final boolean IS_EXPELL_BKTRANSACTION(int _sDate) {
		return _sDate <= DATE_FY_MIN_TO_CREATE || DATE_FY_MAX_TO_CREATE < _sDate;
	}

	/**
	 * 
	 * @return
	 */
	public final boolean getpIsNeedMakeFileFrozen() {
		return pBKFrozenDateChooser.getpDateFYFrozenToDo() > 0;
	}

	/*
	 * Getters & Setters
	 */
	public final boolean ispIsAllFilesInOrder() {
		return pIsAllFilesInOrder;
	}
	public final BKFrozenFiscalYearManager getpBKFrozenFiscalYearManager() {
		return pBKFrozenFiscalYearManager;
	}
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	public final BKFrozenTransactionComputor getpBKFrozenTransactionComputor() {
		return pBKFrozenTransactionComputor;
	}
	public final BKFrozenWriter getpBKFrozenWriter() {
		return pBKFrozenWriter;
	}
	public static final int getDATE_FY_MIN_TO_CREATE() {
		return DATE_FY_MIN_TO_CREATE;
	}
	public final void setDATE_FY_MIN_TO_CREATE(int _sDATE_FY_MIN_TO_CREATE) {
		DATE_FY_MIN_TO_CREATE = _sDATE_FY_MIN_TO_CREATE;
	}
	public static final int getDATE_FY_MAX_TO_CREATE() {
		return DATE_FY_MAX_TO_CREATE;
	}
	public final void setDATE_FY_MAX_TO_CREATE(int _sDATE_FY_MAX_TO_CREATE) {
		DATE_FY_MAX_TO_CREATE = _sDATE_FY_MAX_TO_CREATE;
	}
	public final BKFrozenMerger getpBKFrozenMerger() {
		return pBKFrozenMerger;
	}
	public final BKFrozenDateChooser getpBKFrozenDateChooser() {
		return pBKFrozenDateChooser;
	}
	public static final List<Integer> getLIST_FY_FROZEN() {
		return LIST_FY_FROZEN;
	}
	public static final void setLIST_FY_FROZEN(List<Integer> lIST_FY_FROZEN) {
		LIST_FY_FROZEN = lIST_FY_FROZEN;
	}
	public final int getpDateFYFrozenToDownload() {
		return pBKFrozenDateChooser.getpDateFYFrozenToDownload();
	}
	public final int getpDateLastBKTransactionToCountInAFYFrozen() {
		return pDateLastBKTransactionToCountInAFYFrozen;
	}


	public final void setpDateLastBKTransactionToCountInAFYFrozen(int pDateLastBKTransactionToCountInAFYFrozen) {
		this.pDateLastBKTransactionToCountInAFYFrozen = pDateLastBKTransactionToCountInAFYFrozen;
	}
	

}

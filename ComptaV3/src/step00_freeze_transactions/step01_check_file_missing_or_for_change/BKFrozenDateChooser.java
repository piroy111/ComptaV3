package step00_freeze_transactions.step01_check_file_missing_or_for_change;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.BKFrozenManager;

public class BKFrozenDateChooser {

	public BKFrozenDateChooser(BKFrozenManager _sBKFrozenManager) {
		pBKFrozenManager = _sBKFrozenManager;
		/*
		 * 
		 */
		pBKFrozenFileMissingChecker = new BKFrozenFileMissingChecker(this);
		pBKFrozenFileChangeChecker = new BKFrozenFileChangeChecker(this);
		pBKFrozenDateAlreadyDone = new BKFrozenDateAlreadyDone(this);
	}

	/*
	 * Data
	 */
	private BKFrozenManager pBKFrozenManager;
	private List<Integer> pListDateFYFrozen;
	private List<Integer> pListDateFYFrozenToDo;
	private int pDateFYFrozenToDownload;
	private int pDateFYFrozenToDo;
	private BKFrozenFileMissingChecker pBKFrozenFileMissingChecker;
	private BKFrozenFileChangeChecker pBKFrozenFileChangeChecker;
	private BKFrozenDateAlreadyDone pBKFrozenDateAlreadyDone;

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Load the dates from the CONF file which we don't want to re-do
		 */
		pBKFrozenDateAlreadyDone.loadConfFile();
		/*
		 * Compute the list of date frozen which we should have
		 */
		computeListDateFYFrozen();
		pListDateFYFrozenToDo = new ArrayList<>();
		/*
		 * Compute the dates in error if there is a missing report frozen
		 */
		pBKFrozenFileMissingChecker.checkForMissingFYFrozen();
		/*
		 * Compute the dates in error if there is a file source which has changed (BKTransaction, BKDelivery)
		 */
		pBKFrozenFileChangeChecker.check();
		/*
		 * deduce from the missing or from the changes the date to do / Choose the dates for the report to do and the previous report to use
		 */
		BasicPrintMsg.displayTitle(this, "List of dates frozen to do");
		if (pListDateFYFrozenToDo.size() > 0) {
			Collections.sort(pListDateFYFrozenToDo);
			pDateFYFrozenToDo = pListDateFYFrozenToDo.get(0);
			int lIdx = pListDateFYFrozen.indexOf(pDateFYFrozenToDo);
			if (lIdx == -1) {
				BKCom.errorCodeLogic();
			}
			if (lIdx == 0) {
				pDateFYFrozenToDownload = -1;
			} else {
				pDateFYFrozenToDownload = pListDateFYFrozen.get(lIdx - 1);
			}
		} else {
			pDateFYFrozenToDo = -1;
			pDateFYFrozenToDownload = pListDateFYFrozen.get(pListDateFYFrozen.size() - 1);
			if (pDateFYFrozenToDownload != pBKFrozenManager.getpDateFYFrozenToUse(BasicDateInt.getmToday())) {
				BKCom.errorCodeLogic();
			}
		}
		BasicPrintMsg.display(this, "List of dates frozen existing= " + pListDateFYFrozen);
		BasicPrintMsg.display(this, "pDateFYFrozenToDownload= " + pDateFYFrozenToDownload);
		BasicPrintMsg.display(this, "pDateFYFrozenToDo= " + pDateFYFrozenToDo);
		/*
		 * Set the range of dates to keep the BKTransactions + Communication
		 */
		pBKFrozenManager.setDATE_FY_MIN_TO_CREATE(pDateFYFrozenToDownload);
		if (pDateFYFrozenToDo > 0) {
			pBKFrozenManager.setDATE_FY_MAX_TO_CREATE(pDateFYFrozenToDo);
		}
		if (BKFrozenManager.getDATE_FY_MIN_TO_CREATE() > BKFrozenManager.getDATE_FY_MAX_TO_CREATE()) {
			BKCom.errorCodeLogic();
		}
		BasicPrintMsg.display(this, "BKTransactionManager.getDATE_FY_MIN_TO_CREATE()= " + BKFrozenManager.getDATE_FY_MIN_TO_CREATE());
		BasicPrintMsg.display(this, "BKTransactionManager.getDATE_FY_MAX_TO_CREATE()= " + BKFrozenManager.getDATE_FY_MAX_TO_CREATE());
		BasicPrintMsg.display(this, "BKTransaction is kept if Date belongs to ]" + BKFrozenManager.getDATE_FY_MIN_TO_CREATE() + ", " + BKFrozenManager.getDATE_FY_MAX_TO_CREATE() + "]");
		/*
		 * Check that the FY which we want to do has not been done already (and declared in the CONF file)
		 */
		pBKFrozenDateAlreadyDone.check();
	}

	/**
	 * 
	 * @param _sDateBKTransaction
	 * @return
	 */
	public final boolean getpIsKeepBKTransaction(int _sDateFYAssociated) {
		if (pDateFYFrozenToDo > 0) {
			return pDateFYFrozenToDownload < _sDateFYAssociated && _sDateFYAssociated <= pDateFYFrozenToDo;
		} else {
			return pDateFYFrozenToDownload < _sDateFYAssociated;
		}
	}	

	/**
	 * 
	 */
	public final void computeListDateFYFrozen() {
		BasicPrintMsg.displayTitle(this, "Identify the dates of FY Frozen which should exist");
		/*
		 * Compute the list of possible FYFrozen 
		 */
		int lDateStartCompta = BKStaticConst.getDATE_START_COMPTA_V3();
		pListDateFYFrozen = new ArrayList<>();
		for (int lDate = BasicDateInt.getmToday(); lDate >= lDateStartCompta; lDate = BasicDateInt.getmPlusYear(lDate, -1)) {
			int lDateFYFrozen = pBKFrozenManager.getpDateFYFrozenToUse(lDate);
			if (lDateFYFrozen != -1 && !pListDateFYFrozen.contains(lDateFYFrozen)) {
				pListDateFYFrozen.add(lDateFYFrozen);
			}
		}
		Collections.sort(pListDateFYFrozen);
		BKFrozenManager.setLIST_FY_FROZEN(pListDateFYFrozen);
		BasicPrintMsg.display(this, "List of dates where we should have a FY frozen= " + pListDateFYFrozen);
		/*
		 * Compute the date of the last BKTransaction which can count in a FYFrozen
		 */
		int lFYFrozenMax = pListDateFYFrozen.get(pListDateFYFrozen.size() - 1);
		int lDateMaxBKTransaction = lFYFrozenMax;
		while (BKFrozenManager.getpDateFYFrozenToAllocate(lDateMaxBKTransaction) <= BasicDateInt.getmToday()) {
			lDateMaxBKTransaction = BasicDateInt.getmPlusDay(lDateMaxBKTransaction, 1);
		}
		pBKFrozenManager.setpDateLastBKTransactionToCountInAFYFrozen(BasicDateInt.getmPlusDay(lDateMaxBKTransaction, -1));
		BasicPrintMsg.display(this, "The last date of a BKTransaction to be counted in is " + lDateMaxBKTransaction);
	}

	/**
	 * 
	 * @param _sDateFYFrozenToDo
	 */
	protected final void declareNewDateFYFrozenToDo(int _sDateFYFrozenToDo) {
		if (!pListDateFYFrozenToDo.contains(_sDateFYFrozenToDo)) {
			pListDateFYFrozenToDo.add(_sDateFYFrozenToDo);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKFrozenManager getpBKFrozenManager() {
		return pBKFrozenManager;
	}
	public final int getpDateFYFrozenToDownload() {
		return pDateFYFrozenToDownload;
	}
	public final int getpDateFYFrozenToDo() {
		return pDateFYFrozenToDo;
	}
	public final List<Integer> getpListDateFYFrozen() {
		return pListDateFYFrozen;
	}
	public final List<Integer> getpListDateFYFrozenToDo() {
		return pListDateFYFrozenToDo;
	}

	



}

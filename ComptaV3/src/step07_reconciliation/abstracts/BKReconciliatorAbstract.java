package step07_reconciliation.abstracts;

import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.BKFrozenManager;
import step03_partitions.abstracts.objects.BKPartitionManager;

public abstract class BKReconciliatorAbstract {

	public BKReconciliatorAbstract(BKReconciliatorManager _sBKReconciliatorManager) {
		pBKReconciliatorManager = _sBKReconciliatorManager;
		/*
		 * 
		 */
		pBKReconciliatorManager.declareNewBKReconciliatorAbstract(this);
		pBKPartitionManager = pBKReconciliatorManager.getpBKPartitionManager();
	}

	/*
	 * Abstract
	 */
	public abstract String getpDetailsOfChecksPerformed();
	public abstract void computeIsPassTest(List<Integer> _sListDateToReconcile);	
	/*
	 * Data
	 */
	protected BKReconciliatorManager pBKReconciliatorManager;
	protected BKPartitionManager pBKPartitionManager;

	/**
	 * 
	 */
	protected final void computeIsPassTest() {
		/*
		 * Set Dates on which we will run the reconciliation
		 */
		int lDateStart = BasicDateInt.getmPlusBusinessDays(BKStaticConst.getDATE_FIRST_CHANGED_TRANSACTIONS(), -1);
		lDateStart = Math.max(lDateStart, BasicDateInt.getmPlusDay(BKStaticConst.getDATE_START_COMPTA_V3(), 1));
		lDateStart = Math.max(lDateStart, BasicDateInt.getmPlusDay(BKFrozenManager.getDATE_FY_MIN_TO_CREATE(), 1));
		int lDateStop = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		List<Integer> lListDateToReconcile = BasicDateInt.getmListDays(lDateStart, lDateStop);
		/*
		 * Communication
		 */
		BasicPrintMsg.display(this, "Dates to reconcile= [" + lDateStart + " -> " + lDateStop + "]");
		/*
		 * Actual reconciliation
		 */
		computeIsPassTest(lListDateToReconcile);
		/*
		 * 
		 */
		BasicPrintMsg.display(this, "All good");
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKReconciliatorManager getpBKReconciliatorManager() {
		return pBKReconciliatorManager;
	}
	public final BKPartitionManager getpBKPartitionManager() {
		return pBKPartitionManager;
	}
	
	
	
}

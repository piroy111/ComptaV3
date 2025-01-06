package step04_debug.standalones.buildfilebalances;

import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.transaction.BKTransaction;

class BLBalanceDateManager {

	protected BLBalanceDateManager(BLManager _sBLManager) {
		pBLManager = _sBLManager;
		/*
		 * 
		 */
		pTreeMapDateToBLBalanceDate = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private BLManager pBLManager;
	private TreeMap<Integer, BLBalanceDate> pTreeMapDateToBLBalanceDate;
	
	/**
	 * Compute balances
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Compute missing balances");
		/*
		 * Create the BLBalanceDate
		 */
		for (int lDate : pBLManager.getpBLMissingDatesFinder().getpListMissingDates()) {
			getpOrCreateBLBalanceDate(lDate);
		}
		/*
		 * Feed the BKTransaction into the BLBalanceDate
		 */
		for (BKTransaction lBKTransaction : pBLManager.getpBLTransactionsLoader().getpListBKTransaction()) {
			/*
			 * Load BKTransaction
			 */
			BKAsset lBKAsset = lBKTransaction.getpBKAsset();
			double lAmount = lBKTransaction.getpQuantity();
			double lPrice = lBKTransaction.getpPrice();
			/*
			 * Feed
			 */
			for (BLBalanceDate lBLBalanceDate : pTreeMapDateToBLBalanceDate.values()) {
				if (lBKTransaction.getpDate() <= lBLBalanceDate.getpDate()) {
					lBLBalanceDate.getpOrCreateBLBalanceDateAsset(lBKAsset).addNewData(lAmount, lPrice);
				}
			}
		}
		BasicPrintMsg.display(this, "All good");
	}

	/**
	 * Classic
	 * @param _sDate
	 * @return
	 */
	public final BLBalanceDate getpOrCreateBLBalanceDate(int _sDate) {
		BLBalanceDate lBLBalanceDate = pTreeMapDateToBLBalanceDate.get(_sDate);
		if (lBLBalanceDate == null) {
			lBLBalanceDate = new BLBalanceDate(_sDate, this);
			pTreeMapDateToBLBalanceDate.put(_sDate, lBLBalanceDate);
		}
		return lBLBalanceDate;
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final BLManager getpBLManager() {
		return pBLManager;
	}
	public final TreeMap<Integer, BLBalanceDate> getpTreeMapDateToBLBalanceDate() {
		return pTreeMapDateToBLBalanceDate;
	}
}

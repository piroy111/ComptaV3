package step02_load_transactions.objects.holdings;

import java.util.HashMap;
import java.util.Map;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKHoldingManager {

	public BKHoldingManager() {
		pMapBKAssetToBKHolding = new HashMap<>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			getpOrCreateBKHolding(lBKAsset);
		}
	}

	/*
	 * Data
	 */
	private Map<BKAsset, BKHolding> pMapBKAssetToBKHolding;

	/**
	 * 
	 */
	public final void reset() {
		for (BKHolding lBKHolding : pMapBKAssetToBKHolding.values()) {
			lBKHolding.reset();
		}
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void addNewData(BKTransaction _sBKTransaction) {
		BKAsset lBKAsset = _sBKTransaction.getpBKAsset();
		BKHolding lBKHolding = getpOrCreateBKHolding(lBKAsset);
		lBKHolding.addNewData(_sBKTransaction.getpQuantityOrigin(), _sBKTransaction.getpPrice(), 
				"BKTransaction from the file: " + _sBKTransaction.getpOrigin());
	}
	
	/**
	 * Classic get or create
	 * @param _sBKAsset
	 * @return
	 */
	public final BKHolding getpOrCreateBKHolding(BKAsset _sBKAsset) {
		BKHolding lBKHolding = pMapBKAssetToBKHolding.get(_sBKAsset);
		if (lBKHolding == null) {
			lBKHolding = new BKHolding(_sBKAsset);
			pMapBKAssetToBKHolding.put(_sBKAsset, lBKHolding);
		}
		return lBKHolding;
	}
	
	/**
	 * Says if the BKHoldingManager contains the same values as _sBKHoldingManager for each BKHolding<br>
	 * @param _sBKHoldingManager
	 * @return
	 */
	public final boolean getpIsEqual(BKHoldingManager _sBKHoldingManager) {
		/*
		 * By construction the Maps contains all the BKAsset. It makes it easy to compare
		 */
		for (BKHolding lBKHolding : pMapBKAssetToBKHolding.values()) {
			BKHolding lBKHoldingOther = _sBKHoldingManager.getpMapBKAssetToBKHolding().get(lBKHolding.getpBKAsset());
			if (!lBKHolding.getpIsEquals(lBKHoldingOther)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Getters & Setters
	 */
	public final Map<BKAsset, BKHolding> getpMapBKAssetToBKHolding() {
		return pMapBKAssetToBKHolding;
	}



}

package step04_debug.standalones.buildfilebalances;

import java.util.TreeMap;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

class BLBalanceDate {

	protected BLBalanceDate(int _sDate, BLBalanceDateManager _sBLBalanceDateManager) {
		pDate = _sDate;
		pBLBalanceDateManager = _sBLBalanceDateManager;
		/*
		 * 
		 */
		pTreeMapBKAssetToBLBalanceDateAsset = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private BLBalanceDateManager pBLBalanceDateManager;
	private TreeMap<BKAsset, BLBalanceDateAsset> pTreeMapBKAssetToBLBalanceDateAsset;
	
	/**
	 * Classic
	 * @param _sBKAsset
	 * @return
	 */
	public final BLBalanceDateAsset getpOrCreateBLBalanceDateAsset(BKAsset _sBKAsset) {
		BLBalanceDateAsset lBLBalanceDateAsset = pTreeMapBKAssetToBLBalanceDateAsset.get(_sBKAsset);
		if (lBLBalanceDateAsset == null) {
			lBLBalanceDateAsset = new BLBalanceDateAsset(this, _sBKAsset);
			pTreeMapBKAssetToBLBalanceDateAsset.put(_sBKAsset, lBLBalanceDateAsset);
		}
		return lBLBalanceDateAsset;
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final BLBalanceDateManager getpBLBalanceDateManager() {
		return pBLBalanceDateManager;
	}
	public final TreeMap<BKAsset, BLBalanceDateAsset> getpTreeMapBKAssetToBLBalanceDateAsset() {
		return pTreeMapBKAssetToBLBalanceDateAsset;
	}
	
	
	
}

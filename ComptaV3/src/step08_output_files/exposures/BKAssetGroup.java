package step08_output_files.exposures;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

class BKAssetGroup {

	protected BKAssetGroup(BKAsset _sBKAsset) {
		pBKAsset = _sBKAsset;
	}
	
	/*
	 * Data
	 */
	private BKAsset pBKAsset;
	private double pHolding;
	private double pPvLUSD;
	
	/**
	 * 
	 * @param _sHolding
	 */
	public final void addHolding(double _sHolding) {
		pHolding += _sHolding;
	}

	/**
	 * 
	 * @param _sPPvLUSD
	 */
	public final void addpPvLUSD(double _sPPvLUSD) {
		pPvLUSD += _sPPvLUSD;
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpHolding() {
		return pHolding;
	}
	public final double getpPvLUSD() {
		return pPvLUSD;
	}

	
	
	
}

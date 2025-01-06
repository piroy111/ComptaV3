package step08_output_files.purchasesandsales;

import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

class Asset {

	protected Asset(BKAsset _sBKAsset, BKAssetMetal _sBKAssetMetal) {
		pBKAsset = _sBKAsset;
		pBKAssetMetal = _sBKAssetMetal;
		/*
		 * 
		 */
		pBKAssetPaperMetal = pBKAssetMetal.getpBKAssetPaperMetal();
	}
	
	/*
	 * Data
	 */
	private BKAssetMetal pBKAssetMetal;
	private BKAsset pBKAsset;
	private BKAssetPaperMetal pBKAssetPaperMetal;
	
	/**
	 * Premium of physical is given by the CONF file of historical prices
	 * @param _sDate
	 * @return
	 */
	public final double getpPremiumBuyBackAtBunker(int _sDate) {
		double lPriceUSD = pBKAssetMetal.getpPriceUSD(_sDate);
		double lPremiumForBuyBack = lPriceUSD / pBKAssetPaperMetal.getpPriceUSD(_sDate) - 1;
		return lPremiumForBuyBack;
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final BKAssetMetal getpBKAssetMetal() {
		return pBKAssetMetal;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final BKAssetPaperMetal getpBKAssetPaperMetal() {
		return pBKAssetPaperMetal;
	}
	
	
}

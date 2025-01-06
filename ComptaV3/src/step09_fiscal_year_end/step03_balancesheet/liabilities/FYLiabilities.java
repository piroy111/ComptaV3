package step09_fiscal_year_end.step03_balancesheet.liabilities;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

public class FYLiabilities extends FYLiabilitiesAbstract {

	public FYLiabilities(BKAsset _sBKAsset, FYLiabilitiesGroup _sFYLiabilitiesGroup) {
		super(_sBKAsset.getpName());
		pBKAsset = _sBKAsset;
		pFYLiabilitiesGroup = _sFYLiabilitiesGroup;
	}
	
	/*
	 * Data
	 */
	private BKAsset pBKAsset;
	private FYLiabilitiesGroup pFYLiabilitiesGroup;
	
	/*
	 * Getters & Setters
	 */
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final FYLiabilitiesGroup getpFYLiabilitiesGroup() {
		return pFYLiabilitiesGroup;
	}
	
	
}

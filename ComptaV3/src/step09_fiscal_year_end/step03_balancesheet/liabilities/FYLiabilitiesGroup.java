package step09_fiscal_year_end.step03_balancesheet.liabilities;

import java.util.TreeMap;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

public class FYLiabilitiesGroup extends FYLiabilitiesAbstract {

	protected FYLiabilitiesGroup(String _sName) {
		super(_sName);
		/*
		 * 
		 */
		pTreeMapBKAssetToFYLiabilities = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private TreeMap<BKAsset, FYLiabilities> pTreeMapBKAssetToFYLiabilities;

	/**
	 * Classic get or create
	 * @param _sBKAsset
	 * @return
	 */
	public final FYLiabilities getpOrCreateFYLiabilities(BKAsset _sBKAsset) {
		FYLiabilities lFYLiabilities = pTreeMapBKAssetToFYLiabilities.get(_sBKAsset);
		if (lFYLiabilities == null) {
			lFYLiabilities = new FYLiabilities(_sBKAsset, this);
			pTreeMapBKAssetToFYLiabilities.put(_sBKAsset, lFYLiabilities);
		}
		return lFYLiabilities;
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public static String getName(BKAsset _sBKAsset) {
		return _sBKAsset.getpAssetTypeStr();
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final TreeMap<BKAsset, FYLiabilities> getpTreeMapBKAssetToFYLiabilities() {
		return pTreeMapBKAssetToFYLiabilities;
	}
	
	
}

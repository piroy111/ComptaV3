package step09_fiscal_year_end.step01_amortization.intangible_asset;

import java.util.TreeMap;

public class FYIntangibleAsset {

	public FYIntangibleAsset(String _sName, FYIntangibleAssetManager _sFYIntangibleAssetManager) {
		pName = _sName;
		pFYIntangibleAssetManager = _sFYIntangibleAssetManager;
		/*
		 * 
		 */
		pTreeMapDateToFYIntangibleAssetDate = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private String pName;
	private FYIntangibleAssetManager pFYIntangibleAssetManager;
	private TreeMap<Integer, FYIntangibleAssetYear> pTreeMapDateToFYIntangibleAssetDate;
	
	/**
	 * Classic get or create
	 * @param _sName
	 * @return
	 */
	public final FYIntangibleAssetYear getpOrCreateFYIntangibleAssetDate(int _sDate) {
		FYIntangibleAssetYear lFYIntangibleAssetDate = pTreeMapDateToFYIntangibleAssetDate.get(_sDate);
		if (lFYIntangibleAssetDate == null) {
			lFYIntangibleAssetDate = new FYIntangibleAssetYear(_sDate, this);
			pTreeMapDateToFYIntangibleAssetDate.put(_sDate, lFYIntangibleAssetDate);
		}
		return lFYIntangibleAssetDate;
	}

	/*
	 * Getters & Setters
	 */
	public final FYIntangibleAssetManager getpFYIntangibleAssetManager() {
		return pFYIntangibleAssetManager;
	}
	public final TreeMap<Integer, FYIntangibleAssetYear> getpTreeMapDateToFYIntangibleAssetDate() {
		return pTreeMapDateToFYIntangibleAssetDate;
	}
	public final String getpName() {
		return pName;
	}
}

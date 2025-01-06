package step01_objects_from_conf_files.asset.assetabstract;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst.mode_nav;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

public abstract class BKAsset implements Comparable<BKAsset> {

	public BKAsset(String _sName, mode_nav _sModeNav, int _sIdxForSort) {
		pName = _sName;
		pModeNav = _sModeNav;
		pIdxForSort = _sIdxForSort;
		/*
		 * 
		 */
		pTreeMapDateToPrice = new TreeMap<>();
		pListBKAssetDependant = new ArrayList<>();
		pAssetTypeStr = this.getClass().getSimpleName().substring(BKAsset.class.getSimpleName().length());
		BKAssetManager.declareNewBKAsset(this);
	}
	
	/*
	 * Data
	 */
	protected BKAssetManager pBKAssetManager;
	protected String pName;
	protected TreeMap<Integer, Double> pTreeMapDateToPrice;
	protected int pIdxForSort;
	protected String pAssetTypeStr;
	protected mode_nav pModeNav;
	protected BKAsset pBKAssetUnderlying;
	private String pUnderlyingStr;
	private List<BKAsset> pListBKAssetDependant;

	/**
	 * 
	 * @param _sDateMax
	 * @param _sPrice
	 */
	protected final void addNewPrice(int _sDateMax, double _sPrice) {
		pTreeMapDateToPrice.put(_sDateMax, _sPrice);
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final double getpPriceUSD(int _sDate) {
		if (pTreeMapDateToPrice == null || pTreeMapDateToPrice.size() == 0) {
			BKCom.error("The file of historical prices does not contain any price for the asset= " + pName
					+ ". You must put at least one price"
					+ "\nFile= " + BKStaticDir.getCONF() + BKStaticNameFile.getCONF_HISTORICAL_PRICES());
		}
		if (_sDate < pTreeMapDateToPrice.firstKey()) {
			return pTreeMapDateToPrice.firstEntry().getValue();
		} else {
			return pTreeMapDateToPrice.floorEntry(_sDate).getValue();
		}
	}

	/**
	 * Compare first with the type, then with the name, so we can group the assets by types in the display
	 */
	@Override public int compareTo(BKAsset _sBKAsset) {
		int lCompareBKAssetType = pAssetTypeStr.compareTo(_sBKAsset.pAssetTypeStr);
		if (lCompareBKAssetType != 0) {
			return lCompareBKAssetType;
		} else {
			return Integer.compare(pIdxForSort, _sBKAsset.pIdxForSort);
		}
	}

	/**
	 * 
	 * @return
	 */
	public final double getpPriceUSD() {
		return getpPriceUSD(BasicDateInt.getmToday());
	}
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return pName;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getpIsPaper() {
		return pModeNav == mode_nav.PAPER;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final BKAssetManager getpBKAssetManager() {
		return pBKAssetManager;
	}
	public final TreeMap<Integer, Double> getpTreeMapDateToPrice() {
		return pTreeMapDateToPrice;
	}
	public final String getpAssetTypeStr() {
		return pAssetTypeStr;
	}
	public final mode_nav getpModeNav() {
		return pModeNav;
	}
	public final BKAsset getpBKAssetUnderlying() {
		return pBKAssetUnderlying;
	}
	protected final void setpBKAssetUnderlying(BKAsset _sPBKAssetUnderlying) {
		pBKAssetUnderlying = _sPBKAssetUnderlying;
	}
	protected final String getpUnderlyingStr() {
		return pUnderlyingStr;
	}
	protected final void setpUnderlyingStr(String _sPUnderlyingStr) {
		pUnderlyingStr = _sPUnderlyingStr;
	}
	public final int getpIdxForSort() {
		return pIdxForSort;
	}
	/**
	 * Gives the list of BKAsset of which this BKAsset is the underlying<br>
	 * Example: GOLD BAR OZ --> [GOLD LOAN OZ, GOLD PROCESSED AT REFINERY, XAU]
	 * @return
	 */
	public final List<BKAsset> getpListBKAssetDependant() {
		return pListBKAssetDependant;
	}
	public final void addpListBKAssetDependant(BKAsset _sBKAssetDependant) {
		if (!pListBKAssetDependant.contains(_sBKAssetDependant)) {
			pListBKAssetDependant.add(_sBKAssetDependant);
		}
	}

}

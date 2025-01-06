package step01_objects_from_conf_files.asset.assettype.abstraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

public abstract class BKAssetTypeAbstract implements Comparable<BKAssetTypeAbstract> {

	public BKAssetTypeAbstract(BKAssetTypeManager _sBKAssetTypeManager) {
		pBKAssetTypeManager = _sBKAssetTypeManager;
		/*
		 * 
		 */
		pIdxForSort = (++IDX_FOR_SORT);
		pName = this.getClass().getSimpleName();
		pMapNameToBKAsset = new TreeMap<>();
		pListBKAssetSorted = new ArrayList<>();
		pBKAssetTypeManager.declareNewBKAssetType(this);
	}

	/*
	 * Data
	 */
	private String pName;
	private BKAssetTypeManager pBKAssetTypeManager;
	private Map<String, BKAsset> pMapNameToBKAsset;
	private List<BKAsset> pListBKAssetSorted;
	private int pIdxForSort;
	private static int IDX_FOR_SORT = 0;

	/**
	 * 
	 * @param _sBKAsset
	 */
	public final void declareNewBKAsset(BKAsset _sBKAsset) {
		if (!pMapNameToBKAsset.containsKey(_sBKAsset.getpName())) {
			pMapNameToBKAsset.put(_sBKAsset.getpName(), _sBKAsset);
			pListBKAssetSorted.add(_sBKAsset);
			Collections.sort(pListBKAssetSorted);
		}
	}

	/**
	 * 
	 */
	@Override public int compareTo(BKAssetTypeAbstract _sBKAssetTypeAbstract) {
		return Integer.compare(pIdxForSort, _sBKAssetTypeAbstract.pIdxForSort);
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return pName;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final BKAssetTypeManager getpBKAssetTypeManager() {
		return pBKAssetTypeManager;
	}
	public final int getpIdxForSort() {
		return pIdxForSort;
	}
	public final Map<String, BKAsset> getpMapNameToBKAsset() {
		return pMapNameToBKAsset;
	}
	public final List<BKAsset> getpListBKAssetSorted() {
		return pListBKAssetSorted;
	}
	public static final int getIDX_FOR_SORT() {
		return IDX_FOR_SORT;
	}
	
	
}

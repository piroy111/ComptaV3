package step01_objects_from_conf_files.asset.assettype.abstraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import staticdata.com.BKCom;
import step01_objects_from_conf_files.asset.assettype.objects.BKTypeCurrency;
import step01_objects_from_conf_files.asset.assettype.objects.BKTypeLeasing;
import step01_objects_from_conf_files.asset.assettype.objects.BKTypeMetal;
import step01_objects_from_conf_files.asset.assettype.objects.BKTypePaper;
import step01_objects_from_conf_files.asset.assettype.objects.BKTypePaperCurrency;
import step01_objects_from_conf_files.asset.assettype.objects.BKTypePaperMetal;

public class BKAssetTypeManager {

	public BKAssetTypeManager() {
		pMapNameToBKAssetType = new TreeMap<>();
		pListBKAssetTypeAbstractSorted = new ArrayList<>();
		instantiateBKAssetType();
	}
	
	
	/*
	 * Data
	 */
	private Map<String, BKAssetTypeAbstract> pMapNameToBKAssetType;
	private List<BKAssetTypeAbstract> pListBKAssetTypeAbstractSorted;
	
	/**
	 * Instantiate manually each BKType
	 */
	private void instantiateBKAssetType() {
		new BKTypeCurrency(this);
		new BKTypeLeasing(this);
		new BKTypeMetal(this);
		new BKTypePaperCurrency(this);
		new BKTypePaperMetal(this);
		new BKTypePaper(this);
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	public final BKAssetTypeAbstract getpAndCheckBKAssetType(String _sName, String _sFileNameOrigin) {
		if (!pMapNameToBKAssetType.containsKey(_sName)) {
			BKCom.error("The " + BKAssetTypeAbstract.class.getSimpleName() + " does not exist in the program"
					+ ". Check the file, the name must not have been written correctly"
					+ "\nName= '" + _sName + "'"
					+ "\nList of name that I can accept= " + pMapNameToBKAssetType.keySet());
		}
		return pMapNameToBKAssetType.get(_sName);
	}
	
	/**
	 * 
	 * @param _sBKAssetTypeAbstract
	 */
	public final void declareNewBKAssetType(BKAssetTypeAbstract _sBKAssetTypeAbstract) {
		pMapNameToBKAssetType.put(_sBKAssetTypeAbstract.getpName(), _sBKAssetTypeAbstract);
		pListBKAssetTypeAbstractSorted.add(_sBKAssetTypeAbstract);
		Collections.sort(pListBKAssetTypeAbstractSorted);
	}

	/*
	 * Getters & Setters
	 */
	public final Map<String, BKAssetTypeAbstract> getpMapNameToBKAssetType() {
		return pMapNameToBKAssetType;
	}
	public final List<BKAssetTypeAbstract> getpListBKAssetTypeAbstractSorted() {
		return pListBKAssetTypeAbstractSorted;
	}
	
}

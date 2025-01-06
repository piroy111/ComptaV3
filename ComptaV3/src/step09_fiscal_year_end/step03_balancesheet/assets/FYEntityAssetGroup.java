package step09_fiscal_year_end.step03_balancesheet.assets;

import java.util.TreeMap;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.entity.BKEntity;

public class FYEntityAssetGroup extends FYEntityAbstract {

	public FYEntityAssetGroup(String _sName) {
		super(_sName);
		/*
		 * 
		 */
		pTreeMapKeyToFYEntityAsset = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private TreeMap<String, FYEntityAsset> pTreeMapKeyToFYEntityAsset;
	
	/**
	 * 
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @return
	 */
	public final FYEntityAsset getpOrCreateFYEntityAsset(BKEntity _sBKEntity, BKAsset _sBKAsset) {
		String lKey = FYEntityAsset.getKey(_sBKEntity, _sBKAsset);
		FYEntityAsset lFYEntityAsset = pTreeMapKeyToFYEntityAsset.get(lKey);
		if (lFYEntityAsset == null) {
			lFYEntityAsset = new FYEntityAsset(lKey, this);
			pTreeMapKeyToFYEntityAsset.put(lKey, lFYEntityAsset);
			/*
			 * 
			 */
			lFYEntityAsset.setpBKAsset(_sBKAsset);
			lFYEntityAsset.setpBKEntity(_sBKEntity);
		}
		return lFYEntityAsset;
	}	

	/**
	 * Unique key for get or create
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @return
	 */
	public static String getName(BKEntity _sBKEntity, BKAsset _sBKAsset) {
		String lNameEntity = _sBKEntity.getpName();
		if (lNameEntity.contains("_")) {
			lNameEntity = lNameEntity.split("_", -1)[0];
		}
		return lNameEntity + " - " + _sBKAsset.getpAssetTypeStr();
	}

	/*
	 * Getters & Setters
	 */
	public final TreeMap<String, FYEntityAsset> getpTreeMapKeyToFYEntityAsset() {
		return pTreeMapKeyToFYEntityAsset;
	}


	
}

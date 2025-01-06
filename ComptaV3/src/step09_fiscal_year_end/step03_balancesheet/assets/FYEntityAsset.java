package step09_fiscal_year_end.step03_balancesheet.assets;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.entity.BKEntity;

public class FYEntityAsset extends FYEntityAbstract {

	public FYEntityAsset(String _sKey, FYEntityAssetGroup _sFYEntityAssetGroup) {
		super(_sKey);
		pFYEntityAssetGroup = _sFYEntityAssetGroup;
	}

	/*
	 * Data
	 */
	private FYEntityAssetGroup pFYEntityAssetGroup;
	private BKEntity pBKEntity;
	private BKAsset pBKAsset;

	
	/**
	 * Unique key for get or create
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @return
	 */
	public static String getKey(BKEntity _sBKEntity, BKAsset _sBKAsset) {
		return _sBKEntity.getpName() + " - " + _sBKAsset.getpName();
	}

	/*
	 * Getters & Setters
	 */
	public final FYEntityAssetGroup getpFYEntityAssetGroup() {
		return pFYEntityAssetGroup;
	}
	public final BKEntity getpBKEntity() {
		return pBKEntity;
	}
	public final void setpBKEntity(BKEntity _sPBKEntity) {
		pBKEntity = _sPBKEntity;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final void setpBKAsset(BKAsset _sPBKAsset) {
		pBKAsset = _sPBKAsset;
	}


	
}

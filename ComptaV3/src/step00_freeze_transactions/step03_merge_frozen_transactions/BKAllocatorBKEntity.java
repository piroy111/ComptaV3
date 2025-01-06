package step00_freeze_transactions.step03_merge_frozen_transactions;

import java.util.HashMap;
import java.util.Map;

import basicmethods.AMNumberTools;
import staticdata.datas.BKStaticConst.type_entity;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.entity.BKEntity;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKEntity;

public class BKAllocatorBKEntity {


	protected BKAllocatorBKEntity(BKPartitionPerBKEntity _sBKPartitionPerBKEntity, int _sDateFYToDo) {
		pMapBKAssetToBKEntityToMerge = new HashMap<>();
		/*
		 * 
		 */
		initiate(_sBKPartitionPerBKEntity, _sDateFYToDo);
	}

	/*
	 * Data
	 */
	private Map<BKAsset, BKEntity> pMapBKAssetToBKEntityToMerge;


	/**
	 * 
	 * @param _sBKPartitionPerBKEntity
	 * @param _sDateFYToDo
	 */
	public final void initiate(BKPartitionPerBKEntity _sBKPartitionPerBKEntity, int _sDateFYToDo) {
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = _sBKPartitionPerBKEntity.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDateFYToDo);
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			/*
			 * Case of a normal asset -> We check the largest quantity
			 */
			if (!BKAssetManager.getpIsPhysicalMetal(lBKAsset)) {
				BKEntity lBKEntityToMerge = null;
				double lHoldingMax = 0.;
				for (String lKey : lMapKeyToBKTransactionPartitionDate.keySet()) {
					BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
					/*
					 * Load data
					 */
					BKEntity lBKEntity = _sBKPartitionPerBKEntity.getpBKEntity(lKey);
					double lHolding = Math.abs(lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset));
					/*
					 * 
					 */
					if (lBKEntity.getpTypeEntity() == type_entity.PHYSICAL && AMNumberTools.isGreaterStrict(lHolding, lHoldingMax)) {
						lHoldingMax = lHolding;
						lBKEntityToMerge = lBKEntity;
					}
				}
				pMapBKAssetToBKEntityToMerge.put(lBKAsset, lBKEntityToMerge);
			} 
		}
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @param _sBKEntity
	 * @return
	 */
	public final BKEntity getpBKEntityToMerge(BKAsset _sBKAsset, BKEntity _sBKEntity) {
		if (_sBKEntity.getpTypeEntity() == type_entity.PHYSICAL) {
			return _sBKEntity;
		} else {
			BKEntity lBKEntityToMerge = pMapBKAssetToBKEntityToMerge.get(_sBKAsset);
			if (lBKEntityToMerge == null) {
				return _sBKEntity;
			} else {
				return lBKEntityToMerge;
			}
		}
	}


}


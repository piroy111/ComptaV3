package step08_output_files.bkentity;

import java.util.Map;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_BKEntityAndBKAssetsHoldings extends BKOutputAbstract {

	public BKOutput_BKEntityAndBKAssetsHoldings(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		/*
		 * Header
		 */
		String lLineHeader1 = "" + lDate + ",,";
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			lLineHeader1 += "," + lBKAsset.getpName();
		}
		addNewLineToWrite(lLineHeader1);
		String lLineHeader2 = "BKEntity";
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			lLineHeader2 += "," + lBKAsset.getpPriceUSD(lDate);
		}
		addNewLineToWrite(lLineHeader2);
		/*
		 * Loop on BKEntity and BKAsset
		 */
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKEntity().getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(lDate);		
		if (lMapKeyToBKTransactionPartitionDate != null) {
			/*
			 * 
			 */
			for (BKEntity lBKEntity : BKEntityManager.getListBKEntity()) {
				String lKey = BKPartitionPerBKEntity.getKey(lBKEntity);
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
				if (lBKTransactionPartitionDate != null) {
					String lLine = lBKEntity.getpName();
					for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
						BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
						/*
						 * Write
						 */
						if (lBKHoldingAssetDate == null) {
							lLine += ",";
						} else {
							lLine += "," + lBKHoldingAssetDate.getpHolding();
						}
					}
					addNewLineToWrite(lLine);
				}
			}
		}
	}


}

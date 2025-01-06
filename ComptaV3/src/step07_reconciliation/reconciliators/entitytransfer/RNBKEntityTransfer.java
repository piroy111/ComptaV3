package step07_reconciliation.reconciliators.entitytransfer;

import java.util.List;
import java.util.Map;

import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNBKEntityTransfer extends BKReconciliatorAbstract {

	public RNBKEntityTransfer(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	@Override public String getpDetailsOfChecksPerformed() {
		return "Each virtual BKEntity has zero holding";
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		BKPartitionPerBKEntity lBKPartitionPerBKEntity = pBKPartitionManager.getpBKPartitionPerBKEntity();
		for (int lDate : _sListDateToReconcile) {
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lBKPartitionPerBKEntity
					.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(lDate);
			if (lMapKeyToBKTransactionPartitionDate == null) {
				continue;
			}
			/*
			 * Loop on all the existing BKEntity and select only the ones with a transfer type
			 */
			for (String lKey : lMapKeyToBKTransactionPartitionDate.keySet()) {
				BKEntity lBKEntity = lBKPartitionPerBKEntity.getpBKEntity(lKey);
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
				if (lBKEntity.equals(BKEntityManager.getBKEntityTransfer())) {
					/*
					 * Loop on the BKAsset to check their holding is at 0
					 */
					for (BKAsset lBKAsset : lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().keySet()) {
						BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
						double lValueUSD = lBKHoldingAssetDate.getpHolding() * lBKAsset.getpPriceUSD(lDate);
						/*
						 * If value in US$ > error acceptable then we issue an error
						 */
						if (Math.abs(lValueUSD) > BKStaticConst.getERROR_ACCEPTABLE_COMPTA()) { 
							String lErrorMsg = "An entity ends up with holding whereas it is a virtual entity. Only physical entities (banks, vaults, etc.) can end up with non-zero holdings."
									+ "\nBKEntity= " + lBKEntity.getpName()
									+ "\nBKAsset detained= " + lBKAsset
									+ "\nHolding of BKAsset= " + lBKHoldingAssetDate.getpHolding()
									+ "\nValue of the holding in US$= " + lValueUSD;
							BKCom.error(lErrorMsg);
						}
					}
				}
			}
		}
	}

}

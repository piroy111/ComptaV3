package step08_output_files.history_nav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_HistoryNAV extends BKOutputAbstract {

	public BKOutput_HistoryNAV(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
		super(_sBKOutputManager);
		/*
		 * 
		 */
		pBKAccount = _sBKAccount;
		addNewSuffixToNameFile(pBKAccount.getpEmail());
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;
	
	@Override public void buildFileContent() {
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(pBKAccount.getpKey());
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * We reduce the list of BKAsset which we will print to the one on which there has been a position non zero at some day
		 */		
		List<BKAsset> lListBKAssetNotNull = new ArrayList<>();
		List<BKAsset> lListBKAssetToCheck = new ArrayList<>(BKAssetManager.getpListBKAssetSorted());
		for (int lDate : lTreeMapDateToBKTransactionPartitionDate.keySet()) {
			BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
			List<BKAsset> lListBKAssetToWithdraw = new ArrayList<>();
			for (BKAsset lBKAsset : lListBKAssetToCheck) {
				BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
				if (lBKHoldingAssetDate != null && !AMNumberTools.isNaNOrZero(lBKHoldingAssetDate.getpNAV())) {
					lListBKAssetNotNull.add(lBKAsset);
					lListBKAssetToWithdraw.add(lBKAsset);
				}
			}
			lListBKAssetToCheck.removeAll(lListBKAssetToWithdraw);
			if (lListBKAssetToCheck.size() == 0) {
				break;
			}
		}
		Collections.sort(lListBKAssetNotNull);
		/*
		 * Header
		 */
		addNewHeader("Date,Total NAV,");
		for (BKAsset lBKAsset : lListBKAssetNotNull) {
			addNewHeader(lBKAsset.getpName());
		}
		/*
		 * Write file
		 */
		for (int lDate : lTreeMapDateToBKTransactionPartitionDate.keySet()) {
			if (lDate > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
				continue;
			}
			BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
			String lLine = "" + lDate;
			double lNAVTotal = 0.;
			for (BKAsset lBKAsset : lListBKAssetNotNull) {
				lNAVTotal += lBKTransactionPartitionDate.getpNAVNoNull(lBKAsset);;
			}
			lLine += "," + lNAVTotal;
			lLine += ",";
			for (BKAsset lBKAsset : lListBKAssetNotNull) {
				double lNAV = lBKTransactionPartitionDate.getpNAVNoNull(lBKAsset);
				if (Double.isNaN(lNAV) || Math.abs(lNAV) > 0.0001) {
					lLine += "," + lNAV;
				} else {
					lLine += ",0";
				}
			}
			addNewLineToWrite(lLine);
		}
	}

	
	
	
	
}

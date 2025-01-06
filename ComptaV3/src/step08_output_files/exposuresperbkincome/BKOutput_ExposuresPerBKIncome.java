package step08_output_files.exposuresperbkincome;

import java.util.TreeMap;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_ExposuresPerBKIncome extends BKOutputAbstract {

	public BKOutput_ExposuresPerBKIncome(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDateCompta = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		/*
		 * Header
		 */
		addNewHeader("BKIncome/BKAsset,");
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			addNewHeader(lBKAsset.getpName());
		}
		/*
		 * Loop on all BKIncome
		 */
		for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
			String lLine = "";
			/*
			 * Find Partition + write holdings of each BKAsset
			 */
			String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, BKAccountManager.getpBKAccountBunker());
			TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount()
					.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
			if (lTreeMapDateToBKTransactionPartitionDate != null) {
				BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDateCompta);
				if (lBKTransactionPartitionDate != null) {
					for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
						lLine += "," + lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
					}					
				}
			}
			/*
			 * Case we could not retrieve any partition --> we fill with zeros
			 */
			if (lLine.equals("")) {
				for (int lIdx = 0; lIdx < BKAssetManager.getpListBKAssetSorted().size(); lIdx++) {
					lLine += "," + "0";
				}
			}
			/*
			 * Fill the beginning of the line
			 */
			lLine = lBKIncome.getpName()
					+ "," + lBKIncome.getpBKIncomeGroup()
					+ lLine;
			addNewLineToWrite(lLine);
		}
	}

}






















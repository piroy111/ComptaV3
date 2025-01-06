package step08_output_files.bkassets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_BKAsset extends BKOutputAbstract {

	public BKOutput_BKAsset(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
		// TODO Auto-generated constructor stub
	}

	@Override public void buildFileContent() {
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(lDate);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * Write 2 first lines with the list of BKAsset and their price at the lDate
		 */
		String lLine0 = "#" + "," + lDate + ",,BKAsset";
		String lLine1 = "#BKAccount,Account currency,NAV in US$,Price US$";
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			lLine0 += "," + lBKAsset.getpName();
			lLine1 += "," + lBKAsset.getpPriceUSD(lDate);
		}
		addNewLineToWrite(lLine0);
		addNewLineToWrite(lLine1);
		/*
		 * Sort the account by the value of their holdings
		 */
		List<Account> lListAccount = new ArrayList<>();
		for (String lKey : lMapKeyToBKTransactionPartitionDate.keySet()) {
			/*
			 * Load and create Account class used to sort
			 */
			BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lKey, this.getClass().getSimpleName());
			BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
			Account lAccount = new Account(lBKAccount);
			lAccount.setpBKTransactionPartitionDate(lBKTransactionPartitionDate);
			lListAccount.add(lAccount);
			/*
			 * Compute NAV total
			 */
			for (BKHoldingAssetDate lBKHoldingAssetDate : lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().values()) {
				lAccount.addNAV(lBKHoldingAssetDate.getpNAV());
			}
		}
		Collections.sort(lListAccount);
		/*
		 * Write all the accounts sorted with their holdings at lDate
		 */
		for (Account lAccount : lListAccount) {
			BKAccount lBKAccount = lAccount.getpBKAccount();
			String lLine = lBKAccount.getpEmail()
					+ "," + lBKAccount.getpBKAssetCurrency().getpName()
					+ "," + lAccount.getpNAV()
					+ ",";
			BKTransactionPartitionDate lBKTransactionPartitionDate = lAccount.getpBKTransactionPartitionDate();
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
				/*
				 * Load holding
				 */
				double lHolding = 0.;
				BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
				if (lBKHoldingAssetDate != null) {
					lHolding = lBKHoldingAssetDate.getpHolding();
				}
				/*
				 * Write  holding
				 */
				lLine += "," + lHolding;
			}
			addNewLineToWrite(lLine);
		}
	}

	
	
	
	
}

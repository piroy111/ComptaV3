package step07_reconciliation.reconciliators.platform.compta;

import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;

public class RNPlatformLoadCompta_Old {

	public RNPlatformLoadCompta_Old(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
	}

	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Count the number and the total weight of the BKBars in the vault according to the COMPTA");
		/*
		 * Initiate
		 */
		BKPartitionPerBKIncomeAndBKAccount lBKPartitionPerBKIncomeAndAccount = pRNPlatform.getpBKPartitionManager().getpBKPartitionPerBKIncomeAndBKAccount();
		/*
		 * Loop on all the Date / Vault / AssetMetal / Bars
		 */
		for (RNPlatformDate lRNPlatformDate : pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().values()) {
			int lDate = lRNPlatformDate.getpDate();
			for (BKAccount lBKAccount : BKAccountManager.getpListBKAccount()) {
				for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
					if (lBKIncome.getpName().equals(BKStaticConst.getBKINCOME_BARS_OUTSIDE_PLATFORM())) {
						continue;
					}
					String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
					/*
					 * 
					 */
					TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = lBKPartitionPerBKIncomeAndAccount
							.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
					RNPlatformDateAccount lRNPlatformDateAccount = lRNPlatformDate.getpOrCreateRNPlatformDateAccount(lBKAccount);
					if (lTreeMapDateToBKTransactionPartitionDate != null) {
						/*
						 * 
						 */
						BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
						if (lBKTransactionPartitionDate != null) {
							/*
							 * Loop on the BKAsset
							 */
							for (BKAsset lBKAsset : lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().keySet()) {
								double lHolding = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
								if (!AMNumberTools.isNaNOrNullOrZero(lHolding)) {
									/*
									 * Get the object RN to store the value of the holding
									 */
									RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = lRNPlatformDateAccount.getpOrCreateRNPlatformDateAccountAsset(lBKAsset.getpBKAssetUnderlying());
									lRNPlatformDateAccountAsset.addpAmountCompta(lHolding);
									/*
									 * Count the number of bars
									 */
									if (lBKAsset instanceof BKAssetMetal) {
										BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
										for (BKBar lBKBar : lBKHoldingAssetDate.getpTreeMapBKBarToHolding().keySet()) {
											int lHoldingBKBar = lBKHoldingAssetDate.getpTreeMapBKBarToHolding().get(lBKBar);
											if (lHoldingBKBar == 1) {
												lRNPlatformDateAccountAsset.incpNbBKBarInCompta();
											}
										}
									}
								}								
							}
						}
					}
				}
			}
		}
	}


	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}

}

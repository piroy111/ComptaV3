package step08_output_files.client_holding;

import java.util.TreeMap;

import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_ClientHoldingForEmailBody_v2 extends BKOutputAbstract {

	public BKOutput_ClientHoldingForEmailBody_v2(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
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

	/**
	 * 
	 */
	public final String getpDirRoot() {
		return BKStaticDir.getOUTPUT_CLIENT();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getpIsAddTodayInFrontOfNameFile() {
		return false;
	}

	/**
	 * 
	 */
	@Override public void buildFileContent() {
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(pBKAccount.getpKey());
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
		if (lBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * Write the cash balance
		 */
		BKAsset lBKAssetCashBalance = pBKAccount.getpBKAssetCurrency();
		double lCashBalance = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAssetCashBalance);
		addNewLineToWrite("Cash Balance,," + lCashBalance);
		/*
		 * Write USD leasing and GOLD leasing
		 */
		BKAsset lBKAssetUSDLeasing = BKAssetManager.getpAndCheckBKAsset("USD LEASING", this);
		double lUSDLeasing = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAssetUSDLeasing);
		addNewLineToWrite("USD Leasing,," + lUSDLeasing);
		BKAsset lBKAssetGoldLeasing = BKAssetManager.getpAndCheckBKAsset("GOLD LEASING OZ", this);
		double lGoldLeasing = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAssetGoldLeasing);
		addNewLineToWrite("Gold Leasing,," + lGoldLeasing);
		/*
		 * Metals
		 */
		double lPriceCurrencyAccount = pBKAccount.getpBKAssetCurrency().getpPriceUSD();
		double lBarsValuation = 0;
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			double lOz = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAssetMetal);
			double lPriceUSD = lBKAssetMetal.getpPriceUSD();
			double lPriceCurrency = lPriceUSD / lPriceCurrencyAccount;
			double lBalance = lOz * lPriceCurrency;
			lBarsValuation += lBalance;
			addNewLineToWrite(lBKAssetMetal.getpNameMetal() + "," + lOz + "," + lBalance);
		}
		/*
		 * Write bars valuation
		 */
		addNewLineToWrite("Bars valuation,," + lBarsValuation);
		/*
		 * Write NAV
		 */
		addNewLineToWrite("NAV,," + (lCashBalance + lUSDLeasing + lGoldLeasing + lBarsValuation));
	}

}

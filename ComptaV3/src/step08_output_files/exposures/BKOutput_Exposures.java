package step08_output_files.exposures;

import java.util.TreeMap;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_Exposures extends BKOutputAbstract {

	public BKOutput_Exposures(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Header
		 */
		int lDateCompta = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		int lDatePrevious = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateCompta, -1));
		addNewHeader("BKAsset"
				+ ",Holding as of " + lDateCompta
				+ ",Holding as of " + lDateCompta + " in US$"
				+ ",Price valo as of " + lDatePrevious
				+ ",Price valo as of " + lDateCompta
				+ ",Price variation in %"
				+ ",PvL due to exposure of BKAsset (= Holding[" + lDatePrevious + "] * (PriceValo[" + lDateCompta + "] - PriceValo[" + lDatePrevious + "])");
		/*
		 * Find the object to retrieve the holdings
		 */
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate()
				.get(BKAccountManager.getpBKAccountBunker().getpKey());
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDateCompta);
		if (lBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * Group BKAsset per underlings
		 */
		TreeMap<BKAsset, BKAssetGroup> lTreeMapBKAssetToBKAssetGroup = new TreeMap<>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			/*
			 * Get or create
			 */
			BKAssetGroup lBKAssetGroup = lTreeMapBKAssetToBKAssetGroup.get(lBKAsset.getpBKAssetUnderlying());
			if (lBKAssetGroup == null) {
				lBKAssetGroup = new BKAssetGroup(lBKAsset.getpBKAssetUnderlying());
				lTreeMapBKAssetToBKAssetGroup.put(lBKAsset.getpBKAssetUnderlying(), lBKAssetGroup);
			}
			/*
			 * Add holding
			 */
			double lHolding = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
			lBKAssetGroup.addHolding(lHolding);
			/*
			 * Add P/L with the price of the BKAsset (and not the BKAssetGroup)
			 */
			double lPricePrevious = lBKAsset.getpPriceUSD(lDatePrevious);
			double lPriceCurrent = lBKAsset.getpPriceUSD(lDateCompta);
			double lPvL = (lHolding * (lPriceCurrent - lPricePrevious));
			lBKAssetGroup.addpPvLUSD(lPvL);
		}
		/*
		 * 
		 */
		for (BKAssetGroup lBKAssetGroup : lTreeMapBKAssetToBKAssetGroup.values()) {
			/*
			 * Load
			 */
			BKAsset lBKAsset = lBKAssetGroup.getpBKAsset();
			double lHolding = lBKAssetGroup.getpHolding();
			double lPricePrevious = lBKAsset.getpPriceUSD(lDatePrevious);
			double lPriceCurrent = lBKAsset.getpPriceUSD(lDateCompta);
			/*
			 * Write line
			 */
			addNewLineToWrite(lBKAsset.getpName()
					+ "," + lHolding
					+ "," + (lHolding * lPriceCurrent)
					+ "," + lPricePrevious
					+ "," + lPriceCurrent
					+ "," + (lPriceCurrent / lPricePrevious - 1)
					+ "," + lBKAssetGroup.getpPvLUSD(),
					-Math.abs((lHolding * lPriceCurrent)));
		}
	}

}

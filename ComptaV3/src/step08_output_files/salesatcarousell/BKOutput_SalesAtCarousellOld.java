package step08_output_files.salesatcarousell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_SalesAtCarousellOld extends BKOutputAbstract {

	public BKOutput_SalesAtCarousellOld(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		List<Integer>  lListDateEndOfMonth = getpListDateEndOfMonth();
		/*
		 * Initiate storage of history since beginning
		 */		
		Map<BKAssetMetal, Double> lMapBKAssetMetalToTurnoverSinceBeginning = new HashMap<>();
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			lMapBKAssetMetalToTurnoverSinceBeginning.put(lBKAssetMetal, 0.);
		}
		/*
		 * Header
		 */
		addNewHeader("Date");
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			String lHeader = lBKAssetMetal.getpName() + " total sales since beginning"
					+ "," + lBKAssetMetal.getpName() + " sales of the month"
					+ "," + lBKAssetMetal.getpName() + " premium buy back at Bunker";
			addNewHeader(lHeader);
		}
		/*
		 * Initiate partition
		 */
		String lKeyBunker = BKPartitionPerBKAccount.getKey(BKAccountManager.getpBKAccountBunker());
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBunker);
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * Build file content
		 */
		for (int lIdxDate = 0; lIdxDate < lListDateEndOfMonth.size(); lIdxDate++) {
			/*
			 * Load date and date previous month
			 */
			int lDate = lListDateEndOfMonth.get(lIdxDate);
			int lDatePrevious = -1;
			if (lIdxDate > 0) {
				lDatePrevious = lListDateEndOfMonth.get(lIdxDate - 1);
			}
			/*
			 * Loop on metals (columns are per metal)
			 */
			String lLine = "" + lDate;
			for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
				BKAssetPaperMetal lBKAssetPaperMetal = lBKAssetMetal.getpBKAssetPaperMetal();
				/*
				 * Premium of physical is given by the CONF file of historical prices
				 */
				double lPriceUSD = lBKAssetMetal.getpPriceUSD(lDate);
				double lPremiumFroBuyBack = lPriceUSD / lBKAssetPaperMetal.getpPriceUSD(lDate) - 1;
				/*
				 * compute purchases and sales over all accounts
				 */
				double lSalesMonth = 0.;
				/*
				 * Check the bars which we sold this month at CAROUSELL == the bars delivered this month by Bunker
				 */
				for (BKBar lBKBar : lBKAssetMetal.getpMapIDToBKBar().values()) {
					/*
					 * The bar must be delivered this month
					 */
					if (!lBKBar.getpIsDelivered(lDate)) {
						continue;
					}
					/*
					 * The bar must belong to Bunker at the moment of the delivery
					 */
					if (!BKAccountManager.getpBKAccountBunker().equals(lBKBar.getpBKAccountOwnerAtTimeOfDelivery(lDate))) {
						continue;
					}
					/*
					 * The bar must not have been delivered the previous month
					 */
					if (lDatePrevious > 0 && lBKBar.getpIsDelivered(lDatePrevious)) {
						continue;
					}
					/*
					 * Increase the amount of sale in OZ
					 */
					lSalesMonth += lBKBar.getpWeightOz();					
				}
				double lTurnoverMonth = lSalesMonth * lPriceUSD;
				/*
				 * Store turnover since beginning
				 */
				double lTurnoverSinceBeginning = lMapBKAssetMetalToTurnoverSinceBeginning.get(lBKAssetMetal) + lTurnoverMonth;
				lMapBKAssetMetalToTurnoverSinceBeginning.put(lBKAssetMetal, lTurnoverSinceBeginning);
				/*
				 * 
				 */
				lLine += "," + lTurnoverSinceBeginning
						+ "," + lTurnoverMonth
						+ "," + lPremiumFroBuyBack;
			}
			/*
			 * Add all the moves per account
			 */
			addNewLineToWrite(lLine);
		}
	}















}

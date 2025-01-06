package step08_output_files.salesatcarousell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_SalesAtCarousell extends BKOutputAbstract {

	public BKOutput_SalesAtCarousell(BKOutputManager _sBKOutputManager) {
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
		BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_CAROUSELL_BARS(), this.getClass().getSimpleName());
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, BKAccountManager.getpBKAccountBunker());
		/*
		 * Build file content
		 */
		int lDatePrevious = -1;
		for (int lIdxDate = 0; lIdxDate < lListDateEndOfMonth.size(); lIdxDate++) {
			int lDate = lListDateEndOfMonth.get(lIdxDate);
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
				 * compute purchases and sales
				 */
				double lAmountOzSinceBeginning = -pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpHoldingBKAssetNotNull(lKey, lDate, lBKAssetMetal);
				double lAmountOzPrevious = 0;
				if (lDatePrevious > 0) {
					lAmountOzPrevious = -pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpHoldingBKAssetNotNull(lKey, lDatePrevious, lBKAssetMetal);
				}
				/*
				 * Compute turnover of the month
				 */
				double lTurnoverMonth = (lAmountOzSinceBeginning - lAmountOzPrevious) * lPriceUSD;
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
			lDatePrevious = lDate;
		}
	}



}

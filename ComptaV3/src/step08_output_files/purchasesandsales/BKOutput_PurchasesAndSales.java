package step08_output_files.purchasesandsales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.BasicStringSortDouble;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_PurchasesAndSales extends BKOutputAbstract {

	public BKOutput_PurchasesAndSales(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		List<Integer>  lListDateEndOfMonth = getpListDateEndOfMonth();
		BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_BARS(), this.getClass().getSimpleName());
		/*
		 * Initiate the list of assets on which we will count the sales
		 */
		List<Asset> lListAsset = new ArrayList<>();
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			lListAsset.add(new Asset(lBKAssetMetal, lBKAssetMetal));
			lListAsset.add(new Asset(lBKAssetMetal.getpBKAssetLoan(), lBKAssetMetal));
		}
		/*
		 * Initiate storage of history since beginning
		 */		
		Map<BKAsset, Double> lMapBKAssetToTurnoverSinceBeginning = new HashMap<>();
		for (Asset lAsset : lListAsset) {
			lMapBKAssetToTurnoverSinceBeginning.put(lAsset.getpBKAsset(), 0.);
		}
		/*
		 * Header
		 */
		addNewHeader("Date");
		for (Asset lAsset : lListAsset) {
			BKAsset lBKAsset = lAsset.getpBKAsset();
			String lHeader = lBKAsset.getpName() + " turnover since beginning"
					+ "," + lBKAsset.getpName() + " purchases of the month"
					+ "," + lBKAsset.getpName() + " sales of the month"
					+ "," + lBKAsset.getpName() + " premium buy back at Bunker";
			addNewHeader(lHeader);
		}
		addNewHeader("List of all purchases and sales per account sorted from the largest to the smallest");
		/*
		 * Build file content
		 */
		for (int lIdxDate = 0; lIdxDate < lListDateEndOfMonth.size(); lIdxDate++) {
			/*
			 * Load date and date previous month
			 */
			int lDate = lListDateEndOfMonth.get(lIdxDate);
			Integer lDatePrevious = null;
			if (lIdxDate > 0) {
				lDatePrevious = lListDateEndOfMonth.get(lIdxDate - 1);
			}
			/*
			 * Loop on metals (columns are per metal)
			 */
			String lLine = "" + lDate;
			List<BasicStringSortDouble> lListBasicStringSortDouble = new ArrayList<>();
			for (Asset lAsset : lListAsset) {
				BKAsset lBKAsset = lAsset.getpBKAsset();
				double lPriceUSD = lBKAsset.getpPriceUSD(lDate);
				/*
				 * compute purchases and sales over all accounts
				 */
				double lPurchaseMonth = 0.;
				double lSaleMonth = 0.;
				for (BKAccount lBKAccount : BKAccountManager.getpListBKAccountExceptBunker()) {
					/*
					 * Initiate partition
					 */
					
					String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
					TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
							.getpBKPartitionPerBKIncomeAndBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
					if (lTreeMapDateToBKTransactionPartitionDate == null) {
						continue;
					}
					/*
					 * Current
					 */
					BKTransactionPartitionDate lBKTransactionPartitionDateCurrent = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
					double lHoldingBKAssetMetalCurrent = 0.;
					if (lBKTransactionPartitionDateCurrent != null) {
						lHoldingBKAssetMetalCurrent = lBKTransactionPartitionDateCurrent.getpHoldingNoNaNNoNull(lBKAsset);
					}
					/*
					 * Previous
					 */
					double lHoldingBKAssetMetalPrevious = 0.;
					if (lDatePrevious != null) {
						BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lTreeMapDateToBKTransactionPartitionDate.get(lDatePrevious);
						if (lBKTransactionPartitionDatePrevious != null) {
							lHoldingBKAssetMetalPrevious = lBKTransactionPartitionDatePrevious.getpHoldingNoNaNNoNull(lBKAsset);
						}
					}
					/*
					 * Add turnover to sales or purchases of the month
					 */
					double lTurnoverFromAccount = (lHoldingBKAssetMetalCurrent - lHoldingBKAssetMetalPrevious) * lPriceUSD;
					if (lTurnoverFromAccount > 0) {
						lPurchaseMonth += lTurnoverFromAccount;
					} else if (lTurnoverFromAccount < 0){
						lSaleMonth += lTurnoverFromAccount;
					}
					/*
					 * Store the line
					 */
					double lKeyDouble = Math.abs(lTurnoverFromAccount);
					if (lKeyDouble > 1) {
						String lString = lBKAccount.getpEmail()
								+ "(" + lBKAccount.getpSource() + ")"
								+ "= " + BasicPrintMsg.afficheIntegerWithComma(lTurnoverFromAccount).replaceAll(",", " ") + " US$"
								+ " of " + lAsset.getpBKAssetMetal().getpNameMetal();
						BasicStringSortDouble lBasicStringSortDouble = new BasicStringSortDouble(lString, lKeyDouble);
						lListBasicStringSortDouble.add(lBasicStringSortDouble);
					}
				}
				/*
				 * Store turnover since beginning
				 */
				double lTurnoverSinceBeginning = lMapBKAssetToTurnoverSinceBeginning.get(lBKAsset) + lPurchaseMonth + lSaleMonth;
				lMapBKAssetToTurnoverSinceBeginning.put(lBKAsset, lTurnoverSinceBeginning);
				/*
				 * 
				 */
				lLine += "," + lTurnoverSinceBeginning
						+ "," + lPurchaseMonth
						+ "," + lSaleMonth
						+ "," + lAsset.getpPremiumBuyBackAtBunker(lDate);
			}
			/*
			 * Add all the moves per account
			 */
			Collections.sort(lListBasicStringSortDouble);
			Collections.reverse(lListBasicStringSortDouble);
			lLine += "," + BasicString.toString(lListBasicStringSortDouble);
			addNewLineToWrite(lLine);
		}
	}















}

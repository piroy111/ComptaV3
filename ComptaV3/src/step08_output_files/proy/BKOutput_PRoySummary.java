package step08_output_files.proy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPaper;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_PRoySummary extends BKOutputAbstract{

	public BKOutput_PRoySummary(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDateCompta = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		BKAccount lBKAccount = BKAccountManager.getpBKAccountPRoy();
		BKAccount lBKAccountBunker = BKAccountManager.getpBKAccountBunker();
		List<Integer> lListDateToDo = new ArrayList<>();
		lListDateToDo.add(lDateCompta);
		lListDateToDo.add(BasicDateInt.getmPlusMonth(lDateCompta, -1));
		for (int lDate : lListDateToDo) {
			/*
			 * Load the partition for the date per account
			 */
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pBKPartitionManager
					.getpBKPartitionPerBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(lDate);
			if (lMapKeyToBKTransactionPartitionDate == null) {
				continue;
			}
			BKTransactionPartitionDate lBKTransactionPartitionDatePerAccount = lMapKeyToBKTransactionPartitionDate.get(BKPartitionPerBKAccount.getKey(lBKAccount));
			if (lBKTransactionPartitionDatePerAccount == null) {
				continue;
			}
			/*
			 * Exposition to metals (without leasing but with all underlying) and USD
			 */
			double lAmountGold = getpAmount(lBKTransactionPartitionDatePerAccount, lDate, BKAssetManager.getpAndCheckBKAsset("GOLD BAR OZ", this));
			double lAmountSilver = getpAmount(lBKTransactionPartitionDatePerAccount, lDate, BKAssetManager.getpAndCheckBKAsset("SILVER BAR OZ", this));
			double lAmountPlatinum = getpAmount(lBKTransactionPartitionDatePerAccount, lDate, BKAssetManager.getpAndCheckBKAsset("PLATINUM BAR OZ", this));
			addNewLineToWrite(lDate + ",Gold Bars," + lAmountGold);
			addNewLineToWrite(lDate + ",Silver Bars," + lAmountSilver);
			addNewLineToWrite(lDate + ",Platinum Bars," + lAmountPlatinum);
			/*
			 * Compute the amount of USD as the holding in USD plus all the NAVs of the futures (XAG, XAU, XBCO, etc.)
			 */
			BKAsset lBKAssetUSD = BKAssetManager.getpAndCheckBKAsset("USD", this);
			double lAmountUSD = lBKTransactionPartitionDatePerAccount.getpHoldingNoNaNNoNull(lBKAssetUSD);
			for (BKAssetPaper lBKAssetPaper : BKAssetManager.getpListBKAssetPaperSorted()) {
				BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDatePerAccount.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetPaper);
				if (lBKHoldingAssetDate != null) {
					lAmountUSD += lBKHoldingAssetDate.getpNAV();
				}
			}
			addNewLineToWrite(lDate + ",USD," + lAmountUSD);
			/*
			 * Get the exposition to gold in CONDOR
			 */
			BKAsset lBKAssetGoldLeasing = BKAssetManager.getpAndCheckBKAsset("GOLD LEASING OZ", this);
			double lAmountGoldCondor = lBKTransactionPartitionDatePerAccount.getpHoldingNoNaNNoNull(lBKAssetGoldLeasing);
			addNewLineToWrite(lDate + "," + lBKAssetGoldLeasing.getpName() + "," + lAmountGoldCondor);
			/*
			 * Compute the amount of USD invested in CONDOR + Write CONDOR USD
			 */
			BKAsset lBKAssetUSDLeasing = BKAssetManager.getpAndCheckBKAsset("USD LEASING", this);
			double lAmountUSDAtCondor = lBKTransactionPartitionDatePerAccount.getpHoldingNoNaNNoNull(lBKAssetUSDLeasing);
			addNewLineToWrite(lDate + "," + lBKAssetUSDLeasing.getpName() + "," + lAmountUSDAtCondor);
			/*
			 * NAV of Bunker
			 */
			double lNAVBunker = pBKPartitionManager.getpBKPartitionPerBKAccount().getpNAV(lBKAccountBunker, lDate);
			BKIncome lBKIncomeCapital = BKIncomeManager
					.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_CAPITAL(), this);
			String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncomeCapital, lBKAccountBunker);
			double lCapital = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpNAV(lKey, lDate);
			addNewLineToWrite(lDate + ",NAV," + lNAVBunker);
			addNewLineToWrite(lDate + ",Capital," + (-lCapital));
			addNewLineToWrite("#");
		}
	}

	/**
	 * 
	 * @param _sBKTransactionPartitionDate
	 * @param _sDate
	 * @param _sBKAssetMain
	 * @return
	 */
	private double getpAmount(BKTransactionPartitionDate _sBKTransactionPartitionDate, int _sDate, BKAsset _sBKAssetMain) {
		double lHolding = 0.;
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			if (lBKAsset.getpBKAssetUnderlying().equals(_sBKAssetMain)
					&& !lBKAsset.getpIsPaper()) {
				boolean lIsLeasing = lBKAsset.getpName().contains("LEASING");
				if (lIsLeasing) {
					continue;
				}
				lHolding += _sBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
			}
		}
		return lHolding;
	}


}

package step08_output_files.pvl_summary_monthly;

import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_PvlSummaryMonthly extends BKOutputAbstract{

	public BKOutput_PvlSummaryMonthly(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		BKPartitionPerBKIncomeAndBKAccount lBKPartitionPerBKIncomeAndBKAccount= pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount();
		TreeMap<Integer, Map<String, BKTransactionPartitionDate>> lTreeMapDateToMapKeyToBKTransactionPartitionDate = lBKPartitionPerBKIncomeAndBKAccount
				.getpTreeMapDateToMapKeyToBKTransactionPartitionDate();
		BKAccount lBKAccount = BKAccountManager.getpBKAccountBunker();
		int lDateEnd = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		int lDatePrevious = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateEnd, -1));
		/*
		 * Header
		 */
		String lWordPvL = "P/L from " + BasicDateInt.getmPlusDay(lDatePrevious, 1) + " to " + lDateEnd;
		addNewHeader("BKIncome,BKIncomeGroup");
		addNewHeader(lWordPvL);
		/*
		 * 
		 */
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateEnd = lTreeMapDateToMapKeyToBKTransactionPartitionDate.get(lDateEnd);
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDatePrevious = lTreeMapDateToMapKeyToBKTransactionPartitionDate.get(lDatePrevious);
		double lPvLDueToExposure = 0.;
		for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
			/*
			 * Load
			 */
			String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
			double lNAVEnd = 0.;
			double lNAVVeryEnd = 0.;
			BKTransactionPartitionDate lBKTransactionPartitionDateEnd = lMapKeyToBKTransactionPartitionDateEnd.get(lKey);
			if (lBKTransactionPartitionDateEnd != null) {
				lNAVEnd = lBKTransactionPartitionDateEnd.getpNAVWithPricesYesterday();
				lNAVVeryEnd = lBKTransactionPartitionDateEnd.getpNAV();
			}
			double lNAVStart = 0.;
			BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lMapKeyToBKTransactionPartitionDatePrevious.get(lKey);
			if (lBKTransactionPartitionDatePrevious != null) {
				lNAVStart = lBKTransactionPartitionDatePrevious.getpNAV();
			}
			/*
			 * Compute
			 */
			double lPvL = lNAVEnd - lNAVStart;
			lPvLDueToExposure += lNAVVeryEnd - lNAVEnd;
			/*
			 * 
			 */
			addNewLineToWrite(lBKIncome.getpName()
					+ "," + lBKIncome.getpBKIncomeGroup()
					+ "," + lPvL, -Math.abs(lPvL));
		}
		addNewLineToWrite("P/L due to exposures"
				+ ","
				+ "," + lPvLDueToExposure, -Math.abs(lPvLDueToExposure));
	}














}

package step08_output_files.pvl_summary_yearly;

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

public class BKOutput_PvlSummaryYearly extends BKOutputAbstract{

	public BKOutput_PvlSummaryYearly(BKOutputManager _sBKOutputManager) {
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
		int lDatePrevious;
		if (BasicDateInt.getmMonth(lDateEnd) > 3) {	// FY ends in March
			lDatePrevious = BasicDateInt.getmDateInt(BasicDateInt.getmYear(lDateEnd), 3, 31);
		} else {
			lDatePrevious = BasicDateInt.getmDateInt(BasicDateInt.getmYear(lDateEnd) - 1, 3, 31);
		}
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
		for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
			/*
			 * Load
			 */
			String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
			double lNAVVeryEnd = 0.;
			BKTransactionPartitionDate lBKTransactionPartitionDateEnd = lMapKeyToBKTransactionPartitionDateEnd.get(lKey);
			if (lBKTransactionPartitionDateEnd != null) {
				lNAVVeryEnd = lBKTransactionPartitionDateEnd.getpNAV();
			}
			double lNAVStart = 0.;
			if (lMapKeyToBKTransactionPartitionDatePrevious != null) {
				BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lMapKeyToBKTransactionPartitionDatePrevious.get(lKey);
				if (lBKTransactionPartitionDatePrevious != null) {
					lNAVStart = lBKTransactionPartitionDatePrevious.getpNAV();
				}
			}
			/*
			 * Compute
			 */
			double lPvL = lNAVVeryEnd - lNAVStart;
			/*
			 * Write in file
			 */
			addNewLineToWrite(lBKIncome.getpName()
					+ "," + lBKIncome.getpBKIncomeGroup()
					+ "," + lPvL, -Math.abs(lPvL));
		}
	}














}

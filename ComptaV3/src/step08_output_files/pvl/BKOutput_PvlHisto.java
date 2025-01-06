package step08_output_files.pvl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_PvlHisto extends BKOutputAbstract{

	public BKOutput_PvlHisto(BKOutputManager _sBKOutputManager) {
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
		/*
		 * Header
		 */
		addNewHeader("Date");
		for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
			addNewHeader(lBKIncome.getpName());
		}
		/*
		 * Build the list of days end of the month when we will print a double line, one with the prices of yesterday, one with the prices of today<br>
		 * We do this in order to understand the impact of the FOREX exposure<br>
		 */
		List<Integer> lListDateEndOfMonth = new ArrayList<>();
		int lYearEOMonth = 0;
		int lMonthEOMonth = 0;
		for (int lDate : lTreeMapDateToMapKeyToBKTransactionPartitionDate.descendingKeySet()) {
			int lYear = BasicDateInt.getmYear(lDate);
			int lMonth = BasicDateInt.getmMonth(lDate);
			if (lYear != lYearEOMonth || lMonth != lMonthEOMonth) {
				lListDateEndOfMonth.add(lDate);
				lYearEOMonth = lYear;
				lMonthEOMonth = lMonth;
			}
		}
		/*
		 * 
		 */
		for (int lDate : lTreeMapDateToMapKeyToBKTransactionPartitionDate.keySet()) {
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lTreeMapDateToMapKeyToBKTransactionPartitionDate.get(lDate);
			/*
			 * Line with the NAV computed with the FOREX of the previous day
			 */
			if (lListDateEndOfMonth.contains(lDate)) {
				String lLineNAVForexYstd = lDate + "";
				for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
					String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
					double lNAV = 0.;
					BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
					if (lBKTransactionPartitionDate != null) {
						lNAV = lBKTransactionPartitionDate.getpNAVWithPricesYesterday();
					}
					lLineNAVForexYstd += "," + lNAV;
				}
				addNewLineToWrite(lLineNAVForexYstd);
			}
			/*
			 * Line normal with the NAV computed with the FOREX of the same day
			 */
			String lLineNAV = lDate + "";
			for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
				String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
				double lNAV = 0.;
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
				if (lBKTransactionPartitionDate != null) {
					lNAV = lBKTransactionPartitionDate.getpNAV();
				}
				lLineNAV += "," + lNAV;
			}
			addNewLineToWrite(lLineNAV);
		}
	}









}

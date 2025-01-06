package step08_output_files.proy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_PRoyPvlHisto extends BKOutputAbstract{

	public BKOutput_PRoyPvlHisto(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		BKPartitionPerBKIncomeAndBKAccount lBKPartitionPerBKIncomeAndBKAccount= pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount();
		TreeMap<Integer, Map<String, BKTransactionPartitionDate>> lTreeMapDateToMapKeyToBKTransactionPartitionDate = lBKPartitionPerBKIncomeAndBKAccount
				.getpTreeMapDateToMapKeyToBKTransactionPartitionDate();
		BKAccount lBKAccount = BKAccountManager.getpBKAccountPRoy();
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
		 * Check the columns non empty
		 */
		Set<Integer> lSetIdxToKeep = new HashSet<>();
		List<List<String>> lListLineToWrite = new ArrayList<>();
		lSetIdxToKeep.add(0);
		/*
		 * 
		 */
		for (int lDate : lTreeMapDateToMapKeyToBKTransactionPartitionDate.keySet()) {
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lTreeMapDateToMapKeyToBKTransactionPartitionDate.get(lDate);
			/*
			 * Line with the NAV computed with the FOREX of the previous day
			 */
			if (lListDateEndOfMonth.contains(lDate)) {
				List<String> lLineNAVForexYstd = new ArrayList<>();
				lLineNAVForexYstd.add("" + lDate);
				int lIdx = 0;
				for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
					String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
					lIdx++;
					double lNAV = 0.;
					BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
					if (lBKTransactionPartitionDate != null) {
						lNAV = lBKTransactionPartitionDate.getpNAVWithPricesYesterday();
					}
					lLineNAVForexYstd.add("" + lNAV);
					if (!AMNumberTools.isNaNOrNullOrZero(lNAV)) {
						lSetIdxToKeep.add(lIdx);
					}
				}
				lListLineToWrite.add(lLineNAVForexYstd);
			}
			/*
			 * Line normal with the NAV computed with the FOREX of the same day
			 */
			List<String> lLineNAV = new ArrayList<>();
			lLineNAV.add("" + lDate);
			int lIdx = 0;
			for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
				String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
				lIdx++;
				double lNAV = 0.;
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
				if (lBKTransactionPartitionDate != null) {
					lNAV = lBKTransactionPartitionDate.getpNAV();
				}
				lLineNAV.add("" + lNAV);
				if (!AMNumberTools.isNaNOrNullOrZero(lNAV)) {
					lSetIdxToKeep.add(lIdx);
				}
			}
			lListLineToWrite.add(lLineNAV);
		}
		/*
		 * Skip the columns which have only zeros
		 */
		for (List<String> lLineList : lListLineToWrite) {
			String lLine = lLineList.get(0);
			for (int lIdx = 1; lIdx < lLineList.size(); lIdx++) {
				if (lSetIdxToKeep.contains(lIdx)) {
					lLine += "," + lLineList.get(lIdx);
				}
			}
			addNewLineToWrite(lLine);
		}
		/*
		 * Write header
		 */
		addNewHeader("Date");
		int lIdx = 0;
		for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
			if (lSetIdxToKeep.contains(++lIdx)) {
				addNewHeader(lBKIncome.getpName());
			}
		}
	}














}

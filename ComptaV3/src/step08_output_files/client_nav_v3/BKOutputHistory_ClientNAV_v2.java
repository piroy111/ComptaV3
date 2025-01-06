package step08_output_files.client_nav_v3;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicFile;
import basicmethods.BasicString;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts_with_history.BKOutputHistoryAbstract;
import step08_output_files.abstracts_with_history.BKOutputHistoryManager;

public class BKOutputHistory_ClientNAV_v2 extends BKOutputHistoryAbstract {

	public BKOutputHistory_ClientNAV_v2(BKOutputHistoryManager _sBKOutputHistoryManager, BKAccount _sBKAccount) {
		super(_sBKOutputHistoryManager);
		pBKAccount = _sBKAccount;
		/*
		 * 
		 */
		addNewSuffixToNameFile(pBKAccount.getpEmail());
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;
	
	/**
	 * 
	 */
	@Override public String getpHeader() {
		return "Date"
				+ ",Incoming funds in " + pBKAccount.getpBKAssetCurrency().getpName()
				+ ",NAV in "  + pBKAccount.getpBKAssetCurrency().getpName();
	}

	/**
	 * 
	 */
	@Override public List<String> getpListLineToWrite(int _sDateEndOfMonthToDo) {
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Initiate
		 */
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(pBKAccount.getpKey());
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return lListLineToWrite;
		}
		BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_INCOMING_FUNDS(), this);
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, pBKAccount);
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDateIncomingFunds = pBKPartitionManager
				.getpBKPartitionPerBKIncomeAndBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
		/*
		 * get the total NAV over all the assets and write file
		 */
		if (_sDateEndOfMonthToDo > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
			return lListLineToWrite;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(_sDateEndOfMonthToDo);
		if (lBKTransactionPartitionDate == null) {
			return lListLineToWrite;
		}
		/*
		 * Compute NAV in local currency
		 */
		double lNAVUSD = lBKTransactionPartitionDate.getpNAV();
		double lPriceCurrency = pBKAccount.getpBKAssetCurrency().getpPriceUSD(_sDateEndOfMonthToDo);
		double lNAV = lNAVUSD / lPriceCurrency;
		/*
		 * Get the income funds total from the previous file
		 */
		double lIncomingFundsPrevious = 0.;
		BasicFile lBasicFilePrevious = getpBKFileReportPrevious(_sDateEndOfMonthToDo);
		if (lBasicFilePrevious != null) {
			int lSize = lBasicFilePrevious.getmReadFile().getmContentList().size();
			if (lSize > 0) {
				List<String> lLastLine = lBasicFilePrevious.getmReadFile().getmContentList().get(lSize - 1);
				lIncomingFundsPrevious = BasicString.getDouble(lLastLine.get(1));
			}
		}
		/*
		 * Date of previous file
		 */
		int lDatePrevious = -1;
		if (lBasicFilePrevious != null) {
			lDatePrevious = lBasicFilePrevious.getmDate();
		}
		/*
		 * Compute incoming fund in local currency
		 */
		double lIncomingFundsDifferential = 0.;
		if (lTreeMapDateToBKTransactionPartitionDateIncomingFunds != null) {
			BKTransactionPartitionDate lBKTransactionPartitionDateIncomingFunds = lTreeMapDateToBKTransactionPartitionDateIncomingFunds.get(_sDateEndOfMonthToDo);
			if (lBKTransactionPartitionDateIncomingFunds != null) {
				lIncomingFundsDifferential = lBKTransactionPartitionDateIncomingFunds.getpHoldingNoNaNNoNull(pBKAccount.getpBKAssetCurrency());
				BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lTreeMapDateToBKTransactionPartitionDateIncomingFunds.get(lDatePrevious);
				if (lBKTransactionPartitionDatePrevious != null) {
					lIncomingFundsDifferential += -lBKTransactionPartitionDatePrevious.getpHoldingNoNaNNoNull(pBKAccount.getpBKAssetCurrency());
				}
			}			
		}
		/*
		 * Incoming funds
		 */
		double lIncomingFunds = lIncomingFundsPrevious + lIncomingFundsDifferential;
		/*
		 * Write file in the file
		 */
		String lLine = "" + _sDateEndOfMonthToDo
			+ "," + lIncomingFunds
			+ "," + lNAV;
		lListLineToWrite.add(lLine);
		/*
		 * 
		 */
		return lListLineToWrite;
	}

}

package step08_output_files.client_nav;

import java.util.TreeMap;

import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_ClientNAV extends BKOutputAbstract {

	public BKOutput_ClientNAV(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
		super(_sBKOutputManager);
		/*
		 * 
		 */
		pBKAccount = _sBKAccount;
		addNewSuffixToNameFile(pBKAccount.getpEmail());
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;

	/**
	 * 
	 */
	public final String getpDirRoot() {
		return BKStaticDir.getOUTPUT_CLIENT();
	}

	/**
	 * 
	 */
	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(pBKAccount.getpKey());
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_INCOMING_FUNDS(), this);
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, pBKAccount);
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDateIncomingFunds = pBKPartitionManager
				.getpBKPartitionPerBKIncomeAndBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
		/*
		 * Header
		 */
		addNewHeader("Date"
				+ ",Incoming funds in " + pBKAccount.getpBKAssetCurrency().getpName()
				+ ",NAV in "  + pBKAccount.getpBKAssetCurrency().getpName());
		/*
		 * get the total NAV over all the assets and write file
		 */
		for (int lDate : getpListDateEndOfMonth()) {
			if (lDate > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
				continue;
			}
			BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
			if (lBKTransactionPartitionDate == null) {
				continue;
			}
			/*
			 * Compute NAV in local currency
			 */
			double lNAVUSD = lBKTransactionPartitionDate.getpNAV();
			double lPriceCurrency = pBKAccount.getpBKAssetCurrency().getpPriceUSD(lDate);
			double lNAV = lNAVUSD / lPriceCurrency;
			/*
			 * Compute incoming fund in local currency
			 */
			double lIncomingFunds = 0.;
			if (lTreeMapDateToBKTransactionPartitionDateIncomingFunds != null) {
				BKTransactionPartitionDate lBKTransactionPartitionDateIncomingFunds = lTreeMapDateToBKTransactionPartitionDateIncomingFunds.get(lDate);
				if (lBKTransactionPartitionDateIncomingFunds != null) {
					lIncomingFunds = lBKTransactionPartitionDateIncomingFunds.getpHoldingNoNaNNoNull(pBKAccount.getpBKAssetCurrency());
				}
			}
			/*
			 * Write line
			 */
			String lLine = "" + lDate
					+ "," + lIncomingFunds
					+ "," + lNAV;
			addNewLineToWrite(lLine);
		}
	}

}

package step08_output_files.client_nav_v2;

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

public class BKOutput_ClientNAV_v2_old extends BKOutputAbstract {

	public BKOutput_ClientNAV_v2_old(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
		super(_sBKOutputManager);
		/*
		 * 
		 */
		pBKAccount = _sBKAccount;
		addNewSuffixToNameFile(pBKAccount.getpEmail());
		pBKOutput_OneNAVManager = new BKOutput_OneNAVManager();
		pBKOutput_ClientNAV_FileManager = new BKOutput_ClientNAV_FileManager(this);
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;
	private BKOutput_OneNAVManager pBKOutput_OneNAVManager;
	private BKOutput_ClientNAV_FileManager pBKOutput_ClientNAV_FileManager;
	
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
		 * Load NAV from the most recent previous file
		 */
		pBKOutput_ClientNAV_FileManager.findMostRecentFileAndLoadNAVs();
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
			 * Get or create NAV - override any past value
			 */
			BKOutput_OneNAV lBKOutput_OneNAV = pBKOutput_OneNAVManager.getpOrCreateBKOutput_OneNAV(lDate);
			lBKOutput_OneNAV.setpIncomingFunds(lIncomingFunds);
			lBKOutput_OneNAV.setpNAV(lNAV);
		}
		/*
		 * Write file in the file
		 */
		for (BKOutput_OneNAV lBKOutput_OneNAV : pBKOutput_OneNAVManager.getpTreeMapDateToBKOutput_OneNAV().values()) {
			String lLine = "" + lBKOutput_OneNAV.getpDate()
					+ "," + lBKOutput_OneNAV.getpIncomingFunds()
					+ "," + lBKOutput_OneNAV.getpNAV();
			addNewLineToWrite(lLine);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKOutput_OneNAVManager getpBKOutput_OneNAVManager() {
		return pBKOutput_OneNAVManager;
	}

}

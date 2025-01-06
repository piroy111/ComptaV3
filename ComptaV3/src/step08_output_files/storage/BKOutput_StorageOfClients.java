package step08_output_files.storage;

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

public class BKOutput_StorageOfClients extends BKOutputAbstract {

	public BKOutput_StorageOfClients(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_STORAGE(), this);
		int lDateCompta = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		int lDateComptaPrevious = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateCompta, -1));
		int lDateComptaPastPrevious = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateComptaPrevious, -1));
		/*
		 * Header
		 */
		addNewHeader("BKAccount"
				+ ",Storage previous (" + lDateComptaPrevious + ") in US$"
				+ ",Storage current (" + lDateCompta + ") in US$"				
				+ ",Variation in US$"
				+ ",Variation in %");
		/*
		 * Write file content
		 */
		for (BKAccount lBKAccount : BKAccountManager.getpListBKAccount()) {
			String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
			TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
					.getpBKPartitionPerBKIncomeAndBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
			if (lTreeMapDateToBKTransactionPartitionDate != null) {
				/*
				 * Current
				 */
				double lNAVCurrent = 0.;
				BKTransactionPartitionDate lBKTransactionPartitionDateCurrent = lTreeMapDateToBKTransactionPartitionDate.get(lDateCompta);
				if (lBKTransactionPartitionDateCurrent != null) {
					lNAVCurrent = lBKTransactionPartitionDateCurrent.getpNAV();
				}
				/*
				 * Previous
				 */
				double lNAVPrevious = 0.;
				BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lTreeMapDateToBKTransactionPartitionDate.get(lDateComptaPrevious);
				if (lBKTransactionPartitionDatePrevious != null) {
					lNAVPrevious = lBKTransactionPartitionDatePrevious.getpNAV();
				}
				/*
				 * PastPrevious
				 */
				double lNAVPastPrevious = 0.;
				BKTransactionPartitionDate lBKTransactionPartitionDatePastPrevious = lTreeMapDateToBKTransactionPartitionDate.get(lDateComptaPastPrevious);
				if (lBKTransactionPartitionDatePastPrevious != null) {
					lNAVPastPrevious = lBKTransactionPartitionDatePastPrevious.getpNAV();
				}
				/*
				 * Storage
				 */
				double lPriceUSDCurrent = lBKAccount.getpBKAssetCurrency().getpPriceUSD(lDateCompta);
				double lPriceUSDPrevious = lBKAccount.getpBKAssetCurrency().getpPriceUSD(lDateComptaPrevious);
				double lStorageCurrent = (lNAVCurrent - lNAVPrevious) * lPriceUSDCurrent;
				double lStoragePrevious = (lNAVPrevious - lNAVPastPrevious) * lPriceUSDPrevious;
				double lVariation = lStorageCurrent - lStoragePrevious;
				double lVariationPercent = lVariation / lStoragePrevious;
				/*
				 * Write file
				 */
				double lKeySort = -Math.abs(lStorageCurrent);
				addNewLineToWrite(lBKAccount.getpEmail()
						+ "," + lStoragePrevious
						+ "," + lStorageCurrent						
						+ "," + lVariation
						+ "," + lVariationPercent, lKeySort);
			}
		}
	}

	
	
}

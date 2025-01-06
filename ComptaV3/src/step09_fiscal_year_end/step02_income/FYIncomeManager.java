package step09_fiscal_year_end.step02_income;

import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step09_fiscal_year_end.FYManager;
import step09_fiscal_year_end.step02_income.objects.FYIncomeFYGroup;
import step09_fiscal_year_end.step02_income.objects.FYIncomeGroupManager;

public class FYIncomeManager {

	public FYIncomeManager(FYManager _sFYManager) {
		pFYManager = _sFYManager;
		/*
		 * 
		 */
		pFYIncomeGroupManager = new FYIncomeGroupManager(this);
	}

	/*
	 * Data
	 */
	private FYManager pFYManager;
	private FYIncomeGroupManager pFYIncomeGroupManager;

	/**
	 * 
	 */
	public final void loadFYIncome() {
		BasicPrintMsg.displayTitle(this, "Load NAVs for FYIncome");
		/*
		 * Load from dump file (for debug purposes, to avoid the long loading of all the data)
		 */
		if (pFYManager.getpBKLaunchMe() == null) {
			String lDir = "G:/My Drive/Compta_bunker_v3/04_Dump/Old/";
			String lNameFile = "Dump_FY.csv";
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
			for (List<String> lLine : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				String lNameBKIncome = lLine.get(++lIdx);
				double lNAVFYPastPrevious = BasicString.getDouble(lLine.get(++lIdx));
				double lNAVFYPrevious = BasicString.getDouble(lLine.get(++lIdx));
				double lNAVFYCurrent = BasicString.getDouble(lLine.get(++lIdx));
				/*
				 * Feed
				 */
				BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(lNameBKIncome, this);
				pFYIncomeGroupManager.declareNAVForBKIncome(lBKIncome, lNAVFYPastPrevious, lNAVFYPrevious, lNAVFYCurrent);
			}
		}
		/**
		 * @TODO : load from BKPartition
		 */
		else {
			int lDatePastPrevious = pFYManager.getpFYDateManager().getpDateFYPastPrevious();
			int lDatePrevious = pFYManager.getpFYDateManager().getpDateFYPrevious();
			int lDateCurrent = pFYManager.getpFYDateManager().getpDateFYCurrent();
			/*
			 * 
			 */
			for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
				String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, BKAccountManager.getpBKAccountBunker());
				TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pFYManager.getpBKLaunchMe().getpBKPartitionManager().getpBKPartitionPerBKIncomeAndBKAccount()
					.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
				if (lTreeMapDateToBKTransactionPartitionDate != null) {
					/*
					 * Retrieve NAV
					 */
					double lNAVFYPastPrevious = getpNAV(lDatePastPrevious, lTreeMapDateToBKTransactionPartitionDate);
					double lNAVFYPrevious = getpNAV(lDatePrevious, lTreeMapDateToBKTransactionPartitionDate);
					double lNAVFYCurrent = getpNAV(lDateCurrent, lTreeMapDateToBKTransactionPartitionDate);
					/*
					 * Feed
					 */
					pFYIncomeGroupManager.declareNAVForBKIncome(lBKIncome, lNAVFYPastPrevious, lNAVFYPrevious, lNAVFYCurrent);
				}
			}
		}
		/*
		 * Display
		 */
		for (FYIncomeFYGroup lFYIncomeFYGroup : pFYIncomeGroupManager.getpTreeMapNameToFYIncomeFYGroup().values()) {
			BasicPrintMsg.display(this, lFYIncomeFYGroup.getpName() 
					+ "; =" + lFYIncomeFYGroup.getpNAVPastPrevious()
					+ "; =" + lFYIncomeFYGroup.getpNAVPrevious()
					+ "; =" + lFYIncomeFYGroup.getpNAVCurrent()); 
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @param _sTreeMapDateToBKTransactionPartitionDate
	 * @return
	 */
	private double getpNAV(int _sDate, TreeMap<Integer, BKTransactionPartitionDate> _sTreeMapDateToBKTransactionPartitionDate) {
		BKTransactionPartitionDate lBKTransactionPartitionDate = _sTreeMapDateToBKTransactionPartitionDate.get(_sDate);
		if (lBKTransactionPartitionDate == null) {
			return 0.;
		} else {
			return lBKTransactionPartitionDate.getpNAV();
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final FYManager getpFYManager() {
		return pFYManager;
	}
	public final TreeMap<String, FYIncomeFYGroup> getpTreeMapNameToFYIncomeFYGroup() {
		return pFYIncomeGroupManager.getpTreeMapNameToFYIncomeFYGroup();
	}
}

package step09_fiscal_year_end.step03_balancesheet.liabilities;

import java.util.ArrayList;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step09_fiscal_year_end.step03_balancesheet.FYBalanceSheetManager;

public class FYLiabilitiesGroupManager {

	public FYLiabilitiesGroupManager(FYBalanceSheetManager _sFYBalanceSheetManager) {
		pFYBalanceSheetManager = _sFYBalanceSheetManager;
		/*
		 * 
		 */
		pTreeMapNameToFYLiabilitiesGroup = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private FYBalanceSheetManager pFYBalanceSheetManager;
	private TreeMap<String, FYLiabilitiesGroup> pTreeMapNameToFYLiabilitiesGroup;

	/**
	 * Load liabilities
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Load and compute liabilities for the balance sheet");
		/*
		 * Initiate
		 */
		int lDateCurrent = pFYBalanceSheetManager.getpFYManager().getpFYDateManager().getpDateFYCurrent();
		int lDatePrevious = pFYBalanceSheetManager.getpFYManager().getpFYDateManager().getpDateFYPrevious();
		/*
		 * Loop on all BKAsset
		 */
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			double lNAVCurrent = 0.;
			double lNAVPrevious = 0.;
			/*
			 * Loop on BKAccount
			 */
			for (BKAccount lBKAccount : BKAccountManager.getpListBKAccount()) {
				if (lBKAccount.equals(BKAccountManager.getpBKAccountBunker())) {
					continue;
				}
				/*
				 * Find partition
				 */
				String lKey = BKPartitionPerBKAccount.getKey(lBKAccount);
				TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pFYBalanceSheetManager.getpFYManager().getpBKLaunchMe().getpBKPartitionManager()
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
				if (lTreeMapDateToBKTransactionPartitionDate == null) {
					continue;
				}
				/*
				 * Current
				 */
				BKTransactionPartitionDate lBKTransactionPartitionDateCurrent = lTreeMapDateToBKTransactionPartitionDate.get(lDateCurrent);
				if (lBKTransactionPartitionDateCurrent != null) {
					lNAVCurrent += lBKTransactionPartitionDateCurrent.getpNAVNoNull(lBKAsset);
				}
				/*
				 * Previous
				 */
				BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lTreeMapDateToBKTransactionPartitionDate.get(lDatePrevious);
				if (lBKTransactionPartitionDatePrevious != null) {
					lNAVPrevious += lBKTransactionPartitionDatePrevious.getpNAVNoNull(lBKAsset);
				}
			}
			/*
			 * Create objects and add NAV
			 */
			FYLiabilitiesGroup lFYLiabilitiesGroup = getpOrCreateFYLiabilitiesGroup(lBKAsset);
			FYLiabilities lFYLiabilities = lFYLiabilitiesGroup.getpOrCreateFYLiabilities(lBKAsset);
			lFYLiabilitiesGroup.addNAV(lNAVPrevious, lNAVCurrent);
			lFYLiabilities.addNAV(lNAVPrevious, lNAVCurrent);
		}
		/*
		 * Communication
		 */
		for (FYLiabilitiesGroup lFYLiabilitiesGroup : pTreeMapNameToFYLiabilitiesGroup.values()) {
			BasicPrintMsg.display(this, "Created " + lFYLiabilitiesGroup.getpName()
					+ ": grouping " + new ArrayList<>(lFYLiabilitiesGroup.getpTreeMapBKAssetToFYLiabilities().values()));
		}
	}

	/**
	 * Classic get or create
	 * @param _sBKAsset
	 * @return
	 */
	public final FYLiabilitiesGroup getpOrCreateFYLiabilitiesGroup(BKAsset _sBKAsset) {
		String lName = FYLiabilitiesGroup.getName(_sBKAsset);
		FYLiabilitiesGroup lFYLiabilitiesGroup = pTreeMapNameToFYLiabilitiesGroup.get(lName);
		if (lFYLiabilitiesGroup == null) {
			lFYLiabilitiesGroup = new FYLiabilitiesGroup(lName);
			pTreeMapNameToFYLiabilitiesGroup.put(lName, lFYLiabilitiesGroup);
		}
		return lFYLiabilitiesGroup;
	}

	/*
	 * 
	 */
	public final TreeMap<String, FYLiabilitiesGroup> getpTreeMapNameToFYLiabilitiesGroup() {
		return pTreeMapNameToFYLiabilitiesGroup;
	}
	
}

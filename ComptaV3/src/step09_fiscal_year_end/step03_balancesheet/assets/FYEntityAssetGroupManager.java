package step09_fiscal_year_end.step03_balancesheet.assets;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step09_fiscal_year_end.FYManager;
import step09_fiscal_year_end.step03_balancesheet.FYBalanceSheetManager;

public class FYEntityAssetGroupManager {

	public FYEntityAssetGroupManager(FYBalanceSheetManager _sFYBalanceSheetManager) {
		pFYBalanceSheetManager = _sFYBalanceSheetManager;
		/*
		 * 
		 */
		pTreeMapNameToFYEntityAssetGroup = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private FYBalanceSheetManager pFYBalanceSheetManager;
	private TreeMap<String, FYEntityAssetGroup> pTreeMapNameToFYEntityAssetGroup;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Load and compute assets for the balance sheet");
		/*
		 * Initiate
		 */
		FYManager pFYManager = pFYBalanceSheetManager.getpFYManager();
		Map<String, BKEntity> lMapNameToBKEntity = BKEntityManager.getMapKeyToBKEntityPhysical();
		int lDateCurrent = pFYManager.getpFYDateManager().getpDateFYCurrent();
		int lDatePrevious = pFYManager.getpFYDateManager().getpDateFYPrevious();
		BKPartitionPerBKEntity lBKPartitionPerBKEntity =  pFYManager
				.getpBKLaunchMe().getpBKPartitionManager().getpBKPartitionPerBKEntity();
		/*
		 * Loop on all BKEntity (only physical ones) and then all BKAsset per BKEntity
		 */
		for (BKEntity lBKEntity : lMapNameToBKEntity.values()) {
			String lKey = BKPartitionPerBKEntity.getKey(lBKEntity);
			TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = lBKPartitionPerBKEntity
					.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
			if (lTreeMapDateToBKTransactionPartitionDate != null) {
				for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
					/*
					 * NAV + holding current
					 */
					double lNAVCurrent = lBKPartitionPerBKEntity.getpNAVNoNaN(lKey, lDateCurrent, lBKAsset);
					double lHoldingCurrent = lBKPartitionPerBKEntity.getpHoldingBKAssetNotNull(lKey, lDateCurrent, lBKAsset);
					/*
					 * NAV + holding previous
					 */
					double lNAVPrevious = lBKPartitionPerBKEntity.getpNAVNoNaN(lKey, lDatePrevious, lBKAsset);
					double lHoldingPrevious = lBKPartitionPerBKEntity.getpHoldingBKAssetNotNull(lKey, lDatePrevious, lBKAsset);
					/*
					 * Create object and store NAV
					 */
					if (!AMNumberTools.isNaNOrNullOrZero(lNAVCurrent) || !AMNumberTools.isNaNOrNullOrZero(lNAVPrevious)) {
						/*
						 * We put the NAV on the USD if the holding of the asset is zero
						 */
						BKAsset lBKAssetRetained = lBKAsset;
						if (AMNumberTools.isNaNOrNullOrZero(lHoldingCurrent) && AMNumberTools.isNaNOrNullOrZero(lHoldingPrevious)) {
							lBKAssetRetained = BKAssetManager.getpBKAssetCurrencyReference();
						}
						/*
						 * Increment the NAV for the FYEntity
						 */						
						FYEntityAssetGroup lFYEntityAssetGroup = getpOrCreateFYEntityAssetGroup(lBKEntity, lBKAssetRetained);
						FYEntityAsset lFYEntityAsset = lFYEntityAssetGroup.getpOrCreateFYEntityAsset(lBKEntity, lBKAssetRetained);
						lFYEntityAssetGroup.addNAV(lNAVPrevious, lNAVCurrent);
						lFYEntityAsset.addNAV(lNAVPrevious, lNAVCurrent);
					}
				}
			}
		}
		/*
		 * Display
		 */
		for (FYEntityAssetGroup lFYEntityAssetGroup : getpTreeMapNameToFYEntityAssetGroup().values()) {
			BasicPrintMsg.display(this, "Created " + lFYEntityAssetGroup.getpName()
					+ ": grouping= " + new ArrayList<>(lFYEntityAssetGroup.getpTreeMapKeyToFYEntityAsset().values()));
		}
	}
	
	
	/**
	 * 
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @return
	 */
	public final FYEntityAssetGroup getpOrCreateFYEntityAssetGroup(BKEntity _sBKEntity, BKAsset _sBKAsset) {
		String lName = FYEntityAssetGroup.getName(_sBKEntity, _sBKAsset);
		FYEntityAssetGroup lFYEntityAssetGroup = pTreeMapNameToFYEntityAssetGroup.get(lName);
		if (lFYEntityAssetGroup == null) {
			lFYEntityAssetGroup = new FYEntityAssetGroup(lName);
			pTreeMapNameToFYEntityAssetGroup.put(lName, lFYEntityAssetGroup);
		}
		return lFYEntityAssetGroup;
	}	

	/*
	 * Getters & Setters
	 */
	public final FYBalanceSheetManager getpFYBalanceSheetManager() {
		return pFYBalanceSheetManager;
	}
	public final TreeMap<String, FYEntityAssetGroup> getpTreeMapNameToFYEntityAssetGroup() {
		return pTreeMapNameToFYEntityAssetGroup;
	}
	
}

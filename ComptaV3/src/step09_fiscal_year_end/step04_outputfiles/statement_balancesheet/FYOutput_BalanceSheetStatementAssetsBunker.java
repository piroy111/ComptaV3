package step09_fiscal_year_end.step04_outputfiles.statement_balancesheet;

import java.util.TreeMap;

import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_BalanceSheetStatementAssetsBunker extends FYOutputFileAbstract {

	public FYOutput_BalanceSheetStatementAssetsBunker(FYOutputFileManager _sFYOutputFileManager) {
		super(_sFYOutputFileManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDateCurrent = pFYManager.getpFYDateManager().getpDateFYCurrent();
		int lDatePrevious = pFYManager.getpFYDateManager().getpDateFYPrevious();
		/*
		 * Header
		 */
		addNewHeader("BKAsset");
		addNewHeader("FY " + lDateCurrent);
		addNewHeader("FY " + lDatePrevious);
		/*
		 * Initiate
		 */
		String lKey = BKPartitionPerBKAccount.getKey(BKAccountManager.getpBKAccountBunker());
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pFYManager.getpBKLaunchMe()
				.getpBKPartitionManager().getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
		BKTransactionPartitionDate lBKTransactionPartitionDateCurrent = lTreeMapDateToBKTransactionPartitionDate.get(lDateCurrent);
		BKTransactionPartitionDate lBKTransactionPartitionDatePrevious = lTreeMapDateToBKTransactionPartitionDate.get(lDatePrevious);
		/*
		 * Write file content
		 */
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			double lNAVCurrent = 0.;
			double lNAVPrevious = 0.;
			if (lBKTransactionPartitionDateCurrent != null) {
				lNAVCurrent = lBKTransactionPartitionDateCurrent.getpNAVNoNull(lBKAsset);
			}
			if (lBKTransactionPartitionDatePrevious != null) {
				lNAVPrevious = lBKTransactionPartitionDatePrevious.getpNAVNoNull(lBKAsset);
			}
			addNewLineToWrite(lBKAsset.getpName()
					+ "," + lNAVCurrent
					+ "," + lNAVPrevious);
		}
	}

	
	
}

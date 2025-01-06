package step00_freeze_transactions.step04_write_file_missing;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;

public class BKFrozenCapitalWriter {

	public BKFrozenCapitalWriter(BKFrozenManager _sBKFrozenManager) {
		pBKFrozenManager = _sBKFrozenManager;
	}
	
	/*
	 * Data
	 */
	private BKFrozenManager pBKFrozenManager;
	
	
	/**
	 * Write a file with the capital so we can take it over in the next FY and we don't lose the information about the capital
	 */
	public final void run(int _sDateFYToDo) {
		/*
		 * File content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Find the partition to know how much capital
		 */
		BKPartitionPerBKIncomeAndBKAccount lBKPartitionPerBKIncomeAndBKAccount = pBKFrozenManager.getpBKLaunchMe().getpBKPartitionManager().getpBKPartitionPerBKIncomeAndBKAccount();
		BKIncome lBKIncomeCapital = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_CAPITAL(), this);
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncomeCapital, BKAccountManager.getpBKAccountBunker());
		/*
		 * Loop on the assets to know which capital in which asset (USD, SGD, etc.)
		 */
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			double lHolding = lBKPartitionPerBKIncomeAndBKAccount.getpHoldingBKAssetNotNull(lKey, _sDateFYToDo, lBKAsset);
			if (!AMNumberTools.isNaNOrNullOrZero(lHolding)) {
				/*
				 * Write line
				 */
				String lLine = lBKAsset.getpName()
						+ "," + lHolding;
				lListLineToWrite.add(lLine);
			}
		}
		/*
		 * Write file
		 */
		String lHeader = "BKAsset,Capital";
		String lDir = BKStaticDir.getFREEZE_CAPITAL();
		int lDateFile = BasicDateInt.getmPlusDay(_sDateFYToDo, 1);
		String lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FREEZE_CAPITAL();
		BasicFichiers.writeFile(this, lDir, lNameFile, lHeader, lListLineToWrite);
	}

	
	
}

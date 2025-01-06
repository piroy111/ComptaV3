package step04_debug.pull_out_bars_in_error;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step10_launchme.BKLaunchMe;

public class DGPullOutBarsInError {

	public DGPullOutBarsInError(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		/*
		 * 
		 */
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pDir = BKStaticDir.getDEBUG() + this.getClass().getSimpleName() + "/";
		BasicFichiers.getOrCreateDirectory(pDir);
	}
	
	/*
	 * Static
	 */
	private static boolean IS_ACTIVATE = true;
	private static int DATE_TO_CHECK_ERRORS = 20200831;
	/*
	 * Data
	 */
	private BKPartitionManager pBKPartitionManager;
	private BKLaunchMe pBKLaunchMe;
	private String pDir;
	
	/**
	 * Write a file debug with all the BKBars which are in error<br>
	 * Bunker has become debtor of a BKBar, whereas PRoy has never had this bar. It should not happen<br>
	 * Bunker has become debtor of a BKBar, whereas PRoy is not the owner of the BKBar. It should not happen<br>
	 */
	public void run() {
		if (!IS_ACTIVATE) {
			return;
		}
		/*
		 * Initiate
		 */
		BKPartitionPerBKAccount lBKPartitionPerAccount = pBKPartitionManager.getpBKPartitionPerBKAccount();
		String lKeyBunker = BKAccountManager.getpBKAccountBunker().getpKey();
		TreeMap<Integer, BKTransactionPartitionDate> pTreeMapDateToBKTransactionPartitionDateBunker = lBKPartitionPerAccount
				.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBunker);
		String lKeyProy = BKAccountManager.getpBKAccountPRoy().getpKey();
		TreeMap<Integer, BKTransactionPartitionDate> pTreeMapDateToBKTransactionPartitionDatePRoy = lBKPartitionPerAccount
				.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyProy);
		/*
		 * 
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		BKTransactionPartitionDate lBKTransactionPartitionDateBunker = pTreeMapDateToBKTransactionPartitionDateBunker.get(DATE_TO_CHECK_ERRORS);
		if (lBKTransactionPartitionDateBunker != null) {
			for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
				BKHoldingAssetDate lBKHoldingAssetDateBunker = lBKTransactionPartitionDateBunker.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
				if (lBKHoldingAssetDateBunker != null) {
					List<BKBar> lListBKBar = new ArrayList<>(lBKHoldingAssetDateBunker.getpTreeMapBKBarToHolding().keySet());
					for (BKBar lBKBar : lListBKBar) {
						int lHoldingBunker = lBKHoldingAssetDateBunker.getpTreeMapBKBarToHolding().get(lBKBar);
						if (lHoldingBunker == -1) {
							/*
							 * Finds the BKBar in PRoy
							 */
							BKHoldingAssetDate lBKHoldingAssetDatePRoy = pTreeMapDateToBKTransactionPartitionDatePRoy
									.get(DATE_TO_CHECK_ERRORS).getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
							/*
							 * Check
							 */
							String lErrorMsg = "";
							if (lBKHoldingAssetDatePRoy == null) {
								lErrorMsg = "Bunker has become debtor of a BKBar, whereas PRoy has never had this bar. It should not happen";
							}
							int lHoldingPRoy = lBKHoldingAssetDatePRoy.getpHolding(lBKBar);
							if (lHoldingPRoy != 1) {
								lErrorMsg = "Bunker has become debtor of a BKBar, whereas PRoy is not the owner of the BKBar. It should not happen";
							}
							/*
							 * Write a line in the file if there is an error
							 * Bars offset of Bunker
							 */
							lListLineToWrite.add(DATE_TO_CHECK_ERRORS
									+ "," + lBKAssetMetal.getpName()
									+ "," + lBKBar.getpID()
									+ "," + (-lHoldingBunker * lBKBar.getpWeightOz())
									+ "," + "NaN"
									+ "," + BKAccountManager.getpBKAccountBunker().getpEmail()
									+ "," + BKStaticConst.getBKINCOME_OPERATIONS_BARS_HELD_BY_BUNKER()
									+ "," + lErrorMsg);
							/*
							 * Bar mirror to PRoy
							 */
							lListLineToWrite.add(DATE_TO_CHECK_ERRORS
									+ "," + lBKAssetMetal.getpName()
									+ "," + lBKBar.getpID()
									+ "," + (lHoldingBunker * lBKBar.getpWeightOz())
									+ "," + "NaN"
									+ "," + BKAccountManager.getpBKAccountPRoy().getpEmail()
									+ "," + BKStaticConst.getBKINCOME_OPERATIONS_BARS_HELD_BY_BUNKER()
									+ "," + lErrorMsg);
							/*
							 * Credit of loans in order to maintain the holding of PRoy and Bunker
							 * Loan to Bunker
							 */
							lListLineToWrite.add(DATE_TO_CHECK_ERRORS
									+ "," + lBKAssetMetal.getpBKAssetLoan().getpName()
									+ "," + lBKBar.getpID()
									+ "," + (lHoldingBunker * lBKBar.getpWeightOz())
									+ "," + "NaN"
									+ "," + BKAccountManager.getpBKAccountBunker().getpEmail()
									+ "," + BKStaticConst.getBKINCOME_LOAN() + lBKAssetMetal.getpNameMetal()
									+ "," + lErrorMsg);
							/*
							 * Loan PRoy
							 */
							lListLineToWrite.add(DATE_TO_CHECK_ERRORS
									+ "," + lBKAssetMetal.getpBKAssetLoan().getpName()
									+ "," + lBKBar.getpID()
									+ "," + (-lHoldingBunker * lBKBar.getpWeightOz())
									+ "," + "NaN"
									+ "," + BKAccountManager.getpBKAccountPRoy().getpEmail()
									+ "," + BKStaticConst.getBKINCOME_LOAN() + lBKAssetMetal.getpNameMetal()
									+ "," + lErrorMsg);
						}
					}
				}
			}
		}
		/*
		 * Write file
		 */
		String lHeader = BKStaticConst.getHEADER_FILE_TRANSACTIONS();
		String lNameFile = BasicDateInt.getmToday() + "_" + this.getClass().getSimpleName() + ".csv";
		BKComOnFilesWritten.writeFile(com_file_written.Debug, pDir, lNameFile, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "File debug written " + pDir + lNameFile);
	}
	
}

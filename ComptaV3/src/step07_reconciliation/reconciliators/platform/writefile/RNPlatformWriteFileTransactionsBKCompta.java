package step07_reconciliation.reconciliators.platform.writefile;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.reconciliator.RNPlatformBalanceReconciliator;

public class RNPlatformWriteFileTransactionsBKCompta {


	public RNPlatformWriteFileTransactionsBKCompta(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
	}

	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;
	private String pDirPlusNameFile;

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Initiate 
		 */
		String lDir = BKStaticDir.getOUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA();
		String lNameFile = BasicDateInt.getmToday() + BKStaticNameFile.getSUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA();
		pDirPlusNameFile = lDir + lNameFile;
		/*
		 * Build file content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKTransaction lBKTransaction : BKTransactionManager.getpListBKTransactionSorted()) {
			/*
			 * Load
			 */
			int lDate = lBKTransaction.getpDate();
			BKAccount lBKAccount = lBKTransaction.getpBKAccount();
			BKAsset lBKAsset = lBKTransaction.getpBKAsset();
			/*
			 * Case when we skip
			 */
			if (!RNPlatformBalanceReconciliator.getpIsBKAccountValidForReconciliation(lBKAccount, lDate).endsWith("Ok")) {
				continue;
			}
			/*
			 * We change the name of the BKAsset into the underlying, so that we see both the BAR and the LOAN when we will select the BARs in the spreadsheet
			 */
			String lLine = lBKTransaction.getpLineWriteInFile();
			if (!lBKAsset.equals(lBKAsset.getpBKAssetUnderlying())) {
				lLine = lLine.replaceAll(lBKAsset.getpName(), lBKAsset.getpBKAssetUnderlying().getpName());
			}
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write file
		 */
		String lHeader = BKTransaction.getHeaderWriteInFile();
		BKComOnFilesWritten.writeFile(com_file_written.Reconciliator, lDir, lNameFile, lHeader, lListLineToWrite);
	}

	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}
	public final String getpDirPlusNameFile() {
		return pDirPlusNameFile;
	}
}

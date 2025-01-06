package step07_reconciliation.reconciliators.platform.writefile;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import staticdata.datas.BKStaticConst.com_file_written;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.objects.platformtransaction.RNPlatformTransaction;

public class RNPlatformWriteFileTransactions {

	public RNPlatformWriteFileTransactions(RNPlatform _sRNPlatform) {
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
		 * Build file content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (int lDate : pRNPlatform.getpRNPlatformTransactionManager().getpTreeMapDateToListRNPlatformTransaction().keySet()) {
			List<RNPlatformTransaction> lListRNPlatformTransaction = pRNPlatform.getpRNPlatformTransactionManager().getpTreeMapDateToListRNPlatformTransaction().get(lDate);
			for (RNPlatformTransaction lRNPlatformTransaction : lListRNPlatformTransaction) {
				String lLine = lDate
						+ "," + lRNPlatformTransaction.getpBKAccount().getpEmail()
						+ "," + lRNPlatformTransaction.getpBKAccount().getpBKAssetCurrency().getpName()
						+ "," + lRNPlatformTransaction.getpBKAsset().getpName()
						+ "," + lRNPlatformTransaction.getpQuantity()
						+ "," + lRNPlatformTransaction.getpComment();
				lListLineToWrite.add(lLine);
			}
		}
		/*
		 * Write file
		 */
		String lHeader = "Date,BKAccount,BKCurrency of BKAccount,BKAsset,Quantity,Comment";
		String lDir = BKStaticDir.getOUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS();
		String lNameFile = BasicDateInt.getmToday() + BKStaticNameFile.getSUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS();
		pDirPlusNameFile = lDir + lNameFile;
		BKComOnFilesWritten.writeFile(com_file_written.Reconciliator, lDir, lNameFile, lHeader, lListLineToWrite);
	}

	/*
	 * Getters & Setters
	 */
	public final String getpDirPlusNameFile() {
		return pDirPlusNameFile;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}

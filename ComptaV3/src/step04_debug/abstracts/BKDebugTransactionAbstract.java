package step04_debug.abstracts;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticConst.com_file_written;
import step02_load_transactions.objects.transaction.BKTransaction;

public abstract class BKDebugTransactionAbstract {

	public BKDebugTransactionAbstract() {
		pListBKTransactionToPrint = new ArrayList<>();
		BKDebugManager.declareNewBKDebugTransactionAbstract(this);
	}
	
	/*
	 * Abstract
	 */
	public abstract boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction);
	/*
	 * Data
	 */
	private List<BKTransaction> pListBKTransactionToPrint;
	
	/**
	 * 
	 * @param _sBKTransaction
	 */
	protected final void addNewBKTransaction(BKTransaction _sBKTransaction) {
		if (getpIsKeepBKTransaction(_sBKTransaction)) {
			pListBKTransactionToPrint.add(_sBKTransaction);
		}
	}
	
	/**
	 * 
	 */
	protected final String flushFile() {
		/*
		 * Print all the BKTransaction
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKTransaction lBKTransaction : pListBKTransactionToPrint) {
			String lLine = lBKTransaction.getpLineWriteInFile();
			lListLineToWrite.add(lLine);
		}
		/*
		 * 
		 */
		String lNameClass = this.getClass().getSimpleName();
		String lDir = BKStaticDir.getOUTPUT_DEBUG() + lNameClass + "/";
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + "_" + lNameClass + ".csv";
		String lHeader = BKTransaction.getHeaderWriteInFile();
		/*
		 * 
		 */
		BKComOnFilesWritten.writeFile(com_file_written.Debug, lDir, lNameFile, lHeader, lListLineToWrite);
		return lDir + lNameFile;
	}
	
	
	
}

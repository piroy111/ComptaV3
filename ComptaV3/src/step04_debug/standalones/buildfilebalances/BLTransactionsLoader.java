package step04_debug.standalones.buildfilebalances;

import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst.type_entity;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;

class BLTransactionsLoader {

	protected BLTransactionsLoader(BLManager _sBLManager) {
		pBLManager = _sBLManager;
	}

	/*
	 * Data
	 */
	private BLManager pBLManager;
	private List<BKTransaction> pListBKTransaction;

	/**
	 * 
	 */
	public final void run() {
		if (pListBKTransaction == null) {
			BasicPrintMsg.displayTitle(this, "Load transactions into BKTransactionManager");
			/*
			 * BKEntity
			 */
			BKEntity lBKEntity;
			if (pBLManager.getpBLDirsAndNamesFiles().getpDirTransactions().contains("physical")) {
				lBKEntity = BKEntityManager.getpOrCreateBKEntity(pBLManager.getpBLDirsAndNamesFiles().getpMidFix(), type_entity.PHYSICAL);
			} else {
				lBKEntity = BKEntityManager.getBKEntityTransfer();
			}
			/*
			 * Read and upload all the files transactions
			 */
			BasicDir lBasicDir = new BasicDir(pBLManager.getpBLDirsAndNamesFiles().getpDirTransactions(), 
					pBLManager.getpBLDirsAndNamesFiles().getpSuffixTransactions());
			for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
				BKTransactionManager.readFileBKTransaction(lBasicFile.getmReadFile(), lBKEntity, this.getClass().getSimpleName());
			}
			/*
			 * Use the list of BKTransactionManager as the BKTransaction to compute the balances
			 */
			pListBKTransaction = BKTransactionManager.getpListBKTransactionSorted();
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BLManager getpBLManager() {
		return pBLManager;
	}
	public final List<BKTransaction> getpListBKTransaction() {
		return pListBKTransaction;
	}
	public final void setpListBKTransaction(List<BKTransaction> _sPListBKTransaction) {
		pListBKTransaction = _sPListBKTransaction;
	}

}

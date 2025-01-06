package step02_load_transactions.transactions_loaders.loaders;

import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccountManager;
import step02_load_transactions.objects.direntity.BKDirEntity;
import step02_load_transactions.objects.direntity.BKDirEntityManager;
import step02_load_transactions.objects.direntity.BKEntityFilesDate;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;

public class BKPlatformManager extends BKDirEntityManager {

	public BKPlatformManager() {
		super(BKStaticDir.getLOAD_TRANSACTIONS_PLATFORM(), type_entity.TRANSFER);
	}

	@Override public String getpSubFolderNameTransactions() {
		return BKStaticDir.getPLATFORM_SUBFOLDER_TRANSACTIONS();
	}

	@Override public String getpSubFolderNameDocs() {
		return null;
	}

	@Override public String getpSubFolderNameBalances() {
		return null;
	}

	@Override public String getpSuffixFileNameTransactions() {
		return BKStaticNameFile.getSUFFIX_TRANSACTIONS();
	}

	@Override public String getpSuffixFileNameDocs() {
		return null;
	}

	@Override public String getpSuffixFileNameBalances() {
		return null;
	}

	@Override public boolean getpIsAndTreatSpecialCase(BKDirEntity _sBKDirEntity) {
		/*
		 * Case of the purchases and sales done on the platform --> we skip the check that the sum of assets is zero + we create a mirror transaction
		 */
		if (BKStaticDir.getSUB_PURCHASE_AND_SALES().startsWith(_sBKDirEntity.getpNameDirEntity())) {
			BasicPrintMsg.displayTitle(this, "Read BKTransactions + check NAV (Special case treatment) for " + getpDirMain());
			/*
			 * Identify the names of the files
			 */
//			_sBKDirEntity.manageDirEntity();
			/*
			 * Read the files and upload the BKTransactions and create the mirrors
			 */
			for (BKEntityFilesDate lBKEntityFilesDate : _sBKDirEntity.getpTreeMapDateToBKEntityFilesDate().values()) {
				/*
				 * Skip if the files transactions has a date outside of the one allowed by BKFrozenManager
				 */
				if (lBKEntityFilesDate.getpIsExpeledByFrozen()) {
					continue;
				}
				/*
				 * Read BKTransactions
				 */
				List<BKTransaction> lListBKTransaction = lBKEntityFilesDate.readFileTransactions();
				addpErrorMessage(lBKEntityFilesDate.getpErrorMessage());
				exitIfError();
				for (BKTransaction lBKTransaction : lListBKTransaction) {
					_sBKDirEntity.addNewBKTransaction(lBKTransaction);
				}
				/*
				 * Create mirrors, but only after the first date of COMPTA_V3 = 20200831
				 */
				if (lBKEntityFilesDate.getpDate() > BKStaticConst.getDATE_START_COMPTA_V3()) {
					for (BKTransaction lBKTransaction : lListBKTransaction) {
						BKTransaction lBKTransactionMirror = BKTransactionManager.createBKTransaction(lBKTransaction.getpDate(), 
								lBKTransaction.getpBKAsset(), 
								-lBKTransaction.getpQuantity(), 
								lBKTransaction.getpPrice(), 
								BKAccountManager.getpBKAccountBunker(), 
								lBKTransaction.getpComment(),  
								lBKTransaction.getpBKIncome(), 
								lBKTransaction.getpBKEntity(),
								lBKTransaction.getpBKFile(), 
								this.getClass().getSimpleName());
						_sBKDirEntity.addNewBKTransaction(lBKTransactionMirror);
					}
				}
			}
			return true;
		}
		return false;
	}

}

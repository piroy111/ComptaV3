package step02_load_transactions.transactions_loaders.loaders_physical;

import java.util.ArrayList;
import java.util.List;

import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.IBManager;
import step02_load_transactions.interactivebrokers.reconcilewithnav.BKIBManager;
import step02_load_transactions.oanda.OAManager;
import step02_load_transactions.objects.direntity.BKDirEntity;
import step02_load_transactions.objects.direntity.BKDirEntityManager;
import step02_load_transactions.objects.direntity.BKEntityFilesDate;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step04_debug.standalones.buildfilebalances.BLManager;

public abstract class BKPhysicalManager extends BKDirEntityManager {

	public BKPhysicalManager(String _sDirRoot) {
		super(_sDirRoot, type_entity.PHYSICAL, BKPhysicalManager.class.getSimpleName());
	}

	@Override public String getpSubFolderNameTransactions() {
		return BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS();
	}

	@Override public String getpSubFolderNameDocs() {
		return BKStaticDir.getPHYSICAL_SUBFOLDER_DOCS();
	}

	@Override public String getpSubFolderNameBalances() {
		return BKStaticDir.getPHYSICAL_SUBFOLDER_BALANCES();
	}

	@Override public String getpSuffixFileNameTransactions() {
		return BKStaticNameFile.getSUFFIX_TRANSACTIONS();
	}

	@Override public String getpSuffixFileNameDocs() {
		return BKStaticNameFile.getSUFFIX_DOCS();
	}

	@Override public String getpSuffixFileNameBalances() {
		return BKStaticNameFile.getSUFFIX_BALANCES();
	}

	@Override public boolean getpIsAndTreatSpecialCase(BKDirEntity _sBKDirEntity) {
		/*
		 * Case of Interactive Brokers --> the NAV is in the file report of IB, so we use it directly
		 */
		if (_sBKDirEntity.getpNameDirEntity().equals(BKStaticConst.getBKENTITY_IB())) {
			/*
			 * Store the BKTransaction existing to identify afterwards which one were created by IB
			 */
			List<BKTransaction> lListBKTransactionBefore = new ArrayList<>(BKTransactionManager.getpListBKTransactionSorted());
			/*
			 * Create the files transactions from the reports of IB
			 */
			IBManager lIBManager = new IBManager();
			lIBManager.run();
			/*
			 * Load the files transactions and reconcile with NAV
			 */
			BKIBManager lBKIBManager = new BKIBManager(lIBManager);
			lBKIBManager.run();
			/*
			 * Check the names of the files and their presence
			 */
//			_sBKDirEntity.manageDirEntity();
			/*
			 * add all the BKTransactions to the holdings at the level of the entity
			 */
			for (BKTransaction lBKTransaction : BKTransactionManager.getpListBKTransactionSorted()) {
				if (!lListBKTransactionBefore.contains(lBKTransaction)) {
					_sBKDirEntity.addNewBKTransaction(lBKTransaction);
				}
			}
			/*
			 * Check the NAV matches the sum of the BKTransactions for the latest file
			 */
			BKEntityFilesDate lBKEntityFilesDateLast = _sBKDirEntity.getpTreeMapDateToBKEntityFilesDate().floorEntry(BKFrozenManager.getDATE_FY_MAX_TO_CREATE()).getValue();
			lBKEntityFilesDateLast.readFileBalances();
			addpErrorMessage(lBKEntityFilesDateLast.getpBKBalanceFileAbstract().getpIsReconcile());
			exitIfError();
			/*
			 * Reconcile with NAV with manual file
			 */
			return true;
		}
		/*
		 * Case of OANDA --> OANDA does not provide the price at which he recomputes the NAV --> it is impossible to match the NAV of OANDA
		 * Therefore we issue ourselves the file of NAV
		 */
		else if (_sBKDirEntity.getpNameDirEntity().equals(BKStaticConst.getBKENTITY_OANDA())) {
			/*
			 * Write files of BKTransactions from the import files
			 */
			OAManager lOAManager = new OAManager();
			lOAManager.run();
			/*
			 * Identify the names of the files
			 */
//			_sBKDirEntity.manageDirEntity();
			/*
			 * Read the files and upload the BKTransactions
			 */
			List<BKTransaction> lListBKTransaction = new ArrayList<>();
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
				List<BKTransaction> lListBKTransactionForFile = lBKEntityFilesDate.readFileTransactions();
				addpErrorMessage(lBKEntityFilesDate.getpErrorMessage());
				exitIfError();
				for (BKTransaction lBKTransaction : lListBKTransactionForFile) {
					_sBKDirEntity.addNewBKTransaction(lBKTransaction);
				}
				/*
				 * Store all the BKTransactions so we will compute the balance afterwards
				 */
				lListBKTransaction.addAll(lListBKTransactionForFile);
			}
			/*
			 * Write the file balance from the files transactions
			 */
			new BLManager().run(_sBKDirEntity.getpDirEntity(), lListBKTransaction);
			return true;
		}
		/*
		 * Other normal cases
		 */
		return false;
	}



}

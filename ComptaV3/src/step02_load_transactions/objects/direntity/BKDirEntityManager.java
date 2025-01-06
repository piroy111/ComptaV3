package step02_load_transactions.objects.direntity;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMDebug;
import basicmethods.AMNumberTools;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst.type_entity;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.holdings.BKHolding;
import step02_load_transactions.objects.transaction.BKTransaction;

public abstract class BKDirEntityManager {

	public BKDirEntityManager(String _sDirMain, type_entity _sTypeEntity) {
		this(_sDirMain, _sTypeEntity, null);
	}

	public BKDirEntityManager(String _sDirMain, type_entity _sTypeEntity, String _sClassNameForMidFix) {
		pDirMain = _sDirMain;
		pTypeEntity = _sTypeEntity;
		/*
		 * MidFix is used for the names of the files. It depends on the class name
		 */
		String lClassStr;
		if (_sClassNameForMidFix != null) {
			lClassStr = _sClassNameForMidFix;
		} else {
			lClassStr = this.getClass().getSimpleName();
		}
		pMidFix = getMidFix(lClassStr);
		/*
		 * 
		 */
		pMapKeyToBKDirEntity = new HashMap<>();
		pIsPhysicalOrMirror = getpSuffixFileNameBalances() != null;
	}

	/*
	 * Abstract
	 */
	public abstract String getpSubFolderNameTransactions();
	public abstract String getpSubFolderNameDocs();
	public abstract String getpSubFolderNameBalances();
	public abstract String getpSuffixFileNameTransactions();
	public abstract String getpSuffixFileNameDocs();
	public abstract String getpSuffixFileNameBalances();
	public abstract boolean getpIsAndTreatSpecialCase(BKDirEntity _sBKDirEntity);
	/*
	 * Data 
	 */
	private String pDirMain;
	private type_entity pTypeEntity;
	private String pErrorMessage;
	private Map<String, BKDirEntity> pMapKeyToBKDirEntity;
	private String pMidFix;
	private boolean pIsPhysicalOrMirror;

	/**
	 * 
	 * @return
	 */
	public static String getMidFix(String _sClassSimpleName) {
		return _sClassSimpleName.substring("BK".length(), _sClassSimpleName.length() - "Manager".length());
	}	

	/**
	 * Load all the file names and check they are in the correct format<br>
	 * Check and create the sub folders if they don't exist already<br>
	 * + create the BKEntityFiles for each file inside the sub folder (but does not read the files)
	 */
	public final void createBKEntities() {
		BasicPrintMsg.displayTitle(this, "Load the map of all files for BKEntities for " + this.getClass().getSimpleName());
		pErrorMessage = "";
		/*
		 * Create all BKEntities from the sub-directories. a BKEntity exists if there is a folder
		 */
		List<String> lListDirEntities = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(pDirMain);
		int lNbBKEntities = 0;
		for (String lNameDirEntity : lListDirEntities) {
			if (BasicFichiersNio.getIsDirectory(Paths.get(pDirMain + lNameDirEntity))) {
				BKEntity lBKEntity = BKEntityManager.getpOrCreateBKEntity(lNameDirEntity, pTypeEntity);
				getpOrCreateBKDirEntity(lBKEntity, lNameDirEntity);
				lNbBKEntities++;
			}
		}
		BasicPrintMsg.display(this, "Number of BKEntities created= " + lNbBKEntities);
		exitIfError();
		/*
		 * Load all the file names and check they are in the correct format
		 * Check and create the sub folders if they don't exist already
		 * + create the BKEntityFiles for each file inside the sub folder (but does not read the files)
		 */
		BasicPrintMsg.displayTitle(this, "Check directories");
		for (BKDirEntity lBKDirEntity : pMapKeyToBKDirEntity.values()) {
			BasicPrintMsg.display(this, "Checking '" + lBKDirEntity.getpNameDirEntity() + "'");
			lBKDirEntity.manageDirEntity();
		}
		exitIfError();
	}

	/**
	 * Load all files (transactions, balance, proof) and check balances are consistent with sum of BKTransactions
	 */
	public final void readFiles() {
		/*
		 * If special case
		 */
		List<BKDirEntity> lListBKDirEntityToDo = new ArrayList<>();
		for (BKDirEntity lBKDirEntity : pMapKeyToBKDirEntity.values()) {
			boolean lIsSpecialCase = getpIsAndTreatSpecialCase(lBKDirEntity);
			if (!lIsSpecialCase) {
				lListBKDirEntityToDo.add(lBKDirEntity);
			}
		}
		/*
		 * Case of physical --> Read all the files + create BKTransactions + check that for each date the NAV (or balance) is matched
		 */
		if (pIsPhysicalOrMirror) {
			BasicPrintMsg.displayTitle(this, "Read BKTransactions + check NAV for " + pDirMain);
			for (BKDirEntity lBKDirEntity : lListBKDirEntityToDo) {
				for (BKEntityFilesDate lBKEntityFilesDate : lBKDirEntity.getpTreeMapDateToBKEntityFilesDate().values()) {
					/*
					 * Skip if the files transactions has a date outside of the one allowed by BKFrozenManager
					 */
					if (lBKEntityFilesDate.getpIsExpeledByFrozen()) {
						continue;
					}
					/*
					 * read all the files
					 */
					lBKEntityFilesDate.readFileBalances();
					lBKEntityFilesDate.readFileDocsSupporting();
					List<BKTransaction> lListBKTransaction = lBKEntityFilesDate.readFileTransactions();
					addpErrorMessage(lBKEntityFilesDate.getpErrorMessage());
					exitIfError();
					/*
					 * add all the BKTransactions to the holdings at the level of the entity
					 */
					for (BKTransaction lBKTransaction : lListBKTransaction) {
						lBKDirEntity.addNewBKTransaction(lBKTransaction);
					}
					/*
					 * Check the NAV matches the sum of the BKTransactions
					 * We skip if the BKEntity is a vault because the reconciliation will be done in RNManager
					 */
					if (!lBKDirEntity.getpBKEntity().getpIsVault()) {
						addpErrorMessage(lBKEntityFilesDate.getpBKBalanceFileAbstract().getpIsReconcile());
						exitIfError();
					}
				}
			}
		} 
		/*
		 * Case of mirror --> we check that the sum of all the BKTransactions is zero for each account
		 */
		else {
			BasicPrintMsg.displayTitle(this, "Read BKTransactions + check the sum of all BKTranscations is zero");
			BasicPrintMsg.display(this, "pDirMain= " + pDirMain);
			for (BKDirEntity lBKDirEntity : lListBKDirEntityToDo) {
				for (BKEntityFilesDate lBKEntityFilesDate : lBKDirEntity.getpTreeMapDateToBKEntityFilesDate().values()) {
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
					/*
					 * add all the BKTransactions to the holdings at the level of the entity
					 */
					for (BKTransaction lBKTransaction : lListBKTransaction) {
						lBKDirEntity.addNewBKTransaction(lBKTransaction);
					}
				}
				/*
				 * Check that all transactions are in mirror for all BKAsset, all BKAccount
				 */
				for (BKAsset lBKAsset : lBKDirEntity.getpBKHoldingManager().getpMapBKAssetToBKHolding().keySet()) {
					BKHolding lBKHolding = lBKDirEntity.getpBKHoldingManager().getpMapBKAssetToBKHolding().get(lBKAsset);
					if (lBKHolding == null) {
						BKCom.errorCodeLogic();
					}
					if (!AMNumberTools.isZero(lBKHolding.getpQty())) {
						addpErrorMessage("The sum of the transactions should be zero as those are not physical transactions"
								+ "\nBKAsset in error= " + lBKAsset.getpName()
								+ "\nSum of all BKTransactions for this BKAsset= " + lBKHolding.getpQty()
								+ "\nBKEntity= " + lBKDirEntity.getpBKEntity().getpName()
								+ "\nBKDirEntity= " + lBKDirEntity.getpDirTransactions());
					} else if (lBKAsset.getpIsPaper() && !AMNumberTools.isNaNOrNullOrZero(lBKHolding.getpNNNExec())) {
						addpErrorMessage("The sum of the transaction has a NNNExec not null whereas the BKAsset is a paper type. Even if the sum of the quantity is zero, the NNNExec should be zero also"
								+ "\nBKAsset in error= " + lBKAsset.getpName()
								+ "\nSum of all BKTransactions for this BKAsset= " + lBKHolding.getpQty()
								+ "\nTotal NNNExec= " + lBKHolding.getpNNNExec()
								+ "\nBKEntity= " + lBKDirEntity.getpBKEntity().getpName()
								+ "\nBKDirEntity= " + lBKDirEntity.getpDirTransactions());
					}
				}
			}
		}
		exitIfError();
	}


	/**
	 * @deprecated
	 * Load all files and check balances
	 */
	public final void load() {
		BasicPrintMsg.displaySuperTitle(this, "Load files of BKTransactions for " + pMidFix);
		pErrorMessage = "";
		/*
		 * List the BKDirEntity to do
		 */
		List<BKDirEntity> lListBKDirEntityToDo = new ArrayList<>();
		List<String> lListDirEntities = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(pDirMain);
		for (String lNameDirEntity : lListDirEntities) {
			if (BasicFichiersNio.getIsDirectory(Paths.get(pDirMain + lNameDirEntity))) {
				BKEntity lBKEntity = BKEntityManager.getpOrCreateBKEntity(lNameDirEntity, pTypeEntity);
				BKDirEntity lBKDirEntity = getpOrCreateBKDirEntity(lBKEntity, lNameDirEntity);
				/*
				 * Treat special case
				 */
				boolean lIsSpecialCase = getpIsAndTreatSpecialCase(lBKDirEntity);
				if (!lIsSpecialCase) {
					lListBKDirEntityToDo.add(lBKDirEntity);
				}
			}
		}
		exitIfError();
		/*
		 * Load all the file names and check they are in the correct format
		 * Check and create the sub folders if they don't exist already
		 * + create the BKEntityFiles for each file inside the sub folder (but does not read the files)
		 */
		BasicPrintMsg.displayTitle(this, "Check directories");
		for (BKDirEntity lBKDirEntity : lListBKDirEntityToDo) {
			lBKDirEntity.manageDirEntity();
		}
		exitIfError();
		/*
		 * Case of physical --> Read all the files + create BKTransactions + check that for each date the NAV (or balance) is matched
		 */
		if (pIsPhysicalOrMirror) {
			BasicPrintMsg.displayTitle(this, "Read BKTransactions + check NAV");
			for (BKDirEntity lBKDirEntity : lListBKDirEntityToDo) {
				for (BKEntityFilesDate lBKEntityFilesDate : lBKDirEntity.getpTreeMapDateToBKEntityFilesDate().values()) {
					/*
					 * Skip if the files transactions has a date outside of the one allowed by BKFrozenManager
					 */
					if (lBKEntityFilesDate.getpIsExpeledByFrozen()) {
						continue;
					}
					/*
					 * read all the files
					 */
					lBKEntityFilesDate.readFileBalances();
					lBKEntityFilesDate.readFileDocsSupporting();
					List<BKTransaction> lListBKTransaction = lBKEntityFilesDate.readFileTransactions();
					addpErrorMessage(lBKEntityFilesDate.getpErrorMessage());
					exitIfError();
					/*
					 * add all the BKTransactions to the holdings at the level of the entity
					 */
					for (BKTransaction lBKTransaction : lListBKTransaction) {
						lBKDirEntity.addNewBKTransaction(lBKTransaction);
					}
					/*
					 * Check the NAV matches the sum of the BKTransactions
					 */
					addpErrorMessage(lBKEntityFilesDate.getpBKBalanceFileAbstract().getpIsReconcile());
					exitIfError();
				}
			}
		} 
		/*
		 * Case of mirror --> we check that the sum of all the BKTransactions is zero for each account
		 */
		else {
			BasicPrintMsg.displayTitle(this, "Read BKTransactions + check the sum of all BKTranscations is zero");
			for (BKDirEntity lBKDirEntity : lListBKDirEntityToDo) {
				for (BKEntityFilesDate lBKEntityFilesDate : lBKDirEntity.getpTreeMapDateToBKEntityFilesDate().values()) {
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
					/*
					 * add all the BKTransactions to the holdings at the level of the entity
					 */
					for (BKTransaction lBKTransaction : lListBKTransaction) {
						lBKDirEntity.addNewBKTransaction(lBKTransaction);
					}
				}
				/*
				 * Check that all transactions are in mirror for all BKAsset, all BKAccount
				 */
				for (BKAsset lBKAsset : lBKDirEntity.getpBKHoldingManager().getpMapBKAssetToBKHolding().keySet()) {
					BKHolding lBKHolding = lBKDirEntity.getpBKHoldingManager().getpMapBKAssetToBKHolding().get(lBKAsset);
					if (lBKHolding == null) {
						BKCom.errorCodeLogic();
					}
					if (!AMNumberTools.isZero(lBKHolding.getpQty())) {
						addpErrorMessage("The sum of the transactions should be zero as those are not physical transactions"
								+ "\nBKAsset in error= " + lBKAsset.getpName()
								+ "\nSum of all BKTransactions for this BKAsset= " + lBKHolding.getpQty());
					}
				}
			}
		}
		exitIfError();
	}

	/**
	 * 
	 */
	public final void exitIfError() {
		if (!pErrorMessage.equals("")) {
			BKCom.error(pErrorMessage);
		}
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private BKDirEntity getpOrCreateBKDirEntity(BKEntity _sBKEntity, String _sNameDirEntity) {
		BKDirEntity lBKDirEntity = pMapKeyToBKDirEntity.get(_sNameDirEntity);
		if (lBKDirEntity == null) {
			lBKDirEntity = new BKDirEntity(_sBKEntity, _sNameDirEntity, this);
			pMapKeyToBKDirEntity.put(_sNameDirEntity, lBKDirEntity);
		}
		return lBKDirEntity;
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return pDirMain;
	}

	/*
	 * Getters & Setters
	 */
	public final String getpDirMain() {
		return pDirMain;
	}
	public final void addpErrorMessage(String _spErrorMessage) {
		if (_spErrorMessage != null && !_spErrorMessage.equals("")) {
			pErrorMessage += _spErrorMessage;
		}
	}
	public final String getpErrorMessage() {
		return pErrorMessage;
	}
	public final String getpMidFix() {
		return pMidFix;
	}


}

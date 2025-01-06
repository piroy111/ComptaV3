package step02_load_transactions.objects.transaction;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import staticdata.dateloaders.BKStaticDateEarliestTransactionLoader;
import step00_freeze_transactions.BKFrozenManager;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.file.BKFile;
import step02_load_transactions.objects.file_transaction.BKTransactionFile;
import step02_load_transactions.objects.file_transaction.BKTransactionFileManager;
import step02_load_transactions.transactions_loaders.frozen.BKFrozenCapitalLoader;
import step02_load_transactions.transactions_loaders.frozen.BKFrozenTransactionLoader;
import step04_debug.abstracts.BKDebugManager;

public class BKTransactionManager {

	/*
	 * Data
	 */
	private static List<BKTransaction> pListBKTransactionSorted = new ArrayList<>();
	private static TreeMap<Integer, List<BKTransaction>> pTreeMapDateToListBKTransaction = new TreeMap<>();
	private static boolean IS_NEED_SORT = false;
	private static boolean IS_NO_MORE_BKTRANSACTION = false;
	private static BKTransactionFileManager pBKTransactionFileManager = new BKTransactionFileManager("Computation");
	
	/**
	 * 
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sQuantity
	 * @param _sPrice
	 * @param _sBKAccount
	 * @param _sComment
	 * @param _sFileNameOrigin
	 * @param _sBKIncome
	 * @return
	 */
	public static final BKTransaction createBKTransaction(int _sDate, 
			BKAsset _sBKAsset, 
			double _sQuantity, 
			double _sPrice, 
			BKAccount _sBKAccount,
			String _sComment,
			BKIncome _sBKIncome,
			BKEntity _sBKEntity,
			BKFile<?, ?> _sBKFile,
			String _sClassSender) {
		if (IS_NO_MORE_BKTRANSACTION) {
			BKCom.error("Impossible to create a new BKTtransaction because pIsNoMoreBKTransaction= true");
		}
		/*
		 * Check if the date is after the date when we stop loading the transactions
		 */
		//		if (_sDate > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
		//			BasicPrintMsg.display(null, "Ignored transaction for date= " + _sDate + "; from file= '" + _sFileNameOrigin + "'");
		//			return null;
		//		}
		if (AMNumberTools.isZero(_sQuantity) && AMNumberTools.isNaNOrZero(_sPrice)) {
			return null;
		}
		/*
		 * Check the date is not before the date of the frozen FY which we did download
		 */
		if (BKFrozenManager.IS_EXPELL_BKTRANSACTION(_sDate) && !(_sClassSender.equals(BKFrozenTransactionLoader.class.getSimpleName()) || _sClassSender.equals(BKFrozenCapitalLoader.class.getSimpleName()))) {
			BKCom.error("We cannot create a BKTransaction which is expelled by BKFrozenManager");
		}
		/*
		 * Case the BKFile is null --> it means that the BKTransaction has been created by COMPTA. then we cannot be doing a BKFrozen
		 */
		if (_sBKFile  == null) {
			if (_sDate <= BKFrozenManager.getLIST_FY_FROZEN().get(BKFrozenManager.getLIST_FY_FROZEN().size() - 1)) {
				BKCom.errorCodeLogic();
			}
		}
		/*
		 * Case BKFile is not null --> We store the origin of the BKTransaction with its BKFile and we fill a new event from BKTransaction into BKFile
		 */
		else {
			_sBKFile.declareNewEvent(_sDate);
		}
		
		/*
		 * Check the BKBar and change the weight to the one of the BKBar (this is because many times, there is an error in the far digit of the weight)  
		 */
		double lQuantity = _sQuantity;
		BKBar lBKBar = null;
		if (_sBKAsset instanceof BKAssetMetal) {
			/*
			 * Identify the BKBar
			 */
			BKAssetMetal lBKAssetMetal = (BKAssetMetal) _sBKAsset;
			lBKBar = lBKAssetMetal.getpOrCreateAndCheckBKBar(_sComment, _sQuantity, _sBKFile == null ? null : _sBKFile.getpNameFile());
			lQuantity = lBKBar.getpWeightOz() * Math.signum(_sQuantity);
			/*
			 * Crash if the BKEntity is not a vault
			 */
			if (_sBKEntity != null && _sBKEntity.getpTypeEntity() == type_entity.PHYSICAL && !_sBKEntity.getpIsVault()) {
				BKCom.error("We cannot have a BKBar which is not in a vault or at a refiner"
						+ "\nVaults are BKEntities whose name begins with '" + BKStaticConst.getBKENTITY_PREFIX_VAULTS() + "'"
						+ "\nRefiners are BKEntities whose name begins with '" + BKStaticConst.getBKENTITY_PREFIX_REFINERS() + "'"
						+ "\n"
						+ "\nBKBar in error= " + lBKBar
						+ "\nBKEntity in error= " + _sBKEntity);
			}
		}
		/*
		 * Create
		 */
		BKTransaction lBKTransaction = new BKTransaction(_sDate, _sBKAsset, lQuantity, _sPrice, _sBKAccount, _sComment, _sBKIncome, _sBKEntity, _sBKFile, _sClassSender);
		/*c
		 * Update the time of earliest transaction created
		 */
		BKStaticDateEarliestTransactionLoader.updateEarliestDate(_sDate, _sBKFile == null ? null : _sBKFile.getpTimeStamp());
		/*
		 * Add BKBar if it exists
		 */
		if (lBKBar != null) {
			lBKTransaction.setpBKBar(lBKBar);
			lBKBar.declareNewBKTransaction(lBKTransaction);
		}
		lBKTransaction.setpQuantityOrigin(_sQuantity);
		/*
		 * Get or create
		 */		
		List<BKTransaction> lListBKTransaction = pTreeMapDateToListBKTransaction.get(_sDate);
		if (lListBKTransaction == null) {
			lListBKTransaction = new ArrayList<>();
			pTreeMapDateToListBKTransaction.put(_sDate, lListBKTransaction);
		}
		/*
		 * Notify a new BKTransaction has been created
		 */
		BKDebugManager.addNewBKTransaction(lBKTransaction);
		/*
		 * Add to lists
		 */
		lListBKTransaction.add(lBKTransaction);
		pListBKTransactionSorted.add(lBKTransaction);
		IS_NEED_SORT = true;
		return lBKTransaction;
	}

	/**
	 * 
	 * @param _sReadFile
	 */
	public static List<BKTransaction> readFileBKTransaction(ReadFile _sReadFileTransactions, BKEntity _sBKEntity, String _sClassSender) {
		List<BKTransaction> lListBKTransaction = new ArrayList<>();
		/*
		 * Error messages
		 */
		String lHeader = _sReadFileTransactions.getmHeader();
		if (!lHeader.startsWith(BKStaticConst.getHEADER_FILE_TRANSACTIONS())) {
			BKCom.error("The header is incorrect"
					+ BKStaticConst.getNTAB() + "Header in file= '" + lHeader + "'"
					+ BKStaticConst.getNTAB() + "Header expected '" + BKStaticConst.getHEADER_FILE_TRANSACTIONS() + "'"
					+ BKStaticConst.getNTAB() + "File DIR in question= '" + _sReadFileTransactions.getmDir() + "'"
					+ BKStaticConst.getNTAB() + "File Name in question= '" + _sReadFileTransactions.getmNameFile() + "'");
		} else if (!_sReadFileTransactions.getmIsFileReadSuccessFully()) {
			BKCom.error("Error when reading the file '" + _sReadFileTransactions.getmDirPlusNameFile() + "'");
		}
		/*
		 * Case the file is correct
		 */
		else {
			String lFileNameOrigin = _sReadFileTransactions.getmNameFile();
			Path lPath = Paths.get(_sReadFileTransactions.getmDirPlusNameFile());
			BKTransactionFile lBKTransactionFile = pBKTransactionFileManager.getpAndCheckBKFile(lPath);
			for (List<String> lLineStr : _sReadFileTransactions.getmContentList()) {
				if (lLineStr.size() == 0 || lLineStr.get(0).equals("")) {
					continue;
				}
				/*
				 * Load line
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLineStr.get(++lIdx));
				/*
				 * Continue reading the line
				 */
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLineStr.get(++lIdx), lFileNameOrigin);
				String lComment = lLineStr.get(++lIdx);
				double lQuantity = BasicString.getDouble(lLineStr.get(++lIdx));
				double lPrice = BasicString.getDouble(lLineStr.get(++lIdx));
				BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lLineStr.get(++lIdx), lFileNameOrigin);
				BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(lLineStr.get(++lIdx), lFileNameOrigin);
				/*
				 * Suppress the ',' in the comment
				 */
				lComment = lComment.replaceAll(",", ";");
				/*
				 * Create BKTransaction and store it in the Map
				 */
				BKTransaction lBKTransaction = createBKTransaction(lDate, lBKAsset, lQuantity, lPrice, lBKAccount, lComment,
						lBKIncome, _sBKEntity, lBKTransactionFile, _sClassSender);
				if (lBKTransaction != null) {
					lListBKTransaction.add(lBKTransaction);
				}
			}
		}
		return lListBKTransaction;
	}

	/*
	 * Getters & Setters
	 */
	public static final List<BKTransaction> getpListBKTransactionSorted() {
		if (IS_NEED_SORT) {
			Collections.sort(pListBKTransactionSorted);
			IS_NEED_SORT = false;
		}
		return pListBKTransactionSorted;
	}
	public static final TreeMap<Integer, List<BKTransaction>> getpTreeMapDateToListBKTransaction() {
		return pTreeMapDateToListBKTransaction;
	}
	public static final boolean getIS_NO_MORE_BKTRANSACTION() {
		return IS_NO_MORE_BKTRANSACTION;
	}
	public static final void setIS_NO_MORE_BKTRANSACTION(boolean _sIS_NO_MORE_BKTRANSACTION) {
		IS_NO_MORE_BKTRANSACTION = _sIS_NO_MORE_BKTRANSACTION;
	}
	public static final BKTransactionFileManager getpBKTransactionFileManager() {
		return pBKTransactionFileManager;
	}


}

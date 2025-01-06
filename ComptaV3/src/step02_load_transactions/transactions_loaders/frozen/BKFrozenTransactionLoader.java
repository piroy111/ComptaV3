package step02_load_transactions.transactions_loaders.frozen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.objects.frozen_fiscal_year.BKFrozenFiscalYear;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.direntity.BKDirEntity;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.file_transaction.BKTransactionFile;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step10_launchme.BKLaunchMe;

public class BKFrozenTransactionLoader {

	public BKFrozenTransactionLoader(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		/*
		 * 
		 */
		pMapBKEntityToListBKTransactionFromFrozen = new HashMap<>();
		pBKFrozenCapitalLoader = new BKFrozenCapitalLoader(_sBKLaunchMe);
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private BKFrozenCapitalLoader pBKFrozenCapitalLoader;
	private static Map<BKEntity, List<BKTransaction>> pMapBKEntityToListBKTransactionFromFrozen;

	/**
	 * 
	 */
	public final void listFilesBKFrozenTransaction() {
		/*
		 * 
		 */
		BasicPrintMsg.displayTitle(this, "List the files of BKrozenTransaction");
		String lDir = BKStaticDir.getFREEZE_BKTRANSACTIONS();
		int lNbFilesCreated = BKTransactionManager.getpBKTransactionFileManager().getpMapKeyToBKFile().size();
		for (int lDateFYFrozen : pBKLaunchMe.getpBKFrozenManager().getpBKFrozenDateChooser().getpListDateFYFrozen()) {
			int lDateFile = BasicDateInt.getmPlusDay(lDateFYFrozen, 1);
			String lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FY_TRANSACTIONS_FREEZE();
			Path lPath = Paths.get(lDir + lNameFile);
			if (BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
				BKTransactionManager.getpBKTransactionFileManager().getpOrCreateBKFile(lPath);
			}
		}
		/*
		 * Capital
		 */
		lNbFilesCreated += pBKFrozenCapitalLoader.listFilesBKFrozenTransaction();
		/*
		 * Display - communication
		 */
		lNbFilesCreated += BKTransactionManager.getpBKTransactionFileManager().getpMapKeyToBKFile().size() - lNbFilesCreated;
		BasicPrintMsg.display(this, "Number of files BKrozenTransaction present and identified= " + lNbFilesCreated);
	}

	/**
	 * Load the BKTransactionFrozen from the file and create BKTransaction
	 */
	public final void loadBKTransactionFrozen() {
		BasicPrintMsg.displaySuperTitle(this, "Import frozen transactions");
		int lDateFYToDownload = pBKLaunchMe.getpBKFrozenManager().getpBKFrozenDateChooser().getpDateFYFrozenToDownload();
		BasicPrintMsg.display(this, "FY frozen to download= " + lDateFYToDownload);
		if (lDateFYToDownload > 0) {
			/*
			 * Retrieve file
			 */
			String lDir = BKStaticDir.getFREEZE_BKTRANSACTIONS();
			int lDateFile = BasicDateInt.getmPlusDay(lDateFYToDownload, 1);
			String lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FY_TRANSACTIONS_FREEZE();
			Path lPath = Paths.get(lDir + lNameFile);
			if (!BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
				BKCom.errorCodeLogic();
			}
			ReadFile lReadFile = new ReadFile(lPath, comReadFile.FULL_COM);
			/*
			 * Check file
			 */
			String lHeader = lReadFile.getmHeader();
			if (!lHeader.equals(BKStaticConst.getHEADER_FILE_FISCAL_YEAR_FROZEN())) {
				BasicPrintMsg.error("The header in the file is not the one expected"
						+ "\n"
						+ "\nHeader in the file= '" + lHeader + "'"
						+ "\nHeader expected   = '" + BKStaticConst.getHEADER_FILE_FISCAL_YEAR_FROZEN() + "'"
						+ "\n"
						+ "\nFile= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
			int lMonth = BasicDateInt.getmMonth(lDateFile);
			int lDay = BasicDateInt.getmDay(lDateFile);
			if (lMonth != 4 || lDay != 1) {
				BasicPrintMsg.error("The date of the file is incorrect. We can freeze transactions only at the end of fiscal year"
						+ "\n"
						+ "\nDate of file= " + lDateFile
						+ "\nExpected: 1st of April"
						+ "\n"
						+ "\nFile= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
			/*
			 * Read file and load transactions frozen
			 */
			BKFrozenFiscalYear lBKFrozenFiscalYear = pBKLaunchMe.getpBKFrozenManager().getpBKFrozenFiscalYearManager().getpOrCreateBKFrozenFiscalYear(lDateFYToDownload);
			lBKFrozenFiscalYear.setpReadFile(lReadFile);
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLineStr.get(++lIdx));
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLineStr.get(++lIdx), lReadFile.getmDirPlusNameFile());
				double lQuantity = BasicString.getDouble(lLineStr.get(++lIdx));
				double lPrice = BasicString.getDouble(lLineStr.get(++lIdx));
				String lBKAccountEmail = lLineStr.get(++lIdx);
				String lBKAccountCurrency = lLineStr.get(++lIdx);
				String lComment = lLineStr.get(++lIdx);
				String lBKIncomeStr = lLineStr.get(++lIdx);
				String lBKEntityName = lLineStr.get(++lIdx);
				String lBKEntityType = lLineStr.get(++lIdx);
				/*
				 * Check date is consistent with the file
				 */
				if (lDateFile != BasicDateInt.getmPlusDay(lDate, 1)) {
					BasicPrintMsg.error("The date of the transaction is not consistent with the file"
							+ "\nDate of the transaction= " + lDate
							+ "\nDate expected (date of the file - 1 day)= " + BasicDateInt.getmPlusDay(lDateFile, -1)
							+ "\nFile= '" + lReadFile.getmDirPlusNameFile() + "'");
				}
				/*
				 * BKAccount
				 */
				BKAssetCurrency lBKAssetCurrencyBKAccount = BKAssetManager.getpAndCheckBKAssetCurrency(lBKAccountCurrency, this);
				String lKeyBKAccount = BKAccount.getKey(lBKAccountEmail, lBKAssetCurrencyBKAccount);
				BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lKeyBKAccount, this);
				/*
				 * BKEntity
				 */
				type_entity lTypeEntity = type_entity.valueOf(lBKEntityType);
				String lKeyBKEntity = BKEntity.getKey(lBKEntityName, lTypeEntity);
				BKEntity lBKEntity = BKEntityManager.getpAndCheckBKEntityFromKey(lKeyBKEntity, this);
				/*
				 * BKIncome
				 */
				BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(lBKIncomeStr, this);
				/*
				 * BKFile
				 */
				BKTransactionFile lBKTransactionFile = BKTransactionManager.getpBKTransactionFileManager().getpOrCreateBKFile(lPath);
				/*
				 * Create BKTransaction
				 */
				BKTransaction lBKTransaction = BKTransactionManager.createBKTransaction(lDate, lBKAsset, lQuantity, lPrice, lBKAccount, lComment, lBKIncome, lBKEntity, lBKTransactionFile, this.getClass().getSimpleName());
				/*
				 * Add to the map
				 */
				getpOrCreateListTransactionFromFrozen(lBKEntity).add(lBKTransaction);
				/*
				 * Case the BKEntity is physical --> we declare the BKTransaction in order to have a proper check of balance files when we continue to read balance files
				 */
				BKDirEntity lBKDirEntity = lBKEntity.getpBKDirEntity();
				if (lBKDirEntity == null) {
					BKCom.error("There is no BKDirEntity for the given BKEntity. You must create a folder of transaction somewhere"
							+ "\nBKEntity= '" + lBKEntity.getpKey() + "'");
				}
				lBKDirEntity.addNewBKTransaction(lBKTransaction);
			}
			BasicPrintMsg.display(this, lReadFile.getmNameFile() + " loaded");
		}
		/*
		 * Load the transaction for the capital
		 */
		pBKFrozenCapitalLoader.loadBKTransactionFrozen();
	}

	/**
	 * 
	 * @param _sBKEntity
	 */
	public static final List<BKTransaction> getpOrCreateListTransactionFromFrozen(BKEntity _sBKEntity) {
		List<BKTransaction> lListBKTransaction = pMapBKEntityToListBKTransactionFromFrozen.get(_sBKEntity);
		if (lListBKTransaction == null) {
			lListBKTransaction = new ArrayList<>();
			pMapBKEntityToListBKTransactionFromFrozen.put(_sBKEntity, lListBKTransaction);
		}
		return lListBKTransaction;
	}
























}

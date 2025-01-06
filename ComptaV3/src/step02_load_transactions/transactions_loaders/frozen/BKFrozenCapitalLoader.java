package step02_load_transactions.transactions_loaders.frozen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.file_transaction.BKTransactionFile;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step10_launchme.BKLaunchMe;

public class BKFrozenCapitalLoader {

	public BKFrozenCapitalLoader(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;

	/**
	 * 
	 */
	public final int listFilesBKFrozenTransaction() {
		BasicPrintMsg.displayTitle(this, "List the files of BKrozenTransaction regarding the capital");
		String lDir = BKStaticDir.getFREEZE_CAPITAL();
		int lNbFilesCreated = BKTransactionManager.getpBKTransactionFileManager().getpMapKeyToBKFile().size();
		for (int lDateFYFrozen : pBKLaunchMe.getpBKFrozenManager().getpBKFrozenDateChooser().getpListDateFYFrozen()) {
			int lDateFile = BasicDateInt.getmPlusDay(lDateFYFrozen, 1);
			String lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FREEZE_CAPITAL();
			Path lPath = Paths.get(lDir + lNameFile);
			if (BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
				BKTransactionManager.getpBKTransactionFileManager().getpOrCreateBKFile(lPath);
			}
		}
		lNbFilesCreated = BKTransactionManager.getpBKTransactionFileManager().getpMapKeyToBKFile().size() - lNbFilesCreated;
		return lNbFilesCreated;
	}

	/**
	 * Load the BKTransactionFrozen from the file and create BKTransaction
	 */
	public final void loadBKTransactionFrozen() {
		BasicPrintMsg.displayTitle(this, "Import frozen transactions for the capital");
		int lDateFYToDownload = pBKLaunchMe.getpBKFrozenManager().getpBKFrozenDateChooser().getpDateFYFrozenToDownload();
		BasicPrintMsg.display(this, "FY frozen to download= " + lDateFYToDownload);
		if (lDateFYToDownload > 0) {
			/*
			 * Retrieve file
			 */
			String lDir = BKStaticDir.getFREEZE_CAPITAL();
			int lDateFile = lDateFYToDownload;
			String lNameFile = BasicDateInt.getmPlusDay(lDateFile, 1) + BKStaticNameFile.getSUFFIX_FREEZE_CAPITAL();
			Path lPath = Paths.get(lDir + lNameFile);
			if (!BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
				BKCom.errorCodeLogic();
			}
			ReadFile lReadFile = new ReadFile(lPath, comReadFile.FULL_COM);
			/*
			 * Check file
			 */
			int lMonth = BasicDateInt.getmMonth(lDateFile);
			int lDay = BasicDateInt.getmDay(lDateFile);
			if (lMonth != 3 || lDay != 31) {
				BasicPrintMsg.error("The date of the file is incorrect. We can freeze transactions only at the end of fiscal year"
						+ "\n"
						+ "\nDate of file= " + lDateFile
						+ "\nExpected: 31st of March"
						+ "\n"
						+ "\nFile= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
			/*
			 * Read file and load transactions frozen
			 */
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				BKAssetCurrency lBKAssetCurrency = BKAssetManager.getpAndCheckBKAssetCurrency(lLineStr.get(++lIdx), lReadFile.getmNameFile());
				double lCapital = BasicString.getInt(lLineStr.get(++lIdx));
				/*
				 * BKAccount
				 */
				BKAccount lBKAccount = BKAccountManager.getpBKAccountBunker();
				/*
				 * BKEntity
				 */
				BKEntity lBKEntity = BKEntityManager.getBKEntityTransfer();
				/*
				 * BKIncome
				 */
				BKIncome lBKIncomeCapital = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_CAPITAL(), this);
				String lComment = lBKIncomeCapital.getpName();
				BKIncome lBKIncomeHoldingEndOfYear = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_HOLDING_FROZEN(), this);
				/*
				 * BKFile
				 */
				BKTransactionFile lBKTransactionFile = BKTransactionManager.getpBKTransactionFileManager().getpOrCreateBKFile(lPath);
				/*
				 * Create BKTransaction
				 */
				BKTransaction lBKTransactionCapital = BKTransactionManager.createBKTransaction(lDateFile, lBKAssetCurrency, lCapital, Double.NaN, lBKAccount, lComment, lBKIncomeCapital, lBKEntity, lBKTransactionFile, this.getClass().getSimpleName());
				BKTransaction lBKTransactionMirror = BKTransactionManager.createBKTransaction(lDateFile, lBKAssetCurrency, -lCapital, Double.NaN, lBKAccount, lComment, lBKIncomeHoldingEndOfYear, lBKEntity, lBKTransactionFile, this.getClass().getSimpleName());
				/*
				 * Add to the map
				 */
				BKFrozenTransactionLoader.getpOrCreateListTransactionFromFrozen(lBKEntity).add(lBKTransactionCapital);
				BKFrozenTransactionLoader.getpOrCreateListTransactionFromFrozen(lBKEntity).add(lBKTransactionMirror);
			}
		}
	}
	
}

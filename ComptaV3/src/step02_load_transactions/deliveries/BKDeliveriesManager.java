package step02_load_transactions.deliveries;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.file_delivery.BKDeliveryFile;
import step02_load_transactions.objects.file_delivery.BKDeliveryFileManager;
import step02_load_transactions.objects.transaction.BKTransactionManager;

public class BKDeliveriesManager {

	public BKDeliveriesManager() {
		/*
		 * Instantiate
		 */
		pListBKDeliveryDir = new ArrayList<>();
		pBKDeliveryFileManager = new BKDeliveryFileManager("Computation");
		/*
		 * Initiate - declare the directories in which there are the files deliveries
		 */
		pListBKDeliveryDir.add(new BKDeliveryDir(BKStaticDir.getDELIVERIES(), BKStaticNameFile.getSUFFIX_DELIVERIES()));
		pListBKDeliveryDir.add(new BKDeliveryDir(BKStaticDir.getMANUAL_SALES(), BKStaticNameFile.getSUFFIX_DELIVERIES()));
	}
	
	
	/*
	 * Data
	 */
	private boolean pIsDeliveriesLoaded;
	private List<BKDeliveryDir> pListBKDeliveryDir;
	private BKDeliveryFileManager pBKDeliveryFileManager;
	
	/**
	 * 
	 */
	public final void listFilesBKDelivery() {
		BasicPrintMsg.displayTitle(this, "List the files of delivery");
		for (BKDeliveryDir lBKDeliveryDir : pListBKDeliveryDir) {
			BasicPrintMsg.display(this, "Check dir= '" + lBKDeliveryDir.getpDir() + "'");
			pBKDeliveryFileManager.createAllBKFileInDirAndSubDir(Paths.get(lBKDeliveryDir.getpDir()), lBKDeliveryDir.getpSuffix());
		}
		BasicPrintMsg.display(this, "Number of files delivery found= " + pBKDeliveryFileManager.getpMapKeyToBKFile().size());
	}
	
	/**
	 * 
	 */
	public void checkAndLoadStatus() {
		if (!pIsDeliveriesLoaded) {
			pIsDeliveriesLoaded = true;
			BasicPrintMsg.displayTitle(this, "Load files delivery and update BKBar status");			
			/*
			 * Read files
			 */
			for (BKDeliveryDir lBKDeliveryDir : pListBKDeliveryDir) {
				readFiles(lBKDeliveryDir.getpDir(), lBKDeliveryDir.getpSuffix());
			}
			/*
			 * 
			 */
			BasicPrintMsg.display(this, "All done");
		}
	}

	/**
	 * 
	 * @param _sDir
	 * @param _sSuffix
	 */
	private void readFiles(String _sDir, String _sSuffix) {
		BasicDir lBasicDir = new BasicDir(_sDir, _sSuffix);
		for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
			/*
			 * Skip the file if BKFrozenManager expels it
			 */
			if (BKFrozenManager.IS_EXPELL_BKTRANSACTION(lBasicFile.getmDate())) {
				continue;
			}
			/*
			 * Otherwise, we read the file and we take into account the deliveries
			 */
			for (List<String> lLineStr : lBasicFile.getmReadFile().getmContentList()) {
				String lFileNameOrigin = lBasicFile.getmReadFile().getmDirPlusNameFile();
				/*
				 * Load line
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLineStr.get(++lIdx));
				String lID = lLineStr.get(++lIdx);
				BKAssetMetal lBKAssetMetal = BKAssetManager.getpAndCheckBKAssetMetal(lLineStr.get(++lIdx), lFileNameOrigin);
				double lWeight = BasicString.getDouble(lLineStr.get(++lIdx));
				BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lLineStr.get(++lIdx), lFileNameOrigin);
				BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(lLineStr.get(++lIdx), lFileNameOrigin);
				/*
				 * Get BKBar
				 */
				BKBar lBKBar = lBKAssetMetal.getpOrCreateAndCheckBKBar(lID, lWeight, lFileNameOrigin);
				/*
				 * Forbid deliveries of FUTURE bars
				 */
				if (lBKBar.getpIsBarFuture()) {
					BKCom.error("a FUTURE bar cannot be delivered"
							+ "\nFile in error= '" + lFileNameOrigin + "'");
				}
				/*
				 * Check that the bar belongs to the account at the moment of the delivery
				 */
				if (lDate > BKStaticConst.getDATE_START_COMPTA_V3() && !lBKAccount.equals(lBKBar.getpBKAccountOwner(lDate))) {
					BKCom.error("A BKBar is delivered but the owner in the conf file does not match the actual owner of the bar"
							+ "\nBKBar= " + lBKBar.getpID()
							+ "\nDate of delivery= " + lDate
							+ "\nOwner as per the file delivery= '" + lBKAccount.getpEmail() + "'"
							+ "\nOwner as per the Compta= " + lBKBar.getpBKAccountOwner(lDate)
							+ "\nFile delivery= '" + lBasicFile.getmReadFile().getmDirPlusNameFile() + "'");
				}
				/*
				 * Declare delivery
				 */
				lBKBar.declareDelivery(lDate);
				/*
				 * Get the delivery file
				 */
				BKDeliveryFile lBKDeliveryFile = pBKDeliveryFileManager.getpAndCheckBKFile(lBasicFile.getmReadFile().getmPath());
				/*
				 * Get the BKEntity physical --> the BKBar must have been declared somewhere before we deliver it
				 */
				BKEntity lBKEntity = lBKBar.getpBKEntity(lDate);
				/*
				 * Create a transaction as a sales at zero, so the bar is not counted in the inventory (holding + NAV)
				 */
				BKTransactionManager.createBKTransaction(lDate, lBKAssetMetal, lWeight, 
						Double.NaN, lBKAccount, lID, lBKIncome, lBKEntity, lBKDeliveryFile, this.getClass().getSimpleName());
			}
			BasicPrintMsg.display(this, "Read file '" + lBasicFile.getmNameFile() + "'");
		}
		BasicPrintMsg.display(this, "Read " + lBasicDir.getmListDate().size() + " files");
	}

	

	/*
	 * Setters & Getters
	 */
	public final BKDeliveryFileManager getpBKDeliveryFileManager() {
		return pBKDeliveryFileManager;
	}
}

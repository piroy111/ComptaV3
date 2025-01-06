package step02_load_transactions.interactivebrokers.reconcilewithnav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.IBManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step02_load_transactions.transactions_loaders.frozen.BKFrozenTransactionLoader;
import step02_load_transactions.transactions_loaders.loaders_physical.BKPhysicalManager;

public class BKIBManager {

	public static void main(String[] _sArgs) {
		IBManager lIBManager = new IBManager();
		lIBManager.run();
		new BKIBManager(lIBManager).run();
	}
	
	
	public BKIBManager(IBManager _sIBManager) {
		pIBManager = _sIBManager;
		/*
		 * Initiate
		 */
		pBKIBCheckCash = new BKIBCheckCash(this);
		pBKIBCheckMarkToMarket = new BKIBCheckMarkToMarketAndNAV(this);
		pBKIBLoadIBFiles = new BKIBLoadIBFiles(this);
	}

	/*
	 * Data
	 */
	private IBManager pIBManager;
	private Map<String, List<BKTransaction>> pMapCommentToListBKTransaction;
	private BKIBCheckCash pBKIBCheckCash;
	private BKIBCheckMarkToMarketAndNAV pBKIBCheckMarkToMarket;
	private BKIBLoadIBFiles pBKIBLoadIBFiles;

	/**
	 * Check all the BKTransactions give back the NAV of the Interactive Brokers report
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "IB: Check all the BKTransactions give back the NAV of the Interactive Brokers report");
		/*
		 * Load
		 */
		loadListBKTransaction();
		pBKIBLoadIBFiles.loadIBReports();
		/*
		 * Check first the simple cash flows. It would allow to detect obvious mistakes
		 */
		pBKIBCheckCash.run();
		/*
		 * Check the Mark to market and the NAV: this is a more complex check 
		 */		
		pBKIBCheckMarkToMarket.run();
	}

	/**
	 * 
	 */
	private void loadListBKTransaction() {
		/*
		 * Get DIR and suffix
		 */
		String lDirTransactions = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL() 
				+ BKStaticConst.getBKENTITY_IB() + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS();
		String lMidFix = BKPhysicalManager.getMidFix(BKPhysicalManager.class.getSimpleName());
		String lSuffixTransactions = "_" + BKStaticConst.getBKENTITY_IB()
				+ "_" + lMidFix
				+ BKStaticNameFile.getSUFFIX_TRANSACTIONS();
		/*
		 * Check that the files are well written
		 */
		BasicFichiers.checkAllFilesWrittenWithSuffix(lDirTransactions, lSuffixTransactions);
		/*
		 * BKEntity for all new transactions created
		 */
		BKEntity lBKEntity = BKEntityManager.getpAndCheckBKEntityFromName(BKStaticConst.getBKENTITY_IB(), this);
		/*
		 * Load all transactions and save them in a Map for future reconciliation
		 */
		BasicDir lBasicDir = new BasicDir(lDirTransactions, lSuffixTransactions);
		List<BKTransaction> lListBKTransactionAll = new ArrayList<BKTransaction>();
		for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
			if (BKFrozenManager.IS_EXPELL_BKTRANSACTION(lBasicFile.getmDate())) {
				continue;
			}
			lListBKTransactionAll.addAll(BKTransactionManager.readFileBKTransaction(lBasicFile.getmReadFile(), lBKEntity, this.getClass().getSimpleName()));
		}
		/*
		 * Add the BKTransaction from frozen
		 */
		lListBKTransactionAll.addAll(BKFrozenTransactionLoader.getpOrCreateListTransactionFromFrozen(lBKEntity));
		/*
		 * Create Map
		 */
		pMapCommentToListBKTransaction = new HashMap<String, List<BKTransaction>>();
		pMapCommentToListBKTransaction.put(IBStatic.getCOMMENT_MINI_GOLD(), new ArrayList<>());
		for (BKTransaction lBKTransaction : lListBKTransactionAll) {
			String lComment = lBKTransaction.getpComment().split(";")[0];
			List<BKTransaction> lListBKTransaction = pMapCommentToListBKTransaction.get(lComment);
			if (lListBKTransaction == null) {
				lListBKTransaction = new ArrayList<>();
				pMapCommentToListBKTransaction.put(lComment, lListBKTransaction);
			}
			lListBKTransaction.add(lBKTransaction);
		}
//		/*
//		 * Write file in debug
//		 */
//		String lDir = StaticDir.getOUTPUT_DEBUG();
//		String lNameFile = IBStatic.getNAME_FILE_DEBUG_BKTRANSACTION();
//		List<String> lListLineToWrite = new ArrayList<>();
//		for (BKTransaction lBKTransaction : lListBKTransactionAll) {
//			String lLine = lBKTransaction.getpDate()
//					+ "," + lBKTransaction.getpBKAsset()
//					+ "," + lBKTransaction.getpQuantity()
//					+ "," + lBKTransaction.getpBKPrice()
//					+ "," + lBKTransaction.getpComment()
//					+ "," + lBKTransaction.getpBKIncome()
//					+ "," + lBKTransaction.getpValueUSD()
//					+ "," + lBKTransaction.getpFileNameOrigin()
//					+ "," + lBKTransaction.getpBKAccount();
//			lListLineToWrite.add(lLine);
//		}
//		String lHeader = "Date,Asset,Quantity,Price,Comment,BKIncome,ValueUSD,FileNameOrigin,BKAccount";
//		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
//		BasicPrintMsg.display(this, "File debug written= " + lDir + lNameFile);
	}

	/*
	 * Getters & Setters
	 */
	public final Map<String, List<BKTransaction>> getpMapCommentToListBKTransaction() {
		return pMapCommentToListBKTransaction;
	}
	public final BKIBCheckCash getpBKIBCheckCash() {
		return pBKIBCheckCash;
	}
	public final IBManager getpIBManager() {
		return pIBManager;
	}
	public final BKIBLoadIBFiles getpBKIBLoadIBFiles() {
		return pBKIBLoadIBFiles;
	}












}

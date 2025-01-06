package step00_freeze_transactions.objects.frozen_fiscal_year;

import java.util.HashMap;
import java.util.Map;

import basicmethods.ReadFile;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.objects.frozen_transaction.BKFrozenTransaction;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.interactivebrokers.frozen.IBFrozen;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKFrozenFiscalYear {

	
	protected BKFrozenFiscalYear(int _sDate, BKFrozenFiscalYearManager _sBKFrozenFiscalYearManager) {
		pDate = _sDate;
		pBKFrozenFiscalYearManager = _sBKFrozenFiscalYearManager;
		/*
		 * 
		 */
		pMapKeyToBKFrozenTransaction = new HashMap<>();
		
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private BKFrozenFiscalYearManager pBKFrozenFiscalYearManager;
	private Map<String, BKFrozenTransaction> pMapKeyToBKFrozenTransaction;
	private ReadFile pReadFile;

	/**
	 * 
	 * @param _sBKAsset
	 * @param _sBKAccount
	 * @param _sComment
	 * @param _sBKEntity
	 * @return
	 */
	public final BKFrozenTransaction getpOrCreateBKFrozenTransaction(BKAsset _sBKAsset, BKAccount _sBKAccount, String _sComment, BKEntity _sBKEntity) {
		String lBKAccountEmail = _sBKAccount.getpEmail();
		String lBKAccountCurrency = _sBKAccount.getpBKAssetCurrency().getpName();
		String lBKEntityName = _sBKEntity.getpName();
		String lBKEntityType = _sBKEntity.getpTypeEntity().toString();
		return getpOrCreateBKFrozenTransaction(_sBKAsset, lBKAccountEmail, lBKAccountCurrency, _sComment, lBKEntityName, lBKEntityType);
	}
			
	
	/**
	 * 
	 * @param _sBKAssetStr
	 * @param _sQuantity
	 * @param _sPrice
	 * @param _sBKAccountEmail
	 * @param _sBKAccountCurrency
	 * @param _sComment
	 * @param _sBKIncomeStr
	 * @param _sBKEntityName
	 * @param _sBKEntityIssuer
	 * @param _sBKEntityType
	 * @return
	 */
	public final BKFrozenTransaction getpOrCreateBKFrozenTransaction(
			BKAsset _sBKAsset,
			String _sBKAccountEmail,
			String _sBKAccountCurrency,
			String _sComment,
			String _sBKEntityName,
			String _sBKEntityType) {
		String lKey = BKFrozenTransaction.getKey(pDate, _sBKAsset.getpName(), _sBKAccountEmail, _sBKAccountCurrency, _sComment, _sBKEntityName, _sBKEntityType);
		BKFrozenTransaction lBKFrozenTransaction = pMapKeyToBKFrozenTransaction.get(lKey);
		if (lBKFrozenTransaction == null) {
			/*
			 * Comment remains the same if the asset is a metal, otherwise it gets standardised
			 */
			String lComment;
			if (BKAssetManager.getpIsPhysicalMetal(_sBKAsset)) {
				lComment = _sComment;
			} else if (_sBKEntityName.equals(BKStaticConst.getBKENTITY_IB())) {
				lComment = IBFrozen.getpCommentFromBKAsset(_sBKAsset);
			} else {
				lComment = "Holding as of " + pDate;
			}
			/*
			 * Create and store in the map
			 */
			lBKFrozenTransaction = new BKFrozenTransaction(this, _sBKAsset.getpName(), _sBKAccountEmail, _sBKAccountCurrency, lComment, _sBKEntityName, _sBKEntityType);
			pMapKeyToBKFrozenTransaction.put(lKey, lBKFrozenTransaction);
		}
		return lBKFrozenTransaction;
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void addNewBKTransaction(BKTransaction _sBKTransaction) {
		/*
		 * Get key
		 */
		BKAsset lBKAsset = _sBKTransaction.getpBKAsset();
		String lBKAccountEmail = _sBKTransaction.getpBKAccount().getpEmail();
		String lBKAccountCurrency = _sBKTransaction.getpBKAccount().getpBKAssetCurrency().getpName();
		String lComment = _sBKTransaction.getpComment();
		String lBKEntityName = _sBKTransaction.getpBKEntity().getpName();
		String lBKEntityType = _sBKTransaction.getpBKEntity().getpTypeEntity().toString();
		/*
		 * Get existing frozen transaction and add the BKTransaction
		 */
		BKFrozenTransaction lBKFrozenTransaction = getpOrCreateBKFrozenTransaction(lBKAsset, lBKAccountEmail, lBKAccountCurrency, lComment, lBKEntityName, lBKEntityType);
		lBKFrozenTransaction.addBKTransaction(_sBKTransaction);
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final BKFrozenFiscalYearManager getpBKFrozenFiscalYearManager() {
		return pBKFrozenFiscalYearManager;
	}
	public final Map<String, BKFrozenTransaction> getpMapKeyToBKFrozenTransaction() {
		return pMapKeyToBKFrozenTransaction;
	}
	public final ReadFile getpReadFile() {
		return pReadFile;
	}
	public final void setpReadFile(ReadFile pReadFile) {
		this.pReadFile = pReadFile;
	}
	
	
	
	
	
}

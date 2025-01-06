package step00_freeze_transactions.objects.frozen_transaction;

import basicmethods.AMDebug;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.objects.frozen_fiscal_year.BKFrozenFiscalYear;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKFrozenTransaction {

	public BKFrozenTransaction(BKFrozenFiscalYear _sBKFrozenFiscalYear,
			String _sBKAssetStr,
			String _sBKAccountEmail,
			String _sBKAccountCurrency,
			String _sComment,
			String _sBKEntityName,
			String _sBKEntityType) {
		pBKFrozenFiscalYear = _sBKFrozenFiscalYear;
		pBKAssetStr = _sBKAssetStr;
		pBKAccountEmail = _sBKAccountEmail;
		pBKAccountCurrency = _sBKAccountCurrency;
		pComment = _sComment;
		pBKEntityName = _sBKEntityName;
		pBKEntityType = _sBKEntityType;
		/*
		 * 
		 */
		pBKIncomeStr = BKStaticConst.getBKINCOME_HOLDING_FROZEN();
		pDate = pBKFrozenFiscalYear.getpDate();
	}
	
	/*
	 * intrinsic
	 */
	private BKFrozenFiscalYear pBKFrozenFiscalYear;
	private int pDate;
	private String pBKAssetStr;
	private String pBKAccountEmail;
	private String pBKAccountCurrency;
	private String pComment;
	private String pBKIncomeStr;
	private String pBKEntityName;
	private String pBKEntityType;
	/*
	 * Data
	 */
	private double pQuantity;
	private double pPrice;
	private double pProductQuantityPrice;

	/**
	 * 
	 * @return
	 */
	public static String getKey(int _sDate,
			String _sBKAssetStr,
			String _sBKAccountEmail,
			String _sBKAccountCurrency,
			String _sComment,
			String _sBKEntityName,
			String _sBKEntityType) {
		return _sDate + ";;" + _sBKAssetStr + ";;" + _sBKAccountEmail + ";;" + _sBKAccountCurrency + ";;" + _sComment + ";;" + _sBKEntityName + ";;" + _sBKEntityType;
	}

	/**
	 * 
	 * @param _sQuantity
	 * @param _sPrice
	 */
	public final void addBKTransaction(BKTransaction _sBKTransaction) {
		addQuantityPrice(_sBKTransaction.getpQuantity(), _sBKTransaction.getpPrice());
	}

	/**
	 * 
	 * @param _sQuantity
	 * @param _sPrice
	 */
	public final void addQuantityPrice(double _sQuantity, double _sPrice) {
		pQuantity += _sQuantity;
		pProductQuantityPrice += _sQuantity * _sPrice;
		pPrice = pProductQuantityPrice / pQuantity;
	}
	
	/**
	 * 
	 */
	public final void reset() {
		pQuantity = 0;
		pProductQuantityPrice = 0;
		pPrice = 0;
	}
	
	/**
	 * 
	 * @param _sComment
	 */
	public final void addNewComment(String _sComment) {
		pComment = BasicPrintMsg.addErrorMessage(pComment, _sComment, true);
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final String getpBKAssetStr() {
		return pBKAssetStr;
	}
	public final double getpQuantity() {
		return pQuantity;
	}
	public final double getpPrice() {
		return pPrice;
	}
	public final String getpBKAccountEmail() {
		return pBKAccountEmail;
	}
	public final String getpBKAccountCurrency() {
		return pBKAccountCurrency;
	}
	public final String getpComment() {
		return pComment;
	}
	public final String getpBKIncomeStr() {
		return pBKIncomeStr;
	}
	public final String getpBKEntityName() {
		return pBKEntityName;
	}
	public final String getpBKEntityType() {
		return pBKEntityType;
	}
	public final BKFrozenFiscalYear getpBKFrozenFiscalYear() {
		return pBKFrozenFiscalYear;
	}
	public final double getpProductQuantityPrice() {
		return pProductQuantityPrice;
	}
	public final void setpBKIncomeStr(String pBKIncomeStr) {
		this.pBKIncomeStr = pBKIncomeStr;
	}
	
	
}

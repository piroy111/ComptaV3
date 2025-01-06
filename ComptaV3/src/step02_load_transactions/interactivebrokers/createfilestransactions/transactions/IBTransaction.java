package step02_load_transactions.interactivebrokers.createfilestransactions.transactions;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;

public class IBTransaction implements Comparable<IBTransaction> {

	protected IBTransaction(int _sDate, String _sComment, 
			BKAsset _sBKAsset, double _sAmount, double _sPrice,  String _sBKIncome) {
		pDate = _sDate;
		pComment = _sComment;
		pBKAsset = _sBKAsset;
		pAmount = _sAmount;
		pPrice = _sPrice;
		/*
		 * Add to BKIncome
		 */
		pBKIncome = BKIncomeManager.getpAndCheckBKIncome(_sBKIncome, this);
		/*
		 * Constant
		 */
		pBKAccount = BKAccountManager.getpBKAccountBunker();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private String pComment;
	private BKAsset pBKAsset;
	private double pAmount;
	private double pPrice;
	private BKAccount pBKAccount;
	private BKIncome pBKIncome;
	
	/**
	 * Classic toString
	 */
	public final String toString() {
		return "pDate= " + pDate
				+ "; pComment= " + pComment
				+ "; pBKAsset= " + pBKAsset
				+ "; pAmount= " + pAmount
				+ "; pPrice= " + pPrice
				+ "; pBKIncome= " + pBKIncome;
	}
	
	@Override public int compareTo(IBTransaction _sIBTransaction) {
		return Integer.compare(pDate, _sIBTransaction.getpDate());
	}
	
	/**
	 * 
	 * @return
	 */
	public final int getpMultiplier() {
		if (pBKAsset.getpName().equals(BKStaticConst.getNAME_BKASSET_OIL())) {
			return IBStatic.getOIL_MULTIPLIER();
		} else if (pBKAsset.getpName().equals(IBStatic.getMINI_GOLD())) {
			return IBStatic.getMINI_GOLD_MULTIPLIER();
		} else {
			return 1;
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final String getpComment() {
		return pComment;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpAmount() {
		return pAmount;
	}
	public final double getpPrice() {
		return pPrice;
	}
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final BKIncome getpBKIncome() {
		return pBKIncome;
	}

	
	
	
}

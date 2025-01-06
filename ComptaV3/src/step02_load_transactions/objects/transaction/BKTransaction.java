package step02_load_transactions.objects.transaction;

import java.nio.file.Path;

import basicmethods.AMDebug;
import basicmethods.AMNumberTools;
import basicmethods.BasicTime;
import staticdata.com.BKCom;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.income.BKIncome;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.file.BKFile;

public class BKTransaction implements Comparable<BKTransaction> {

	/**
	 * 
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sQuantity
	 * @param _sPrice
	 * @param _sBKAccount
	 * @param _sFileNameOrigin
	 * @param _sBKIncome
	 */
	protected BKTransaction(int _sDate, 
			BKAsset _sBKAsset, 
			double _sQuantity, 
			double _sPrice, 
			BKAccount _sBKAccount,
			String _sComment,
			BKIncome _sBKIncome,
			BKEntity _sBKEntity,
			BKFile<?,?> _sBKFile,
			String _sClassSender) {
		pDate = _sDate;
		pBKAsset = _sBKAsset;
		pQuantity = _sQuantity;
		pPrice = _sPrice;
		pBKAccount = _sBKAccount;
		pComment = _sComment;
		pBKFile = _sBKFile;
		pBKIncome = _sBKIncome;
		pBKEntity = _sBKEntity;
		pClassSender = _sClassSender;
		/*
		 * 
		 */
		if (pBKEntity == null || pBKIncome == null) {
			BKCom.errorCodeLogic();
		}
	}

	/*
	 * Data
	 */
	private int pDate;
	private BKAsset pBKAsset;
	private String pComment;
	private double pQuantity;
	private double pQuantityOrigin;	// in case we changed the quantity (for a BKBar for example, so it matches the exact weight of the BKBar)
	private double pPrice;
	private BKAccount pBKAccount;
	private BKIncome pBKIncome;
	private BKEntity pBKEntity;	
	private BKBar pBKBar;
	private BKFile<?,?> pBKFile;
	private String pClassSender;

	/**
	 * 
	 */
	public String toString() {
		return "Date= " + pDate 
				+ "; BKAsset= " + pBKAsset.getpName() 
				+ "; Comment= " + pComment
				+ "; Qty= " + pQuantity 
				+ "; Price= " + pPrice
				+ "; BKAccount= " + pBKAccount.getpEmail()
				+ "; BKIncome= " + pBKIncome.getpName()
				+ "; BKEntity= " + pBKEntity.getpName();
	}

	/**
	 * @return tells if this BKTransaction is related to _sBKBar
	 * @param _sBKBar
	 */
	public final boolean getpIsOfBKBar(BKBar _sBKBar) {
		return _sBKBar.getpBKAssetMetal().equals(pBKAsset)
				&& pComment.equals(_sBKBar.getpID());
	}

	/**
	 * 
	 * @return
	 */
	public static String getHeaderWriteInFile() {
		return "Date,BKAsset,Comment,Quantity,Price,BKAccount,FileNameOrigin,BKIncome,BKIncomeGroup,BKEntity"
				+ ",OWner (of and if BKBar),BKEntity (of and if BKBar),Date of last change of file origin in Windows";
	}

	/**
	 * 
	 * @return
	 */
	public final String getpLineWriteInFile() {
		String lLine = pDate 
				+ "," + pBKAsset
				+ "," + pComment
				+ "," + pQuantity
				+ "," + pPrice
				+ "," + pBKAccount
				+ "," + getpOrigin()
				+ "," + pBKIncome
				+ "," + pBKIncome.getpBKIncomeGroup()
				+ "," + pBKEntity;
		if (pBKBar != null) {
			lLine += "," + pBKBar.getpBKAccountOwner(pDate)
				+ "," + pBKBar.getpBKEntity(pDate);
		} else {
			lLine += ",-,-";
		}
		lLine += "," + BasicTime.getDateFromTimeStamp(getpTimeStampFileOrigin());
		return lLine;
	}

	@Override public int compareTo(BKTransaction _sBKTransaction) {
		return Integer.compare(pDate, _sBKTransaction.getpDate());
	}

	/**
	 * 
	 * @return
	 */
	public final String getpLineForErrorMsg() {
		return "\nBKTransaction in error= "
				+ "\n    Date= " + pDate 
				+ "\n    BKAsset= " + pBKAsset.getpName()
				+ "\n    Comment= " + pComment
				+ "\n    Qty= " + pQuantity 
				+ "\n    Price= " + pPrice
				+ "\n    BKAccount= " + pBKAccount.getpEmail()
				+ "\n    BKIncome= " + pBKIncome.getpName()
				+ "\n	 BKEntity= " + pBKEntity.getpName()
				+ "\n    FileNameOrigin= '" + getpOrigin() + "'"
				;		
	}

	/**
	 * 
	 * @return
	 */
	public final long getpTimeStampFileOrigin() {
		if (pBKFile == null) {
			return -1;
		} else {
			return pBKFile.getpTimeStamp();
		}
	}

	/**
	 * 
	 * @return
	 */
	public final String getpOrigin() {
		if (pBKFile == null) {
			return pClassSender;
		} else {
			return pBKFile.getpNameFile();
		}
	}

	/**
	 * 
	 * @return
	 */
	public final Path getpPathBKTransactionFile() {
		if (pBKFile == null) {
			return null;
		} else {
			return pBKFile.getpPath();
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpQuantity() {
		return pQuantity;
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
	public final String getpComment() {
		return pComment;
	}
	public final BKBar getpBKBar() {
		return pBKBar;
	}
	protected final void setpBKBar(BKBar _sPBKBar) {
		pBKBar = _sPBKBar;
	}
	protected final void setpQuantity(double _sPQuantity) {
		pQuantity = _sPQuantity;
	}
	public final double getpQuantityOrigin() {
		return pQuantityOrigin;
	}
	protected final void setpQuantityOrigin(double _sPQuantityOrigin) {
		pQuantityOrigin = _sPQuantityOrigin;
	}
	public final BKEntity getpBKEntity() {
		return pBKEntity;
	}
	public final String getpClassSender() {
		return pClassSender;
	}
	public final BKFile<?, ?> getpBKFile() {
		return pBKFile;
	}


}

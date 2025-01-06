package step02_load_transactions.objects.direntity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step00_freeze_transactions.BKFrozenManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.balances.BKBalanceFile;
import step02_load_transactions.objects.balances.BKBalanceFileAbstract;
import step02_load_transactions.objects.balances.BKNAVFile;
import step02_load_transactions.objects.direntity.BKDirEntity.typeFile;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;

public class BKEntityFilesDate implements Comparable<BKEntityFilesDate> {

	protected BKEntityFilesDate(int _sDate, BKDirEntity _sBKDirEntity) {
		pDate = _sDate;
		pBKDirEntity = _sBKDirEntity;
		/*
		 * 
		 */
		pErrorMessage = "";
		pListFileNameSupportingDoc = new ArrayList<>();
	}

	/*
	 * Data
	 */
	private BKDirEntity pBKDirEntity;
	private int pDate;
	private List<String> pListFileNameSupportingDoc;
	private String pNameFileTransactions;
	private ReadFile pReadFileTransactions;
	private ReadFile pReadFileBalances;
	private String pErrorMessage;
	private BKBalanceFileAbstract pBKBalanceFileAbstract;
	private Path pPathFileTransactions;
	private Path pPathFileBalances;
	
	/**
	 * Read files from the Path given previously by BKDirEntity.manageDirEntity
	 */
	public final void loadFiles() {
		if (pReadFileTransactions == null) {
			pReadFileTransactions = new ReadFile(pPathFileTransactions, comReadFile.FULL_COM);
			pReadFileBalances = new ReadFile(pPathFileBalances, comReadFile.FULL_COM);
		}
	}
	
	/**
	 * @return true if the file transactions contain only transactions with dates outside of the range defined by BKFrozenManager<br>
	 * Presuppose that a file of transactions cannot have 2 different transactions on 2 different fiscal years<br>
	 */
	public final boolean getpIsExpeledByFrozen() {
		return BKFrozenManager.IS_EXPELL_BKTRANSACTION(pDate);
	}
	
	/**
	 * 
	 */
	public final void checkAllFilesAreHere() {
		/*
		 * file Balance
		 */
		if (pBKDirEntity.getpDirBalances() != null && pReadFileBalances == null) {
			pErrorMessage += "\nMissing balance file"
					+ pBKDirEntity.getpErrorMsgLocation(typeFile.BALANCES)
					+ BKStaticConst.getNTAB() + "Missing date= " + pDate;
		}
		/*
		 * File support
		 */
		if (pBKDirEntity.getpDirDocs() != null) {
			if (pListFileNameSupportingDoc.size() == 0) {
				pErrorMessage += "\nMissing supporting documents file"
						+ pBKDirEntity.getpErrorMsgLocation(typeFile.DOCS)
						+ BKStaticConst.getNTAB() + "Missing date= " + pDate;
			}
		}
		/*
		 * File transactions
		 */
		if (pReadFileTransactions == null) {
			pErrorMessage += "\nMissing transactions file"
					+ pBKDirEntity.getpErrorMsgLocation(typeFile.TRANSACTIONS)
					+ BKStaticConst.getNTAB() + "Missing date= " + pDate;
		}
	}
	
	/**
	 * 
	 */
	public final void readFileBalances() {
		if (pBKDirEntity.getpDirBalances() == null) {
			return;
		}
		/*
		 * If error
		 */
		if (pBKDirEntity.getpDirBalances() != null && pPathFileBalances == null) {
			pErrorMessage += "\nMissing balance file"
					+ pBKDirEntity.getpErrorMsgLocation(typeFile.BALANCES)
					+ BKStaticConst.getNTAB() + "Missing date= " + pDate;
		}
		/*
		 * Normal case -> we read the balance file
		 */
		else {
			if (pReadFileBalances == null) {
				pReadFileBalances = new ReadFile(pPathFileBalances, comReadFile.FULL_COM);
			}
			BasicPrintMsg.display(this, "Reading balances from " + pReadFileBalances.getmDirPlusNameFile());
			String lHeader = pReadFileBalances.getmHeader();
			/*
			 * Case of a file written with the full data
			 */
			if (BKStaticConst.getHEADER_BALANCE_EXPECTED_FULL().equals(lHeader)) {
				BKBalanceFile lBKBalanceFile = new BKBalanceFile(this);
				for (List<String> lLineStr : pReadFileBalances.getmContentList()) {
					if (lLineStr.size() == 0 || lLineStr.get(0).equals("")) {
						continue;
					}
					/*
					 * Load line
					 */
					int lIdx = -1;
					BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLineStr.get(++lIdx), pReadFileBalances.getmDirPlusNameFile());
					double lQtyPos = BasicString.getDouble(lLineStr.get(++lIdx));
					double lPriceExecPos = BasicString.getDouble(lLineStr.get(++lIdx));
					double lQtyNeg = BasicString.getDouble(lLineStr.get(++lIdx));
					double lPriceExecNeg = BasicString.getDouble(lLineStr.get(++lIdx));
					/*
					 * Fill Map
					 */
					lBKBalanceFile.addNewData(lBKAsset, lQtyPos, lPriceExecPos, pReadFileBalances.getmDirPlusNameFile());
					lBKBalanceFile.addNewData(lBKAsset, lQtyNeg, lPriceExecNeg, pReadFileBalances.getmDirPlusNameFile());
				}
				pBKBalanceFileAbstract = lBKBalanceFile;
			}
			/*
			 * Case the file is written with simplified data (not positive and negative)
			 */
			else if (BKStaticConst.getHEADER_BALANCE_EXPECTED_SIMPLIFIED().equals(lHeader)) {
				BKBalanceFile lBKBalanceFile = new BKBalanceFile(this);
				for (List<String> lLineStr : pReadFileBalances.getmContentList()) {
					/*
					 * Load line
					 */
					int lIdx = -1;
					BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLineStr.get(++lIdx), pReadFileBalances.getmDirPlusNameFile());
					double lQty = BasicString.getDouble(lLineStr.get(++lIdx));
					double lPriceExec = BasicString.getDouble(lLineStr.get(++lIdx));
					/*
					 * Case the lQty is 0 and the price executed is not NaN --> we need to create 2 transactions
					 */
					if (AMNumberTools.isZero(lQty) && !AMNumberTools.isNaNOrZero(lPriceExec)) {
						lBKBalanceFile.addNewData(lBKAsset, 1, lPriceExec, pReadFileBalances.getmDirPlusNameFile());
						lBKBalanceFile.addNewData(lBKAsset, -1, 0., pReadFileBalances.getmDirPlusNameFile());
					}
					/*
					 * Normal case
					 */
					else {
						lBKBalanceFile.addNewData(lBKAsset, lQty, lPriceExec, pReadFileBalances.getmDirPlusNameFile());
					}
				}
				pBKBalanceFileAbstract = lBKBalanceFile;
			}
			/*
			 * Case the header is for simple physical assets
			 */
			else if (BKStaticConst.getHEADER_BALANCE_EXPECTED_PHYSICAL().equals(lHeader)) {
				BKBalanceFile lBKBalanceFile = new BKBalanceFile(this);
				for (List<String> lLineStr : pReadFileBalances.getmContentList()) {					
					/*
					 * Load line
					 */
					int lIdx = -1;
					BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLineStr.get(++lIdx), pReadFileBalances.getmDirPlusNameFile());
					double lQty = BasicString.getDouble(lLineStr.get(++lIdx));
					/*
					 * Store data
					 */
					lBKBalanceFile.addNewData(lBKAsset, lQty, Double.NaN, pReadFileBalances.getmDirPlusNameFile());
				}
				pBKBalanceFileAbstract = lBKBalanceFile;
			}
			/*
			 * Case the header is for NAV
			 */
			else if (BKStaticConst.getHEADER_BALANCE_NAV().equals(lHeader)) {
				BKNAVFile lBKNAVFile = new BKNAVFile(this);
				for (List<String> lLineStr : pReadFileBalances.getmContentList()) {					
					/*
					 * Load line
					 */
					int lIdx = -1;
					String lNAVOrBKAssetStr = lLineStr.get(++lIdx).toUpperCase();
					double lQuantity = BasicString.getDouble(lLineStr.get(++lIdx));
					double lValueOrPrice = BasicString.getDouble(lLineStr.get(++lIdx));

					/*
					 * Case of NAV
					 */
					if (lNAVOrBKAssetStr.equals("NAV")) {
						lBKNAVFile.setpNAV(lQuantity);
					} else {
						BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lNAVOrBKAssetStr, pReadFileBalances.getmDirPlusNameFile());
						lBKNAVFile.declareNewAmount(lBKAsset, lQuantity);
						lBKNAVFile.declarePriceForNAV(lBKAsset, lValueOrPrice);
					}
				}
				pBKBalanceFileAbstract = lBKNAVFile;
			}
			/*
			 * Case the header is not correct --> Error: we impose to have the correct header
			 */
			else {
				BKCom.error("The header is incorrect"
						+ BKStaticConst.getNTAB() + "Header in file= '" + lHeader + "'"
						+ BKStaticConst.getNTAB() + "Header1 expected '" + BKStaticConst.getHEADER_BALANCE_EXPECTED_SIMPLIFIED() + "'"
						+ BKStaticConst.getNTAB() + "or Header2 expected '" + BKStaticConst.getHEADER_BALANCE_EXPECTED_FULL() + "'"
						+ BKStaticConst.getNTAB() + "or Header3 expected '" + BKStaticConst.getHEADER_BALANCE_NAV() + "'"
						+ BKStaticConst.getNTAB() + "File= '" + pReadFileBalances.getmDirPlusNameFile() + "'");
			}

		}
	}

	/**
	 * Display an error message if we expect a DOC file and there is not any
	 */
	public final void readFileDocsSupporting() {
		if (pBKDirEntity.getpDirDocs() != null) {
			if (pListFileNameSupportingDoc.size() == 0) {
				pErrorMessage += "\nMissing supporting documents file"
						+ pBKDirEntity.getpErrorMsgLocation(typeFile.DOCS)
						+ BKStaticConst.getNTAB() + "Missing date= " + pDate;
			}
		}
	}

	/**
	 * 
	 */
	public final List<BKTransaction> readFileTransactions() {
		/*
		 * Initiate
		 */
		List<BKTransaction> lListBKTransaction = new ArrayList<>();
		if (pPathFileTransactions == null) {
			pErrorMessage += "\nMissing transactions file"
					+ pBKDirEntity.getpErrorMsgLocation(typeFile.TRANSACTIONS)
					+ BKStaticConst.getNTAB() + "Missing date= " + pDate;
		} else {
			if (pReadFileTransactions == null) {
				pReadFileTransactions = new ReadFile(pPathFileTransactions, comReadFile.FULL_COM);
			}
			BasicPrintMsg.display(this, "Read transactions from " + pReadFileTransactions.getmDirPlusNameFile());			
			/*
			 * Error messages
			 */
			String lHeader = pReadFileTransactions.getmHeader();
			if (lHeader == null || !lHeader.startsWith(BKStaticConst.getHEADER_FILE_TRANSACTIONS())) {
				pErrorMessage += "\nThe header is incorrect"
						+ BKStaticConst.getNTAB() + "Header in file= '" + lHeader + "'"
						+ BKStaticConst.getNTAB() + "Header expected '" + BKStaticConst.getHEADER_FILE_TRANSACTIONS() + "'"
						+ BKStaticConst.getNTAB() + "File DIR in question= '" + pReadFileTransactions.getmDir() + "'"
						+ BKStaticConst.getNTAB() + "File Name in question= '" + pReadFileTransactions.getmNameFile() + "'";
			} else if (!pReadFileTransactions.getmIsFileReadSuccessFully()) {
				pErrorMessage += "\nError when reading the file '" + pReadFileTransactions.getmDirPlusNameFile() + "'";
			}
			/*
			 * Case the file is correct
			 */
			else {
				List<BKTransaction> lListBKTransactionFile = BKTransactionManager.readFileBKTransaction(pReadFileTransactions, pBKDirEntity.getpBKEntity(), this.getClass().getSimpleName());
				lListBKTransaction.addAll(lListBKTransactionFile);
			}
		}
		return lListBKTransaction;
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return "pDate= " + pDate
				+ "; pFileNameBalances= '" + (pReadFileBalances == null ? "null" : pReadFileBalances.getmNameFile() + "'")
				+ "; pNameFileTransactions= " + pNameFileTransactions
				+ "; pListFileNameSupportingDoc= " + pListFileNameSupportingDoc;
	}

	@Override public int compareTo(BKEntityFilesDate _sBKTransactionFile) {
		return Integer.compare(pDate, _sBKTransactionFile.getpDate());
	}

	/**
	 * declare path of the file transaction to this BKEntityFilesDate and also to the BKTransactionFileManager
	 * @param _sPPathFileTransactions
	 */
	public final void addpPathFileTransactions(Path _sPPathFileTransactions) {
		if (pNameFileTransactions != null) {
			BKCom.errorCodeLogic();
		}
		pPathFileTransactions = _sPPathFileTransactions;
		pNameFileTransactions = pPathFileTransactions.getFileName().toString();
		BKTransactionManager.getpBKTransactionFileManager().getpOrCreateBKFile(_sPPathFileTransactions);
	}

	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final List<String> getpListFileNameSupportingDoc() {
		return pListFileNameSupportingDoc;
	}
	public final void addNewPathSupportingDoc(Path _sPathSupportingDoc) {
		if (_sPathSupportingDoc != null) {
			String lNameFile = _sPathSupportingDoc.getFileName().toString();
			if (!pListFileNameSupportingDoc.contains(lNameFile)) {
				pListFileNameSupportingDoc.add(lNameFile);
			}
		}
	}
	public final void setpPathFileBalances(Path _sPPathFileBalances) {
		pPathFileBalances = _sPPathFileBalances;
	}
	public final BKDirEntity getpBKDirEntity() {
		return pBKDirEntity;
	}
	public final ReadFile getpReadFileBalances() {
		return pReadFileBalances;
	}
	public final String getpErrorMessage() {
		return pErrorMessage;
	}
	public final BKBalanceFileAbstract getpBKBalanceFileAbstract() {
		return pBKBalanceFileAbstract;
	}
	public final ReadFile getpReadFileTransactions() {
		return pReadFileTransactions;
	}



}

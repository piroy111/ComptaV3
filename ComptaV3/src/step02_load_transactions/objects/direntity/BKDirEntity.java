package step02_load_transactions.objects.direntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.holdings.BKHoldingManager;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKDirEntity {

	/**
	 * 
	 * @param _sNameEntity : for example UOB
	 * @param _sBKDirEntityManager
	 */
	public BKDirEntity(BKEntity _sBKEntity, String _sNameDirEntity, BKDirEntityManager _sBKDirEntityManager) {
		pBKEntity = _sBKEntity;
		pNameDirEntity = _sNameDirEntity;
		pBKDirEntityManager = _sBKDirEntityManager;
		/*
		 * 
		 */
		pTreeMapDateToBKEntityFilesDate = new TreeMap<>();
		pListBKTransaction = new ArrayList<>();
		pBKHoldingManager = new BKHoldingManager();
		setDirsAndFileNames();
		pBKEntity.declareNewBKDirEntityRelated(this);
	}

	/*
	 * ENUM
	 */
	protected enum typeFile {TRANSACTIONS, BALANCES, DOCS}
	/*
	 * DIR + Name files
	 */
	private String pDirEntity;
	private String pDirTransactions;
	private String pDirDocs;
	private String pDirBalances;
	private String pSuffixFileNameTransactions;
	private String pSuffixFileNameDocs;
	private String pSuffixFileNameBalances;
	/*
	 * Data
	 */
	private BKEntity pBKEntity;
	private String pNameDirEntity;
	private BKDirEntityManager pBKDirEntityManager;
	private List<BKTransaction> pListBKTransaction;
	private BKHoldingManager pBKHoldingManager;
	private TreeMap<Integer, BKEntityFilesDate> pTreeMapDateToBKEntityFilesDate;
	
	/**
	 * Set the DIRS and the suffix of the files names from the data given by the manager
	 */
	private final void setDirsAndFileNames() {
		/*
		 * DIR
		 */
		pDirEntity = pBKDirEntityManager.getpDirMain() + pNameDirEntity + "/";
		pDirTransactions = pDirEntity + pBKDirEntityManager.getpSubFolderNameTransactions();
		if (pBKDirEntityManager.getpSubFolderNameDocs() != null) {
			pDirDocs = pDirEntity + pBKDirEntityManager.getpSubFolderNameDocs();
		}
		if (pBKDirEntityManager.getpSubFolderNameBalances() != null) {
			pDirBalances = pDirEntity + pBKDirEntityManager.getpSubFolderNameBalances();
		}
		/*
		 * Suffix of file names
		 */
		String lSuffix0 = "_" + pNameDirEntity + "_" + pBKDirEntityManager.getpMidFix();
		pSuffixFileNameTransactions = lSuffix0 + pBKDirEntityManager.getpSuffixFileNameTransactions();
		if (pBKDirEntityManager.getpSuffixFileNameDocs() != null) {
			pSuffixFileNameDocs = lSuffix0 + pBKDirEntityManager.getpSuffixFileNameDocs();
		}
		if (pBKDirEntityManager.getpSuffixFileNameBalances() != null) {
			pSuffixFileNameBalances = lSuffix0 + pBKDirEntityManager.getpSuffixFileNameBalances();
		}
	}

	/**
	 * Create the folders if they don't exist<br>
	 * Check the names of the files are well written<br>
	 * Check all 3 files are here<br>
	 */
	public final void manageDirEntity() {
		manageDirEntity(pDirTransactions, pSuffixFileNameTransactions, typeFile.TRANSACTIONS);
		manageDirEntity(pDirDocs, pSuffixFileNameDocs, typeFile.DOCS);
		manageDirEntity(pDirBalances, pSuffixFileNameBalances, typeFile.BALANCES);
		/*
		 * Check all files are here
		 */
		for (BKEntityFilesDate lBKEntityFilesDate : pTreeMapDateToBKEntityFilesDate.values()) {
			pBKDirEntityManager.addpErrorMessage(lBKEntityFilesDate.getpErrorMessage());
		}
		pBKDirEntityManager.exitIfError();
	}
	
	/**
	 * Create the sub-directories if they don't exist<br>
	 * Check that the name of the file has the proper format<br>
	 * @param _sNameEntity
	 * @param _sSubFolderCategory
	 * @param _sSuffixFileNameCategory
	 */
	private void manageDirEntity(String _sSubDirCategory, String _sSuffixFileNameCategory, typeFile _sTypeFile) {
		if (_sSubDirCategory == null) {
			return;
		}
		/*
		 * Create the sub folders category if they don't exist
		 */
		Path lPathSub = Paths.get(_sSubDirCategory);
		if (!BasicFichiersNioRaw.getIsAlreadyExist(lPathSub)) {
			BasicFichiersNio.createDirectory(lPathSub);
			BasicPrintMsg.display(this, "Create new directory: '" + lPathSub + "'");
		}
		/*
		 * Set the format that we require for this entity and this suffix
		 */
		boolean lIsEndFlexible = false;
		String lMsgFormat = "YYYYMMDD" + _sSuffixFileNameCategory;
		if (!lMsgFormat.endsWith("csv")) {
			lMsgFormat += "***";
			lIsEndFlexible = true;
		}
		/*
		 * Check the format of the files inside the sub-directory
		 */
		List<Path> lListPathFile = BasicFichiersNio.getListFilesAndSubFiles(lPathSub);
		for (Path lPathFile : lListPathFile) {
			String lNameFile = lPathFile.getFileName().toString();
			if ((!lIsEndFlexible && lNameFile.length() != lMsgFormat.length()) || (lIsEndFlexible && lNameFile.length() < lMsgFormat.length())) {
				pBKDirEntityManager.addpErrorMessage("\nFile name must be in the format '" + lMsgFormat + "'; FileName= '" + lNameFile + "'"
						+ BKStaticConst.getNTAB() + "File= '" + _sSubDirCategory + lNameFile);
			} else {
				String lDateStr = BasicString.getDate(lNameFile.substring(0, 8));
				if (lDateStr.length() < 8) {
					pBKDirEntityManager.addpErrorMessage("\nFile name should begin with a date YYYYMMDD; FileName= '" + lNameFile + "'"
							+ BKStaticConst.getNTAB() + "File= '" + _sSubDirCategory + lNameFile);
				} else {
					int lDate = BasicString.getInt(lDateStr);
					if (lDate < 10000000 || lDate > 30000000) {
						pBKDirEntityManager.addpErrorMessage("\nFile name should begin with a real date YYYYMMDD; FileName= '" + lNameFile + "'"
								+ BKStaticConst.getNTAB() + "File= '" + _sSubDirCategory + lNameFile);
					} else {
						String lStart = lDate + _sSuffixFileNameCategory;
						if (!lNameFile.startsWith(lStart)) {
							pBKDirEntityManager.addpErrorMessage("\nFile name should begin with '" + lStart + "'; FileName= '" + lNameFile + "'"
									+ BKStaticConst.getNTAB() + "File= '" + _sSubDirCategory + lNameFile);
						} else {
							/*
							 * We passed all the error checks --> get or create a new BKTransactionFile and assign the name of the file
							 */
							BKEntityFilesDate lBKEntityFiles = getpOrCreateBKEntityFilesDate(lDate);
							if (_sTypeFile == typeFile.TRANSACTIONS) {
								lBKEntityFiles.addpPathFileTransactions(lPathFile);
							} else if (_sTypeFile == typeFile.BALANCES) {
								lBKEntityFiles.setpPathFileBalances(lPathFile);
							} else if (_sTypeFile == typeFile.DOCS) {
								lBKEntityFiles.addNewPathSupportingDoc(lPathFile);
							} else {
								BKCom.errorCodeLogic();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void addNewBKTransaction(BKTransaction _sBKTransaction) {
		if (pListBKTransaction.contains(_sBKTransaction)) {
			BKCom.errorCodeLogic();
		}
		/*
		 * 
		 */
		pListBKTransaction.add(_sBKTransaction);
		pBKHoldingManager.addNewData(_sBKTransaction);
	}	
	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final BKEntityFilesDate getpOrCreateBKEntityFilesDate(int _sDate) {
		BKEntityFilesDate lBKEntityFiles = pTreeMapDateToBKEntityFilesDate.get(_sDate);
		if (lBKEntityFiles == null) {
			lBKEntityFiles = new BKEntityFilesDate(_sDate, this);
			pTreeMapDateToBKEntityFilesDate.put(_sDate, lBKEntityFiles);
		}
		return lBKEntityFiles;
	}

	/**
	 * 
	 * @return
	 */
	public final String getpErrorMsgLocation(typeFile _sTypeFile) {
		String lDir;
		switch (_sTypeFile) {
		case TRANSACTIONS : lDir = pDirTransactions; break;
		case BALANCES : lDir = pDirBalances; break;
		case DOCS : lDir = pDirDocs; break;
		default: lDir = "N/A";
		}
		return BKStaticConst.getNTAB() + "Name entity= '" + pNameDirEntity + "'"
		+ BKStaticConst.getNTAB() + "Dir= '" + lDir + "'";
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		return "Name= " + pNameDirEntity
				+ "Dir= " + pDirEntity;
	}

	/*
	 * Getters & Setters
	 */
	public final String getpNameDirEntity() {
		return pNameDirEntity;
	}
	public final List<BKTransaction> getpListBKTransaction() {
		return pListBKTransaction;
	}
	public final BKDirEntityManager getpBKTransactionEntityManager() {
		return pBKDirEntityManager;
	}
	public final TreeMap<Integer, BKEntityFilesDate> getpTreeMapDateToBKEntityFilesDate() {
		return pTreeMapDateToBKEntityFilesDate;
	}
	public final String getpDirEntity() {
		return pDirEntity;
	}
	public final void setpDirEntity(String _sPDirEntity) {
		pDirEntity = _sPDirEntity;
	}
	public final String getpDirTransactions() {
		return pDirTransactions;
	}
	public final String getpDirDocs() {
		return pDirDocs;
	}
	public final String getpDirBalances() {
		return pDirBalances;
	}
	public final String getpSuffixFileNameTransactions() {
		return pSuffixFileNameTransactions;
	}
	public final String getpSuffixFileNameDocs() {
		return pSuffixFileNameDocs;
	}
	public final String getpSuffixFileNameBalances() {
		return pSuffixFileNameBalances;
	}
	public final BKEntity getpBKEntity() {
		return pBKEntity;
	}
	public final BKHoldingManager getpBKHoldingManager() {
		return pBKHoldingManager;
	}
	public final BKDirEntityManager getpBKDirEntityManager() {
		return pBKDirEntityManager;
	}

}

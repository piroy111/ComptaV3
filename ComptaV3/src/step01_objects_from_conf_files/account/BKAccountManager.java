package step01_objects_from_conf_files.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKAccountManager {

	/*
	 * Data
	 */
	private static List<BKAccount> pListBKAccount;
	private static Map<String, BKAccount> pMapKeyToBKAccount;
	private static BKAccount pBKAccountBunker;
	private static BKAccount pBKAccountPRoy;
	private static List<BKAccount> pListBKAccountExceptBunkerAndPRoy;
	private static List<BKAccount> pListBKAccountExceptBunker;

	/**
	 * 
	 */
	public static final void loadBKAccounts() {
		if (pListBKAccount == null) {
			BasicPrintMsg.displayTitle(null, "Load and create BKAccount from conf file");
			/*
			 * Initiate
			 */
			pListBKAccount = new ArrayList<>();
			pMapKeyToBKAccount = new HashMap<>();
			pListBKAccountExceptBunkerAndPRoy = new ArrayList<>();
			pListBKAccountExceptBunker = new ArrayList<>();
			/*
			 * Read file
			 */
			String lDir = BKStaticDir.getCONF();
			String lNameFile = BKStaticNameFile.getCONF_ACCOUNTS();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
			/*
			 * read file content and create objects BKAccount
			 */
			for (List<String> lLine : lReadFile.getmContentList()) {
				if (lLine.size() >= 3) {
					/*
					 * Load 
					 */
					int lIdx = -1;
					String lOwner = lLine.get(++lIdx);
					String lCurrency = lLine.get(++lIdx);
					String lEmail = lLine.get(++lIdx);
					String lJoint = BasicString.getItem(lLine, ++lIdx);
					String lSource = BasicString.getItem(lLine, ++lIdx);
					/*
					 * Skip if undefined
					 */
					if (lCurrency.equals("undefined")) {
						System.out.println(BKAccountManager.class.getSimpleName() + ": Skip account because the currency is undefined; Line= " + lLine);
						continue;
					}
					/*
					 * Special case of CAROUSELL --> we skip because it is a fake account
					 */
					if (lEmail.equals(BKStaticConst.getACCOUNT_CAROUSELL())) {
						System.out.println(BKAccountManager.class.getSimpleName() + ": Skip account; Line= " + lLine);
						continue;
					}
					/*
					 * Load the main currency of the account
					 */
					BKAssetCurrency lBKAssetCurrency = BKAssetManager.getpAndCheckBKAssetCurrency(lCurrency, 
							lReadFile.getmDirPlusNameFile());
					/*
					 * create the BKAccount
					 */
					String lKey = BKAccount.getKey(lEmail, lBKAssetCurrency);
					if (pMapKeyToBKAccount.containsKey(lKey)) {
						BKCom.error("Duplicated account in the conf file"
								+ "\nEmail= " + lEmail + "\nCurrency= " + lCurrency
								+ "\nFile= " + lDir + lNameFile);
					}
					BKAccount lBKAccount = new BKAccount(lEmail, lBKAssetCurrency);
					pListBKAccount.add(lBKAccount);
					pMapKeyToBKAccount.put(lKey, lBKAccount);
					/*
					 * 
					 */
					if (lBKAccount.getpEmail().equals(BKStaticConst.getACCOUNT_BUNKER())) {
						pBKAccountBunker = lBKAccount;
					} else if (lBKAccount.getpEmail().equals(BKStaticConst.getACCOUNT_PROY())) {
						pBKAccountPRoy = lBKAccount;
						pListBKAccountExceptBunker.add(lBKAccount);
					} else {
						pListBKAccountExceptBunkerAndPRoy.add(lBKAccount);
						pListBKAccountExceptBunker.add(lBKAccount);
					}
					/*
					 * Fill the data of the BKAccount
					 */
					lBKAccount.setpJoint(lJoint);
					lBKAccount.setpOwner(lOwner);
					lBKAccount.setpSource(lSource);
				}
			}
		}
	}

	/**
	 * 
	 * @param _sKeyStr= email if there is only one BKAccount with this email. Or 'email; currency' if there are several
	 * @param _sFileOrigin
	 * @return
	 */
	public static final BKAccount getpAndCheckBKAccount(String _sKeyStr, Object _sSender) {
		loadBKAccounts();
		/*
		 * Find the BKAccount with the same email
		 */
		BKAccount lBKAccountFound = null;
		for (BKAccount lBKAccountLoop : pListBKAccount) {
			if (lBKAccountLoop.getpEmail().equals(_sKeyStr)) {
				if (lBKAccountFound == null) {
					lBKAccountFound = lBKAccountLoop;
				} else {
					BKCom.error("We ask to find a BKAccount from an email, but there are several currencies associated."
							+ "\nEmail= '" + _sKeyStr + "'"
							+ "\n1st account associated= " + lBKAccountFound
							+ "\n2nd account associated= " + lBKAccountLoop
							+ "\nFile origin which gave the email= '" + BasicPrintMsg.displaySender(_sSender) + "'"
							+ "\nYou must specify in the file which account you want by writting the 'email; currency'");							
				}
			}
		}
		if (lBKAccountFound != null) {
			return lBKAccountFound;
		}
		/*
		 * Case no BKAccount was found -> error message
		 */		
		if (!pMapKeyToBKAccount.containsKey(_sKeyStr)) {
			BKCom.error("We ask for a BKAccount which does not exist"
					+ "\nEmail requested= '" + _sKeyStr + "'"
					+ "\nFile origin in which this email is read= '" + BasicPrintMsg.displaySender(_sSender) + "'"
					+ "\nThe email is not present in the conf file '" + BKStaticDir.getCONF() + BKStaticNameFile.getCONF_ACCOUNTS());
			return null;
		} else {
			return pMapKeyToBKAccount.get(_sKeyStr);
		}
	}

	/*
	 * Getters & Setters
	 */
	public static final List<BKAccount> getpListBKAccount() {
		loadBKAccounts(); return pListBKAccount;
	}
	public static final BKAccount getpBKAccountBunker() {
		loadBKAccounts(); return pBKAccountBunker;
	}
	public static final BKAccount getpBKAccountPRoy() {
		loadBKAccounts(); return pBKAccountPRoy;
	}
	public static final List<BKAccount> getpListBKAccountExceptBunkerAndPRoy() {
		loadBKAccounts(); return pListBKAccountExceptBunkerAndPRoy;
	}
	public static final List<BKAccount> getpListBKAccountExceptBunker() {
		loadBKAccounts(); return pListBKAccountExceptBunker;
	}



}

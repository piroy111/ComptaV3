package step01_objects_from_conf_files.income;

import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;

public class BKIncomeManager {

	/*
	 * Data
	 */
	private static TreeMap<String, BKIncome> pTreeMapNameToBKIncome;
	private static TreeMap<String, BKIncomeFYGroup> pTreeMapNameToBKIncomeFYGroup;

	/**
	 * 
	 * @param _sName
	 * @param _sFileOrigin
	 * @return
	 */
	public static BKIncome getpAndCheckBKIncome(String _sName, Object _sFileOrigin) {
		loadBKIncome();
		BKIncome lBKIncome = pTreeMapNameToBKIncome.get(_sName);
		if (lBKIncome == null) {
			BKCom.error("The BKIncome does not exist in the conf file. You must either correct the file origin or add a new BKIncome in the conf file"
					+ "\nBKIncome= '" + _sName + "'"
					+ "\nFileOrigin= '" + BasicPrintMsg.displaySender(_sFileOrigin) + "'"
					+ "\nFile conf of BKIncome= '" + BKStaticDir.getCONF() + BKStaticNameFile.getCONF_BKINCOME() + "'");
		}
		return lBKIncome;
	}

	/**
	 * 
	 */
	public static void loadBKIncome() {
		if (pTreeMapNameToBKIncome == null) {
			/*
			 * Initiate
			 */
			BasicPrintMsg.displayTitle(null, "Load BKIncome from the conf file");
			pTreeMapNameToBKIncome = new TreeMap<>();
			pTreeMapNameToBKIncomeFYGroup = new TreeMap<>();
			/*
			 * Find the right file of BKIncome to read and download
			 */
			String lDir = BKStaticDir.getCONF_BKIncome();
			List<String> lListNameFile = BasicFichiersNio.getListFilesInDirectory(lDir);
			ReadFile lReadFile = null;
			String lDateFYWishedStr = "" + Math.max(0, BKFrozenManager.getDATE_FY_MIN_TO_CREATE());			
			for (String lNameFile : lListNameFile) {
				if (lNameFile.endsWith(BKStaticNameFile.getCONF_BKINCOME())
						&& lNameFile.startsWith(lDateFYWishedStr)) {
					lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
				}				
			}
			if (lReadFile == null) {
				BKCom.error("The conf file for BKIncome is missing. You must give me a new file BKCincome for this year"
						+ "\nDir in which you must write the file= '" + lDir + "'"
						+ "\nName of file BKIncome expected= '" + lDateFYWishedStr + "_........" + BKStaticNameFile.getCONF_BKINCOME() + "'");
			}
			/*
			 * Read file content and create objects BKIncome
			 */
			for (List<String> lLine : lReadFile.getmContentList()) {
				int lIdx = -1;
				String lName = lLine.get(++lIdx);
				String lBKIncomeGroup = lLine.get(++lIdx);
				String lNameFY = lLine.get(++lIdx);
				String lNameFYGroup = lLine.get(++lIdx);
				/*
				 * Check
				 */
				if (lNameFY.equals("") || lNameFYGroup.equals("")) {
					BKCom.error("You must fill the column 'BKIncomeFY' and 'BKIncomeFYGroup' for all the BKIncomes"
							+ "\nLine with missing 'BKIncomeFY'= " + lLine
							+ "\nFile= '" + lDir + lReadFile.getmNameFile() + "'");
				}
				/*
				 * Create objects
				 */
				BKIncome lBKIncome = getpOrCreateBKIncome(lName);
				BKIncomeFYGroup lBKIncomeFYGroup = getpOrCreateBKIncomeFYGroup(lNameFYGroup);
				BKIncomeFY lBKIncomeFY = lBKIncomeFYGroup.getpOrCreateBKIncomeFY(lNameFY);
				/*
				 * Links
				 */
				lBKIncome.setpBKIncomeGroup(lBKIncomeGroup);
				lBKIncome.setpBKIncomeFY(lBKIncomeFY);
				lBKIncomeFY.declareNewBKIncome(lBKIncome);
			}
		}
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static BKIncome getpOrCreateBKIncome(String _sName) {
		BKIncome lBKIncome = pTreeMapNameToBKIncome.get(_sName);
		if (lBKIncome == null) {
			lBKIncome = new BKIncome(_sName);
			pTreeMapNameToBKIncome.put(_sName, lBKIncome);
		}
		return lBKIncome;
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static BKIncomeFYGroup getpOrCreateBKIncomeFYGroup(String _sName) {
		BKIncomeFYGroup lBKIncomeFYGroup = pTreeMapNameToBKIncomeFYGroup.get(_sName);
		if (lBKIncomeFYGroup == null) {
			lBKIncomeFYGroup = new BKIncomeFYGroup(_sName);
			pTreeMapNameToBKIncomeFYGroup.put(_sName, lBKIncomeFYGroup);
		}
		return lBKIncomeFYGroup;
	}

	/*
	 * Getters & Setters
	 */
	public static final TreeMap<String, BKIncome> getpTreeMapNameToBKIncome() {
		loadBKIncome(); return pTreeMapNameToBKIncome;
	}
	public static final TreeMap<String, BKIncomeFYGroup> getpTreeMapNameToBKIncomeFYGroup() {
		return pTreeMapNameToBKIncomeFYGroup;
	}
	











}

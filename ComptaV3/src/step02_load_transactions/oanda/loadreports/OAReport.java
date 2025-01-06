package step02_load_transactions.oanda.loadreports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class OAReport {

	public OAReport(ReadFile _sReadFile, OAReportManager _sOAReportManager) {
		pReadFile = _sReadFile;
		pOAReportManager = _sOAReportManager;
		/*
		 * 
		 */
		pDate = BasicString.getInt(BasicString.getDate(_sReadFile.getmNameFile()));
		pMapColumnToIdxForNewVerison = new HashMap<>();
	}

	/*
	 * Data
	 */
	private ReadFile pReadFile;
	private OAReportManager pOAReportManager;
	private List<OATransaction> pListOATransaction;
	private int pDate;
	private Map<String, Integer> pMapColumnToIdxForNewVerison = new HashMap<>();

	/**
	 * Load file and create the OATransaction
	 */
	public final void loadFile() {
		/*
		 * Initiate
		 */
		pListOATransaction = new ArrayList<OATransaction>();
		boolean lIsNewVersion = pReadFile.getmContentList().get(0).contains("Direction");
		if (lIsNewVersion) {
			loadIdxForHeaderNewVersion();
		}
		/*
		 * Read file
		 */
		for (int lIdxLine = 1; lIdxLine < pReadFile.getmContentList().size(); lIdxLine++) {
			List<String> lLineStr = pReadFile.getmContentList().get(lIdxLine);
			if (lIsNewVersion) {
				readLineNewVersion(lLineStr);
			} else {
				readLineOldVersion(lLineStr);
			}
		}
		/*
		 * Sort
		 */
		Collections.sort(pListOATransaction);
	}

	/**
	 * 
	 */
	private void loadIdxForHeaderNewVersion() {
		List<String> lListHeader = pReadFile.getmContentList().get(0);
		String lMsgError = "";
		lMsgError += getpError("Ticket", lListHeader);
		lMsgError += getpError("Date", lListHeader);
		lMsgError += getpError("Transaction", lListHeader);
		lMsgError += getpError("Details", lListHeader);
		lMsgError += getpError("Instrument", lListHeader);
		lMsgError += getpError("Price", lListHeader);
		lMsgError += getpError("Units", lListHeader);
		lMsgError += getpError("Direction", lListHeader);
		lMsgError += getpError("Financing", lListHeader);
		lMsgError += getpError("Commission", lListHeader);
		lMsgError += getpError("Amount", lListHeader);
		if (!lMsgError.equals("")) {
			lMsgError += "The header is not correct in the file OANDA"
					+ ". Most likely OANDA changed its format"
					+ ". You must change manually the titles of the columns back to what they were"
					+ lMsgError
					+ "\nFile in error= '" + pReadFile.getmDirPlusNameFile() + "'";
		}
	}
	
	/**
	 * 
	 * @param _sHeader
	 * @param _sListHeader
	 * @return
	 */
	private String getpError(String _sHeader, List<String> _sListHeader) {
		if (!_sListHeader.contains(_sHeader)) {
			return "\n'" + _sHeader + "' is missing in the header";  
		}
		int lIdx = _sListHeader.indexOf(_sHeader);
		pMapColumnToIdxForNewVerison.put(_sHeader, lIdx);
		return "";
	}
	
	/**
	 * New version of OANDA files
	 * @param _sLineStr
	 */
	private void readLineNewVersion(List<String> _sLineStr) {
		/*
		 * Load line
		 */
		String lID = _sLineStr.get(pMapColumnToIdxForNewVerison.get("Ticket"));
		String lDateStr = _sLineStr.get(pMapColumnToIdxForNewVerison.get("Date"));
		String lTransaction = _sLineStr.get(pMapColumnToIdxForNewVerison.get("Transaction"));
		String lDetails = _sLineStr.get(pMapColumnToIdxForNewVerison.get("Details"));
		String lSymbol = _sLineStr.get(pMapColumnToIdxForNewVerison.get("Instrument"));
		double lPrice = BasicString.getDouble(_sLineStr.get(pMapColumnToIdxForNewVerison.get("Price")));
		double lQuantity = BasicString.getDouble(_sLineStr.get(pMapColumnToIdxForNewVerison.get("Units")));
		String lDirection = _sLineStr.get(pMapColumnToIdxForNewVerison.get("Direction"));
		double lInterests = BasicString.getDouble(_sLineStr.get(pMapColumnToIdxForNewVerison.get("Financing")));
		double lCommissions = BasicString.getDouble(_sLineStr.get(pMapColumnToIdxForNewVerison.get("Commission")));
		double lAmount = BasicString.getDouble(_sLineStr.get(pMapColumnToIdxForNewVerison.get("Amount")));
		/*
		 * Check ID
		 */
		if (lID.equals("") || !pOAReportManager.checkAndStoreNewID(lID, true)) {
			return;
		}
		/*
		 * Is a trade or a cash flow?
		 */
		boolean lIsTradeOrCash = !AMNumberTools.isZero(lPrice);
		/*
		 * Case of a Trade: Buy or Sell for a Trade ? + commissions
		 */
		BKAsset lBKAsset = null;
		String lComment = null;
		if (lIsTradeOrCash) {
			/*
			 * Skip if fake trade
			 */
			if (!lTransaction.equals("ORDER_FILL")) {
				return;
			}
			if (lDetails.contains("MIGRATION")) {
				return;
			}
			/*
			 * Find the way: buy or sell
			 */
			lComment = "Trade";
			if (lDirection.equals("Sell")) {
				lQuantity = -lQuantity;
			} else if (!lDirection.equals("Buy")) {
				BasicPrintMsg.error("Error"
						+ "\nLineStr= " + _sLineStr.toString()
						+ "\nFile= " + pReadFile.getmDirPlusNameFile());
			}
			/*
			 * Commissions
			 */
			lCommissions = lInterests + lCommissions;
		}
		/*
		 * Case of a cash flow
		 */
		else {
			if (AMNumberTools.isNaNOrZero(lInterests) && AMNumberTools.isNaNOrZero(lAmount)) {
				return;
			}
			if (lTransaction.equals("TRANSFER_FUNDS")) {
				lComment = "Deposit";
				lQuantity = lAmount;
			} else if (lTransaction.equals("DAILY_FINANCING")) {
				lComment = "Interests";
				lQuantity = lInterests;
			} else {
				BasicPrintMsg.error("Error"
						+ "\nLineStr= " + _sLineStr.toString()
						+ "\nFile= " + pReadFile.getmDirPlusNameFile());
			}			
			lBKAsset = BKAssetManager.getpAndCheckBKAsset("USD", this);
		}
		/*
		 * lBKAsset
		 */
		double lQuantityOANDA = lQuantity;
		if (lBKAsset == null) {
			lBKAsset = getBKAsset(lSymbol);
			if (getIsInvertedPrice(lSymbol)) {
				lQuantity = -lQuantity * lPrice;
				lPrice = 1/ lPrice;
			}
		}
		/*
		 * Load date as a String + special case of the year if the year has only 2 digits --> we add '20' in front
		 */
		String[] lArray = lDateStr.split("/", -1);
		String lDayStr = lArray[1];
		String lMonthStr = lArray[0];
		String lYearStr = lArray[2].substring(0, 4);
		if (!BasicString.getIsNumber(lYearStr) && !lYearStr.startsWith("20")) {
			lYearStr = "20" + lYearStr.substring(0, 2);
		}
		/*
		 * Check Errors in DateStr
		 */
		String lErrorMsg = "";
		if (lDayStr.length() > 2 || !BasicString.getIsNumber(lDayStr)) {
			lErrorMsg += "\nThe day for the date is incorrect (length >2 or is not a number)";
		}
		if (lMonthStr.length() > 2 || !BasicString.getIsNumber(lMonthStr)) {
			lErrorMsg += "\nThe month for the date is incorrect (length >2 or  is not a number)";
		}
		if (lYearStr.length() != 4 || !BasicString.getIsNumber(lYearStr)) {
			lErrorMsg += "\nThe length of the year is incorrect (length != 4 or is not a number)";
		}
		if (!lErrorMsg.equals("")) {
			lErrorMsg += "\nlDateStr= '" + lDateStr + "'"
					+ "\nlDayStr= '" + lDayStr + "'"
					+ "\nlMonthStr= '" + lMonthStr + "'"
					+ "\nlYearStr= '" + lYearStr + "'"
					+ "\nFile= " + pReadFile.getmDirPlusNameFile();
			BKCom.error(lErrorMsg);
		}
		/*
		 * Date
		 */
		
		int lDay = BasicString.getInt(lDayStr);
		int lMonth = BasicString.getInt(lMonthStr);
		int lYear = BasicString.getInt(lYearStr);
		int lDate = BasicDateInt.getmDateInt(lYear, lMonth, lDay);
		if (lDate < 20000000 || lDate > 30000000) {
			BKCom.error("Date is incorrect"
					+ "\nDate from file= '" + lDateStr + "'"
					+ "\nDate interpreted by program= " + lDate
					+ "\nFile= " + pReadFile.getmDirPlusNameFile());
		}
		/*
		 * Create OATransaction
		 */
		OATransaction lOATransaction = new OATransaction(lID, lComment, lBKAsset, lQuantity, lDate, lPrice, lIsTradeOrCash, lSymbol, lQuantityOANDA);
		/*
		 * Case the transaction is a migration --> we keep it apart
		 */
		if (lDetails.contains(OAStatic.getDETAILS_MIGRATION())) {
			pOAReportManager.getpOAMigrationManager().declareNewOATransactionMigration(lOATransaction);
		}
		/*
		 * Case of a normal transaction --> we keep it in the report
		 */
		pListOATransaction.add(lOATransaction);
		if (!AMNumberTools.isNaNOrZero(lCommissions)) {
			OATransaction lOATransactionComm = new OATransaction(lID, "Commissions", BKAssetManager.getpAndCheckBKAsset("USD", this), 
					lCommissions, lDate, Double.NaN, lIsTradeOrCash, lSymbol, lQuantityOANDA);
			pListOATransaction.add(lOATransactionComm);
		}
	}

	/**
	 * 
	 * @param _sLineStr
	 */
	private void readLineOldVersion(List<String> _sLineStr) {
		/*
		 * Load line
		 */
		int lIdx = -1;
		String lID = _sLineStr.get(++lIdx);
		++lIdx;
		String lType = _sLineStr.get(++lIdx);
		String lSymbol = _sLineStr.get(++lIdx);
		double lQuantity = BasicString.getDouble(_sLineStr.get(++lIdx));
		String lDateStr = _sLineStr.get(++lIdx);
		double lPrice = BasicString.getDouble(_sLineStr.get(++lIdx));
		++lIdx;
		double lInterests = BasicString.getDouble(_sLineStr.get(++lIdx));
		++lIdx;
		++lIdx;
		++lIdx;
		double lAmount = BasicString.getDouble(_sLineStr.get(++lIdx));
		/*
		 * Continue if it is a migration
		 */
		if (lType.contains(" (System Migration)")) {
			return;
		}
		/*
		 * Check ID
		 */
		if (!pOAReportManager.checkAndStoreNewID(lID, false)) {
			return;
		}
		/*
		 * Is a trade or a cash flow?
		 */
		boolean lIsTradeOrCash = !AMNumberTools.isZero(lPrice);
		/*
		 * Case of a Trade: Buy or Sell for a Trade ? + commissions
		 */
		BKAsset lBKAsset = null;
		String lComment = null;
		double lCommissions = Double.NaN;
		if (lIsTradeOrCash) {
			/*
			 * Skip if fake trade
			 */
			if (AMNumberTools.isNaNOrZero(lAmount)) {
				return;
			}
			/*
			 * Special case of the issue I have every month
			 */
			if (lID.equals("11090345542")) {
				lType = "Sell";
			}
			/*
			 * Find the way: buy or sell
			 */
			lComment = "Trade";
			if (lType.contains("Sell")) {
				lQuantity = -lQuantity;
			} else if (!lType.contains("Buy")) {
				BasicPrintMsg.error("We cannot know the way(buy/sell); of the trade."
						+ " You must put it manually by putting 'Buy' or 'Sell' in the column 'Type' of the csv file"
						+ "\nLineStr= " + _sLineStr.toString()
						+ "\nFile= " + pReadFile.getmDirPlusNameFile());
			}
			/*
			 * Commissions
			 */
			lCommissions = lInterests;
		}
		/*
		 * Case of a cash flow
		 */
		else {
			if (lType.equals("Interest")) {
				lComment = "Interests";
				lQuantity = lInterests;
			} else  {
				lComment = "Deposit";
				lQuantity = lAmount;
				if (lType.toUpperCase().contains("WITHDRAWAL")) {
					lQuantity = -lQuantity;
				}

			}			
			lBKAsset = BKAssetManager.getpAndCheckBKAsset("USD", this);
		}
		/*
		 * lBKAsset
		 */
		double lQuantityOANDA = lQuantity;
		if (lBKAsset == null) {
			lBKAsset = getBKAsset(lSymbol);
			if (getIsInvertedPrice(lSymbol)) {
				lQuantity = -lQuantity * lPrice;
				lPrice = 1/ lPrice;
			}
		}
		/*
		 * Date
		 */
		String[] lArray = lDateStr.split("-", -1);
		int lDay = BasicString.getInt(lArray[2]);
		int lMonth = BasicString.getInt(lArray[1]);
		int lYear = BasicString.getInt(lArray[0]);
		int lDate = BasicDateInt.getmDateInt(lYear, lMonth, lDay);
		/*
		 * Create OATransaction
		 */
		OATransaction lOATransaction = new OATransaction(lID, lComment, lBKAsset, lQuantity, lDate, lPrice, lIsTradeOrCash, lSymbol, lQuantityOANDA);
		pListOATransaction.add(lOATransaction);
		if (!AMNumberTools.isNaNOrZero(lCommissions)) {
			OATransaction lOATransactionComm = new OATransaction(lID, "Commissions", BKAssetManager.getpAndCheckBKAsset("USD", this), 
					lCommissions, lDate, Double.NaN, lIsTradeOrCash, lSymbol, lQuantityOANDA);
			pListOATransaction.add(lOATransactionComm);
		}
	}

	/**
	 * Convert the asset OANDA into a BKAsset
	 * @param _sAssetOANDA
	 * @return
	 */
	public static BKAsset getBKAsset(String lSymbol) {
		/*
		 * Initiate
		 */
		String[] lArrayStr = lSymbol.split("/", -1);
		String lSymbol0 = lArrayStr[0];
		String lSymbol1 = lArrayStr[1];
		String lNameAsset;
		/*
		 * Build Symbol
		 */
		if (lSymbol0.startsWith("X") || lSymbol.length() == 3) {
			lNameAsset = lSymbol0;
		} else if (lSymbol0.equals(OAStatic.getOIL())) {
			lNameAsset = BKStaticConst.getNAME_BKASSET_OIL();
		} else if (lSymbol1.equals("USD")) {
			lNameAsset = "X" + lSymbol0;
		} else {
			lNameAsset = "X" + lSymbol1;
		}
		/*
		 * Return BKAsset
		 */
		return BKAssetManager.getpAndCheckBKAsset(lNameAsset, "OAReport");
	}
	
	/**
	 * If the asset is inverted we should do the following:<br>
	 * Quantity -> -Quantity * Price
	 * Price -> 1 / Price; 
	 * @param lSymbol
	 * @return
	 */
	public static boolean getIsInvertedPrice(String lSymbol) {
		return lSymbol.startsWith("USD");
	}
	
	/*
	 * Getters & Setters
	 */
	public final List<OATransaction> getpListOATransaction() {
		return pListOATransaction;
	}
	public final int getpDate() {
		return pDate;
	}
















}

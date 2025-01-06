package step01_objects_from_conf_files.asset.assetabstract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetLeasing;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperMetal;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPaper;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPhysical;
import step01_objects_from_conf_files.asset.bar.BKBar;

public class BKAssetManager {

	public BKAssetManager() {}

	/*
	 * Data
	 */
	private static Map<String, BKAsset> pMapNameToBKAsset;
	private static List<BKAsset> pListBKAssetSorted;
	private static BKAssetCurrency pBKAssetCurrencyReference;
	/*
	 * Map specific of each BKAsset
	 */
	private static Map<String, BKAssetCurrency> pMapNameToBKAssetCurrency;
	private static Map<String, BKAssetLeasing> pMapNameToBKAssetLeasing;
	private static Map<String, BKAssetMetal> pMapNameToBKAssetMetal;
	private static Map<String, BKAssetPaperCurrency> pMapNameToBKAssetPaperCurrency;
	private static Map<String, BKAssetPaperMetal> pMapNameToBKAssetPaperMetal;
	private static Map<String, BKAssetPaper> pMapNameToBKAssetPaper;
	private static Map<String, BKAssetPhysical> pMapNameToBKAssetPhysical;
	/*
	 * List sorted specific of each BKAsset
	 */
	private static List<BKAssetCurrency> pListBKAssetCurrencySorted;
	private static List<BKAssetLeasing> pListBKAssetLeasingSorted;
	private static List<BKAssetMetal> pListBKAssetMetalSorted;
	private static List<BKAssetPaperCurrency> pListBKAssetPaperCurrencySorted;
	private static List<BKAssetPaperMetal> pListBKAssetPaperMetalSorted;
	private static List<BKAssetPaper> pListBKAssetPaperSorted;
	private static List<BKAssetPhysical> pListBKAssetPhysicalSorted;

	/**
	 * 
	 */
	public static final void loadBKAssets() {
		if (pMapNameToBKAsset == null) {
			BasicPrintMsg.displayTitle(null, BKAssetManager.class.getSimpleName() + "Load BKAsset from conf file");
			/*
			 * Initiate
			 */
			pMapNameToBKAsset = new HashMap<>();
			pListBKAssetSorted = new ArrayList<>();
			pMapNameToBKAssetCurrency = new HashMap<>();
			pMapNameToBKAssetLeasing = new HashMap<>();
			pMapNameToBKAssetMetal = new HashMap<>();
			pMapNameToBKAssetPaperCurrency = new HashMap<>();
			pMapNameToBKAssetPaperMetal = new HashMap<>();
			pMapNameToBKAssetPaper = new HashMap<>();
			pMapNameToBKAssetPhysical = new HashMap<>();
			pListBKAssetCurrencySorted = new ArrayList<>();
			pListBKAssetLeasingSorted = new ArrayList<>();
			pListBKAssetMetalSorted = new ArrayList<>();
			pListBKAssetPaperCurrencySorted = new ArrayList<>();
			pListBKAssetPaperMetalSorted = new ArrayList<>();
			pListBKAssetPaperSorted = new ArrayList<>();
			pListBKAssetPhysicalSorted = new ArrayList<>();
			/*
			 * Read file
			 */
			String lDir = BKStaticDir.getCONF();
			String lNameFile = BKStaticNameFile.getCONF_HISTORICAL_PRICES();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
			/*
			 * Check file is correct
			 */
			if (!lReadFile.getmIsFileReadSuccessFully()) {
				BKCom.error("Error when reading file " + lDir + lNameFile);
			}
			if (lReadFile.getmContentList().size() < 3) {
				BKCom.error("The file does not contain enough lines; File= " + lDir + lNameFile);
			}
			/*
			 * Create the assets and asset types
			 */
			List<String> lLineBKAsset = lReadFile.getmContentList().get(0);
			List<String> lLineAssetType = lReadFile.getmContentList().get(1);
			List<String> lLineUnderlying = lReadFile.getmContentList().get(2);
			int lCount = Math.min(lLineBKAsset.size(), Math.min(lLineAssetType.size(), lLineUnderlying.size()));
			for (int lIdxColumn = 1; lIdxColumn < lCount; lIdxColumn++) {
				/*
				 * Load line
				 */
				String lAssetName = lLineBKAsset.get(lIdxColumn);
				String lAssetTypeStr = lLineAssetType.get(lIdxColumn);
				String lUnderlying = lLineUnderlying.get(lIdxColumn);
				/*
				 * Check if the asset type exist
				 */
				String lBKAssetStr = BKAsset.class.getSimpleName();
				if (lAssetTypeStr.startsWith("BK")) {
					if (!lAssetTypeStr.startsWith(lBKAssetStr)) {
						lAssetTypeStr = lBKAssetStr	+ lAssetTypeStr.substring("BK".length());
					}
				} else {
					lAssetTypeStr = lBKAssetStr + lAssetTypeStr;
				}
				/*
				 * Create objects
				 */
				BKAsset lBKAsset;
				if (lAssetTypeStr.equals(BKAssetCurrency.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetCurrency(lAssetName, lIdxColumn);
				} 
				else if (lAssetTypeStr.equals(BKAssetLeasing.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetLeasing(lAssetName, lIdxColumn);
				}
				else if (lAssetTypeStr.equals(BKAssetMetal.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetMetal(lAssetName, lIdxColumn);				
				}
				else if (lAssetTypeStr.equals(BKAssetPaperCurrency.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetPaperCurrency(lAssetName, lIdxColumn);				
				}
				else if (lAssetTypeStr.equals(BKAssetPaperMetal.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetPaperMetal(lAssetName, lIdxColumn);				
				}
				else if (lAssetTypeStr.equals(BKAssetPhysical.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetPhysical(lAssetName, lIdxColumn);				
				}
				else if (lAssetTypeStr.equals(BKAssetPaper.class.getSimpleName())) {
					lBKAsset = getpOrCreateBKAssetPaper(lAssetName, lIdxColumn);				
				}
				else {
					BKCom.error("The type of the BKAsset given in the conf file does not exist as a java class. You must change the error, or create a new java class"
							+ "\nlAssetName= '" + lAssetName + "'"
							+ "\nlAssetTypeStr= '" + lAssetTypeStr + "'"
							+ "File= " + lReadFile.getmDirPlusNameFile());
					lBKAsset = null;
				}
				/*
				 * Link to the type
				 */
				lBKAsset.setpUnderlyingStr(lUnderlying);
				/*
				 * Load the prices for the BKAsset
				 */
				for (int lIdxLine = 2; lIdxLine < lReadFile.getmContentList().size(); lIdxLine++) {
					List<String> lLine = lReadFile.getmContentList().get(lIdxLine);
					if (lLine.size() > lIdxColumn) {
						int lDate = BasicString.getInt(lLine.get(0));
						double lPrice = BasicString.getDouble(lLine.get(lIdxColumn));
						lBKAsset.addNewPrice(lDate, lPrice);
					}
				}		
			}
			pBKAssetCurrencyReference = getpAndCheckBKAssetCurrency("USD", BKAssetManager.class.getSimpleName());
			/*
			 * Assign underlying and check that it does exist
			 */
			for (BKAsset lBKAsset : pListBKAssetSorted) {
				String lUnderlyingStr = lBKAsset.getpUnderlyingStr();
				if (!lUnderlyingStr.equals("")) {
					BKAsset lBKAssetUnderlying = null;
					for (BKAsset lBKAssetLoop : pListBKAssetSorted) {
						if (lBKAssetLoop.getpName().equals(lUnderlyingStr)) {
							lBKAssetUnderlying = lBKAssetLoop;
							break;
						}
					}
					if (lBKAssetUnderlying == null) {
						BKCom.error("The underlying given in the conf file does not exist"
								+ "\nBKAsset= '" + lBKAsset.getpName() + "'"
								+ "\nlUnderlyingStr= '" + lUnderlyingStr + "'"
								+ "File= " + lReadFile.getmDirPlusNameFile());
					}
					lBKAsset.setpBKAssetUnderlying(lBKAssetUnderlying);
					lBKAssetUnderlying.addpListBKAssetDependant(lBKAsset);
				} else {
					lBKAsset.setpBKAssetUnderlying(lBKAsset);
					lBKAsset.addpListBKAssetDependant(lBKAsset);
				}
			}
			/*
			 * Display
			 */
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
				BasicPrintMsg.display(BKAssetManager.class.getSimpleName(), lBKAsset.getpName()
						+ "; AssetTypeStr= " + lBKAsset.getpAssetTypeStr()
						+ "; IdxForSort= " + lBKAsset.getpIdxForSort());
			}
		}
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static final BKAssetCurrency getpOrCreateBKAssetCurrency(String _sName, int _sIdxForSort) {
		BKAssetCurrency lBKAssetCurrency = pMapNameToBKAssetCurrency.get(_sName);
		if (lBKAssetCurrency == null) {
			lBKAssetCurrency = new BKAssetCurrency(_sName, _sIdxForSort);
			pMapNameToBKAssetCurrency.put(_sName, lBKAssetCurrency);
			pListBKAssetCurrencySorted.add(lBKAssetCurrency);
			Collections.sort(pListBKAssetCurrencySorted);
		}
		return lBKAssetCurrency;
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static final BKAssetLeasing getpOrCreateBKAssetLeasing(String _sName, int _sIdxForSort) {
		BKAssetLeasing lBKAssetLeasing = pMapNameToBKAssetLeasing.get(_sName);
		if (lBKAssetLeasing == null) {
			lBKAssetLeasing = new BKAssetLeasing(_sName, _sIdxForSort);
			pMapNameToBKAssetLeasing.put(_sName, lBKAssetLeasing);
			pListBKAssetLeasingSorted.add(lBKAssetLeasing);
			Collections.sort(pListBKAssetLeasingSorted);
		}
		return lBKAssetLeasing;
	}
	
	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static BKAssetMetal getpOrCreateBKAssetMetal(String _sName, int _sIdxForSort) {
		BKAssetMetal lBKAssetMetal = pMapNameToBKAssetMetal.get(_sName);
		if (lBKAssetMetal == null) {
			lBKAssetMetal = new BKAssetMetal(_sName, _sIdxForSort);
			pMapNameToBKAssetMetal.put(_sName, lBKAssetMetal);
			pListBKAssetMetalSorted.add(lBKAssetMetal);
			Collections.sort(pListBKAssetMetalSorted);
		}
		return lBKAssetMetal;
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static final BKAssetPaperCurrency getpOrCreateBKAssetPaperCurrency(String _sName, int _sIdxForSort) {
		BKAssetPaperCurrency lBKAssetPaperCurrency = pMapNameToBKAssetPaperCurrency.get(_sName);
		if (lBKAssetPaperCurrency == null) {
			lBKAssetPaperCurrency = new BKAssetPaperCurrency(_sName, _sIdxForSort);
			pMapNameToBKAssetPaperCurrency.put(_sName, lBKAssetPaperCurrency);
			pListBKAssetPaperCurrencySorted.add(lBKAssetPaperCurrency);
			Collections.sort(pListBKAssetPaperCurrencySorted);
			pMapNameToBKAssetPaper.put(_sName, lBKAssetPaperCurrency);
			pListBKAssetPaperSorted.add(lBKAssetPaperCurrency);
			Collections.sort(pListBKAssetPaperSorted);
		}
		return lBKAssetPaperCurrency;
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static final BKAssetPaperMetal getpOrCreateBKAssetPaperMetal(String _sName, int _sIdxForSort) {
		BKAssetPaperMetal lBKAssetPaperMetal = pMapNameToBKAssetPaperMetal.get(_sName);
		if (lBKAssetPaperMetal == null) {
			lBKAssetPaperMetal = new BKAssetPaperMetal(_sName, _sIdxForSort);
			pMapNameToBKAssetPaperMetal.put(_sName, lBKAssetPaperMetal);
			pListBKAssetPaperMetalSorted.add(lBKAssetPaperMetal);
			Collections.sort(pListBKAssetPaperMetalSorted);
			pMapNameToBKAssetPaper.put(_sName, lBKAssetPaperMetal);
			pListBKAssetPaperSorted.add(lBKAssetPaperMetal);
			Collections.sort(pListBKAssetPaperSorted);
		}
		return lBKAssetPaperMetal;
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static final BKAssetPhysical getpOrCreateBKAssetPhysical(String _sName, int _sIdxForSort) {
		BKAssetPhysical lBKAssetPhysical = pMapNameToBKAssetPhysical.get(_sName);
		if (lBKAssetPhysical == null) {
			lBKAssetPhysical = new BKAssetPhysical(_sName, _sIdxForSort);
			pMapNameToBKAssetPhysical.put(_sName, lBKAssetPhysical);
			pListBKAssetPhysicalSorted.add(lBKAssetPhysical);
			Collections.sort(pListBKAssetPhysicalSorted);
		}
		return lBKAssetPhysical;
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static final BKAssetPaper getpOrCreateBKAssetPaper(String _sName, int _sIdxForSort) {
		BKAssetPaper lBKAssetPaper = pMapNameToBKAssetPaper.get(_sName);
		if (lBKAssetPaper == null) {
			lBKAssetPaper = new BKAssetPaper(_sName, _sIdxForSort);
			pMapNameToBKAssetPaper.put(_sName, lBKAssetPaper);
			pListBKAssetPaperSorted.add(lBKAssetPaper);
			Collections.sort(pListBKAssetPaperSorted);
		}
		return lBKAssetPaper;
	}

	/**
	 * 
	 */	
	public static final BKAsset getpAndCheckBKAsset(String _sName, Object _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAsset, _sName, BasicPrintMsg.displaySender(_sFileNameOrigin), null);
		return pMapNameToBKAsset.get(_sName);
	}

	/**
	 * 
	 */	
	public static final BKAssetCurrency getpAndCheckBKAssetCurrency(String _sName, Object _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetCurrency, _sName, _sFileNameOrigin, BKAssetCurrency.class);
		return pMapNameToBKAssetCurrency.get(_sName);
	}

	/**
	 * 
	 */	
	public static final BKAssetMetal getpAndCheckBKAssetMetal(String _sName, String _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetMetal, _sName, _sFileNameOrigin, BKAssetMetal.class);
		return pMapNameToBKAssetMetal.get(_sName);
	}

	/**
	 * 
	 */	
	public static final BKAssetPaperCurrency getpAndCheckBKAssetPaperCurrency(String _sName, String _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetPaperCurrency, _sName, _sFileNameOrigin, BKAssetPaperCurrency.class);
		return pMapNameToBKAssetPaperCurrency.get(_sName);
	}

	/**
	 * 
	 */	
	public static final BKAssetPaperMetal getpAndCheckBKAssetPaperMetal(String _sName, String _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetPaperMetal, _sName, _sFileNameOrigin, BKAssetPaperMetal.class);
		return pMapNameToBKAssetPaperMetal.get(_sName);
	}

	/**
	 * 
	 */	
	public static final BKAssetPhysical getpAndCheckBKAssetPhysical(String _sName, String _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetPhysical, _sName, _sFileNameOrigin, BKAssetPhysical.class);
		return pMapNameToBKAssetPhysical.get(_sName);
	}
	
	/**
	 * 
	 */	
	public static final BKAssetLeasing getpAndCheckBKAssetLeasing(String _sName, String _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetLeasing, _sName, _sFileNameOrigin, BKAssetLeasing.class);
		return pMapNameToBKAssetLeasing.get(_sName);
	}

	/**
	 * 
	 */	
	public static final BKAssetPaper getpAndCheckBKAssetPaper(String _sName, String _sFileNameOrigin) {
		loadBKAssets();
		check(pMapNameToBKAssetPaper, _sName, _sFileNameOrigin, BKAssetPaper.class);
		return pMapNameToBKAssetPaper.get(_sName);
	}

	/**
	 * 
	 * @param _sBKAsset
	 */
	private static void check(Map<String, ?> _sMapNameToBKAsset, String _sName, Object _sFileNameOrigin, Class<?> _sClassBKAsset) {
		if (!pMapNameToBKAsset.containsKey(_sName)) {
			BKCom.error("The BKAsset does not exist in the file of historical price."
					+ "\nCheck if the BKAsset was written correctly by whoever called this method."
					+ "\nIf the reason is the BKAsset is new, you must add a column in the file of historical prices"
					+ "\n"
					+ "\nWrong name of the BKAsset= '" + _sName + "'"
					+ "\nFile giving the wrong Name of BKAsset= " + BasicPrintMsg.displaySender(_sFileNameOrigin)
					+ "\n"
					+ "\nList of available asset= " + pMapNameToBKAsset.keySet()
					+ "\nFile historical prices= '" + BKStaticDir.getCONF() + BKStaticNameFile.getCONF_HISTORICAL_PRICES() + "'");
		} else if (_sClassBKAsset != null && !_sMapNameToBKAsset.containsKey(_sName)) {
			BKAsset lBKAsset = pMapNameToBKAsset.get(_sName);
			BKCom.error("The BKAsset is of a different class than the one requested by the method."
					+ "\nBKAsset you requested in the code= '" + _sName + "'"
					+ "\nClass of the BKAsset you requested in the code= '" + lBKAsset.getClass().getSimpleName() + "'"
					+ "\nClass of BKAsset which should have been passed= '" + _sClassBKAsset.getSimpleName() + "'"
					+ "\nList of available asset= " + _sMapNameToBKAsset.keySet());
			
		}
	}

	/**
	 * 
	 * @param _sBKAsset
	 */
	protected static final void declareNewBKAsset(BKAsset _sBKAsset) {
		BKAsset lBKAssetAlreadyHere = pMapNameToBKAsset.get(_sBKAsset.getpName());
		if (lBKAssetAlreadyHere == null) {
			pMapNameToBKAsset.put(_sBKAsset.getpName(), _sBKAsset);
			pListBKAssetSorted.add(_sBKAsset);
			Collections.sort(pListBKAssetSorted);
		} else if (!lBKAssetAlreadyHere.equals(_sBKAsset)) {
			BKCom.error("2 BKAssets have the same name in the file of historical price but different types. You cannot call this method."
					+ "\nFile historical prices= '" + BKStaticDir.getCONF() + BKStaticNameFile.getCONF_HISTORICAL_PRICES() + "'"
					+ "\nName= '" + _sBKAsset.getpName() + "'"
					+ "\nType1= '" + lBKAssetAlreadyHere.getpAssetTypeStr()
					+ "\nType2= '" + _sBKAsset.getpAssetTypeStr());
		}
	}

	/**
	 * 
	 * @param _sID
	 * @return
	 */
	public static BKBar getpBKBar(String _sID) {
		BKBar lBKBar = null;
		for (BKAssetMetal lBKAssetMetal : getpMapNameToBKAssetMetal().values()) {
			if (lBKAssetMetal.getpMapIDToBKBar().containsKey(_sID)) {
				if (lBKBar == null) {
					lBKBar = lBKAssetMetal.getpMapIDToBKBar().get(_sID);
				} else {
					BKCom.error("The ID of the BKBAr is the same for 2 different metals"
							+ "\nID= " + _sID
							+ "\nFirst metal= " + lBKBar.getpBKAssetMetal()
							+ "\nSecond metal= " + lBKAssetMetal);
				}
			}
		}
		return lBKBar;
	}	
	
	/**
	 * @return message in the form "Message: List of Metals"<br>
	 * Message: "Y" = the BKBar exist, "N" = the BKBar does not exist, "Duplicate"= the BKBar is duplicated for several metals<br>
	 * List of Metals:  the list of BKAssetMetal which have the BKBar<br> 
	 * @param _sID
	 */
	public static String getpIsBKBarExist(String _sID) {
		String lMessage = null;
		List<BKAssetMetal> lListBKAssetMetal = new ArrayList<>();
		for (BKAssetMetal lBKAssetMetal : getpMapNameToBKAssetMetal().values()) {
			if (lBKAssetMetal.getpMapIDToBKBar().containsKey(_sID)) {
				if (lMessage == null) {
					lMessage = "Y";
				} else {
					lMessage = "Duplicated";
				}
				lListBKAssetMetal.add(lBKAssetMetal);
			}
		}
		if (lMessage == null) {
			lMessage = "N";
		} else {
			lMessage += ": " + lListBKAssetMetal.toString();
		}
		return lMessage;
	}
	
	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public static boolean getpIsCurrency(BKAsset _sBKAsset) {
		if (_sBKAsset instanceof BKAssetCurrency) {
			return true;
		}
		for (BKAssetCurrency lBKAssetCurrency : pListBKAssetCurrencySorted) {
			if (lBKAssetCurrency.getpName().equals(_sBKAsset.getpName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public static boolean getpIsPaperCurrency(BKAsset _sBKAsset) {
		if (_sBKAsset instanceof BKAssetPaperCurrency) {
			return true;
		}
		for (BKAssetPaperCurrency lBKAssetPaperCurrency : pListBKAssetPaperCurrencySorted) {
			if (lBKAssetPaperCurrency.getpName().equals(_sBKAsset.getpName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public static boolean getpIsPhysicalMetal(BKAsset _sBKAsset) {
		if (_sBKAsset instanceof BKAssetMetal) {
			return true;
		}
		for (BKAssetMetal lBKAssetMetal : pListBKAssetMetalSorted) {
			if (lBKAssetMetal.getpName().equals(_sBKAsset.getpName())) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Getters & Setters
	 */
	public static final List<BKAsset> getpListBKAssetSorted() {
		loadBKAssets(); return pListBKAssetSorted;
	}
	public static final Map<String, BKAsset> getpMapNameToBKAsset() {
		loadBKAssets(); return pMapNameToBKAsset;
	}
	public static final Map<String, BKAssetCurrency> getpMapNameToBKAssetCurrency() {
		loadBKAssets(); return pMapNameToBKAssetCurrency;
	}
	public static final Map<String, BKAssetLeasing> getpMapNameToBKAssetLeasing() {
		loadBKAssets(); return pMapNameToBKAssetLeasing;
	}
	public static final Map<String, BKAssetMetal> getpMapNameToBKAssetMetal() {
		loadBKAssets(); return pMapNameToBKAssetMetal;
	}
	public static final Map<String, BKAssetPaperCurrency> getpMapNameToBKAssetPaperCurrency() {
		loadBKAssets(); return pMapNameToBKAssetPaperCurrency;
	}
	public static final Map<String, BKAssetPaperMetal> getpMapNameToBKAssetPaperMetal() {
		loadBKAssets(); return pMapNameToBKAssetPaperMetal;
	}
	public static final Map<String, BKAssetPaper> getpMapNameToBKAssetPaper() {
		loadBKAssets(); return pMapNameToBKAssetPaper;
	}
	public static final Map<String, BKAssetPhysical> getpMapNameToBKAssetPhysical() {
		loadBKAssets(); return pMapNameToBKAssetPhysical;
	}
	public static final List<BKAssetCurrency> getpListBKAssetCurrencySorted() {
		loadBKAssets(); return pListBKAssetCurrencySorted;
	}
	public static final List<BKAssetLeasing> getpListBKAssetLeasingSorted() {
		loadBKAssets(); return pListBKAssetLeasingSorted;
	}
	public static final List<BKAssetMetal> getpListBKAssetMetalSorted() {
		loadBKAssets(); return pListBKAssetMetalSorted;
	}
	public static final List<BKAssetPaperCurrency> getpListBKAssetPaperCurrencySorted() {
		loadBKAssets(); return pListBKAssetPaperCurrencySorted;
	}
	public static final List<BKAssetPaperMetal> getpListBKAssetPaperMetalSorted() {
		loadBKAssets(); return pListBKAssetPaperMetalSorted;
	}
	public static final List<BKAssetPaper> getpListBKAssetPaperSorted() {
		loadBKAssets(); return pListBKAssetPaperSorted;
	}
	public static final List<BKAssetPhysical> getpListBKAssetPhysicalSorted() {
		loadBKAssets(); return pListBKAssetPhysicalSorted;
	}
	public static final BKAssetCurrency getpBKAssetCurrencyReference() {
		loadBKAssets(); return pBKAssetCurrencyReference;
	}



}


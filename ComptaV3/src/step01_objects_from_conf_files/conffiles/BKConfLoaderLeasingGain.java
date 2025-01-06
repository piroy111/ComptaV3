                                                                                                                                  package step01_objects_from_conf_files.conffiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetLeasing;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;

public class BKConfLoaderLeasingGain {

	
	/*
	 * Data
	 */
	private static boolean IS_LOADED = false;
	private static Map<BKEntity, Map<BKAssetLeasing, Map<BKAccount, Double>>> mapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit = new HashMap<>();
	
	/**
	 * 
	 */
	public static void loadConfFile() {
		if (!IS_LOADED) {
			IS_LOADED = true;
			Object lSender = new BKConfLoaderLeasingGain();
			BasicPrintMsg.displayTitle(lSender, "Load cost of storage from conf file");
			/*
			 * 
			 */
			String lDir = BKStaticDir.getCONF();
			String lNameFile = BKStaticNameFile.getCONF_BKLEASING();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, ReadFile.comReadFile.FULL_COM);
			/*
			 * Read file
			 */
			List<String> lLineBKEntityStr = lReadFile.getmContentList().get(0);
			List<String> lLineBKAssetStr = lReadFile.getmContentList().get(1);
			for (int lIdx = 1; lIdx < lLineBKEntityStr.size(); lIdx++) {
				/*
				 * Load line
				 */
				String lNameBKEntity = lLineBKEntityStr.get(lIdx);
				String lBKAssetStr = lLineBKAssetStr.get(lIdx);
				/*
				 * Get or create BKEntity
				 */
				BKEntity lBKEntity = BKEntityManager.getpOrCreateBKEntity(lNameBKEntity, type_entity.PHYSICAL);
				/*
				 * get or create sub-map BKEntity
				 */
				Map<BKAssetLeasing, Map<BKAccount, Double>> lMapBKAssetToMapBKAccountToGainForBunker = mapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit.get(lBKEntity);
				if (lMapBKAssetToMapBKAccountToGainForBunker == null) {
					lMapBKAssetToMapBKAccountToGainForBunker = new HashMap<>();
					mapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit.put(lBKEntity, lMapBKAssetToMapBKAccountToGainForBunker);
				}
				/*
				 * Get or create sub-map BKAccount
				 */
				BKAssetLeasing lBKAssetLeasing = BKAssetManager.getpAndCheckBKAssetLeasing(lBKAssetStr, lReadFile.getmDirPlusNameFile());
				Map<BKAccount, Double> lMapBKAccountToGainForBunker = lMapBKAssetToMapBKAccountToGainForBunker.get(lBKAssetLeasing);
				if (lMapBKAccountToGainForBunker == null) {
					lMapBKAccountToGainForBunker = new HashMap<>();
					lMapBKAssetToMapBKAccountToGainForBunker.put(lBKAssetLeasing, lMapBKAccountToGainForBunker);
				}
				/*
				 * 
				 */
				for (int lKdx = 2; lKdx < lReadFile.getmContentList().size(); lKdx++) {
					/*
					 * Read lines which start with BKAccount
					 */
					List<String> lLineGainStr = lReadFile.getmContentList().get(lKdx);
					String lBKAccountStr = lLineGainStr.get(0);
					Double lGainForBunker = BasicString.getDouble(lLineGainStr.get(lIdx));
					/*
					 * Get BKAccount
					 */
					BKAccount lBKAccount = null;
					if (!lBKAccountStr.toUpperCase().startsWith("DEFAULT")) {
						lBKAccount = BKAccountManager.getpAndCheckBKAccount(lBKAccountStr, lReadFile.getmDirPlusNameFile());
					}
					/*
					 * Assign the gain for Bunker to the BKAccount
					 */
					lMapBKAccountToGainForBunker.put(lBKAccount, lGainForBunker);
				}
			}
			/*
			 * Communication
			 */
			BasicPrintMsg.display(lSender, "All BKAssetLeasing have been assigned with a Gain; Map= " + mapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit.toString());
		}
	}

	/**
	 * 
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @param _sBKAccount
	 * @param _sSender
	 */
	public static double getProfitPercent(BKEntity _sBKEntity, BKAsset _sBKAsset, BKAccount _sBKAccount, Object _sSender) {
		Map<BKAssetLeasing, Map<BKAccount, Double>> lMapBKAssetToMapBKAccountToGainForBunker = mapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit.get(_sBKEntity);
		if (lMapBKAssetToMapBKAccountToGainForBunker == null) {
			BasicPrintMsg.error("The BKEntity does not exist in the conf file. Please add it to the conf file"
					+ "\nBKEntity to add= '" + _sBKEntity + "'"
					+ "\nConf file= '" + BKStaticNameFile.getCONF_BKLEASING() + "'"
					+ "\nPart of the program which is asking= '" + _sSender.toString() + "'");
		}
		Map<BKAccount, Double> lMapBKAccountToGainForBunker = lMapBKAssetToMapBKAccountToGainForBunker.get(_sBKAsset);
		if (lMapBKAccountToGainForBunker == null) {
			BasicPrintMsg.error("The BKAsset does not exist in the conf file. Please add it to the conf file"
					+ "\nBKAsset to add= '" + _sBKAsset + "'"
					+ "\nConf file= '" + BKStaticNameFile.getCONF_BKLEASING() + "'"
					+ "\nPart of the program which is asking= '" + _sSender.toString() + "'");
		}
		if (lMapBKAccountToGainForBunker.containsKey(_sBKAccount)) {
			return lMapBKAccountToGainForBunker.get(_sBKAccount);
		} else {
			return lMapBKAccountToGainForBunker.get(null);
		}
	}
	
	/**
	 * 
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @param _sSender
	 * @return
	 */
	public static double getProfitPercentForBunker(BKEntity _sBKEntity, BKAsset _sBKAsset, BKAccount _sBKAccount, Object _sSender) {
		double lProfitPercentBunker = getProfitPercent(_sBKEntity, _sBKAsset, BKAccountManager.getpBKAccountBunker(), _sSender);
		if (_sBKAccount.equals(BKAccountManager.getpBKAccountBunker())) {
			return lProfitPercentBunker;
		} else {
			double lProfitPercent = getProfitPercent(_sBKEntity, _sBKAsset, _sBKAccount, _sSender);
			return lProfitPercentBunker - lProfitPercent;
		}		
	}
	
	/**
	 * 
	 * @return
	 */
	public static final Map<BKEntity, Map<BKAssetLeasing, Map<BKAccount, Double>>> getMapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit() {
		return mapBKEntityToMapBKAssetLeasingToMapBKAccountToProfit;
	}
	
	/**
	 * 
	 * @param _sBKEntity
	 * @param _sSender
	 * @return change BKEntity used for transfers into physical BKEntities where the BKLeasing asset is held<br>
	 * FaiGold_related --> FaiFold_Leasing<br>
	 * Condor_related --> Condor_Leasing<br>
	 */
	public static BKEntity getpBKEntityPhysical(BKEntity _sBKEntity, Object _sSender) {
		if (_sBKEntity.getpTypeEntity() == type_entity.TRANSFER) {
			String lName = _sBKEntity.getpName();
			if (lName.endsWith(BKStaticConst.getBKENTITY_LEASING_RELATED())) {
				lName = lName.replaceAll(BKStaticConst.getBKENTITY_LEASING_RELATED(), BKStaticConst.getBKENTITY_LEASING());
				return BKEntityManager.getpAndCheckBKEntityFromName(lName, _sSender);
			}
		}
		return _sBKEntity;
	}
	
	
	
	
	
	
	
	
	
	
}

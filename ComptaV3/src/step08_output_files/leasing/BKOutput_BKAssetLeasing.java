package step08_output_files.leasing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMDebug;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetLeasing;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.entity.BKEntity;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_BKAssetLeasing extends BKOutputAbstract {

	public BKOutput_BKAssetLeasing(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	/*
	 * Data
	 */
	private static Map<String, AccountEntityAssetLeasing> pMapKeyToAccountEntityAssetLeasing;
	
	/**
	 * If the leasing is frozen in the CONF file then the program will not recompute the leasing gain estimate and thus we cannot do this file<br>
	 * We need to re-take the previous file<br>
	 */
	public final boolean getpIsRetakePreviousFile() {
		return pMapKeyToAccountEntityAssetLeasing == null;
	}
	
	/**
	 * 
	 */
	@Override public void buildFileContent() {
		/*
		 * If pMapKeyToAccountEntityAssetLeasing == null then it means that the transactions are frozen from the CONF file and we did not compute them
		 * It should not happen since we are using the getpIsRetakePreviousFile method
		 */
		if (getpIsRetakePreviousFile()) {
			BasicPrintMsg.errorCodeLogic();
		}
		/*
		 * Create the items for sorting and display
		 */
		List<AccountEntityAssetLeasing> lListAccountEntityAssetLeasing = new ArrayList<>(pMapKeyToAccountEntityAssetLeasing.values());
		Collections.sort(lListAccountEntityAssetLeasing);
		/*
		 * Write Header
		 */
		addNewHeader("Date end of month,BKAccount,BKEntity,BKAsset"
				+ ",Holding in units (end of month),Holding in US$ (end of month)"
				+ ",Holding in units (average for the month),Holding in US$ (average for the month)"
				+ ",Gain Bunker in %,Gain Bunker in US$,");
		/*
		 * Write file
		 */
		for (AccountEntityAssetLeasing lAccountEntityAssetLeasing : lListAccountEntityAssetLeasing) {
			
			//////////////////////////////////////////////////
			if (lAccountEntityAssetLeasing.getpBKAccount().getpKey().equals("jakircevict@gmail.com; USD") 
					&& lAccountEntityAssetLeasing.getpBKAssetLeasing().getpName().equals("USD LEASING")) {
				AMDebug.DEBUG();
			}
			
			//////////////////////////////////////////////////
			
			String lLine = lAccountEntityAssetLeasing.getpLastDate()
					+ "," + lAccountEntityAssetLeasing.getpBKAccount()
					+ "," + lAccountEntityAssetLeasing.getpBKEntity().getpName().replaceAll(BKStaticConst.getBKENTITY_LEASING(), "")
					+ "," + lAccountEntityAssetLeasing.getpBKAssetLeasing().getpName()
					+ "," + lAccountEntityAssetLeasing.getpAmountInUnitsLastDate()
					+ "," + lAccountEntityAssetLeasing.getpAmountInUSDLastDate()
					+ "," + lAccountEntityAssetLeasing.getpAmountInUnits()
					+ "," + lAccountEntityAssetLeasing.getpAmountInUSD()
					+ "," + lAccountEntityAssetLeasing.getpGainBunkerInPercent()
					+ "," + lAccountEntityAssetLeasing.getpGainBunkerInUSD();
			addNewLineToWrite(lLine);
		}
	}

	/**
	 * Bridge from the COMPTA COMPUTOR to the file output so that the data is the same
	 * @param _sBKEntityLeasing
	 * @param _sBKAccount
	 * @param _sBKAssetLeasing
	 * @param _sHolding
	 * @param _sNNNUSD
	 * @param _sGainBunkerInUSD
	 * @param _sDate
	 */
	public static void declareNewGain(BKEntity _sBKEntityLeasing, BKAccount _sBKAccount, BKAssetLeasing _sBKAssetLeasing, double _sHolding, double _sNNNUSD, double _sGainBunkerInUSD, int _sDate) {
		
		//////////////////////////////////////////////////
		if (_sBKAccount.getpKey().equals("jakircevict@gmail.com; USD") && _sBKAssetLeasing.getpName().equals("USD LEASING")) {
			AMDebug.DEBUG();
		}
		
		//////////////////////////////////////////////////
		
		/*
		 * Get the AccountEntityAssetLeasing
		 */
		AccountEntityAssetLeasing lAccountEntityAssetLeasing = getpOrCreateAccountEntityAssetLeasing(_sBKAccount, _sBKEntityLeasing, _sBKAssetLeasing);
		/*
		 * Store the data for the file output which we will print later
		 */
		lAccountEntityAssetLeasing.addNewSetData(_sDate, _sHolding, _sNNNUSD);
		lAccountEntityAssetLeasing.addpGainBunkerInUSD(_sGainBunkerInUSD);
	}
	
	
	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @return
	 */
	protected static String getKey(BKAccount _sBKAccount, BKEntity _sBKEntity, BKAsset _sBKAsset) {
		return _sBKAccount + ";;" + _sBKEntity + ";;" + _sBKAsset;
	}

	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKEntity
	 * @param _sBKAsset
	 * @return
	 */
	protected static AccountEntityAssetLeasing getpOrCreateAccountEntityAssetLeasing(BKAccount _sBKAccount, BKEntity _sBKEntity, BKAssetLeasing _sBKAssetLeasing) {
		if (pMapKeyToAccountEntityAssetLeasing == null) {
			pMapKeyToAccountEntityAssetLeasing = new HashMap<>();
		}
		String lKey = getKey(_sBKAccount, _sBKEntity, _sBKAssetLeasing);
		AccountEntityAssetLeasing lAccountEntityAssetLeasing = pMapKeyToAccountEntityAssetLeasing.get(lKey);
		if (lAccountEntityAssetLeasing == null) {
			lAccountEntityAssetLeasing = new AccountEntityAssetLeasing(_sBKAccount, _sBKEntity, _sBKAssetLeasing);
			pMapKeyToAccountEntityAssetLeasing.put(lKey, lAccountEntityAssetLeasing);
		}
		return lAccountEntityAssetLeasing;
	}



}


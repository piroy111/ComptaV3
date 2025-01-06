package step07_reconciliation.reconciliators.vaults.vault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBarType;
import step02_load_transactions.objects.entity.BKEntity;
import step07_reconciliation.reconciliators.vaults.bartypeinvault.RNBarTypeMetalVaultDate;

public class RNVaultDate implements Comparable<RNVaultDate> {

	public RNVaultDate(int _sDate, BKEntity _sBKEntity) {
		pDate = _sDate;
		pBKEntity = _sBKEntity;
		/*
		 * 
		 */
		pKey = getKey(pDate, pBKEntity);
		pMapKeyToRNBarTypeMetalVaultDate = new HashMap<>();
		pTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate = new TreeMap<>();
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			pTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate.put(lBKAssetMetal, new ArrayList<>());
		}
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private BKEntity pBKEntity;
	private String pKey;
	private Map<String, RNBarTypeMetalVaultDate> pMapKeyToRNBarTypeMetalVaultDate;
	private TreeMap<BKAssetMetal, List<RNBarTypeMetalVaultDate>> pTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate;

	/**
	 * 
	 * @return
	 */
	public RNBarTypeMetalVaultDate getpOrCreateRNBarTypeMetalVaultDate(BKBarType _sBKBarType) {
		String lKey = RNBarTypeMetalVaultDate.getKey(_sBKBarType, this);
		RNBarTypeMetalVaultDate lRNBarTypeMetalVaultDate = pMapKeyToRNBarTypeMetalVaultDate.get(lKey);
		if (lRNBarTypeMetalVaultDate == null) {
			lRNBarTypeMetalVaultDate = new RNBarTypeMetalVaultDate(_sBKBarType, this);
			pMapKeyToRNBarTypeMetalVaultDate.put(lKey, lRNBarTypeMetalVaultDate);
			/*
			 * 
			 */
			BKAssetMetal lBKAssetMetal = _sBKBarType.getpBKAssetMetal();
			List<RNBarTypeMetalVaultDate> lListRNBarTypeMetalVaultDate = pTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate.get(lBKAssetMetal);
			lListRNBarTypeMetalVaultDate.add(lRNBarTypeMetalVaultDate);
		}
		return lRNBarTypeMetalVaultDate;
	}	
	
	/**
	 * 
	 * @param _sDate
	 * @param _sBKVault
	 * @return
	 */
	protected static String getKey(int _sDate, BKEntity _sBKEntity) {
		return _sDate + ";;" + _sBKEntity.getpName();
	}
	
	/**
	 * 
	 */
	public String toString() {
		return pDate + "-" + pBKEntity.getpName();
	}
	
	/**
	 * 
	 */
	@Override public int compareTo(RNVaultDate _sRNVaultDate) {
		Integer lCompareDate = Integer.compare(pDate, _sRNVaultDate.pDate);
		if (lCompareDate != 0) {
			return lCompareDate;
		}
		return pBKEntity.compareTo(_sRNVaultDate.getpBKEntity());
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final String getpKey() {
		return pKey;
	}
	public final Map<String, RNBarTypeMetalVaultDate> getpMapKeyToRNBarTypeMetalVaultDate() {
		return pMapKeyToRNBarTypeMetalVaultDate;
	}
	public final TreeMap<BKAssetMetal, List<RNBarTypeMetalVaultDate>> getpTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate() {
		return pTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate;
	}
	public final BKEntity getpBKEntity() {
		return pBKEntity;
	}

	

	
}

package step07_reconciliation.reconciliators.vaults.bartypeinvault;

import step01_objects_from_conf_files.asset.bar.BKBarType;
import step07_reconciliation.reconciliators.vaults.vault.RNVaultDate;

public class RNBarTypeMetalVaultDate implements Comparable<RNBarTypeMetalVaultDate> {

	public RNBarTypeMetalVaultDate(BKBarType _sBKBarType, RNVaultDate _sRNVaultDate) {
		pBKBarType = _sBKBarType;
		pRNVaultDate = _sRNVaultDate;
		/*
		 * 
		 */
		pKey = getKey(pBKBarType, pRNVaultDate);
	}
	
	/*
	 * Data
	 */
	private BKBarType pBKBarType;
	private RNVaultDate pRNVaultDate;
	private String pKey;
	private int pVaultBarsQuantity;
	private double pVaultBarsWeightOz;
	private int pComptaBarsQuantity;
	private double pComptaBarsWeightOz;

	/**
	 * 
	 * @param _sBKBarType
	 * @param _sRNVaultDate
	 * @return
	 */
	public static String getKey(BKBarType _sBKBarType, RNVaultDate _sRNVaultDate) {
		return _sBKBarType.getpName() + ";;" + _sRNVaultDate.getpKey();
	}

	@Override public int compareTo(RNBarTypeMetalVaultDate _sRNBarTypeMetalVaultDate) {
		Integer lCompareVault = pRNVaultDate.compareTo(_sRNBarTypeMetalVaultDate.pRNVaultDate);
		if (lCompareVault !=0) {
			return lCompareVault;
		}
		return pBKBarType.compareTo(_sRNBarTypeMetalVaultDate.pBKBarType);
	}

	/**
	 * 
	 * @param _sQuantityBars
	 * @param _sWeightOzBars
	 */
	public final void addNewDataInVault(int _sQuantityBars, double _sWeightOzBars) {
		pVaultBarsQuantity += _sQuantityBars;
		pVaultBarsWeightOz += _sWeightOzBars;
	}

	/**
	 * 
	 * @param _sQuantityBars
	 * @param _sWeightOzBars
	 */
	public final void addNewDataInCompta(int _sQuantityBars, double _sWeightOzBars) {
		pComptaBarsQuantity += _sQuantityBars;
		pComptaBarsWeightOz += _sWeightOzBars;
	}

	/*
	 * Getters & Setters
	 */
	public final BKBarType getpBKBarType() {
		return pBKBarType;
	}
	public final RNVaultDate getpRNVaultDate() {
		return pRNVaultDate;
	}
	public final String getpKey() {
		return pKey;
	}
	public final int getpVaultBarsQuantity() {
		return pVaultBarsQuantity;
	}
	public final double getpVaultBarsWeightOz() {
		return pVaultBarsWeightOz;
	}
	public final int getpComptaBarsQuantity() {
		return pComptaBarsQuantity;
	}
	public final double getpComptaBarsWeightOz() {
		return pComptaBarsWeightOz;
	}

	
	
	
	
}

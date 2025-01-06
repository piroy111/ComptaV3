package step07_reconciliation.reconciliators.vaults.vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import step02_load_transactions.objects.entity.BKEntity;
import step07_reconciliation.reconciliators.vaults.RNVault;

public class RNVaultDateManager {

	public RNVaultDateManager(RNVault _sRNVaultManager) {
		pRNVaultManager = _sRNVaultManager;
		/*
		 * 
		 */
		pMapKeyToRNVaultMap = new HashMap<>();
		pListRNVaultDateSorted = new ArrayList<>();
		pTreeMapDateToListRNVaultDate = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private RNVault pRNVaultManager;
	private Map<String, RNVaultDate> pMapKeyToRNVaultMap;
	private List<RNVaultDate> pListRNVaultDateSorted;
	private TreeMap<Integer, List<RNVaultDate>> pTreeMapDateToListRNVaultDate;

	/**
	 * 
	 * @param _sBKEntity
	 * @param _sDate
	 * @return
	 */
	public final RNVaultDate getpOrCreateRNVaultDate(BKEntity _sBKEntity, int _sDate) {
		String lKey = RNVaultDate.getKey(_sDate, _sBKEntity);
		RNVaultDate lRNVaultDate = pMapKeyToRNVaultMap.get(lKey);
		if (lRNVaultDate == null) {
			lRNVaultDate = new RNVaultDate(_sDate, _sBKEntity);
			pMapKeyToRNVaultMap.put(lKey, lRNVaultDate);
			/*
			 * 
			 */
			pListRNVaultDateSorted.add(lRNVaultDate);
			List<RNVaultDate> lListRNVaultDate = pTreeMapDateToListRNVaultDate.get(_sDate);
			if (lListRNVaultDate == null) {
				lListRNVaultDate = new ArrayList<>();
				pTreeMapDateToListRNVaultDate.put(_sDate, lListRNVaultDate);
			}
			lListRNVaultDate.add(lRNVaultDate);
		}
		return lRNVaultDate;
	}
	
	/**
	 * 
	 */
	public final void sortListRNVaultDate() {
		Collections.sort(pListRNVaultDateSorted);
	}

	/*
	 * Getters & Setters
	 */
	public final RNVault getpRNVaultManager() {
		return pRNVaultManager;
	}
	public final Map<String, RNVaultDate> getpMapKeyToRNVaultMap() {
		return pMapKeyToRNVaultMap;
	}
	public final List<RNVaultDate> getpListRNVaultDateSorted() {
		return pListRNVaultDateSorted;
	}
	public final TreeMap<Integer, List<RNVaultDate>> getpTreeMapDateToListRNVaultDate() {
		return pTreeMapDateToListRNVaultDate;
	}
	
}

package step07_reconciliation.reconciliators.platform.objects.platformtransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.RNPlatform;

public class RNPlatformTransactionManager {

	public RNPlatformTransactionManager(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
		/*
		 * 
		 */
		pMapKeyToRNPlatformTransaction = new HashMap<>();
		pTreeMapDateToListRNPlatformTransaction = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;
	private Map<String, RNPlatformTransaction> pMapKeyToRNPlatformTransaction;
	private TreeMap<Integer, List<RNPlatformTransaction>> pTreeMapDateToListRNPlatformTransaction;
	
	
	/**
	 * 
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sComment
	 * @param _sQuantity
	 * @param _sBKAccount
	 * @return
	 */
	public final RNPlatformTransaction getpOrCreateRNPlatformTransaction(int _sDate,
			BKAsset _sBKAsset,
			String _sComment,
			double _sQuantity,
			BKAccount _sBKAccount) {
		String lKey = RNPlatformTransaction.getKey(_sDate, _sBKAsset, _sComment, _sQuantity, _sBKAccount);
		RNPlatformTransaction lRNPlatformTransaction = pMapKeyToRNPlatformTransaction.get(lKey);
		if (lRNPlatformTransaction == null) {
			lRNPlatformTransaction = new RNPlatformTransaction(_sDate, _sBKAsset, _sComment, _sQuantity, _sBKAccount);
			pMapKeyToRNPlatformTransaction.put(lKey, lRNPlatformTransaction);
			/*
			 * 
			 */
			List<RNPlatformTransaction> lListRNPlatformTransaction = pTreeMapDateToListRNPlatformTransaction.get(_sDate);
			if (lListRNPlatformTransaction == null) {
				lListRNPlatformTransaction = new ArrayList<>();
				pTreeMapDateToListRNPlatformTransaction.put(_sDate, lListRNPlatformTransaction);
			}
			lListRNPlatformTransaction.add(lRNPlatformTransaction);
		}
		return lRNPlatformTransaction;
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}
	public final TreeMap<Integer, List<RNPlatformTransaction>> getpTreeMapDateToListRNPlatformTransaction() {
		return pTreeMapDateToListRNPlatformTransaction;
	}
	
}

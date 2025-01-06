package step03_partitions.abstracts.objects;

import java.util.Map;
import java.util.TreeMap;

import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDateManager;

public abstract class BKPartitionAbstract extends BKTransactionPartitionDateManager {

	public BKPartitionAbstract(BKPartitionManager _sBKPartitionManager) {
		pBKPartitionManager = _sBKPartitionManager;
		/*
		 * 
		 */
		pBKPartitionManager.declareNewBKPartitionAbstract(this);
	}
	
	/*
	 * Data
	 */
	private BKPartitionManager pBKPartitionManager;
	
	/**
	 * get and compute the NAV for the given date
	 * @return
	 */
	public final double getpNAV(String _sKey, int _sDate) {
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.get(_sDate);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			return Double.NaN;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate= lMapKeyToBKTransactionPartitionDate.get(_sKey);
		if (lBKTransactionPartitionDate == null) {
			return Double.NaN;
		}
		return lBKTransactionPartitionDate.getpNAV();
	}
	
	/**
	 * get and compute the NAV for the given date
	 * @return
	 */
	public final double getpNAVNoNaN(String _sKey, int _sDate) {
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.get(_sDate);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			return 0.;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate= lMapKeyToBKTransactionPartitionDate.get(_sKey);
		if (lBKTransactionPartitionDate == null) {
			return 0.;
		}
		return lBKTransactionPartitionDate.getpNAV();
	}

	/**
	 * Retrieve the holding of the BKAsset and return 0 if it is not held
	 * @param _sKey : as defined in the BKPartition
	 * @param _sDate
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpHoldingBKAssetNotNull(String _sKey, int _sDate, BKAsset _sBKAsset) {
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pMapKeyToTreeMapDateToBKTransactionPartitionDate.get(_sKey);
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return 0.;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(_sDate);
		if (lBKTransactionPartitionDate == null) {
			return 0.;
		}
		return lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(_sBKAsset);
	}
	
	/**
	 * get and compute the NAV for the given date
	 * @return
	 */
	public final double getpNAVNoNaN(String _sKey, int _sDate, BKAsset _sBKAsset) {
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.get(_sDate);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			return 0.;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate= lMapKeyToBKTransactionPartitionDate.get(_sKey);
		if (lBKTransactionPartitionDate == null) {
			return 0.;
		}
		return lBKTransactionPartitionDate.getpNAVNoNull(_sBKAsset);
	}

	
	
	
	
	
}

package step01_objects_from_conf_files.asset.bar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDebug;
import basicmethods.DMap;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.transaction.BKTransaction;


public class BKBar implements Comparable<BKBar> {

	public BKBar(String _sID, BKAssetMetal _sBKAssetMetal) {
		pID = _sID;
		pBKAssetMetal = _sBKAssetMetal;
		/*
		 * 
		 */
		pWeightOz = Double.NaN;
		pListFileNameOrigin = new ArrayList<>();
		pTreeMapDateToListBKTransaction = new TreeMap<>();
		/*
		 * Check if the BKBar is a fake
		 */
		String lID = pID.toUpperCase();
		pIsBarFuture = getIsFakeBar(lID);
		/*
		 * By default the BKBar belongs to nobody
		 */
		pDMapBKAccount = new DMap<>(null);
		/*
		 * By default the BKBar is nowhere
		 */
		pDMapBKEntityPhysical = new DMap<>(null);
		/*
		 * By default the bar is not delivered
		 */
		pDMapIsDelivered = new DMap<>(false);
		pDMapBKAccountAtTimeOfDelivery = new DMap<>(null);
	}

	/*
	 * Data
	 */
	private String pID;
	private BKAssetMetal pBKAssetMetal;
	private double pWeightOz;
	private List<String> pListFileNameOrigin;
	private BKBarType pBKBarType;
	private boolean pIsBarFuture;
	private TreeMap<Integer, List<BKTransaction>> pTreeMapDateToListBKTransaction;
	private DMap<BKAccount> pDMapBKAccount;
	private DMap<BKEntity> pDMapBKEntityPhysical;
	private DMap<BKAccount> pDMapBKAccountAtTimeOfDelivery;
	private DMap<Boolean> pDMapIsDelivered;
	
	/**
	 * 
	 * @param _sFileNameOrigin
	 */
	public final void declareNewFileNameOrigin(String _sFileNameOrigin) {
		if (!pListFileNameOrigin.contains(_sFileNameOrigin)) {
			pListFileNameOrigin.add(_sFileNameOrigin);
		}
	}

	/**
	 * 
	 * @param _sNameBar
	 * @return
	 */
	public static boolean getIsFakeBar(String _sNameBar) {
		String lNameBarUpper = _sNameBar.toUpperCase();
		for (String lNameFake : BKStaticConst.getLIST_NAMES_FAKE_BARS()) {
			if (lNameBarUpper.contains(lNameFake)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return pBKBarType 
				+ "; ID= " + pID
				+ "; weight(oz)= " + pWeightOz;
	}

	/**
	 * Set the status of the bar at delivered
	 *  + Store the owner of the bar at the time of delivery
	 * @param _sDate
	 */
	public final void declareDelivery(int _sDate) {
		pDMapBKAccountAtTimeOfDelivery.put(_sDate, getpBKAccountOwner(_sDate));
		pDMapIsDelivered.put(_sDate, true);
	}
	
	/**
	 * 1st Key= ascending metal<br>
	 * 2nd Key= the smallest bars first<br>
	 */
	@Override public int compareTo(BKBar _sBKBar) {
		/*
		 * Sort in terms of Metal
		 */
		Integer lCompareMetal = pBKAssetMetal.compareTo(_sBKBar.getpBKAssetMetal());
		if (lCompareMetal != 0) {
			return lCompareMetal;
		}
		/*
		 * Sort with the smallest bars first
		 */
		double lDifference = pWeightOz - _sBKBar.pWeightOz;
		if (lDifference > BKStaticConst.getERROR_ACCEPTABLE_WEIGHT_BARS()) {
			return 1;
		} else if (lDifference < -BKStaticConst.getERROR_ACCEPTABLE_WEIGHT_BARS()) {
			return -1;
		} else {
			return pID.compareTo(_sBKBar.pID);
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final BKAccount getpBKAccountOwner(int _sDate) {
		return pDMapBKAccount.get(_sDate);
	}
	public final BKEntity getpBKEntity(int _sDate) {
		return pDMapBKEntityPhysical.get(_sDate);
	}
	public final BKAccount getpBKAccountOwnerAtTimeOfDelivery(int _sDate) {
		return pDMapBKAccountAtTimeOfDelivery.get(_sDate);
	}
	public final boolean getpIsDelivered(int _sDate) {
		return pDMapIsDelivered.get(_sDate);
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void declareNewBKTransaction(BKTransaction _sBKTransaction) {
		int lDateBKTransaction = _sBKTransaction.getpDate();
		/*
		 * Store in memory
		 */
		getpListBKTransaction(lDateBKTransaction).add(_sBKTransaction);
		/*
		 * Change ownership
		 */
		List<BKTransaction> lListBKTransaction = new ArrayList<>();
		for (int lDate : pTreeMapDateToListBKTransaction.keySet()) {
			lListBKTransaction.addAll(getpListBKTransaction(lDate));
			if (lDate >= lDateBKTransaction) {				
				if (lListBKTransaction.size() > 0) {
					/*
					 * Count all the BKTransaction of that date to determine the ownership
					 */
					Map<BKAccount, Integer> lMapBKAccountToHolding = new HashMap<>();
					for (BKTransaction lBKTransaction : lListBKTransaction) {
						BKAccount lBKAccount = lBKTransaction.getpBKAccount();
						Integer lHolding = lMapBKAccountToHolding.get(lBKTransaction.getpBKAccount());
						if (lHolding == null) {
							lHolding = 0;
						}
						if (AMNumberTools.isPositiveStrict(lBKTransaction.getpQuantity())) {
							lHolding += 1;
						} else if (AMNumberTools.isNegativeStrict(lBKTransaction.getpQuantity())) {
							lHolding += -1;
						}
						lMapBKAccountToHolding.put(lBKAccount, lHolding);
					}
					/*
					 * Store the ownership
					 */
					BKAccount lBKAccountOwner = null;
					for (BKAccount lBKAccount : lMapBKAccountToHolding.keySet()) {
						if (lMapBKAccountToHolding.get(lBKAccount) == 1) {
							lBKAccountOwner = lBKAccount;
							break;
						}
					}
					pDMapBKAccount.put(lDate, lBKAccountOwner);
				}
			}
		}
		/*
		 * Change BKEntity
		 */
		for (int lDate : pTreeMapDateToListBKTransaction.keySet()) {
			if (lDate >= lDateBKTransaction) {
				lListBKTransaction = getpListBKTransaction(lDate);
				/*
				 * Count all the BKTransaction of that date to determine the entity
				 */
				Map<BKEntity, Integer> lMapBKEntityToHolding = new HashMap<>();
				for (BKTransaction lBKTransaction : lListBKTransaction) {
					BKEntity lBKEntity = lBKTransaction.getpBKEntity();
					if (BKEntityManager.getIsPhysical(lBKEntity)) {
						Integer lHolding = lMapBKEntityToHolding.get(lBKEntity);
						if (lHolding == null) {
							lHolding = 0;
						}
						if (AMNumberTools.isPositiveStrict(lBKTransaction.getpQuantity())) {
							lHolding += 1;
						} else if (AMNumberTools.isNegativeStrict(lBKTransaction.getpQuantity())) {
							lHolding += -1;
						}
						lMapBKEntityToHolding.put(lBKEntity, lHolding);
					}
				}
				/*
				 * Store the entity physical
				 */
				if (lMapBKEntityToHolding.size() > 0) {
					BKEntity lBKEntityPhysical = null;
					for (BKEntity lBKEntity : lMapBKEntityToHolding.keySet()) {
						if (lMapBKEntityToHolding.get(lBKEntity) == 1) {
							lBKEntityPhysical = lBKEntity;
							break;
						}
					}
					pDMapBKEntityPhysical.put(lDate, lBKEntityPhysical);
				}
			}
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final List<BKTransaction> getpListBKTransaction(int _sDate) {
		List<BKTransaction> lListBKTransaction = pTreeMapDateToListBKTransaction.get(_sDate);
		if (lListBKTransaction == null) {
			lListBKTransaction = new ArrayList<>();
			pTreeMapDateToListBKTransaction.put(_sDate, lListBKTransaction);
		}
		return lListBKTransaction;
	}

	/*
	 * Getters & Setters
	 */
	public final String getpID() {
		return pID;
	}
	public final double getpWeightOz() {
		return pWeightOz;
	}
	public final void setpWeightOz(double pWeightOz) {
		this.pWeightOz = pWeightOz;
	}
	public final List<String> getpListFileNameOrigin() {
		return pListFileNameOrigin;
	}
	public final BKBarType getpBKBarType() {
		return pBKBarType;
	}
	public final void setpBKBarType(BKBarType pBKBarType) {
		this.pBKBarType = pBKBarType;
	}
	public final BKAssetMetal getpBKAssetMetal() {
		return pBKAssetMetal;
	}
	public final boolean getpIsBarFuture() {
		return pIsBarFuture;
	}

	








}

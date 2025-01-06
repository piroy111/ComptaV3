package step03_partitions.abstracts.partitions;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst.mode_nav;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKHoldingAssetDate {

	protected BKHoldingAssetDate(BKAsset _sBKAsset, BKTransactionPartitionDate _sBKTransactionPartitionDate) {
		pBKAsset = _sBKAsset;
		pBKTransactionPartitionDate = _sBKTransactionPartitionDate;
		/*
		 * 
		 */
		pListBKTransactionToday = new ArrayList<>();
		pNNNUSDExecToday = 0;
		pTreeMapBKBarToHolding = new TreeMap<>();
		pTreeMapBKBarToQuantityToday = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private BKAsset pBKAsset;
	private BKTransactionPartitionDate pBKTransactionPartitionDate;
	private double pNAV;
	private double pNAVWithPricesYesterday;
	private List<BKTransaction> pListBKTransactionToday;
	private double pHolding;	// since start (not only today)
	private double pNNNUSDExec; // since start (not only today)
	private double pNNNUSDExecToday;
	private TreeMap<BKBar, Integer> pTreeMapBKBarToHolding;
	private TreeMap<BKBar, Integer> pTreeMapBKBarToQuantityToday;

	/**
	 * 
	 */
	protected final void computeHoldingAndNAV() {
		/*
		 * Initiate
		 */
		BKTransactionPartitionDate lBKTransactionPartitionDate = pBKTransactionPartitionDate.getpBKTransactionPartitionDateYesterday();
		BKHoldingAssetDate lBKAssetHoldingDateYesterday = null;
		if (lBKTransactionPartitionDate != null) {
			lBKAssetHoldingDateYesterday = lBKTransactionPartitionDate.getpOrCreateBKHoldingAssetDate(pBKAsset);		
		}
		/*
		 * NNNUSDExec (case of the paper only)(all prices of papers are already in US$)
		 */
		pNNNUSDExecToday = 0.;
		for (BKTransaction lBKTransactionToday : pListBKTransactionToday) {
			double lNNNUSDExec = lBKTransactionToday.getpQuantity() * lBKTransactionToday.getpPrice();
			if (Double.isFinite(lNNNUSDExec)) {
				pNNNUSDExecToday += lNNNUSDExec;
			}
		}
		pNNNUSDExec = pNNNUSDExecToday;
		if (lBKAssetHoldingDateYesterday != null) {
			pNNNUSDExec += lBKAssetHoldingDateYesterday.getpNNNUSDExec();
		}
		/*
		 * Holding from the past
		 */
		pHolding = 0.;
		if (lBKAssetHoldingDateYesterday != null) {
			pHolding += lBKAssetHoldingDateYesterday.getpHolding();
		}
		/*
		 * Holdings from today
		 */
		for (BKTransaction lBKTransactionToday : pListBKTransactionToday) {
			double lQuantity = lBKTransactionToday.getpQuantity();
			if (Double.isFinite(lQuantity)) {
				pHolding += lQuantity;
			}
		}
		/*
		 * NAV today
		 */
		double lPriceUSDToday = pBKAsset.getpPriceUSD(getpDate());
		if (pBKAsset.getpModeNav() == mode_nav.PHYSICAL) {
			pNAV = pHolding * lPriceUSDToday;
		} else if (pBKAsset.getpModeNav() == mode_nav.PAPER) {
			pNAV = pHolding * lPriceUSDToday - pNNNUSDExec;
		} else {
			BKCom.error("The mode of computing the NAV is not handled. You must upgrade the code here."
					+ "\nmode_nav= " + pBKAsset.getpModeNav());
		}
		/*
		 * NAV today with the prices of yesterday
		 */
		double lPriceUSDYesterday = pBKAsset.getpPriceUSD(BasicDateInt.getmPlusDay(getpDate(), -1));
		if (pBKAsset.getpModeNav() == mode_nav.PHYSICAL) {
			pNAVWithPricesYesterday = pHolding * lPriceUSDYesterday;
		} else if (pBKAsset.getpModeNav() == mode_nav.PAPER) {
			pNAVWithPricesYesterday = pHolding * lPriceUSDYesterday - pNNNUSDExec;
		} else {
			BKCom.error("The mode of computing the NAV is not handled. You must upgrade the code here."
					+ "\nmode_nav= " + pBKAsset.getpModeNav());
		}
		/*
		 * Special case of BKBar (only for physical metals)
		 */
		if (pBKAsset instanceof BKAssetMetal) {
			BKAssetMetal lBKAssetMetal = (BKAssetMetal) pBKAsset;
			/*
			 * BKBars from today
			 */
			pTreeMapBKBarToQuantityToday.clear(); 
			for (BKTransaction lBKTransaction : pListBKTransactionToday) {
				/*
				 * Load
				 */
				String lID = lBKTransaction.getpComment();
				double lWeight = lBKTransaction.getpQuantity();
				String lFileNameOrigin = lBKTransaction.getpOrigin();
				/*
				 * Check if the bar exists and is consistent with what we already input as data
				 */
				BKBar lBKBar = lBKAssetMetal.getpOrCreateAndCheckBKBar(lID, lWeight, lFileNameOrigin);
				/*
				 * Update the holding of the BKBar 
				 */
				Integer lHolding = pTreeMapBKBarToQuantityToday.get(lBKBar);
				if (lHolding == null) {
					lHolding = 0;
				}
				if (AMNumberTools.isPositiveStrict(lWeight)) {
					lHolding++;
				} else if (AMNumberTools.isNegativeStrict(lWeight)) {
					lHolding--;
				}
				pTreeMapBKBarToQuantityToday.put(lBKBar, lHolding);
			}
			/*
			 * BKBars from past
			 */
			pTreeMapBKBarToHolding.clear();
			if (lBKAssetHoldingDateYesterday != null) {
				pTreeMapBKBarToHolding = new TreeMap<>(lBKAssetHoldingDateYesterday.getpTreeMapBKBarToHolding());
			}
			/*
			 * BKBars from today
			 */
			List<BKBar> lListBKBarToWithdraw = new ArrayList<>();
			for (BKBar lBKBar : pTreeMapBKBarToQuantityToday.keySet()) {
				Integer lHolding = pTreeMapBKBarToHolding.get(lBKBar);
				if (lHolding == null) {
					lHolding = 0;
				}
				lHolding += pTreeMapBKBarToQuantityToday.get(lBKBar);
				pTreeMapBKBarToHolding.put(lBKBar, lHolding);
				/*
				 * Withdraw the BKBars from the Map
				 */
				if (lHolding == 0) {
					lListBKBarToWithdraw.add(lBKBar);
				}
			}
			/*
			 * Withdraw the BKBars from the Map
			 */
			for (BKBar lBKBar : lListBKBarToWithdraw) {
				pTreeMapBKBarToHolding.remove(lBKBar);
			}
		}
	}

	/**
	 * useful for paper assets; useless for physical assets
	 * @param _sBKTransaction
	 */
	protected final void declareNewBKTransaction(BKTransaction _sBKTransaction) {
		if (!pListBKTransactionToday.contains(_sBKTransaction)) {
			pListBKTransactionToday.add(_sBKTransaction);
		}
	}

	/**
	 * Classic
	 */
	public String toString() {
		return "BKAsset= " + pBKAsset + "; NAV= " + pNAV
				+ "; Holding= " + pHolding;
	}

	/**
	 * 
	 * @return
	 */
	public final boolean getpIsZero() {
		return AMNumberTools.isNaNOrZero(pNAV) || AMNumberTools.isNaNOrZero(pHolding);
	}

	/**
	 * 
	 * @param _sBKHoldingAssetDate
	 * @return
	 */
	public final boolean getpIsEqual(BKHoldingAssetDate _sBKHoldingAssetDate) {
		return getpDate() == _sBKHoldingAssetDate.getpDate() 
				&& AMNumberTools.isEqual(pHolding, _sBKHoldingAssetDate.pHolding)
				&& AMNumberTools.isEqual(pNNNUSDExec, _sBKHoldingAssetDate.pNNNUSDExec);		
	}

	/**
	 * 
	 * @param _sBKBar
	 * @return
	 */
	public final int getpHolding(BKBar _sBKBar) {
		if (pTreeMapBKBarToHolding.containsKey(_sBKBar)) {
			return pTreeMapBKBarToHolding.get(_sBKBar);
		} else {
			return 0;
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final BKTransactionPartitionDate getpBKTransactionPartitionDate() {
		return pBKTransactionPartitionDate;
	}
	public final double getpNAV() {
		return pNAV;
	}
	public final int getpDate() {
		return pBKTransactionPartitionDate.getpDate();
	}
	public final double getpNNNUSDExec() {
		return pNNNUSDExec;
	}
	public final double getpHolding() {
		return pHolding;
	}
	public final double getpNNNUSDExecToday() {
		return pNNNUSDExecToday;
	}
	public final List<BKTransaction> getpListBKTransactionToday() {
		return pListBKTransactionToday;
	}
	public final TreeMap<BKBar, Integer> getpTreeMapBKBarToHolding() {
		return pTreeMapBKBarToHolding;
	}
	public final TreeMap<BKBar, Integer> getpTreeMapBKBarToQuantityToday() {
		return pTreeMapBKBarToQuantityToday;
	}
	public final double getpNAVWithPricesYesterday() {
		return pNAVWithPricesYesterday;
	}



}

package step03_partitions.abstracts.partitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicTime;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKTransactionPartitionDate {

	protected BKTransactionPartitionDate(int _sDate, String _sKeyPartition,
			BKTransactionPartitionDateManager _sBKTransactionPartitionDateManager) {
		pDate = _sDate;
		pKeyPartition = _sKeyPartition;
		pBKTransactionPartitionDateManager = _sBKTransactionPartitionDateManager;
		/*
		 * 
		 */
		pListBKTransactionToday = new ArrayList<>();
		pMapBKAssetToBKHoldingAssetDate = new HashMap<>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			getpOrCreateBKHoldingAssetDate(lBKAsset);
		}
	}

	/*
	 * Data
	 */
	private String pKeyPartition;
	private int pDate;
	private BKTransactionPartitionDateManager pBKTransactionPartitionDateManager;
	private double pNAV;
	private double pNAVWithPricesYesterday;
	/*
	 * 
	 */
	private Map<BKAsset, BKHoldingAssetDate> pMapBKAssetToBKHoldingAssetDate;
	private List<BKTransaction> pListBKTransactionToday;
	private BKTransactionPartitionDate pBKTransactionPartitionDateYesterday;

	/**
	 * 
	 */
	protected final void computeNAV() {
		pNAV = 0.;
		pNAVWithPricesYesterday = 0.;
		for (BKHoldingAssetDate lBKAssetHoldingDate : pMapBKAssetToBKHoldingAssetDate.values()) {
			lBKAssetHoldingDate.computeHoldingAndNAV();
			if (!AMNumberTools.isNaNOrNullOrZero(lBKAssetHoldingDate.getpNAV())) {
				pNAV += lBKAssetHoldingDate.getpNAV();
			}
			if (!AMNumberTools.isNaNOrNullOrZero(lBKAssetHoldingDate.getpNAVWithPricesYesterday())) {
				pNAVWithPricesYesterday += lBKAssetHoldingDate.getpNAVWithPricesYesterday();
			}
		}
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public final BKHoldingAssetDate getpOrCreateBKHoldingAssetDate(BKAsset _sBKAsset) {
		BKHoldingAssetDate lBKAssetHoldingDate = pMapBKAssetToBKHoldingAssetDate.get(_sBKAsset);
		if (lBKAssetHoldingDate == null) {
			lBKAssetHoldingDate = new BKHoldingAssetDate(_sBKAsset, this);
			pMapBKAssetToBKHoldingAssetDate.put(_sBKAsset, lBKAssetHoldingDate);
		}
		return lBKAssetHoldingDate;
	}

	/**
	 * Needs a computeNAV to be totally effective, hence we keep it as protected, and the call must be made from BKTransactionPartitionDateManager
	 */
	protected final void addNewBKTransaction(BKTransaction _sBKTransaction) {
		if (!pListBKTransactionToday.contains(_sBKTransaction)) {
			pListBKTransactionToday.add(_sBKTransaction);
			getpOrCreateBKHoldingAssetDate(_sBKTransaction.getpBKAsset()).declareNewBKTransaction(_sBKTransaction);
		}
	}

	/**
	 * Classic toString
	 */
	public String toString() {
		String lToString = pDate + "; BKAssetHolding= " ;
		String lSeparator = "";
		for (BKHoldingAssetDate lBKAssetHoldingDate : pMapBKAssetToBKHoldingAssetDate.values()) {
			if (!lBKAssetHoldingDate.getpIsZero()) {
				lToString += lSeparator + lBKAssetHoldingDate;
				lSeparator = " // ";
			}
		}
		return lToString;
	}
	
	/**
	 * 
	 * @param _sBKTransactionPartitionDate
	 */
	public final void displayDifferences(BKTransactionPartitionDate _sBKTransactionPartitionDateCompare) {
		BasicPrintMsg.displayTitle(this, "Compare 2 BKTransactionPartitionDate (a main versus a compare)");
		/*
		 * Date
		 */
		if (pDate == _sBKTransactionPartitionDateCompare.getpDate()) {
			System.out.println("DateMain == DateCompare == " + pDate + " -> same date for both");
		} else {
			BasicTime.sleep(50); System.err.println("DateMain= " + pDate + " <> DateCompare= " +  _sBKTransactionPartitionDateCompare.getpDate());
		}
		/*
		 * Holdings
		 */
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			/*
			 * Load
			 */
			BKHoldingAssetDate lBKHoldingAssetDateMain = pMapBKAssetToBKHoldingAssetDate.get(lBKAsset);
			BKHoldingAssetDate lBKHoldingAssetDateCompare = _sBKTransactionPartitionDateCompare.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
			/*
			 * 
			 */
			int lJustification = 38;
			String lLine = BasicPrintMsg.getJustifiedText("BKAsset= " + lBKAsset.getpName(), lJustification)
					+ BasicPrintMsg.getJustifiedText("HoldingMain= " + lBKHoldingAssetDateMain.getpHolding(), lJustification)
					+ BasicPrintMsg.getJustifiedText("HoldingCompare= " + lBKHoldingAssetDateMain.getpHolding(), lJustification)
					+ BasicPrintMsg.getJustifiedText("NNNExecMain= " + lBKHoldingAssetDateMain.getpNNNUSDExec(), lJustification)
					+ BasicPrintMsg.getJustifiedText("NNNExecCompare= " + lBKHoldingAssetDateMain.getpNNNUSDExec(), lJustification);
			if (lBKHoldingAssetDateMain.getpIsEqual(lBKHoldingAssetDateCompare)) {
				System.out.println(lLine + " --> Ok");
			} else {
				BasicTime.sleep(50); System.err.println(lLine + " --> DIFFERENT");
			}
		}
	}
	
	/**
	 * Handle the case of null and return 0. in this case<br>
	 * Never returns NaN<br>
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpHoldingNoNaNNoNull(BKAsset _sBKAsset) {
		BKHoldingAssetDate lBKHoldingAssetDate = pMapBKAssetToBKHoldingAssetDate.get(_sBKAsset);
		if (lBKHoldingAssetDate == null) {
			return 0.;
		}
		if (!Double.isFinite(lBKHoldingAssetDate.getpHolding())) {
			return 0.;
		}
		return lBKHoldingAssetDate.getpHolding();
	}

	/**
	 * Handle the case of null and return 0. in this case<br>
	 * Never returns NaN<br>
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpNAVNoNull(BKAsset _sBKAsset) {
		BKHoldingAssetDate lBKHoldingAssetDate = pMapBKAssetToBKHoldingAssetDate.get(_sBKAsset);
		if (lBKHoldingAssetDate == null) {
			return 0.;
		}
		return lBKHoldingAssetDate.getpNAV();
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKTransactionPartitionDateManager getpBKTransactionPartitionDateManager() {
		return pBKTransactionPartitionDateManager;
	}
	public final int getpDate() {
		return pDate;
	}
	public final Map<BKAsset, BKHoldingAssetDate> getpMapBKAssetToBKHoldingAssetDate() {
		return pMapBKAssetToBKHoldingAssetDate;
	}
	public final List<BKTransaction> getpListBKTransactionToday() {
		return pListBKTransactionToday;
	}
	public final BKTransactionPartitionDate getpBKTransactionPartitionDateYesterday() {
		return pBKTransactionPartitionDateYesterday;
	}
	protected final void setpBKTransactionPartitionDateYesterday(
			BKTransactionPartitionDate pBKTransactionPartitionDateYesterday) {
		this.pBKTransactionPartitionDateYesterday = pBKTransactionPartitionDateYesterday;
	}
	public final String getpKeyPartition() {
		return pKeyPartition;
	}
	public final double getpNAV() {
		return pNAV;
	}
	public final double getpNAVWithPricesYesterday() {
		return pNAVWithPricesYesterday;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}

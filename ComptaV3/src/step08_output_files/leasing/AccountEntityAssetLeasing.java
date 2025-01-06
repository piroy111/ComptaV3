package step08_output_files.leasing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMNumberTools;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetLeasing;
import step02_load_transactions.objects.entity.BKEntity;

class AccountEntityAssetLeasing implements Comparable<AccountEntityAssetLeasing> {

	protected AccountEntityAssetLeasing(BKAccount _sBKAccount, BKEntity _sBKEntity, BKAssetLeasing _sBKAssetLeasing) {
		pBKAccount = _sBKAccount;
		pBKEntity = _sBKEntity;
		pBKAssetLeasing = _sBKAssetLeasing;
		/*
		 * 
		 */
		pListDate = new ArrayList<>();
	}
	
	/*
	 * Intrinsic data
	 */
	private BKAccount pBKAccount;
	private BKEntity pBKEntity;
	private BKAssetLeasing pBKAssetLeasing;
	/*
	 * Carrying data
	 */
	private double pAmountInUnits;
	private double pAmountInUSD;
	private double pGainBunkerInUSD;
	private List<Integer> pListDate;
	private double pAmountInUnitsLastDate;
	private double pAmountInUSDLastDate;
	
	@Override public int compareTo(AccountEntityAssetLeasing _sAccountEntityAssetLeasing) {
		return -Double.compare(pGainBunkerInUSD, _sAccountEntityAssetLeasing.pGainBunkerInUSD);
	}
	
	
	public final double getpAmountInUnits() {
		return pAmountInUnits / pListDate.size();
	}
	public final double getpAmountInUSD() {
		return pAmountInUSD / pListDate.size();
	}
	public final double getpGainBunkerInPercent() {
		if (AMNumberTools.isNaNOrNullOrZero(getpAmountInUSD())) {
			return 0;
		}
		return pGainBunkerInUSD / getpAmountInUSD() * 365 / pListDate.size();
	}

	
	public final void addNewSetData(int _sDate, double _sAmountInUnits, double _sAmountInUSD) {
		int lLastDate = getpLastDate();
		if (_sDate == lLastDate) {
			pAmountInUnitsLastDate += _sAmountInUnits;
			pAmountInUSDLastDate += _sAmountInUSD;
		} else if (_sDate > lLastDate) {
			pAmountInUnitsLastDate = _sAmountInUnits;
			pAmountInUSDLastDate = _sAmountInUSD;
			pListDate.add(_sDate);
		} else if (!pListDate.contains(_sDate)) {
			pListDate.add(_sDate);
			Collections.sort(pListDate);
		}
		pAmountInUSD += _sAmountInUSD;
		pAmountInUnits += _sAmountInUnits;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public final int getpLastDate() {
		if (pListDate.size() == 0) {
			return -1;
		} else {
			return pListDate.get(pListDate.size() - 1);
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final BKEntity getpBKEntity() {
		return pBKEntity;
	}
	public final BKAssetLeasing getpBKAssetLeasing() {
		return pBKAssetLeasing;
	}
	public final double getpGainBunkerInUSD() {
		return pGainBunkerInUSD;
	}
	public final void addpGainBunkerInUSD(double pGainBunkerInUSD) {
		this.pGainBunkerInUSD += pGainBunkerInUSD;
	}
	public final List<Integer> getpListDate() {
		return pListDate;
	}
	public final double getpAmountInUnitsLastDate() {
		return pAmountInUnitsLastDate;
	}
	public final double getpAmountInUSDLastDate() {
		return pAmountInUSDLastDate;
	}

	
	
	
	
	
	
	
	
	
	
}

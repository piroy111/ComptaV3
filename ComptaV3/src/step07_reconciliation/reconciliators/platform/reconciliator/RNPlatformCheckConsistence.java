package step07_reconciliation.reconciliators.platform.reconciliator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;
import step07_reconciliation.reconciliators.platform.objects.platformtransaction.RNPlatformTransaction;

public class RNPlatformCheckConsistence {

	public RNPlatformCheckConsistence(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
	}
	
	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;
	private Map<BKAccount, Map<BKAsset, Double>> pMapBKAccountToMapBKAssetToExpectedHolding;

	
	/**
	 * 
	 */
	public final void run() {
		List<Integer> lListDate = new ArrayList<>(pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().keySet());
		for (int lIdx = 0; lIdx < lListDate.size() - 1; lIdx++) {
			/*
			 * Load 2 files of balances
			 */
			RNPlatformDate lRNPlatformDatePast = pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().get(lListDate.get(lIdx));
			RNPlatformDate lRNPlatformDateCurrent = pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().get(lListDate.get(lIdx + 1));
			int lDateStart = lRNPlatformDatePast.getpDate();
			int lDateStop = lRNPlatformDateCurrent.getpDate();
			/*
			 * We only reconcile two consecutive months
			 */
			if (BasicDateInt.getmNumberMonths(lDateStart, lDateStop) <= 1) {
				/*
				 * Initiate, reset the map
				 */
				pMapBKAccountToMapBKAssetToExpectedHolding = new HashMap<>();
				/*
				 * Store the holding of past balance in the Map
				 */
				for (BKAccount lBKAccount : lRNPlatformDatePast.getpMapBKAccountToRNPlatformDateAccount().keySet()) {
					RNPlatformDateAccount lRNPlatformDateAccount = lRNPlatformDatePast.getpMapBKAccountToRNPlatformDateAccount().get(lBKAccount);
					for (BKAsset lBKAsset : lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().keySet()) {
						RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().get(lBKAsset);
						double lHoldingPast = lRNPlatformDateAccountAsset.getpAmountPlatform();
						setpHolding(lBKAccount, lBKAsset, lHoldingPast);
					}
				}
				/*
				 * Add the sum of the transactions to the holding of the past balance stored in the Map
				 */
				for (int lDate = BasicDateInt.getmPlusDay(lDateStart, 1); lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
					List<RNPlatformTransaction> lListRNPlatformTransaction = pRNPlatform.getpRNPlatformTransactionManager().getpTreeMapDateToListRNPlatformTransaction().get(lDate);
					if (lListRNPlatformTransaction != null) {
						for (RNPlatformTransaction lRNPlatformTransaction : lListRNPlatformTransaction) {
							/*
							 * Load transaction
							 */
							BKAccount lBKAccount = lRNPlatformTransaction.getpBKAccount();
							BKAsset lBKAsset = lRNPlatformTransaction.getpBKAsset();
							double lAmount = lRNPlatformTransaction.getpQuantity();
							/*
							 * Add to holding
							 */
							addpHolding(lBKAccount, lBKAsset, lAmount);
						}
					}
				}
				/*
				 * Add to the keys of the map the BKAccount and BKAsset of the current balance in case they are not there already<br>
				 * this will facilitate the match<br>
				 */
				for (BKAccount lBKAccount : lRNPlatformDateCurrent.getpMapBKAccountToRNPlatformDateAccount().keySet()) {
					RNPlatformDateAccount lRNPlatformDateAccount = lRNPlatformDateCurrent.getpMapBKAccountToRNPlatformDateAccount().get(lBKAccount);
					for (BKAsset lBKAsset : lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().keySet()) {
						getpHolding(lBKAccount, lBKAsset);
					}
				}
				/*
				 * Compare the Map of past balance + sum transactions to the current balances (they should be the same)
				 */
				String lErrorMsg = "";
				for (BKAccount lBKAccount : pMapBKAccountToMapBKAssetToExpectedHolding.keySet()) {
					Map<BKAsset, Double> lMapBKAssetToExpectedHolding = pMapBKAccountToMapBKAssetToExpectedHolding.get(lBKAccount);
					for (BKAsset lBKAsset : lMapBKAssetToExpectedHolding.keySet()) {
						double lHoldingPastPlusTransaction = lMapBKAssetToExpectedHolding.get(lBKAsset);
						double lHoldingCurrent = lRNPlatformDateCurrent.getpAmountPlatform(lBKAccount, lBKAsset);
						/*
						 * Compare
						 */
						double lDifferenceUSD = Math.abs(lHoldingPastPlusTransaction - lHoldingCurrent) * lBKAsset.getpPriceUSD(lDateStop);
						if (lDifferenceUSD > 0.01) {
							lErrorMsg += "\nBKAccount= " + lBKAccount.getpEmail()
								+ "; BKAsset= " + lBKAsset.getpName()
								+ "; Holding past + current transactions= " + lHoldingPastPlusTransaction
								+ "; Holding current= " + lHoldingCurrent
								+ "; lDifferenceUSD= " + lDifferenceUSD;
						}
					}
				}
				if (!lErrorMsg.equals("")) {
					lErrorMsg = "Error! The balances given by the platform are not consistent with the transactions given by the platform"
							+ "\nThis is not an error of Compta. This is an error of the files dumped by the platform and given to the compta"
							+ "\n"
							+ "\nI took the file the holding of the previous month given by the platform ('" + lRNPlatformDatePast.getpNameFile() + "') "
									+ "\nand I added all the transactions of the month ('" + lRNPlatformDateCurrent.getpDate() + BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS() + "'),"
									+ "\nand this does not give the holding of the files of the platform of the current month ('" + lRNPlatformDateCurrent.getpNameFile() + ")'"
							+ "\n"
							+ "\nDate of file balance of platform past   = " + lRNPlatformDatePast.getpDate()
							+ "\nDate of file balance of platform current= " + lRNPlatformDateCurrent.getpDate()
							+ "\n"
							+ "\nList of errors: "
							+ lErrorMsg;
					BKCom.error(lErrorMsg);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param _sBKAccount
	 * @return
	 */
	private Map<BKAsset, Double> getpOrCreateMapBKAssetToExpectedHolding(BKAccount _sBKAccount) {
		Map<BKAsset, Double> lMapBKAssetToExpectedHolding = pMapBKAccountToMapBKAssetToExpectedHolding.get(_sBKAccount);
		if (lMapBKAssetToExpectedHolding == null) {
			lMapBKAssetToExpectedHolding = new HashMap<>();
			pMapBKAccountToMapBKAssetToExpectedHolding.put(_sBKAccount, lMapBKAssetToExpectedHolding);
		}
		return lMapBKAssetToExpectedHolding;
	}
	
	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKAsset
	 * @return
	 */
	private Double getpHolding(BKAccount _sBKAccount, BKAsset _sBKAsset) {
		Map<BKAsset, Double> lMapBKAssetToExpectedHolding = getpOrCreateMapBKAssetToExpectedHolding(_sBKAccount);
		Double lHolding = lMapBKAssetToExpectedHolding.get(_sBKAsset);
		if (lHolding == null) {
			lHolding = 0.;
			setpHolding(_sBKAccount, _sBKAsset, lHolding);
		}
		return lHolding;
	}
	
	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKAsset
	 * @return
	 */
	private void setpHolding(BKAccount _sBKAccount, BKAsset _sBKAsset, double _sHolding) {
		Map<BKAsset, Double> lMapBKAssetToExpectedHolding = getpOrCreateMapBKAssetToExpectedHolding(_sBKAccount);
		lMapBKAssetToExpectedHolding.put(_sBKAsset, _sHolding);
	}
	
	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKAsset
	 * @return
	 */
	private void addpHolding(BKAccount _sBKAccount, BKAsset _sBKAsset, double _sAmountToAdd) {
		Double lHolding = getpHolding(_sBKAccount, _sBKAsset);
		lHolding += _sAmountToAdd;
		setpHolding(_sBKAccount, _sBKAsset, lHolding);
	}
	
}

package step08_output_files.bkbars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.asset.bar.BKBarType;
import step01_objects_from_conf_files.asset.bar.BKBarTypeManager;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_BKBarType extends BKOutputAbstract {

	public BKOutput_BKBarType(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
		// TODO Auto-generated constructor stub
	}

	/*
	 * ENUM
	 */
	private enum bar_status {IN_VAULT, FAKE_FUTURE, DELIVERED}

	
	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		addNewHeader("Date,BKBarType,Natural weight");
		for (bar_status lBarStatus : bar_status.values()) {
			addNewHeader("Value in US$ - " + lBarStatus.toString() + " - Bunker (US$)");
			addNewHeader("Value in US$ - " + lBarStatus.toString() + " - Clients (US$)");
		}
		for (bar_status lBarStatus : bar_status.values()) {
			addNewHeader("Number of BKBars - " + lBarStatus.toString() + " - Bunker");
			addNewHeader("Number of BKBars - " + lBarStatus.toString() + " - Clients");
		}
		for (bar_status lBarStatus : bar_status.values()) {
			addNewHeader("Amount in OZ - " + lBarStatus.toString() + " - Bunker (Oz)");
			addNewHeader("Amount in OZ - " + lBarStatus.toString() + " - Clients (Oz)");
		}		
		List<BKBarType> lListBKBarType = new ArrayList<>(BKBarTypeManager.getpMapNameToBKBarType().values());
		Collections.sort(lListBKBarType);
		/*
		 * Write the data for each BKBarType
		 */
		for (BKBarType lBKBarType : lListBKBarType) {
			String lLine = lDate
					+ "," + lBKBarType.getpName()
					+ "," + lBKBarType.getpNaturalWeightStr();
			/*
			 * Compute the amount of BKBars for each status
			 */
			Map<bar_status, Double> lMapBarStatusToAmountOzBunker = new HashMap<>();
			Map<bar_status, Double> lMapBarStatusToAmountOzClients = new HashMap<>();
			Map<bar_status, Integer> lMapBarStatusToNumberBunker = new HashMap<>();
			Map<bar_status, Integer> lMapBarStatusToNumberClients = new HashMap<>();
			for (bar_status lBarStatus : bar_status.values()) {
				lMapBarStatusToAmountOzBunker.put(lBarStatus, 0.);
				lMapBarStatusToAmountOzClients.put(lBarStatus, 0.);
				lMapBarStatusToNumberBunker.put(lBarStatus, 0);
				lMapBarStatusToNumberClients.put(lBarStatus, 0);
			}
			for (BKBar lBKBar : lBKBarType.getpListBKBar()) {
				bar_status lBarStatus = getpStatus(lBKBar, lDate);
				BKAccount lBKAccount = lBKBar.getpBKAccountOwner(lDate);
				if (lBKAccount != null) {
					if (lBKAccount.equals(BKAccountManager.getpBKAccountBunker())) {
						lMapBarStatusToAmountOzBunker.put(lBarStatus, lMapBarStatusToAmountOzBunker.get(lBarStatus) + lBKBar.getpWeightOz());
						lMapBarStatusToNumberBunker.put(lBarStatus, lMapBarStatusToNumberBunker.get(lBarStatus) + 1);
					} else {
						lMapBarStatusToAmountOzClients.put(lBarStatus, lMapBarStatusToAmountOzClients.get(lBarStatus) + lBKBar.getpWeightOz());
						lMapBarStatusToNumberClients.put(lBarStatus, lMapBarStatusToNumberClients.get(lBarStatus) + 1);
					}
				}
			}
			/*
			 * Write status and quantity in OZ and the USD
			 */
			double lPriceUSD = lBKBarType.getpBKAssetMetal().getpPriceUSD(lDate);
			boolean lIsPrintLine = false;
			for (bar_status lBarStatus : bar_status.values()) {
				lLine += "," + (lMapBarStatusToAmountOzBunker.get(lBarStatus) * lPriceUSD)
						+ "," + (lMapBarStatusToAmountOzClients.get(lBarStatus) * lPriceUSD);
				lIsPrintLine = lIsPrintLine 
						|| !AMNumberTools.isNaNOrNullOrZero(lMapBarStatusToAmountOzBunker.get(lBarStatus))
						|| !AMNumberTools.isNaNOrNullOrZero(lMapBarStatusToAmountOzClients.get(lBarStatus));
			}
			for (bar_status lBarStatus : bar_status.values()) {
				lLine += "," + lMapBarStatusToNumberBunker.get(lBarStatus)
						+ "," + lMapBarStatusToNumberClients.get(lBarStatus);
				lIsPrintLine = lIsPrintLine 
						|| lMapBarStatusToNumberBunker.get(lBarStatus) != 0
						|| lMapBarStatusToNumberClients.get(lBarStatus) != 0;
			}
			for (bar_status lBarStatus : bar_status.values()) {
				lLine += "," + lMapBarStatusToAmountOzBunker.get(lBarStatus)
						+ "," + lMapBarStatusToAmountOzClients.get(lBarStatus);
				lIsPrintLine = lIsPrintLine 
						|| !AMNumberTools.isNaNOrNullOrZero(lMapBarStatusToAmountOzBunker.get(lBarStatus))
						|| !AMNumberTools.isNaNOrNullOrZero(lMapBarStatusToAmountOzClients.get(lBarStatus));
			}
			if (lIsPrintLine) {
				addNewLineToWrite(lLine);
			}
		}
	}

	/**
	 * 
	 * @param _sBKBar
	 * @param _sDate
	 * @return
	 */
	private bar_status getpStatus(BKBar _sBKBar, int _sDate) {
		/*
		 * Load
		 */
		boolean lIsDelivered = _sBKBar.getpIsDelivered(_sDate);
		boolean lIsFake = _sBKBar.getpIsBarFuture();
		/*
		 * Find correct status
		 */
		if (lIsFake) {
			return bar_status.FAKE_FUTURE;
		} else if (lIsDelivered) {
			return bar_status.DELIVERED;
		} else {
			return bar_status.IN_VAULT;
		}
	}

}

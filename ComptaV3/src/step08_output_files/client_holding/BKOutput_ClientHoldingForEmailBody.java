package step08_output_files.client_holding;

import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_ClientHoldingForEmailBody extends BKOutputAbstract {

	public BKOutput_ClientHoldingForEmailBody(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
		super(_sBKOutputManager);
		/*
		 * 
		 */
		pBKAccount = _sBKAccount;
		addNewSuffixToNameFile(pBKAccount.getpEmail());
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;

	/**
	 * 
	 */
	public final String getpDirRoot() {
		return BKStaticDir.getOUTPUT_CLIENT();
	}

	/**
	 * 
	 * @return
	 */
	public String getpExtension() {
		return "html";
	}

	/**
	 * 
	 * @return
	 */
	public boolean getpIsAddTodayInFrontOfNameFile() {
		return false;
	}

	/**
	 * 
	 */
	@Override public void buildFileContent() {
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(pBKAccount.getpKey());
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(lDate);
		if (lBKTransactionPartitionDate == null) {
			return;
		}
		BKAssetCurrency lBKAssetCurrency = pBKAccount.getpBKAssetCurrency();
		/*
		 * Write introduction message
		 */
		addNewLineToWrite("<br>");
		addNewLineToWrite("Dear " + BasicString.getUpperCaseForNames(pBKAccount.getpOwner()) + ",<br>");
		addNewLineToWrite("<br>");
		addNewLineToWrite("Please find enclosed your Bunker Account Statement and your list of bars as of "
				+ BasicDateInt.getmDateStr(lDate) + "<br>");
		/*
		 * Compute data to put in the email body
		 */
		double lCashBalance = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAssetCurrency);
		int lNbBars = 0;
		double lValuationUSD = 0.;
		String lMsgNbBars = "";
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			int lNbBarsMetal = 0;
			for (BKAsset lBKAsset : lBKAssetMetal.getpListBKAssetDependant()) {
				BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);				
				if (lBKHoldingAssetDate != null) {
					for (BKBar lBKBar : lBKHoldingAssetDate.getpTreeMapBKBarToHolding().keySet()) {
						int lHoldingBKBar = lBKHoldingAssetDate.getpTreeMapBKBarToHolding().get(lBKBar);
						if (lHoldingBKBar == 1) {
							lNbBars++;
							lNbBarsMetal++;
							lValuationUSD += lBKBar.getpWeightOz() * lBKAssetMetal.getpPriceUSD(lDate);
						}
					}
				}
			}
			if (lNbBarsMetal != 0) {
				if (!lMsgNbBars.equals("")) {
					lMsgNbBars += "; ";
				}
				lMsgNbBars += BasicPrintMsg.afficheIntegerWithComma(lNbBarsMetal) + " " + lBKAssetMetal.getpNameMetal();
			}
		}
		/*
		 * Compute outcome
		 */
		double lValuation = lValuationUSD / lBKAssetCurrency.getpPriceUSD(lDate);
		if (!lMsgNbBars.equals("")) {
			lMsgNbBars = "(" + lMsgNbBars + ")";
		}
		/*
		 * Write body
		 */
		addNewLineToWrite("<body style=\"background-color: #FFF7F0;>\">");
		addNewLineToWrite("<br>");
		addNewLineToWrite("<table style=\"background-color: #FFF7F0;\">");
		addNewLineToWrite("<tr>"
				+ "<td>Cash Balance</td>"
				+ "<td>: " + BasicPrintMsg.afficheIntegerWithComma(lCashBalance) + " " + lBKAssetCurrency.getpName() + "</td>"
				+ "</tr>");
		addNewLineToWrite("<tr>"
				+ "<td>Total of Physical Bars Owned</td>" 
				+ "<td>: " + BasicPrintMsg.afficheIntegerWithComma(lNbBars) + " Bars " + lMsgNbBars + "</td>"
				+ "</tr>");
		addNewLineToWrite("<tr>"
				+ "<td>Bars valuation</td>"
				+ "<td>: " + BasicPrintMsg.afficheIntegerWithComma(lValuation) + " " + lBKAssetCurrency.getpName() + "</td>"
				+ "</tr>");
		addNewLineToWrite("</table>");
		addNewLineToWrite("<br>");
		addNewLineToWrite("Best Regards<br>");
		addNewLineToWrite("Bunker Group Team<br>");
	}

}

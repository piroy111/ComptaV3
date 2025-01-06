package step08_output_files.client_holding;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_ClientHolding extends BKOutputAbstract {

	public BKOutput_ClientHolding(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
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
		/*
		 * We reduce the list of BKAsset which we will print to the one on which there has been a position non zero at some day
		 */
		List<BKAsset> lListBKAssetNotNull = new ArrayList<>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
			if (lBKHoldingAssetDate != null && !AMNumberTools.isNaNOrZero(lBKHoldingAssetDate.getpHolding())) {
				lListBKAssetNotNull.add(lBKAsset);
			}
		}
		/*
		 * Write file --> the account Name
		 */
		addNewLineToWrite("Account holder '" + pBKAccount.getpEmail() + "'");
		addNewLineToWrite("Currency of account is '" + pBKAccount.getpBKAssetCurrency() + "'");
		if (lListBKAssetNotNull.size() == 0) {
			return;
		}
		addNewLineToWrite("");
		/*
		 * Write file --> the total holding of each asset
		 */
		addNewLineToWrite(",Amount,Value in US$");
		for (BKAsset lBKAsset : lListBKAssetNotNull) {
			/*
			 * Name of asset
			 */
			String lName = lBKAsset.getpName();
			if (lName.contains("LOAN")) {
				lName = lName.replaceAll("LOAN", "PROCESSED AT REFINERY");
			}
			/*
			 * Unit of Asset
			 */
			String lUnit = lName;
			if (lUnit.contains("BAR") || lUnit.contains("PROCESS")) {
				lUnit = "Oz";
			}
			/*
			 * Write content
			 */
			double lHolding = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
			double lNAV = lBKTransactionPartitionDate.getpNAVNoNull(lBKAsset);
			addNewLineToWrite("Holding of '" + lName + "'"
					+ "," + BasicPrintMsg.afficheIntegerWithComma(lHolding).replaceAll(",", " ") + " " + lUnit
					+ "," + BasicPrintMsg.afficheIntegerWithComma(lNAV).replaceAll(",", " ") + " US$");
		}
		addNewLineToWrite("");
		/*
		 * Write file --> the BKBars
		 */
		addNewLineToWrite("Metal,Bar ID,Weight in Oz");
		for (BKAsset lBKAsset : lListBKAssetNotNull) {
			if (lBKAsset instanceof BKAssetMetal) {
				BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset);
				for (BKBar lBKBar : lBKHoldingAssetDate.getpTreeMapBKBarToHolding().keySet()) {
					int lHolding = lBKHoldingAssetDate.getpTreeMapBKBarToHolding().get(lBKBar);
					if (lHolding == 1) {
						String lLine = lBKAsset.getpName()
								+ "," + lBKBar.getpID()
								+ "," + lBKBar.getpWeightOz();
						addNewLineToWrite(lLine);
					}
				}
			}
		}
	}

}

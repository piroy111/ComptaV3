package step08_output_files.loans;

import java.util.ArrayList;
import java.util.List;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_Loans extends BKOutputAbstract {

	public BKOutput_Loans(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}
	
	public boolean getpIsDisplayFileInConsole() {
		return true;
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		BKTransactionPartitionDate lBKTransactionPartitionDateBunker = pBKPartitionManager.getpBKPartitionPerBKAccount()
				.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(BKAccountManager.getpBKAccountBunker().getpKey()).get(lDate);
		BKTransactionPartitionDate lBKTransactionPartitionDatePRoy = pBKPartitionManager.getpBKPartitionPerBKAccount()
				.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(BKAccountManager.getpBKAccountPRoy().getpKey()).get(lDate);
		/*
		 * Create the list of BKAsset on which there can be a loan
		 */
		List<BKAsset> lListBKAsset = new ArrayList<>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			if (lBKAsset.getpName().contains("LOAN")) {
				lListBKAsset.add(lBKAsset);
			}
		}
		lListBKAsset.addAll(BKAssetManager.getpListBKAssetCurrencySorted());
		/*
		 * Header
		 */
		addNewHeader("" + lDate);
		for (BKAsset lBKAsset : lListBKAsset) {
			addNewHeader(lBKAsset.getpName());
		}
		/*
		 * Write the amount of loan
		 */
		String lLineBunker = BKAccountManager.getpBKAccountBunker().getpEmail();
		String lLinePRoy = BKAccountManager.getpBKAccountPRoy().getpEmail();
		String lLineBunkerUSD = BKAccountManager.getpBKAccountBunker().getpEmail() + " (US$)";
		String lLinePRoyUSD = BKAccountManager.getpBKAccountPRoy().getpEmail() + " (US$)";
		for (BKAsset lBKAsset : lListBKAsset) {
			double lHoldingBunker = getpHolding(lBKTransactionPartitionDateBunker, lDate, lBKAsset);
			lLineBunker += "," + lHoldingBunker;
			lLineBunkerUSD += "," + (lHoldingBunker * lBKAsset.getpPriceUSD(lDate));
			double lHoldingPRoy = getpHolding(lBKTransactionPartitionDatePRoy, lDate, lBKAsset);
			lLinePRoy += "," + lHoldingPRoy;
			lLinePRoyUSD += "," + (lHoldingPRoy * lBKAsset.getpPriceUSD(lDate));
		}
		addNewLineToWrite(lLineBunker);
		addNewLineToWrite(lLinePRoy);
		addNewLineToWrite(lLineBunkerUSD);
		addNewLineToWrite(lLinePRoyUSD);
	}

	/**
	 * 
	 * @param _sBKTransactionPartitionDate
	 * @param _sDate
	 * @param _sBKAsset
	 * @return
	 */
	private double getpHolding(BKTransactionPartitionDate _sBKTransactionPartitionDate, int _sDate, BKAsset _sBKAsset) {
		if (_sBKTransactionPartitionDate == null) {
			return 0.;
		}
		BKHoldingAssetDate lBKHoldingAssetDate = _sBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(_sBKAsset);
		if (lBKHoldingAssetDate == null) {
			return 0.;
		}
		return lBKHoldingAssetDate.getpHolding();
	}
	
}


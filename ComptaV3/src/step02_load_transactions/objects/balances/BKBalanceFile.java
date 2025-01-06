package step02_load_transactions.objects.balances;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.direntity.BKEntityFilesDate;
import step02_load_transactions.objects.holdings.BKHolding;
import step02_load_transactions.objects.holdings.BKHoldingManager;

public class BKBalanceFile extends BKBalanceFileAbstract {

	public BKBalanceFile(BKEntityFilesDate _sBKEntityFilesDate) {
		super(_sBKEntityFilesDate);
		/*
		 * 
		 */
		pBKHoldingManagerFromFile = new BKHoldingManager();
	}	
	
	/*
	 * Data
	 */
	private BKHoldingManager pBKHoldingManagerFromFile;

	
	@Override public String getpIsReconcile() {
		for (BKAsset lBKAsset : pBKHoldingManagerFromFile.getpMapBKAssetToBKHolding().keySet()) {
			/*
			 * Get the holding according to the file of balance, and the summed holding according to all the BKTransactions created so far
			 */
			BKHolding lHoldingFromFile = pBKHoldingManagerFromFile.getpMapBKAssetToBKHolding().get(lBKAsset);
			BKHolding lHoldingFromSumTransactions = pBKEntityFilesDate.getpBKDirEntity().getpBKHoldingManager().getpMapBKAssetToBKHolding().get(lBKAsset);
			/*
			 * Compare the BKHolding with a special method of the class BKHolding (handles NaN, and prices for paper assets)
			 */
			if (!lHoldingFromFile.getpIsEquals(lHoldingFromSumTransactions)) {
				return "\nThe sum of the transactions does not match the balance"
						+ BKStaticConst.getNTAB() + "Asset= " + lBKAsset.getpName()
						+ "\n"
						+ BKStaticConst.getNTAB() + "Balance from file= " + lHoldingFromFile
						+ BKStaticConst.getNTAB() + "Sum of transactions= " + lHoldingFromSumTransactions
						+ "\n"
						+ BKStaticConst.getNTAB() + "File Balances= '" + pBKEntityFilesDate.getpReadFileBalances() + "'"
						+ BKStaticConst.getNTAB() + "File Transaction= '" + pBKEntityFilesDate.getpReadFileTransactions() + "'"
						+ BKStaticConst.getNTAB() + "Files Docs supporting= " + pBKEntityFilesDate.getpListFileNameSupportingDoc();
			}
		}
		return "";
	}
	
	/**
	 * 
	 * @param _sBKAsset
	 * @param _sQtyExec
	 * @param _sPriceExec
	 * @param _sFileOrigin
	 */
	public final void addNewData(BKAsset _sBKAsset, double _sQtyExec, double _sPriceExec, String _sFileOrigin) {
		BKHolding lBKHolding = pBKHoldingManagerFromFile.getpMapBKAssetToBKHolding().get(_sBKAsset);
		lBKHolding.addNewData(_sQtyExec, _sPriceExec, _sFileOrigin);
	}	
	

	
	/*
	 * Getters & Setters
	 */
	
	
}

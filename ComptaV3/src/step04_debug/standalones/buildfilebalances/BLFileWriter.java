package step04_debug.standalones.buildfilebalances;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;

class BLFileWriter {

	protected BLFileWriter(BLManager _sBLManager) {
		pBLManager = _sBLManager;
	}
	
	/*
	 * Data
	 */
	private BLManager pBLManager;

	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Write missing files balances");
		for (BLBalanceDate lBLBalanceDate : pBLManager.getpBLBalanceDateManager().getpTreeMapDateToBLBalanceDate().values()) {
			/*
			 * Load
			 */
			int lDate = lBLBalanceDate.getpDate();
			String lNameFile = lDate + pBLManager.getpBLDirsAndNamesFiles().getpSuffixBalance();
			String lDir = pBLManager.getpBLDirsAndNamesFiles().getpDirBalance();
			/*
			 * Header
			 */
			String lHeader = "BKASset (as per names in the conf file 'Prices_histo_assets.csv'),Holding positive,Price executed average positive,Holding negative,Price executed average negative";
			/*
			 * Write file content
			 */
			List<String> lListLineToWrite = new ArrayList<>();
			for (BLBalanceDateAsset lBLBalanceDateAsset : lBLBalanceDate.getpTreeMapBKAssetToBLBalanceDateAsset().values()) {
				String lLine = lBLBalanceDateAsset.getpBKAsset().getpName()
						+ "," + lBLBalanceDateAsset.getpHoldingPos()
						+ "," + lBLBalanceDateAsset.getpPriceExecPos()
						+ "," + lBLBalanceDateAsset.getpHoldingNeg()
						+ "," + lBLBalanceDateAsset.getpPriceExecNeg();
				lListLineToWrite.add(lLine);
			}
			BasicFichiers.writeFile(this, lDir, lNameFile, lHeader, lListLineToWrite);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BLManager getpBLManager() {
		return pBLManager;
	}
	
}


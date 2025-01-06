package step02_load_transactions.objects.balances;

import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.direntity.BKEntityFilesDate;
import step02_load_transactions.objects.holdings.BKHolding;

public class BKNAVFile extends BKBalanceFileAbstract {

	public BKNAVFile(BKEntityFilesDate _sBKEntityFilesDate) {
		super(_sBKEntityFilesDate);
		/*
		 * 
		 */
		pNAV = Double.NaN;
		pTreeMapBKAssetToPriceForNAV = new TreeMap<>();
		pTreeMapBKAssetToHolding = new TreeMap<>();
	}
	/*
	 * Data
	 */
	private double pNAV;
	private TreeMap<BKAsset, Double> pTreeMapBKAssetToPriceForNAV;
	private TreeMap<BKAsset, Double> pTreeMapBKAssetToHolding;

	@Override public String getpIsReconcile() {
		/*
		 * Initiate + error if the NAV has not been filled
		 */
		String lErrorMsg = "";
		if (Double.isNaN(pNAV)) {
			lErrorMsg = "The NAV is missing in the file of NAV";
		}
		double lNAVRecomputed = 0.;
		/*
		 * Compute the NAV = - Sum (NNNExec) + Sum (QtyExec * PriceValo)
		 */
		String lMsgQty = "";
		String lMsgContrib = "";
		if (lErrorMsg.equals("")) {
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
				/*
				 * Holding from the summed BKTransactions
				 */
				BKHolding lBKHoldingFromSumBKTransactions = pBKEntityFilesDate.getpBKDirEntity().getpBKHoldingManager().getpMapBKAssetToBKHolding().get(lBKAsset);
				double lHoldingFromBKTransactions = lBKHoldingFromSumBKTransactions.getpQty();
				Double lHoldingFromFile = pTreeMapBKAssetToHolding.get(lBKAsset);
				/*
				 * Special case of the USD --> we add to the summed BKTransactions directly without the PriceValo since it is 1 and not necessarily in the file
				 */
				if (lBKAsset.getpName().equals("USD")) {
					double lNaVContrib = lBKHoldingFromSumBKTransactions.getpQty();
					lNAVRecomputed += lNaVContrib;
					lMsgContrib += "\nContribution of " + lBKAsset.getpName() + "   = " + lNaVContrib + " US$";
					lMsgQty += "\nQuantity of " + lBKAsset.getpName() + "   = " + lNaVContrib + " US$";
				} 
				/*
				 * Check that the holding of the file is the same as the sum of the BKTransactions
				 */
				else if (!BKHolding.getpIsEquals(lHoldingFromBKTransactions, lHoldingFromFile)) {
					if (!lErrorMsg.equals("")) {
						lErrorMsg += "\n";
					}
					lErrorMsg += "\nThe amount given by the file NAV is not the same as the sum of the BKTransactions for the BKAsset"
							+ "\nBKAsset= " + lBKAsset.getpName()
							+ "\nAmount according to file NAV= " + lHoldingFromFile
							+ "\nAmount when I sum all the BKTransactions= " + lHoldingFromBKTransactions;
				}
				/*
				 * NAV += - Sum (NNNExec) + Sum (QtyExec * PriceValo)
				 */
				else {
					double lContribToNAV = 0.;
					/*
					 * Case of a paper asset
					 */
					if (lBKAsset.getpIsPaper()) {
						lContribToNAV = -lBKHoldingFromSumBKTransactions.getpNNNExec();
					}
					/*
					 * General case for all assets
					 */
					if (!AMNumberTools.isNaNOrNullOrZero(lBKHoldingFromSumBKTransactions.getpQty())) {
						Double lPriceValo = pTreeMapBKAssetToPriceForNAV.get(lBKAsset);
						if (AMNumberTools.isNaNOrNullOrZero(lPriceValo)) {
							if (!lErrorMsg.equals("")) {
								lErrorMsg += "\n";
							}
							lErrorMsg += "\nA price of valo is missing in the file NAV because the Qty of the BKAsset is not null"
									+ "\nBKAsset= '" + lBKAsset + "'"
									+ "\nQty of BKAsset= '" + lBKHoldingFromSumBKTransactions.getpQty() + "'"
									+ "\nPriceValo from the file= '" + lPriceValo + "'"; 
						} else {
							lContribToNAV += lBKHoldingFromSumBKTransactions.getpQty() * lPriceValo;
							lMsgContrib += "\nContribution of " + lBKAsset.getpName() + "   = " + lContribToNAV + " US$";
							lMsgQty += "\nQuantity of " + lBKAsset.getpName() + "   = " + lBKHoldingFromSumBKTransactions.getpQty()	+ " @ " + lPriceValo;
						}
					}
					/*
					 * increment NAV
					 */
					lNAVRecomputed += lContribToNAV;
				}
			}
		}
		/*
		 * Check the NAV recomputed versus the NAV of the file
		 */
		if (lErrorMsg.equals("")) {
			double lError = Math.abs(pNAV - lNAVRecomputed);
			double lErrorAcceptable = BKStaticConst.getERROR_ACCEPTABLE_NAV();
			if (lError > lErrorAcceptable || !Double.isFinite(pNAV) || !Double.isFinite(lNAVRecomputed)) {
				lErrorMsg = "\nThe NAV recomputed does not match the NAV from the file"
						+ "\nNAV recomputed = - Sum (NNNExec) + Sum (QtyExec * PriceValo)"
						+ "\n[The sums are over all BKAsset, the NNNExec is computed from the BKTransactions of the BKEntity, the PriceValo are from the file NAV]"
						+ "\n"
						+ "\nNAV recomputed (US$)= " + lNAVRecomputed
						+ "\nNAV from file= " + pNAV
						+ "\nNAV recomputed (US$) - NAV from file= " + (lNAVRecomputed - pNAV)
						+ "\n"
						+ "\nContribution of each BKAsset to the NAV from Compta:"
						+ lMsgContrib
						+ "\n"
						+ "\nQuantity and price valo of each BKAsset from Compta"
						+ lMsgQty;
			} else {
				BasicPrintMsg.display(this,	null);
				BasicPrintMsg.display(this,	"NAV recomputed successfully for Date= " + pBKEntityFilesDate.getpDate()
				+ " and Entity= " + pBKEntityFilesDate.getpBKDirEntity().getpNameDirEntity());
				BasicPrintMsg.display(this,	"NAV recomputed= " + lNAVRecomputed);
				BasicPrintMsg.display(this,	"NAV from file= " + pNAV);
				BasicPrintMsg.display(this,	null);
			}
		}
		/*
		 * Treat error message
		 */
		if (!lErrorMsg.equals("")) {
			lErrorMsg += "\n"
					+ "\nBKEntity in error= '" + pBKEntityFilesDate.getpBKDirEntity().getpNameDirEntity() + "'"
					+ "\nFile of NAV in error= '" + pBKEntityFilesDate.getpReadFileBalances().getmDirPlusNameFile() + "'";
		}
		return lErrorMsg;
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @param _sAmount
	 */
	public final void declareNewAmount(BKAsset _sBKAsset, double _sAmount) {
		if (pTreeMapBKAssetToHolding.containsKey(_sBKAsset)) {
			pBKEntityFilesDate.getpBKDirEntity().getpBKDirEntityManager().addpErrorMessage(completeError("The file NAV contains twice the same BKAsset; BKAsset= " + _sBKAsset.getpName()));
		} else {
			pTreeMapBKAssetToHolding.put(_sBKAsset, _sAmount);
		}
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @param _sPriceForNAV
	 */
	public final void declarePriceForNAV(BKAsset _sBKAsset, double _sPriceForNAV) {
		if (AMNumberTools.isNaNOrNullOrZero(_sPriceForNAV)) {
			pBKEntityFilesDate.getpBKDirEntity().getpBKDirEntityManager().addpErrorMessage(completeError("The file NAV has a NaN or null price for BKAsset"
					+ "; BKAsset= " + _sBKAsset.getpName()
					+ "; Price= " + _sPriceForNAV));
		}
		pTreeMapBKAssetToPriceForNAV.put(_sBKAsset, _sPriceForNAV);
	}

	/**
	 * 
	 * @param _sMsg
	 * @return
	 */
	private String completeError(String _sMsg) {
		String lCompleteMsg = BKStaticConst.getNTAB() + "File Balances= '" + pBKEntityFilesDate.getpReadFileBalances() + "'"
				+ BKStaticConst.getNTAB() + "File Transactions= '" + pBKEntityFilesDate.getpReadFileTransactions() + "'"
				+ BKStaticConst.getNTAB() + "Files Docs supporting= " + pBKEntityFilesDate.getpListFileNameSupportingDoc();
		if (!pBKEntityFilesDate.getpBKDirEntity().getpBKDirEntityManager().getpErrorMessage().contains(lCompleteMsg)) {
			return _sMsg + lCompleteMsg;
		} else {
			return _sMsg;
		}

	}

	/*
	 * Getters & Setters
	 */
	public final double getpNAV() {
		return pNAV;
	}
	public final void setpNAV(double _sPNAV) {
		pNAV = _sPNAV;
	}


}

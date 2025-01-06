package step01_objects_from_conf_files.asset.asset;

import java.util.HashMap;
import java.util.Map;

import basicmethods.AMNumberTools;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPhysical;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.asset.bar.BKBarType;
import step01_objects_from_conf_files.asset.bar.BKBarTypeManager;

public class BKAssetMetal extends BKAssetPhysical {

	public BKAssetMetal(String _sName, int _sIdxForSort) {
		super(_sName,_sIdxForSort);
		/*
		 * 
		 */
		pNameMetal = pName.split(" ", -1)[0];
		pMapIDToBKBar = new HashMap<>();
	}

	/*
	 * Data
	 */
	private String pNameMetal;
	private Map<String, BKBar> pMapIDToBKBar;		//	each bar has an ID curved in the bar
	private BKAssetPhysical pBKAssetLoan;			//	The BKAsset of the corresponding loan
	private BKAssetPaperMetal pBKAssetPaperMetal;	//	the paper corresponding to the metal (XAU, XAG, XPT, etc.)
	private double pCostStorage;
	private double pCostOfBorrowFromProy;
	
	/**
	 * Check that the weight of the BKBar is the same
	 * @param _sID
	 * @param _sWeightOz
	 */
	public final BKBar getpOrCreateAndCheckBKBar(String _sID,
			double _sWeightOz, 
			String _sFileNameOrigin) {
		double lWeightOz = Math.abs(_sWeightOz);
		/*
		 * Check data
		 */
		if (_sID == null || _sID.equals("")) {
			BKCom.error("The _sID passed to create the BKBar are incorrect. It cannot be null or empty"
					+ "\n_sID= '" + _sID + "'"
					+ "\n_sWeightOz= " + lWeightOz
					+ "\n_sFileNameOrigin= '" + _sFileNameOrigin + "'");
		}
		if (!Double.isFinite(lWeightOz) || AMNumberTools.isZero(lWeightOz)) {
			BKCom.error("The _sWeightOz passed to create the BKBar are incorrect. It cannot be NaN or zero"
					+ "\n_sID= '" + _sID + "'"
					+ "\n_sWeightOz= " + lWeightOz
					+ "\n_sFileNameOrigin= '" + _sFileNameOrigin + "'");
		}
		/*
		 * Get or create BKBar from the ID: bars cannot have the same ID across metals
		 */
		BKBar lBKBar = pMapIDToBKBar.get(_sID);
		if (lBKBar == null) {
			lBKBar = new BKBar(_sID, this);
			pMapIDToBKBar.put(_sID, lBKBar);
		}
		/*
		 * Check the weight has not changed + assign the weight to the BKBar
		 */
		if (Double.isNaN(lBKBar.getpWeightOz())) {
			lBKBar.setpWeightOz(lWeightOz);
		} else if (Math.abs(lWeightOz - lBKBar.getpWeightOz()) > BKStaticConst.getERROR_ACCEPTABLE_WEIGHT_BARS()) {
			BKCom.error("The BKBar has had a change of weight"
					+ "\nBKBar= " + lBKBar
					+ "\nNew weight= " + lWeightOz
					+ "\nPrevious weight= " + lBKBar.getpWeightOz()
					+ "\nFile name which gave the new weight= '" + _sFileNameOrigin + "'"
					+ "\nFile names which gave the previous weight= " + lBKBar.getpListFileNameOrigin());
		}
		/*
		 * Store the file name origin
		 */
		lBKBar.declareNewFileNameOrigin(_sFileNameOrigin);
		/*
		 * Assign the BKBarType
		 */
		if (lBKBar.getpBKBarType() == null) {
			BKBarType lBKBarType = BKBarTypeManager.getpBKBarType(lWeightOz, this);
			lBKBar.setpBKBarType(lBKBarType);
			lBKBarType.declareNewBKBar(lBKBar);
		}
		return lBKBar;
	}

	/**
	 * 
	 * @return
	 */
	public final BKAssetPhysical getpBKAssetLoan() {
		if (pBKAssetLoan == null) {
			String lNameAssetLoan = pName.replaceAll("BAR", "LOAN");
			pBKAssetLoan = BKAssetManager.getpAndCheckBKAssetPhysical(lNameAssetLoan, "-");
		}
		return pBKAssetLoan;
	}
	
	/**
	 * 
	 * @return
	 */
	public final BKAssetPaperMetal getpBKAssetPaperMetal() {
		if (pBKAssetPaperMetal == null) {
			for (BKAssetPaperMetal lBKAssetPaperMetal : BKAssetManager.getpListBKAssetPaperMetalSorted()) {
				if (lBKAssetPaperMetal.getpBKAssetUnderlying().equals(this)) {
					pBKAssetPaperMetal = lBKAssetPaperMetal;
					break;
				}
			}
		}
		return pBKAssetPaperMetal;
	}
	
	/*
	 * Getters & Setters
	 */
	public final Map<String, BKBar> getpMapIDToBKBar() {
		return pMapIDToBKBar;
	}
	/**
	 * @return GOLD, SILVER or PLATINUM
	 */
	public final String getpNameMetal() {
		return pNameMetal;
	}
	public final double getpCostStorage() {
		return pCostStorage;
	}
	public final void setpCostStorage(double _sPCostStorage) {
		pCostStorage = _sPCostStorage;
	}
	public final double getpCostOfBorrowFromProy() {
		return pCostOfBorrowFromProy;
	}
	public final void setpCostOfBorrowFromProy(double _sPCostOfBorrowFromProy) {
		pCostOfBorrowFromProy = _sPCostOfBorrowFromProy;
	}

	
	

}

package step09_fiscal_year_end.step01_amortization.intangible_asset;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step09_fiscal_year_end.step01_amortization.FYAmortizationManager;

public class FYIntangibleAssetManager {

	public FYIntangibleAssetManager(FYAmortizationManager _sFYAmortizationManager) {
		pFYAmortizationManager = _sFYAmortizationManager;
		/*
		 * 
		 */
		pTreeMapNameToFYIntangibleAsset = new TreeMap<>();
		pTreeMapDateToListFYIntangibleAssetDate = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private FYAmortizationManager pFYAmortizationManager;
	private TreeMap<String, FYIntangibleAsset> pTreeMapNameToFYIntangibleAsset;
	private TreeMap<Integer, List<FYIntangibleAssetYear>> pTreeMapDateToListFYIntangibleAssetDate;
	
	/**
	 * Read CONF file, create and feed objects intangible assets
	 */
	public final void loadConfFile() {
		BasicPrintMsg.displayTitle(this, "Load conf file of amortization, create object intangible assets and fill them");
		String lDir = BKStaticDir.getCONF_FY_END();
		String lNameFile = BKStaticNameFile.getCONF_FYAMORTIZATION();
		ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
		for (List<String> lLineStr : lReadFile.getmContentList()) {
			/*
			 * Load line
			 */
			int lIdx = -1;
			String lName = lLineStr.get(++lIdx);
			double lAmount = BasicString.getDouble(lLineStr.get(++lIdx));
			int lFYDate = BasicString.getInt(lLineStr.get(++lIdx));
			int lNb = BasicString.getInt(lLineStr.get(++lIdx));
			/*
			 * Create and fill objects
			 */
			FYIntangibleAsset lFYIntangibleAsset = getpOrCreateFYIntangibleAsset(lName);
			FYIntangibleAssetYear lFYIntangibleAssetDate = lFYIntangibleAsset.getpOrCreateFYIntangibleAssetDate(lFYDate);
			lFYIntangibleAssetDate.setpAmountUSD(lAmount);
			lFYIntangibleAssetDate.setpNumberOfYearsSpreading(lNb);
		}
		BasicPrintMsg.display(this, "Created " + lReadFile.getmContentList().size()
				+ " " + FYIntangibleAssetYear.class.getSimpleName());
	}

	/**
	 * Classic get or create
	 * @param _sName
	 * @return
	 */
	public final FYIntangibleAsset getpOrCreateFYIntangibleAsset(String _sName) {
		FYIntangibleAsset lFYIntangibleAsset = pTreeMapNameToFYIntangibleAsset.get(_sName);
		if (lFYIntangibleAsset == null) {
			lFYIntangibleAsset = new FYIntangibleAsset(_sName, this);
			pTreeMapNameToFYIntangibleAsset.put(_sName, lFYIntangibleAsset);
		}
		return lFYIntangibleAsset;
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final List<FYIntangibleAssetYear> getpOrCreateListFYIntangibleAssetDate(int _sDate) {
		List<FYIntangibleAssetYear> lListFYIntangibleAssetDate = pTreeMapDateToListFYIntangibleAssetDate.get(_sDate);
		if (lListFYIntangibleAssetDate == null) {
			lListFYIntangibleAssetDate = new ArrayList<>();
			pTreeMapDateToListFYIntangibleAssetDate.put(_sDate, lListFYIntangibleAssetDate);
		}
		return lListFYIntangibleAssetDate;
	}

	/*
	 * Getters & Setters
	 */
	public final FYAmortizationManager getpFYAmortizationManager() {
		return pFYAmortizationManager;
	}
	public final TreeMap<String, FYIntangibleAsset> getpTreeMapNameToFYIntangibleAsset() {
		return pTreeMapNameToFYIntangibleAsset;
	}
	
	
	
	
	
	
}

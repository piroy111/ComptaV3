package step09_fiscal_year_end.step00_loaders;

import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step09_fiscal_year_end.FYManager;

public class FYPreviousFYIncomeManager {

	public FYPreviousFYIncomeManager(FYManager _sFYManager) {
		pFYManager = _sFYManager;
	}
	
	/*
	 * Data
	 */
	private FYManager pFYManager;
	private TreeMap<Integer, Double> pTreeMapFYDateToFYIncome;
	private double pSumPastFYIncome;
	private double pSumPastProvision;
	
	/**
	 * 
	 */
	public final void loadFromFile() {
		BasicPrintMsg.displayTitle(this, "Load FYIncome validated and frozen");
		/*
		 * 
		 */
		pTreeMapFYDateToFYIncome = new TreeMap<>();
		pSumPastFYIncome = 0.;
		pSumPastProvision = 0.;
		String lDir = BKStaticDir.getCONF_FY_END();
		String lNameFile = BKStaticNameFile.getCONF_FYINCOME_FREEZE();
		ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
		/*
		 * 
		 */
		for (List<String> lLine : lReadFile.getmContentList()) {
			if (lLine.size() >= 2) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLine.get(++lIdx));
				double lFYIncome = BasicString.getDouble(lLine.get(++lIdx));
				double lFYProvision = BasicString.getDouble(lLine.get(++lIdx));
				/*
				 * Store value
				 */
				pTreeMapFYDateToFYIncome.put(lDate, lFYIncome);
				pSumPastFYIncome += lFYIncome;
				pSumPastProvision += lFYProvision;
				/*
				 * Display
				 */
				BasicPrintMsg.display(this, "FY " + lDate + " --> FYIncome= " + lFYIncome + " $");
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final FYManager getpFYManager() {
		return pFYManager;
	}
	public final TreeMap<Integer, Double> getpTreeMapFYDateToFYIncome() {
		return pTreeMapFYDateToFYIncome;
	}
	public final double getpSumPastFYIncome() {
		return pSumPastFYIncome;
	}
	public final double getpSumPastProvision() {
		return pSumPastProvision;
	}
	
	
}

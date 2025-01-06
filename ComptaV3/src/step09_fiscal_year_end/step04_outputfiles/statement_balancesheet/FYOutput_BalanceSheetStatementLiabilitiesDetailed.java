package step09_fiscal_year_end.step04_outputfiles.statement_balancesheet;

import step09_fiscal_year_end.step03_balancesheet.liabilities.FYLiabilities;
import step09_fiscal_year_end.step03_balancesheet.liabilities.FYLiabilitiesGroup;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_BalanceSheetStatementLiabilitiesDetailed extends FYOutputFileAbstract {

	public FYOutput_BalanceSheetStatementLiabilitiesDetailed(FYOutputFileManager _sFYOutputFileManager) {
		super(_sFYOutputFileManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDateCurrent = pFYManager.getpFYDateManager().getpDateFYCurrent();
		int lDatePrevious = pFYManager.getpFYDateManager().getpDateFYPrevious();
		/*
		 * Header
		 */
		addNewHeader("FYAsset,BKAsset");
		addNewHeader(",FY " + lDateCurrent);
		addNewHeader(",,FY " + lDatePrevious);
		/*
		 * Write file content
		 */
		for (FYLiabilitiesGroup lFYLiabilitiesGroup : pFYManager.getpFYBalanceSheetManager().getpTreeMapNameToFYLiabilitiesGroup().values()) {
			addNewLineToWrite("");
			addNewLineToWrite(lFYLiabilitiesGroup.getpName());
			for (FYLiabilities lFYLiabilities : lFYLiabilitiesGroup.getpTreeMapBKAssetToFYLiabilities().values()) {
				addNewLineToWrite("," + lFYLiabilities.getpName()
						+ "," + lFYLiabilities.getpNAVCurrent()
						+ ",,," + lFYLiabilities.getpNAVPrevious());
			}
			addNewLineToWrite(lFYLiabilitiesGroup.getpName()
					+ ",,," + lFYLiabilitiesGroup.getpNAVCurrent()
					+ ",,," + lFYLiabilitiesGroup.getpNAVPrevious());
		}
	}

}

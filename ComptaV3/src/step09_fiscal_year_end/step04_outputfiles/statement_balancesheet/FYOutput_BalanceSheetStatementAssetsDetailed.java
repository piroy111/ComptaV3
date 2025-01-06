package step09_fiscal_year_end.step04_outputfiles.statement_balancesheet;

import step09_fiscal_year_end.step03_balancesheet.assets.FYEntityAsset;
import step09_fiscal_year_end.step03_balancesheet.assets.FYEntityAssetGroup;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_BalanceSheetStatementAssetsDetailed extends FYOutputFileAbstract {

	public FYOutput_BalanceSheetStatementAssetsDetailed(FYOutputFileManager _sFYOutputFileManager) {
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
		addNewHeader("Entity & Asset For report,BKEntity & BKAsset");
		addNewHeader(",FY " + lDateCurrent);
		addNewHeader(",,FY " + lDatePrevious);
		/*
		 * Write file content
		 */
		for (FYEntityAssetGroup lFYEntityAssetGroup : pFYManager.getpFYBalanceSheetManager().getpTreeMapNameToFYEntityAssetGroup().values()) {
			addNewLineToWrite("");
			addNewLineToWrite(lFYEntityAssetGroup.getpName());
			for (FYEntityAsset lFYEntityAsset : lFYEntityAssetGroup.getpTreeMapKeyToFYEntityAsset().values()) {
				addNewLineToWrite("," + lFYEntityAsset.getpName()
						+ "," + lFYEntityAsset.getpNAVCurrent()
						+ ",,," + lFYEntityAsset.getpNAVPrevious());
			}
			addNewLineToWrite(lFYEntityAssetGroup.getpName()
					+ ",,," + lFYEntityAssetGroup.getpNAVCurrent()
					+ ",,," + lFYEntityAssetGroup.getpNAVPrevious());
		}
	}

}

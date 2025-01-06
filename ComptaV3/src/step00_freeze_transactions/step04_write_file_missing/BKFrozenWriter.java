package step00_freeze_transactions.step04_write_file_missing;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step00_freeze_transactions.objects.frozen_fiscal_year.BKFrozenFiscalYear;
import step00_freeze_transactions.objects.frozen_transaction.BKFrozenTransaction;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKFrozenWriter {

	public BKFrozenWriter(BKFrozenManager _sBKFrozenManager) {
		pBKFrozenManager = _sBKFrozenManager;
	}
	
	/*
	 * Data
	 */
	private BKFrozenManager pBKFrozenManager;
	
	
	/**
	 * 
	 */
	public final void run(int _sDateFYToDo) {
		BasicPrintMsg.displayTitle(this, "Write file of BKFrozenTransaction");
		BKFrozenFiscalYear lBKFrozenFiscalYear = pBKFrozenManager.getpBKFrozenFiscalYearManager().getpOrCreateBKFrozenFiscalYear(_sDateFYToDo);
		/*
		 * File content
		 */
		int lNbPhysical = 0;
		int lNbTransfer = 0;
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKFrozenTransaction lBKFrozenTransaction : lBKFrozenFiscalYear.getpMapKeyToBKFrozenTransaction().values()) {
			/*
			 * We keep only the BKFrozenTransaction not null
			 */
			BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lBKFrozenTransaction.getpBKAssetStr(), this.getClass().getSimpleName());
			if (!lBKAsset.getpIsPaper()) {
				if (AMNumberTools.isNaNOrNullOrZero(lBKFrozenTransaction.getpQuantity())) {
					continue;
				}
			} else {
				if (AMNumberTools.isNaNOrNullOrZero(lBKFrozenTransaction.getpQuantity()) && AMNumberTools.isNaNOrNullOrZero(lBKFrozenTransaction.getpPrice())) {
					continue;
				}
			}
			/*
			 * 
			 */
			String lLine = lBKFrozenTransaction.getpDate()
					+ "," + lBKFrozenTransaction.getpBKAssetStr()
					+ "," + lBKFrozenTransaction.getpQuantity()
					+ "," + lBKFrozenTransaction.getpPrice()
					+ "," + lBKFrozenTransaction.getpBKAccountEmail()
					+ "," + lBKFrozenTransaction.getpBKAccountCurrency()
					+ "," + lBKFrozenTransaction.getpComment()
					+ "," + lBKFrozenTransaction.getpBKIncomeStr()
					+ "," + lBKFrozenTransaction.getpBKEntityName()
					+ "," + lBKFrozenTransaction.getpBKEntityType();
			lListLineToWrite.add(lLine);
			/*
			 * 
			 */
			if (lBKFrozenTransaction.getpBKEntityType() == type_entity.PHYSICAL.toString()) {
				lNbPhysical++;
			} else {
				lNbTransfer++;
			}
		}
		/*
		 * Write file
		 */
		String lDir = BKStaticDir.getFREEZE_BKTRANSACTIONS();
		int lDateFile = BasicDateInt.getmPlusDay(_sDateFYToDo, 1);
		String lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FY_TRANSACTIONS_FREEZE();
		String lHeader = BKStaticConst.getHEADER_FILE_FISCAL_YEAR_FROZEN();
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
		/*
		 * Communicate
		 */
		BasicPrintMsg.display(this, "Number of BKFrozenTransaction Physical written= " + lNbPhysical);
		BasicPrintMsg.display(this, "Number of BKFrozenTransaction Transfer written= " + lNbTransfer);
		BasicPrintMsg.display(this, "Number of BKFrozenTransaction Total    written= " + (lNbPhysical + lNbTransfer));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

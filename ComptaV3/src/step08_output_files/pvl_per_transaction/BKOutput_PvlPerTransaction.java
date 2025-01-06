package step08_output_files.pvl_per_transaction;

import java.util.List;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_PvlPerTransaction extends BKOutputAbstract{

	public BKOutput_PvlPerTransaction(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * We flush only for the dates of the month
		 */
		int lDateStop = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		int lDateStart = BasicDateInt.getmFirstDayOfMonth(lDateStop);
		/*
		 * Header
		 */
		addNewHeader(BKTransaction.getHeaderWriteInFile());
		addNewHeader("Price in US$ at which we value the BKAsset as of " + lDateStart);
		addNewHeader("P/L contribution of the BKTransaction in US$ for the month [" + lDateStart + "; " + lDateStop + "]");
		/*
		 * Loop on all the dates of the month of COMPTA and write one line per BKTransaction
		 */
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			List<BKTransaction> lListBKTransaction = BKTransactionManager.getpTreeMapDateToListBKTransaction().get(lDate);
			if (lListBKTransaction != null) {
				for (BKTransaction lBKTransaction : lListBKTransaction) {
					String lLine = lBKTransaction.getpLineWriteInFile();
					BKAsset lBKAsset = lBKTransaction.getpBKAsset();
					/*
					 * Price at which we value the BKAsset
					 */
					double lPriceValo = lBKAsset.getpPriceUSD(lDateStart);
					lLine += "," + lPriceValo;
					/*
					 * Contribution to the P/L of the month in US$
					 */
					double lPvLUSD;
					if (lBKAsset.getpIsPaper()) {
						lPvLUSD = lBKTransaction.getpQuantity() * (lPriceValo - lBKTransaction.getpPrice());
					} else {
						lPvLUSD = lBKTransaction.getpQuantity() * lPriceValo;
					}
					lLine += "," + lPvLUSD;
					/*
					 * Store line
					 */
					addNewLineToWrite(lLine);
				}
			}
		}
	}


}

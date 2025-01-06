package step07_reconciliation.reconciliators.priceshistoareupdated;

import java.util.List;

import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNPricesHistoUpdated extends BKReconciliatorAbstract {

	public RNPricesHistoUpdated(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	/*
	 * Data
	 */
	private boolean pIsCheckDone;
	
	@Override public String getpDetailsOfChecksPerformed() {
		return "Prices up to date in conf file";
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		if (!pIsCheckDone) {
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
				if (lBKAsset.getpTreeMapDateToPrice().size() > 0
						&& lBKAsset.getpTreeMapDateToPrice().lastKey() >= BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
					return;
				}
			}
			BKCom.error("The prices are missing for the date of compta in the conf file"
					+ "\nDate missing= " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()
					+ "\nFile conf to update= " + BKStaticDir.getCONF() + BKStaticNameFile.getCONF_HISTORICAL_PRICES());
			pIsCheckDone = true;
		}		
	}


	
	
}

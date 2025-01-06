package step02_load_transactions.interactivebrokers.createfilestransactions.transactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataNav;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;

public class IBTransactionCreateFromNav {

	protected IBTransactionCreateFromNav(IBTransactionManager _sIBTransactionManager) {
		pIBTransactionManager = _sIBTransactionManager;
	}
	
	/*
	 * Data
	 */
	private IBTransactionManager pIBTransactionManager;
	
	/**
	 * createIBTransactions
	 * @param _sIBDataManager
	 */
	protected final void createFromIBNav(IBDataManager _sIBDataManager) {
		List<IBDataNav> lListIBDataNav = new ArrayList<IBDataNav>(_sIBDataManager.getpMapDateToIBDataNav().values());
		Collections.sort(lListIBDataNav);
		for (int lIdx = 1; lIdx < lListIBDataNav.size(); lIdx++) {
			IBDataNav lIBDataNav0 = lListIBDataNav.get(lIdx - 1);
			IBDataNav lIBDataNav1 = lListIBDataNav.get(lIdx);
			/*
			 * Create IBTransaction
			 */
			double lAmount = lIBDataNav1.getpInterests() - lIBDataNav0.getpInterests();
			pIBTransactionManager.createIBTransactionFromNonTrade(lIBDataNav1.getpDate(), 
					IBStatic.getCOMMENT_INTERESTS(), BKAssetManager.getpBKAssetCurrencyReference(), 
					lAmount, Double.NaN, BKStaticConst.getBKINCOME_IB_INTERESTS());
		}
	}
	
}

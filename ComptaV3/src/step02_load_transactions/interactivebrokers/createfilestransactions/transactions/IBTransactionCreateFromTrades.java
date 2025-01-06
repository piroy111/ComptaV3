package step02_load_transactions.interactivebrokers.createfilestransactions.transactions;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataTrade;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;

public class IBTransactionCreateFromTrades {

	protected IBTransactionCreateFromTrades(IBTransactionManager _sIBTransactionManager) {
		pIBTransactionManager = _sIBTransactionManager;
	}
	
	/*
	 * Data
	 */
	private IBTransactionManager pIBTransactionManager;
	
	/**
	 * 
	 * @param _sIBDataManager
	 */
	protected final void createFromIBTrade(IBDataManager _sIBDataManager) {
		List<IBDataTrade> lListIBDataTrade = new ArrayList<IBDataTrade>(_sIBDataManager.getpMapIDToIBDataTrade().values());
		for (int lIdx = 0; lIdx < lListIBDataTrade.size(); lIdx++) {
			IBDataTrade lIBDataTrade = lListIBDataTrade.get(lIdx);
			/*
			 * Create a IBTransaction for the commissions
			 */
			BKAsset lBKAssetCommissions = BKAssetManager.getpAndCheckBKAsset(lIBDataTrade.getpCommissionsCurrency(), this);
			pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_COMMISSIONS(), 
					lBKAssetCommissions, lIBDataTrade.getpCommissions(), Double.NaN, BKStaticConst.getBKINCOME_IB_COMMISSIONS());
			/*
			 * Case of a CASH
			 */
			if (lIBDataTrade.getpAssetClass().equals("CASH")) {
				String[] lArray = lIBDataTrade.getpSymbol().split("\\.");
				BKAsset 	lBKAssetLeft = BKAssetManager.getpAndCheckBKAsset(lArray[0], this);
				BKAsset 	lBKAssetRight = BKAssetManager.getpAndCheckBKAsset(lArray[1], this);
				double lQuantityLeft = lIBDataTrade.getpQuantityExec();
				double lQuantityRight = -lIBDataTrade.getpQuantityExec() * lIBDataTrade.getpPriceExec();
				/*
				 * Create
				 */
				pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_FOREX(), lBKAssetLeft, lQuantityLeft, Double.NaN, BKStaticConst.getBKINCOME_IB_TRADES());
				pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_FOREX(), lBKAssetRight, lQuantityRight, Double.NaN, BKStaticConst.getBKINCOME_IB_TRADES());
			}
			/*
			 * Case of a SWAP
			 */
			else {
				BKAsset lBKAsset = null;
				double lAmount = lIBDataTrade.getpQuantityExec();
				double lPrice = lIBDataTrade.getpPriceExec();
				String lSymbol = lIBDataTrade.getpSymbol();
				/*
				 * Case of a currency swap
				 */
				if (lIBDataTrade.getpAssetClass().equals("FXCFD")) {
					if (lSymbol.startsWith("USD")) {
						lAmount = -lAmount * lPrice;
						lPrice = 1 / lPrice;
						lBKAsset = BKAssetManager.getpAndCheckBKAsset("X" + lSymbol.substring(4, 7), this);
					} else if (lSymbol.endsWith("USD")) {
						lBKAsset = BKAssetManager.getpAndCheckBKAsset("X" + lSymbol.substring(0, 3), this);
					}
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_FOREX_SWAPS(), lBKAsset, lAmount, lPrice, BKStaticConst.getBKINCOME_IB_TRADES());
				} 
				/*
				 * Case of a paper
				 */
				else if (lIBDataTrade.getpAssetClass().equals("CMDTY") || lIBDataTrade.getpAssetClass().equals("CFD")) {
					lBKAsset = BKAssetManager.getpAndCheckBKAsset(lSymbol.substring(0, 3), this);
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_METAL_SWAPS(), lBKAsset, lAmount, lPrice, BKStaticConst.getBKINCOME_IB_TRADES());
				}
				/*
				 * Case of a future OIL
				 */
				else if (lIBDataTrade.getpAssetClass().equals("FUT") && lSymbol.startsWith(IBStatic.getOIL())) {
					lBKAsset = BKAssetManager.getpAndCheckBKAsset(BKStaticConst.getNAME_BKASSET_OIL(), this);
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_OIL(), lBKAsset, lAmount, lPrice, BKStaticConst.getBKINCOME_IB_TRADES());
				}
				/*
				 * Case of a future GOLD
				 */
				else if (lIBDataTrade.getpAssetClass().equals("FUT") && lSymbol.startsWith(IBStatic.getMINI_GOLD())) {
					lBKAsset = BKAssetManager.getpAndCheckBKAsset(IBStatic.getMINI_GOLD(), this);
					/*
					 * Create
					 */
					pIBTransactionManager.createIBTransactionFromTrade(lIBDataTrade.getpDate(), IBStatic.getCOMMENT_MINI_GOLD(), lBKAsset, lAmount, lPrice, BKStaticConst.getBKINCOME_IB_TRADES());
				}
				/*
				 * Check
				 */
				else {
					BasicPrintMsg.error("IB Error"
							+ "\nI don't recognize the asset class given by IB"
							+ "\nIt might be a new asset class"
							+ "\n"
							+ "\nSymbol in IB= '" + lSymbol + "'"
							+ "\nAsset class given by IB= '" + lIBDataTrade.getpAssetClass() + "'");
				}
			}
		}
	}

	
}

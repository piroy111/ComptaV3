package step02_load_transactions.interactivebrokers.createfilestransactions.transactions;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataCash;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;
import step02_load_transactions.interactivebrokers.createfilestransactions.reports.IBReport;

public class IBTransactionCreateFromCash {

	protected IBTransactionCreateFromCash(IBTransactionManager _sIBTransactionManager) {
		pIBTransactionManager = _sIBTransactionManager;
	}
	
	/*
	 * Data
	 */
	private IBTransactionManager pIBTransactionManager;
	
	/**
	 * 
	 * @param _sIBReport0
	 * @param _sIBReport1
	 */
	protected final void createFromCashReport(IBReport _sIBReport0, IBReport _sIBReport1) {
		int lDate = _sIBReport1.getpDateStop();
		/*
		 * Case there is no overlap --> we take the raw data
		 */
		if (_sIBReport0 == null || _sIBReport1.getpDateStart() > _sIBReport0.getpDateStop()) {
			for (IBDataCash lIBDataCash : _sIBReport1.getpIBDataManager().getpMapSymbolToIBDataCash().values()) {
				BKAssetCurrency lBKAsset = BKAssetManager.getpAndCheckBKAssetCurrency(lIBDataCash.getpSymbol(), this);
				/*
				 * Create
				 */
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_DEPOSITS(), lBKAsset, lIBDataCash.getpDeposits(), Double.NaN, BKStaticConst.getBKINCOME_IB_CASH_IN());
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_INTERESTS(), lBKAsset, lIBDataCash.getpBrokerInterests(), Double.NaN, BKStaticConst.getBKINCOME_IB_INTERESTS());
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_COMMISSIONS(), lBKAsset, lIBDataCash.getpOtherFees(), Double.NaN, BKStaticConst.getBKINCOME_IB_COMMISSIONS());
			}
		} 
		/*
		 * Case there is an overlap --> we need to take the differential
		 */
		else if (_sIBReport0.getpDateStart() == _sIBReport1.getpDateStart()) {
			for (IBDataCash lIBDataCash1 : _sIBReport1.getpIBDataManager().getpMapSymbolToIBDataCash().values()) {
				/*
				 * Load
				 */
				BKAssetCurrency lBKAsset = BKAssetManager.getpAndCheckBKAssetCurrency(lIBDataCash1.getpSymbol(), this);
				IBDataCash lIBDataCash0 = _sIBReport0.getpIBDataManager().getpMapSymbolToIBDataCash().get(lIBDataCash1.getpSymbol());
				/*
				 * Compute Quantities
				 */
				double lAmountDeposit = lIBDataCash1.getpDeposits();
				double lAmountInterest = lIBDataCash1.getpBrokerInterests();
				double lAmountFees = lIBDataCash1.getpOtherFees();
				if (lIBDataCash0 != null) {
					lAmountDeposit += -lIBDataCash0.getpDeposits();
					lAmountInterest += -lIBDataCash0.getpBrokerInterests();
					lAmountFees += -lIBDataCash0.getpOtherFees();
				}
				/*
				 * Create IBTransactions
				 */
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_DEPOSITS(), lBKAsset, lAmountDeposit, Double.NaN, BKStaticConst.getBKINCOME_IB_CASH_IN());
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_INTERESTS(), lBKAsset, lAmountInterest, Double.NaN, BKStaticConst.getBKINCOME_IB_INTERESTS());
				pIBTransactionManager.createIBTransactionFromNonTrade(lDate, IBStatic.getCOMMENT_COMMISSIONS(), lBKAsset, lAmountFees, Double.NaN, BKStaticConst.getBKINCOME_IB_COMMISSIONS());
			}
		}
		/*
		 * Conflict
		 */
		else {
			BasicPrintMsg.error("Error: the report must be strictly consecutive or overlapping with DateStart"
					+ "\nEither DateStart(Report1) > DateStop(Report0) or DateStart(Report1) == DateStart(Report0)"
					+ "\n_sIBReport0= '" + _sIBReport0 + "'"
					+ "\n_sIBReport1= '" + _sIBReport1 + "'");
		}
	}
	
}

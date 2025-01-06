package step02_load_transactions.interactivebrokers.reconcilewithnav;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataClosePrice;
import step02_load_transactions.interactivebrokers.createfilestransactions.data.IBDataManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;
import step02_load_transactions.interactivebrokers.createfilestransactions.reports.IBReport;

public class BKIBClosePrice {

	protected BKIBClosePrice(BKIBManager _sBKIBManager, BKAsset _sBKAsset) {
		pBKIBManager = _sBKIBManager;
		pBKAsset = _sBKAsset;
		/*
		 * Compute
		 */
		computeClosePrice();
	}

	/*
	 * Static
	 */
	private static boolean IS_IGNORE_ZERO_PRICE_IN_IB = true;
	/*
	 * Data
	 */
	private BKIBManager pBKIBManager;
	private BKAsset pBKAsset;
	private double pValue;
	private boolean pIsInverted;
	private String pSymbolIB;
	private String pBKAssetCurrency;

	/**
	 * @return Get the close price used in the NAV in the report file
	 * @param pBKAsset
	 */
	private void computeClosePrice() {
		pIsInverted = false;
		pValue = Double.NaN;
		IBReport lIBReportLast = pBKIBManager.getpBKIBLoadIBFiles().getpIBReportLast();
		IBDataManager lIBDataManager = lIBReportLast.getpIBDataManager();
		/*
		 * Case of OIL
		 */
		if (pBKAsset.getpName().equals(BKStaticConst.getNAME_BKASSET_OIL())) {
			pBKAssetCurrency = "USD";
			IBDataClosePrice lIBDataClosePrice = null;
			for (String lSymbol : lIBDataManager.getpMapSymbolToIBDataClosePrice().keySet()) {
				if (lSymbol.startsWith(IBStatic.getOIL())) {
					lIBDataClosePrice = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(lSymbol);
					if (!AMNumberTools.isNaNOrZero(lIBDataClosePrice.getpValue())) {
						pSymbolIB = lSymbol;
						break;
					}
				}
			}
			if (lIBDataClosePrice == null || Double.isNaN(lIBDataClosePrice.getpValue())) {
				if (IS_IGNORE_ZERO_PRICE_IN_IB) {
					pValue = 0;
				} else {
					BasicPrintMsg.error("I cannot find the close price of OIL in the IB report file"
							+ "\n Oil in BKAsset= " + pBKAsset.getpName()
							+ "\n Oil name looked for= " + IBStatic.getOIL()
							+ "\n File= " + lIBReportLast.getpIBFile().getpPath().toString());
				}
			} else {
				pValue = lIBDataClosePrice.getpValue();
			}
		}
		/*
		 * Case of a currency
		 */
		else if (BKAssetManager.getpIsCurrency(pBKAsset)) {
			pSymbolIB = pBKAsset.getpName();
			pBKAssetCurrency = pSymbolIB;
			IBDataClosePrice lIBDataClosePrice = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(pSymbolIB);
			if (lIBDataClosePrice == null || AMNumberTools.isNaNOrZero(lIBDataClosePrice.getpValue())) {
				BasicPrintMsg.display(this, "Warning ! The currency does not have any price in the report file"
							+ "\n Currency= " + pSymbolIB
							+ "\n File= " + lIBReportLast.getpIBFile().getpPath().toString());
			} else {
				pValue = lIBDataClosePrice.getpValue();
			}
		}
		/*
		 * Case of a swap
		 */
		else {
			/*
			 * Case of a currency
			 */
			if (BKAssetManager.getpIsPaperCurrency(pBKAsset)) {
				/*
				 * Case of a currency -> load all the possible form of symbols
				 */
				String lSymbol0 = pBKAsset.getpName().substring(1, 4);
				String lSymbol1 = lSymbol0 + ".USD";
				String lSymbol2 = "USD." + lSymbol0;
				IBDataClosePrice lIBDataClosePrice0 = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(lSymbol0);
				IBDataClosePrice lIBDataClosePrice1 = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(lSymbol1);
				IBDataClosePrice lIBDataClosePrice2 = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(lSymbol2);
				/*
				 * Look for the symbol of CFD which has a value not null 
				 */
				if (lIBDataClosePrice1 != null) {
					pSymbolIB = lSymbol1;
					pBKAssetCurrency = "USD";
				} else if (lIBDataClosePrice2 != null) {
					pSymbolIB = lSymbol2;
					pBKAssetCurrency = lSymbol0;
					pIsInverted = true;
				} else {
					pSymbolIB = lSymbol0;
					pBKAssetCurrency = "USD";
				}
				/*
				 * 
				 */
				IBDataClosePrice lIBDataClosePrice = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(pSymbolIB);
				if (AMNumberTools.isNaNOrZero(lIBDataClosePrice.getpValue())) {
					if (lIBDataClosePrice0 != null && !AMNumberTools.isNaNOrZero(lIBDataClosePrice0.getpValue())) {
						if (pIsInverted) {
							pValue = 1 / lIBDataClosePrice0.getpValue();
						} else {
							pValue = lIBDataClosePrice0.getpValue();
						}
					} else {
						pValue = 1.;
					}
				} else {
					pValue = lIBDataClosePrice.getpValue();
				}
			}
			/*
			 * Case of a metal
			 */
			else {
				pBKAssetCurrency = "USD";
				pSymbolIB = pBKAsset.getpName() + pBKAssetCurrency;
				/*
				 * 
				 */
				IBDataClosePrice lIBDataClosePrice = lIBDataManager.getpMapSymbolToIBDataClosePrice().get(pSymbolIB);
				if (lIBDataClosePrice == null || AMNumberTools.isNaNOrZero(lIBDataClosePrice.getpValue())) {
					pValue = 1.;
				} else {
					pValue = lIBDataClosePrice.getpValue();
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public final double getpValueWithInversion() {
		if (pIsInverted) {
			return 1 / pValue;
		} else {
			return pValue;
		}
	}

	/*
	 * Getters & Setters
	 */
	public final double getpValue() {
		if (Double.isNaN(pValue)) {
			return 1.;
		}
		return pValue;
	}
	public final boolean getpIsInverted() {
		return pIsInverted;
	}
	/**
	 * @return Symbol used by IB in their report (Example: 'USD.SGD', 'XAUUSD', 'SGD')
	 */
	public String getpSymbolIB() {
		return pSymbolIB;
	}
	/**
	 * @return Code of the BKAsset which is the currency in which the quantity*price will be DENOMINATED<br>
	 * For example 'USD.SGD' returns 'SGD', 'XAUUSD' return 'USD', 'SGD' return 'SGD'
	 */
	public String getpBKAssetCurrency() {
		return pBKAssetCurrency;
	}
















}

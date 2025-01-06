package step08_output_files.abstracts;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step08_output_files.bkassets.BKOutput_BKAsset;
import step08_output_files.bkbars.BKOutput_BKBarType;
import step08_output_files.bkbars.BKOutput_BKBarTypePRoy;
import step08_output_files.bkentity.BKOutput_BKEntityAndBKAssetsHoldings;
import step08_output_files.bkentity.BKOutput_BKEntityAndBKAssetsNAV;
import step08_output_files.bkincome.BKOutput_BKIncome_to_drop;
import step08_output_files.bkincome.BKOutput_BKIncome_to_keep;
import step08_output_files.client_holding.BKOutput_ClientHolding;
import step08_output_files.client_holding.BKOutput_ClientHoldingForEmailBody;
import step08_output_files.client_holding.BKOutput_ClientHoldingForEmailBody_v2;
import step08_output_files.exposures.BKOutput_Exposures;
import step08_output_files.exposuresperbkincome.BKOutput_ExposuresPerBKIncome;
import step08_output_files.history_bkassets.BKOutput_HistoryBKAssets;
import step08_output_files.history_nav.BKOutput_HistoryNAV;
import step08_output_files.incomingfunds.BKOutput_IncomingFundsFromClients;
import step08_output_files.leasing.BKOutput_BKAssetLeasing;
import step08_output_files.proy.BKOutput_PRoyPvlHisto;
import step08_output_files.proy.BKOutput_PRoySummary;
import step08_output_files.purchasesandsales.BKOutput_PurchasesAndSales;
import step08_output_files.pvl.BKOutput_PvlHisto;
import step08_output_files.pvl_per_asset.BKOutput_PvlPerAsset;
import step08_output_files.pvl_per_transaction.BKOutput_PvlPerTransaction;
import step08_output_files.pvl_summary_monthly.BKOutput_PvlSummaryAndGroupMonthly;
import step08_output_files.pvl_summary_monthly.BKOutput_PvlSummaryMonthly;
import step08_output_files.pvl_summary_yearly.BKOutput_PvlSummaryAndGroupYearly;
import step08_output_files.pvl_summary_yearly.BKOutput_PvlSummaryYearly;
import step08_output_files.salesatcarousell.BKOutput_SalesAtCarousell;
import step08_output_files.storage.BKOutput_StorageOfClients;
import step10_launchme.BKLaunchMe;

public class BKOutputManager {

	public BKOutputManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Write files output");
		/*
		 * Analysis for Bunker
		 */
		new BKOutput_BKIncome_to_drop(this).writeFile();
		new BKOutput_BKIncome_to_keep(this).writeFile();
		new BKOutput_BKBarType(this).writeFile();
		new BKOutput_BKBarTypePRoy(this).writeFile();
		new BKOutput_BKAsset(this).writeFile();
		new BKOutput_BKAssetLeasing(this).writeFile();
		new BKOutput_IncomingFundsFromClients(this).writeFile();
		new BKOutput_PvlHisto(this).writeFile();
		new BKOutput_BKEntityAndBKAssetsNAV(this).writeFile();
		new BKOutput_BKEntityAndBKAssetsHoldings(this).writeFile();
		new BKOutput_PvlPerTransaction(this).writeFile();
		new BKOutput_PvlPerAsset(this).writeFile();
		new BKOutput_Exposures(this).writeFile();
		new BKOutput_PvlSummaryMonthly(this).writeFile();
		new BKOutput_PvlSummaryAndGroupMonthly(this).writeFile();
		new BKOutput_PvlSummaryYearly(this).writeFile();
		new BKOutput_PvlSummaryAndGroupYearly(this).writeFile();
		new BKOutput_PurchasesAndSales(this).writeFile();
		new BKOutput_SalesAtCarousell(this).writeFile();
		new BKOutput_ExposuresPerBKIncome(this).writeFile();
		new BKOutput_StorageOfClients(this).writeFile();		
		/*
		 * PRoy
		 */
		new BKOutput_PRoySummary(this).writeFile();
		new BKOutput_PRoyPvlHisto(this).writeFile();
		new BKOutput_HistoryBKAssets(this, BKAccountManager.getpBKAccountPRoy()).writeFile();
		new BKOutput_HistoryNAV(this, BKAccountManager.getpBKAccountPRoy()).writeFile();
		/*
		 * Client: we write the client files only when the COMPTA has been validated
		 */
		if (!BKStaticConst.getIS_SKIP_WRITE_FILES_CLIENTS()) {
			if (BKStaticConst.getDATE_FREEZE_COMPTA_MIN() >= BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
				for (BKAccount lBKAccount : BKAccountManager.getpListBKAccountExceptBunker()) {
					new BKOutput_ClientHolding(this, lBKAccount).writeFileWithoutTitle();
					new BKOutput_ClientHoldingForEmailBody(this, lBKAccount).writeFileWithoutTitle();
					new BKOutput_ClientHoldingForEmailBody_v2(this, lBKAccount).writeFileWithoutTitle();
//					new BKOutput_ClientNAV(this, lBKAccount).writeFileWithoutTitle();
//					new BKOutput_ClientNAV_v2_old(this, lBKAccount).writeFileWithoutTitle();
				}
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}

}

package step05_transactions_computed_by_compta.computors;

import java.util.HashMap;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetLeasing;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.conffiles.BKConfLoaderLeasingGain;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.partitions.BKPartitionPerBKEntityAndBKAccount;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;
import step08_output_files.leasing.BKOutput_BKAssetLeasing;

public class BKComptaComputorProvisionsLeasing extends BKComptaComputorAbstract {

	public BKComptaComputorProvisionsLeasing(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Data
	 */
	private BKPartitionManager pBKPartitionManager;
	private BKPartitionPerBKEntityAndBKAccount pBKPartitionPerBKEntityAndBKAccount;
	private Map<BKEntity, Double> pMapBKEntityToGainLeasing;

	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "Compute provisions for estimated gain from Condor");		
	}

	@Override public void initiateMonth() {
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pBKPartitionPerBKEntityAndBKAccount = pBKPartitionManager.getpBKPartitionPerBKEntityAndBKAccount();
		pMapBKEntityToGainLeasing = new HashMap<>();
	}

	@Override public void computeNewTransactionsDaily(int _sDate) {
		if (_sDate < BKStaticConst.getDATE_START_LEASING_CONDOR_TYPE()) {
			return;
		}
		/*
		 * Loop on all the BKEntity for which we gave a %gain in the CONF file, then we compute the gain in USD for Bunker
		 */
		for (BKAssetLeasing lBKAssetLeasing : BKAssetManager.getpListBKAssetLeasingSorted()) {
			/*
			 * Detect the BKEntities with a position
			 */
			for (BKEntity lBKEntity : BKEntityManager.getListBKEntity()) {
				/*
				 * FaiGold_related --> FaiFold_Leasing ; Condor_related --> Condor_Leasing
				 */
				BKEntity lBKEntityLeasing = BKConfLoaderLeasingGain.getpBKEntityPhysical(lBKEntity, this);
				/*
				 * Loop over the accounts to select the ones with a position
				 */
				for (BKAccount lBKAccount : BKAccountManager.getpListBKAccount()) {
					String lKeyBKPartition = BKPartitionPerBKEntityAndBKAccount.getKey(lBKEntity, lBKAccount);
					double lHolding = pBKPartitionPerBKEntityAndBKAccount.getpHoldingBKAssetNotNull(lKeyBKPartition, _sDate, lBKAssetLeasing);
					if (!AMNumberTools.isNaNOrNullOrZero(lHolding)) {
						/*
						 * Compute the gain for Bunker
						 */
						double lGainPercentForBunker = BKConfLoaderLeasingGain.getProfitPercentForBunker(lBKEntityLeasing, lBKAssetLeasing, lBKAccount, this);
						double lPriceUSD = lBKAssetLeasing.getpPriceUSD(_sDate);
						double lNNNUSD = lHolding * lPriceUSD;
						double lGainBunkerInUSD = lNNNUSD * lGainPercentForBunker / 365.;
						/*
						 * Store the daily value in the monthly value
						 */
						Double lGainBunkerMonthly = pMapBKEntityToGainLeasing.get(lBKEntityLeasing);
						if (lGainBunkerMonthly == null) {
							lGainBunkerMonthly = 0.;
						}
						lGainBunkerMonthly += lGainBunkerInUSD;
						pMapBKEntityToGainLeasing.put(lBKEntityLeasing, lGainBunkerMonthly);
						/*
						 * Bridge to the file output (display) so that the data displayed is the same as the one which lead to the computation
						 */
						BKOutput_BKAssetLeasing.declareNewGain(lBKEntityLeasing, lBKAccount, lBKAssetLeasing, lHolding, lNNNUSD, lGainBunkerInUSD, _sDate);
					}
				}
			}
		}
	}

	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		if (_sLastDateOfMonth < BKStaticConst.getDATE_START_LEASING_CONDOR_TYPE()) {
			return;
		}
		/*
		 * 
		 */
		for (BKEntity lBKEntity : pMapBKEntityToGainLeasing.keySet()) {
			double lGainLeasingUSD = pMapBKEntityToGainLeasing.get(lBKEntity);
			if (AMNumberTools.isNaNOrNullOrZero(lGainLeasingUSD)) {
				continue;
			}
			/*
			 * Check the existence of the BKIncome
			 */
			String lNameEntity = getpNameEntity(lBKEntity);
			String lBKIncomeStrProvision = BKStaticConst.getBKINCOME_PROVISION() + lNameEntity;
			BKIncome lBKIncomeProvision = BKIncomeManager.getpAndCheckBKIncome(lBKIncomeStrProvision, this);
			String lBKIncomeStrOperation = BKStaticConst.getBKINCOME_LEASING_GAIN() + lNameEntity;
			BKIncome lBKIncomeOperation = BKIncomeManager.getpAndCheckBKIncome(lBKIncomeStrOperation, this);
			/*
			 * Get the amount of provision YtD real time --> we cannot put the provision negative
			 */
			String lKeyPartition = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncomeProvision, BKAccountManager.getpBKAccountBunker());
			double lProvisionUSD = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpHoldingBKAssetNotNull(lKeyPartition, _sLastDateOfMonth, BKAssetManager.getpBKAssetCurrencyReference());
			/*
			 * Communication
			 */
			BasicPrintMsg.displayTitle(this, "lBKEntity= " + lBKEntity);
			BasicPrintMsg.display(this, "lBKEntity= " + lBKEntity + "; Total expected gain= " + BasicPrintMsg.afficheIntegerWithComma(lGainLeasingUSD) + " USD");
			BasicPrintMsg.display(this, "lBKEntity= " + lBKEntity + "; Existing provisions= " + BasicPrintMsg.afficheIntegerWithComma(lProvisionUSD) + " USD");
			/*
			 * Cap the gain
			 */
			lGainLeasingUSD = Math.max(0, Math.min(lProvisionUSD, lGainLeasingUSD));
			/*
			 * Communication
			 */
			BasicPrintMsg.display(this, "Final gain passed taken from provisions= " + BasicPrintMsg.afficheIntegerWithComma(lGainLeasingUSD) + " USD");
			/*
			 * Pass if there is no provision to take
			 */
			if (AMNumberTools.isNegativeOrNull(lGainLeasingUSD)) {
				BasicPrintMsg.display(this, "Nothing done");
				continue;				
			}
			/*
			 * Create the BKTransaction
			 */
			String lComment = "Gain for the month of " 
					+ BasicDateInt.getmMonthName(BasicDateInt.getmMonth(_sLastDateOfMonth)) 
					+ " " + BasicDateInt.getmYear(_sLastDateOfMonth); 
			addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, BKAssetManager.getpBKAssetCurrencyReference(), 
					-lGainLeasingUSD, Double.NaN, 
					BKAccountManager.getpBKAccountBunker(),	lComment, lBKIncomeProvision, pBKEntityTransfer));
			addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, BKAssetManager.getpBKAssetCurrencyReference(), 
					lGainLeasingUSD, Double.NaN, 
					BKAccountManager.getpBKAccountBunker(),	lComment, lBKIncomeOperation, pBKEntityTransfer));
			/*
			 * Communication
			 */
			lProvisionUSD = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpHoldingBKAssetNotNull(lKeyPartition, _sLastDateOfMonth, BKAssetManager.getpBKAssetCurrencyReference());
			BasicPrintMsg.display(this, "Provision after gain passing= " + BasicPrintMsg.afficheIntegerWithComma(lProvisionUSD) + " USD");
		}
	}

	/**
	 * 
	 * @param _sBKEntity
	 * @return
	 */
	private String getpNameEntity(BKEntity _sBKEntity) {
		String lName = _sBKEntity.getpName();
		if (lName.endsWith(BKStaticConst.getBKENTITY_LEASING())) {
			return lName.substring(0, lName.length() - BKStaticConst.getBKENTITY_LEASING().length());
		}
		BKCom.error("The name a BKEntityLeasing should finish with the suffix '"
				+ BKStaticConst.getBKENTITY_LEASING() + "'"
				+ "\nBKEntity in error= " + _sBKEntity);
		return null;
	}



}


package step08_output_files.incomingfunds;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;

class OPIncomingFundDate extends OPRoot {

	protected OPIncomingFundDate(int _sDate, OPIncomingFundDateManager _sOPIncomingFundDateManager) {
		super(_sDate);
		pOPIncomingFundDateManager = _sOPIncomingFundDateManager;
	}

	/*
	 * Data
	 */
	private OPIncomingFundDateManager pOPIncomingFundDateManager;
	private Map<String, OPSourceDate> pTreeMapSourceToOPSource;
	private OPIncomingFundDate pOPIncomingFundDatePrevious;
	private Map<BKAccount, OPAccountDate> pMapBKAccountToAccountDate;

	@Override protected OPRoot getpOPRootPrevious() {
		return pOPIncomingFundDatePrevious;
	}

	/**
	 * 
	 */
	public final void createAndFillOPIncomingFundDate() {
		/*
		 * Initiate
		 */
		pTreeMapSourceToOPSource = new TreeMap<>();
		pMapBKAccountToAccountDate = new HashMap<>();
		BKPartitionPerBKIncomeAndBKAccount lBKPartitionPerBKIncomeAndAccount = pOPIncomingFundDateManager
				.getpBKOutput_IncomingFundsFromClients().getpBKPartitionManager().getpBKPartitionPerBKIncomeAndBKAccount();
		BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_INCOMING_FUNDS(), 
				this.getClass().getSimpleName());
		/*
		 * 
		 */
		for (BKAccount lBKAccount : BKAccountManager.getpListBKAccountExceptBunkerAndPRoy()) {
			/*
			 * Initiate
			 */
			String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncome, lBKAccount);
			TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = lBKPartitionPerBKIncomeAndAccount
					.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
			if (lTreeMapDateToBKTransactionPartitionDate == null) {
				continue;
			}
			BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(pDate);
			/*
			 * Compute the amount of incoming fund since the beginning of time
			 */
			if (lBKTransactionPartitionDate != null) {
				for (BKAssetCurrency lBKAssetCurrency : BKAssetManager.getpListBKAssetCurrencySorted()) {
					double lIncomingFundAccount = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAssetCurrency);
					/*
					 * Add to source
					 */
					OPSourceDate lOPSourceDate = getpOrCreateOPSourceDate(lBKAccount.getpSource());
					lOPSourceDate.addpIncomingFunds(lIncomingFundAccount, lBKAssetCurrency);
					/*
					 * Add to account
					 */
					OPAccountDate lOPAccountDate = getpOrCreateOPAccountDate(lBKAccount);
					lOPAccountDate.addpIncomingFunds(lIncomingFundAccount, lBKAssetCurrency);
					/*
					 * Add to total
					 */
					addpIncomingFunds(lIncomingFundAccount, lBKAssetCurrency);
				}
			}
		}
	}

	/**
	 * 
	 * @param _sSource
	 * @return
	 */
	public final OPSourceDate getpOrCreateOPSourceDate(String _sSource) {
		OPSourceDate lOPSourceDate = pTreeMapSourceToOPSource.get(_sSource);
		if (lOPSourceDate == null) {
			lOPSourceDate = new OPSourceDate(_sSource, this);
			pTreeMapSourceToOPSource.put(_sSource, lOPSourceDate);
		}
		return lOPSourceDate;
	}

	/**
	 * Classic get or create
	 * @param _sBKAccount
	 * @return
	 */
	public final OPAccountDate getpOrCreateOPAccountDate(BKAccount _sBKAccount) {
		OPAccountDate lOPAccountDate = pMapBKAccountToAccountDate.get(_sBKAccount);
		if (lOPAccountDate == null) {
			lOPAccountDate = new OPAccountDate(this, _sBKAccount);
			pMapBKAccountToAccountDate.put(_sBKAccount, lOPAccountDate);
		}
		return lOPAccountDate;
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final OPIncomingFundDateManager getpOPIncomingFundDateManager() {
		return pOPIncomingFundDateManager;
	}
	public final Map<String, OPSourceDate> getpTreeMapSourceToOPSource() {
		return pTreeMapSourceToOPSource;
	}
	public final OPIncomingFundDate getpOPIncomingFundDatePrevious() {
		return pOPIncomingFundDatePrevious;
	}
	public final void setpOPIncomingFundDatePrevious(OPIncomingFundDate _sPOPIncomingFundDatePrevious) {
		pOPIncomingFundDatePrevious = _sPOPIncomingFundDatePrevious;
	}
	public final Map<BKAccount, OPAccountDate> getpMapBKAccountToAccountDate() {
		return pMapBKAccountToAccountDate;
	}









}

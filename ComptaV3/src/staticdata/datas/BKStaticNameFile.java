package staticdata.datas;

public class BKStaticNameFile {

	/*
	 * CONF
	 */
	private static String CONF_HISTORICAL_PRICES = "Prices_histo_assets.csv";
	private static String CONF_ACCOUNTS = "Accounts and currency.csv";
	private static String CONF_BKINCOME = "_BKIncome.csv";
	private static String CONF_BKBARTYPE = "BKBarType.csv";
	private static String CONF_BKASSETMETAL = "BKAssetMetal.csv";
	private static String CONF_BKCURRENCY = "BKCurrency.csv";
	private static String CONF_BKLEASING = "BKLeasing_profit.csv";
	private static String CONF_DATE_FREEZE ="Date_freeze_Compta_creating_files.csv";
	private static String CONF_FYINCOME_FREEZE = "FYIncomeFreeze.csv";
	private static String CONF_FYFROZEN_FREEZE = "FYFrozenFreeze.csv";
	private static String CONF_DRAW_COMMERCIAL = "Draw_commercial.csv";
	/*
	 * SUFFIX
	 */
	private static String SUFFIX_TRANSACTIONS = "_Transactions.csv";
	private static String SUFFIX_DOCS = "_Docs_";
	private static String SUFFIX_BALANCES = "_Balances.csv";
	private static String SUFFIX_DELIVERIES = "_Bars_deliveries.csv";
	private static String SUFFIX_MALCA = "_Malca_Physical_Balances.csv";
	private static String SUFFIX_RECONCILIATOR = "_Reconciliator.csv";
	private static String SUFFIX_RECONCILIATOR_VAULTS = "_Vaults_Reconciliator.csv";
	private static String SUFFIX_PROOF = "_Supporting_evidence.csv";
	private static String SUFFIX_RECONCILIATION_PLATFORM_BALANCE = "_Holdings_clients_end_of_month.csv";
	private static String SUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS = "_Platform_all_transactions_monthly.csv";
	private static String SUFFIX_OUTPUT_RECONCILIATION_PLATFORM_BALANCES = "_Platform_balances_Reconciliator.csv";
	private static String SUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS = "_Platform_transactions_Reconciliator.csv";
	private static String SUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA = "_Platform_BKTransaction_Reconciliator.csv";
	private static String PREFIX_BKBAR_REAL_WEIGHT = "BKBar_real_weight";
	private static String SUFFIX_TIME_STAMP_COMPUTATION_COMPTA = "_Time_stamp_computation_compta.csv";
	private static String SUFFIX_OANDA = "_OANDA.csv";
	private static String SUFFIX_OANDA_CRYPTOS = "_OANDA_Crypto.csv";
	private static String SUFFIX_IB = "_IB_report.csv";
	private static String SUFFIX_TRACK_FILE = "_file_tracker.csv";
	/*
	 * Specific
	 */
	private static String SUFFIX_PURCHASES_FROM_CLIENTS = "_Purchases_and_sales_from_clients_Platform_Transactions.csv";
	/*
	 * FY
	 */
	private static String CONF_FYAMORTIZATION = "FYAmortization.csv";
	private static String SUFFIX_FY_TRANSACTIONS_FREEZE = "_FY_Transactions_freeze.csv";
	private static String SUFFIX_FREEZE_CAPITAL = "_Frozen_capital.csv";
	private static String IS_IN_CHARGE_BKFROZEN = "Is_in_charge_writing_BKFrozen.txt";
	
	/*
	 * Getters & Setters
	 */
	public static final String getCONF_HISTORICAL_PRICES() {
		return CONF_HISTORICAL_PRICES;
	}
	public static final String getCONF_ACCOUNTS() {
		return CONF_ACCOUNTS;
	}
	public static final String getCONF_BKINCOME() {
		return CONF_BKINCOME;
	}
	public static final String getCONF_BKBARTYPE() {
		return CONF_BKBARTYPE;
	}
	public static final String getSUFFIX_TRANSACTIONS() {
		return SUFFIX_TRANSACTIONS;
	}
	public static final String getSUFFIX_DOCS() {
		return SUFFIX_DOCS;
	}
	public static final String getSUFFIX_BALANCES() {
		return SUFFIX_BALANCES;
	}
	public static final String getSUFFIX_DELIVERIES() {
		return SUFFIX_DELIVERIES;
	}
	public static final String getCONF_BKCURRENCY() {
		return CONF_BKCURRENCY;
	}
	public static final String getCONF_BKASSETMETAL() {
		return CONF_BKASSETMETAL;
	}
	public static final String getSUFFIX_MALCA() {
		return SUFFIX_MALCA;
	}
	public static final String getSUFFIX_RECONCILIATOR() {
		return SUFFIX_RECONCILIATOR;
	}
	public static final String getSUFFIX_PROOF() {
		return SUFFIX_PROOF;
	}
	public static final String getSUFFIX_RECONCILIATOR_VAULTS() {
		return SUFFIX_RECONCILIATOR_VAULTS;
	}
	public static final String getSUFFIX_RECONCILIATION_PLATFORM_BALANCE() {
		return SUFFIX_RECONCILIATION_PLATFORM_BALANCE;
	}
	public static final String getSUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS() {
		return SUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS;
	}
	public static final String getSUFFIX_OUTPUT_RECONCILIATION_PLATFORM_BALANCES() {
		return SUFFIX_OUTPUT_RECONCILIATION_PLATFORM_BALANCES;
	}
	public static final String getSUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS() {
		return SUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS;
	}
	public static final String getSUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA() {
		return SUFFIX_OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA;
	}
	public static final String getPREFIX_BKBAR_REAL_WEIGHT() {
		return PREFIX_BKBAR_REAL_WEIGHT;
	}
	public static final String getSUFFIX_TIME_STAMP_COMPUTATION_COMPTA() {
		return SUFFIX_TIME_STAMP_COMPUTATION_COMPTA;
	}
	public static final String getSUFFIX_OANDA() {
		return SUFFIX_OANDA;
	}
	public static final String getSUFFIX_OANDA_CRYPTOS() {
		return SUFFIX_OANDA_CRYPTOS;
	}
	public static final String getSUFFIX_IB() {
		return SUFFIX_IB;
	}
	public static final String getCONF_DATE_FREEZE() {
		return CONF_DATE_FREEZE;
	}
	public static final String getSUFFIX_PURCHASES_FROM_CLIENTS() {
		return SUFFIX_PURCHASES_FROM_CLIENTS;
	}
	public static final String getCONF_FYAMORTIZATION() {
		return CONF_FYAMORTIZATION;
	}
	public static final String getCONF_FYINCOME_FREEZE() {
		return CONF_FYINCOME_FREEZE;
	}
	public static String getCONF_BKLEASING() {
		return CONF_BKLEASING;
	}
	public static final String getSUFFIX_FY_TRANSACTIONS_FREEZE() {
		return SUFFIX_FY_TRANSACTIONS_FREEZE;
	}
	public static final String getSUFFIX_TRACK_FILE() {
		return SUFFIX_TRACK_FILE;
	}
	public static final String getSUFFIX_FREEZE_CAPITAL() {
		return SUFFIX_FREEZE_CAPITAL;
	}
	public static final String getCONF_FYFROZEN_FREEZE() {
		return CONF_FYFROZEN_FREEZE;
	}
	public static final String getCONF_DRAW_COMMERCIAL() {
		return CONF_DRAW_COMMERCIAL;
	}
	public static final String getIS_IN_CHARGE_BKFROZEN() {
		return IS_IN_CHARGE_BKFROZEN;
	}
	
	
	
}

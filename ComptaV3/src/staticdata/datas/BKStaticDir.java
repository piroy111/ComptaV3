package staticdata.datas;

import basicmethods.BasicPrintMsg;

public class BKStaticDir {

	/*
	 * MAIN DRIVE
	 */
	private static String DIR;
	/*
	 * DIR
	 */
	private static String ZZ = "C:/zz_compta_Bunker/";
	private static String CONF = "Compta_bunker_v3/00_Conf_can_add_data_but_cannot_remove_data/";
	private static String CONF2 = "Compta_bunker_v3/00_Conf_can_add_or_delete_data/";
	private static String CONF_FY_END = "Compta_bunker_v3/00_Conf_FY_end/";
	private static String CONF_BKIncome = "Compta_bunker_v3/00_Conf_BKIncome/";
	private static String LOAD_TRANSACTIONS_PHYSICAL = "Compta_bunker_v3/01_Load_transactions_physical/";
	private static String LOAD_TRANSACTIONS_PHYSICAL_CLOSED = "Compta_bunker_v3/01_Load_transactions_physical_inactive/";
	private static String LOAD_TRANSACTIONS_PLATFORM = "Compta_bunker_v3/02_Load_transactions_platform/";
	private static String PLATFORM_TRANSACTIONS = "Compta_bunker_v3/02_Load_transactions_platform/Purchases_and_sales_from_clients/Transactions from platform in compta format/";
	private static String LOAD_TRANSACTIONS_TRANSFER = "Compta_bunker_v3/03_Load_transactions_transfer/";
	private static String DEBUG = "Compta_bunker_v3/04_Dump/Debug/";
	private static String OLD = "Compta_bunker_v3/04_Dump/Old/";
	private static String TIME_STAMP_COMPUTATION_COMPTA = "Compta_bunker_v3/04_Dump/Time_stamp_computation_compta/";	
	private static String DELIVERIES = "Compta_bunker_v3/05_Bars_deliveries_and_manual_sales/Platform/";
	private static String MANUAL_SALES = "Compta_bunker_v3/05_Bars_deliveries_and_manual_sales/Manual_sales/Deliveries";
	private static String LOAD_TRANSACTIONS_COMPTA = "Compta_bunker_v3/07_Transactions_computed_by_compta/";
	private static String RECONCILIATION_PLATFORM_BALANCES = "Compta_bunker_v3/08_Reconciliations/01_Holdings_clients_end_of_month/";
	private static String RECONCILIATION_PLATFORM_TRANSACTIONS = "Compta_bunker_v3/08_Reconciliations/02_Platform_all_transactions_monthly/";
	private static String VAULT_BALANCES = "Compta_bunker_v3/08_Reconciliations/03_Vaults/";
	private static String BKBAR_TRUE_WEIGHT = "Compta_bunker_v3/08_Reconciliations/04_BKBars_real_weight/";
	private static String FY_INPUT_AMORTIZATION = "Compta_bunker_v3/10_Fiscal_year_end/01_Inputs_and_validated_FY/Amortization/";
	private static String FY_INPUT_VALIDATED = "Compta_bunker_v3/10_Fiscal_year_end/01_Inputs_and_validated_FY/Validated_FY_reports/";
	private static String FY_OUTPUT = "Compta_bunker_v3/10_Fiscal_year_end/02_Outputs_files_csv/";
	private static String FREEZE_BKTRANSACTIONS = "Compta_bunker_v3/10_Fiscal_year_end/05_Freeze_BKTransactions/01_BKFrozenTransaction/";
	private static String FREEZE_TRACK_FILES_BKTRANSACTIONS = "Compta_bunker_v3/10_Fiscal_year_end/05_Freeze_BKTransactions/02_Track_files_BKTransactions/";
	private static String RECONCILIATION_BARS_OUTSIDE_PLATFORM = "Compta_bunker_v3/08_Reconciliations/05_Bars_outside_of_platform/";
	private static String FREEZE_CAPITAL = "Compta_bunker_v3/10_Fiscal_year_end/05_Freeze_BKTransactions/03_BKFrozen_capital/";
	/*
	 * SUB-DIRS
	 */
	private static String PHYSICAL_SUBFOLDER_TRANSACTIONS = "Physical transactions in compta format/";
	private static String PHYSICAL_SUBFOLDER_BALANCES = "Balances of physical assets/";
	private static String PHYSICAL_SUBFOLDER_DOCS = "Physical documents supporting proof of balances/";
	private static String PLATFORM_SUBFOLDER_TRANSACTIONS = "Transactions from platform in compta format/";
	private static String TRANSFER_SUBFOLDER_TRANSACTIONS = "Transfers in compta format/";
	private static String COMPTA_SUBFOLDER_TRANSACTIONS = "Transactions computed by compta in compta format/";
	private static String SUB_VAULT_BALANCE = "Balances/";
	private static String SUB_VAULT_PROOF = "Supporting Proof of balances/";
	private static String SUB_PURCHASE_AND_SALES = "Purchases_and_sales_from_clients/";
	private static String SUB_DIR_OANDA_REPORTS = "Import reports OANDA/";
	private static String SUB_DIR_IB_REPORTS = "Import reports InteractiveBroker/";
	private static String SUB_DIR_OANDA = "Broker_OANDA/";
	private static String SUB_DIR_IB = "Broker_InteractiveBroker/";
	/*
	 * Deprecated
	 */
	private static String MALCA = "Compta_bunker_v3/06_Reconciliations/MalcaAmit - bars in the vault/Balances/";
	private static String MALCA_SUPPORTING = "Compta_bunker_v3/06_Reconciliations/MalcaAmit - bars in the vault/Supporting document/";
	/*
	 * Output
	 */
	private static String OUTPUT = "Compta_bunker_v3/09_Output_files/";
	private static String OUTPUT_COMPTA = "Compta_bunker_v3/09_Output_files/Compta/";
	private static String OUTPUT_DEBUG = "Compta_bunker_v3/09_Output_files/Debug/";
	private static String OUTPUT_RECONCILIATION = "Compta_bunker_v3/09_Output_files/Reconciliation/";
	private static String OUTPUT_RECONCILIATION_MAIN = "Compta_bunker_v3/09_Output_files/Reconciliation/Main/";
	private static String OUTPUT_RECONCILIATION_VAULTS = "Compta_bunker_v3/09_Output_files/Reconciliation/Vaults/";
	private static String OUTPUT_RECONCILIATION_PLATFORM_BALANCES = "Compta_bunker_v3/09_Output_files/Reconciliation/Platform_balances/";
	private static String OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS = "Compta_bunker_v3/09_Output_files/Reconciliation/Platform_transactions/";
	private static String OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA = "Compta_bunker_v3/09_Output_files/Reconciliation/Platform_transactions_compta/";
	private static String OUTPUT_CLIENT = "Compta_without_back_up/02_Output_monthly_fils_fo_clients/";

	/**
	 * 
	 * @return
	 */
	public static void detectDIR(String[] _sDir) {
		if (_sDir == null) {
			BasicPrintMsg.error("null passed in the .bat file");
		} else {
			int lIdx = 0;
			while (lIdx < _sDir.length && _sDir[lIdx].equals("")) {
				lIdx++;
			}
			if (lIdx == _sDir.length) {
				BasicPrintMsg.error("No directory passed in the .bat file");
			}
			DIR = _sDir[lIdx];
			while (DIR.contains("\\")) {
				DIR = DIR.replace("\\", "/");
			}
			DIR = DIR.replaceAll("Compta_bunker_v3/11_Program_to_run", "");
			while (DIR.endsWith("//")) {
				DIR = DIR.substring(0, DIR.length() - 1);
			}
			if (!DIR.endsWith("/")) {
				DIR += "/";
			}
			System.out.println("Drive detected= '" + DIR + "'");
		} 
	}

	/*
	 * Getters & Setters
	 */
	public static final String getCONF() {
		return DIR + CONF;
	}
	public static final String getCONF2() {
		return DIR + CONF2;
	}
	public static final String getDEBUG() {
		return DIR + DEBUG;
	}
	public static final String getLOAD_TRANSACTIONS_PHYSICAL() {
		return DIR + LOAD_TRANSACTIONS_PHYSICAL;
	}
	public static final String getLOAD_TRANSACTIONS_PLATFORM() {
		return DIR + LOAD_TRANSACTIONS_PLATFORM;
	}
	public static final String getLOAD_TRANSACTIONS_TRANSFER() {
		return DIR + LOAD_TRANSACTIONS_TRANSFER;
	}
	public static final String getPHYSICAL_SUBFOLDER_TRANSACTIONS() {
		return PHYSICAL_SUBFOLDER_TRANSACTIONS;
	}
	public static final String getPHYSICAL_SUBFOLDER_BALANCES() {
		return PHYSICAL_SUBFOLDER_BALANCES;
	}
	public static final String getPHYSICAL_SUBFOLDER_DOCS() {
		return PHYSICAL_SUBFOLDER_DOCS;
	}
	public static final String getPLATFORM_SUBFOLDER_TRANSACTIONS() {
		return PLATFORM_SUBFOLDER_TRANSACTIONS;
	}
	public static final String getTRANSFER_SUBFOLDER_TRANSACTIONS() {
		return TRANSFER_SUBFOLDER_TRANSACTIONS;
	}
	public static final String getLOAD_TRANSACTIONS_COMPTA() {
		return DIR + LOAD_TRANSACTIONS_COMPTA;
	}
	public static final String getCOMPTA_SUBFOLDER_TRANSACTIONS() {
		return COMPTA_SUBFOLDER_TRANSACTIONS;
	}
	public static final String getDELIVERIES() {
		return DIR + DELIVERIES;
	}
	public static final String getOLD() {
		return DIR + OLD;
	}
	public static final String getMALCA() {
		return DIR + MALCA;
	}
	public static final String getOUTPUT_COMPTA() {
		return DIR + OUTPUT_COMPTA;
	}
	public static final String getOUTPUT_DEBUG() {
		return DIR + OUTPUT_DEBUG;
	}
	public static final String getOUTPUT_RECONCILIATION() {
		return DIR + OUTPUT_RECONCILIATION;
	}
	public static final String getMALCA_SUPPORTING() {
		return DIR + MALCA_SUPPORTING;
	}
	public static final String getOUTPUT() {
		return DIR + OUTPUT;
	}
	public static final String getOUTPUT_RECONCILIATION_MAIN() {
		return DIR + OUTPUT_RECONCILIATION_MAIN;
	}
	public static final String getVAULT_BALANCES() {
		return DIR + VAULT_BALANCES;
	}
	public static final String getSUB_VAULT_BALANCE() {
		return SUB_VAULT_BALANCE;
	}
	public static final String getSUB_VAULT_PROOF() {
		return SUB_VAULT_PROOF;
	}
	public static final String getOUTPUT_RECONCILIATION_VAULTS() {
		return DIR + OUTPUT_RECONCILIATION_VAULTS;
	}
	public static final String getRECONCILIATION_PLATFORM_BALANCES() {
		return DIR + RECONCILIATION_PLATFORM_BALANCES;
	}
	public static final String getRECONCILIATION_PLATFORM_TRANSACTIONS() {
		return DIR + RECONCILIATION_PLATFORM_TRANSACTIONS;
	}
	public static final String getMANUAL_SALES() {
		return DIR + MANUAL_SALES;
	}
	public static final String getOUTPUT_RECONCILIATION_PLATFORM_BALANCES() {
		return DIR + OUTPUT_RECONCILIATION_PLATFORM_BALANCES;
	}
	public static final String getOUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS() {
		return DIR + OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS;
	}
	public static final String getSUB_DIR_OANDA_REPORTS() {
		return SUB_DIR_OANDA_REPORTS;
	}
	public static final String getSUB_DIR_IB_REPORTS() {
		return SUB_DIR_IB_REPORTS;
	}
	public static final String getSUB_PURCHASE_AND_SALES() {
		return SUB_PURCHASE_AND_SALES;
	}
	public static final String getSUB_DIR_OANDA() {
		return SUB_DIR_OANDA;
	}
	public static final String getSUB_DIR_IB() {
		return SUB_DIR_IB;
	}
	public static final String getOUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA() {
		return DIR + OUTPUT_RECONCILIATION_PLATFORM_TRANSACTIONS_COMPTA;
	}
	public static final String getBKBAR_TRUE_WEIGHT() {
		return DIR + BKBAR_TRUE_WEIGHT;
	}
	public static final String getTIME_STAMP_COMPUTATION_COMPTA() {
		return DIR + TIME_STAMP_COMPUTATION_COMPTA;
	}
	public static final String getPLATFORM_TRANSACTIONS() {
		return DIR + PLATFORM_TRANSACTIONS;
	}
	public static final String getOUTPUT_CLIENT() {
		return DIR + OUTPUT_CLIENT;
	}
	public static final String getFY_INPUT_AMORTIZATION() {
		return DIR + FY_INPUT_AMORTIZATION;
	}
	public static final String getFY_INPUT_VALIDATED() {
		return DIR + FY_INPUT_VALIDATED;
	}
	public static final String getFY_OUTPUT() {
		return DIR + FY_OUTPUT;
	}
	public static final String getLOAD_TRANSACTIONS_PHYSICAL_CLOSED() {
		return DIR + LOAD_TRANSACTIONS_PHYSICAL_CLOSED;
	}
	public static final String getFREEZE_BKTRANSACTIONS() {
		return DIR + FREEZE_BKTRANSACTIONS;
	}
	public static final String getFREEZE_TRACK_FILES_BKTRANSACTIONS() {
		return DIR + FREEZE_TRACK_FILES_BKTRANSACTIONS;
	}
	public static final String getCONF_FY_END() {
		return DIR + CONF_FY_END;
	}
	public static final String getCONF_BKIncome() {
		return DIR + CONF_BKIncome;
	}
	public static final String getRECONCILIATION_BARS_OUTSIDE_PLATFORM() {
		return DIR + RECONCILIATION_BARS_OUTSIDE_PLATFORM;
	}
	public static final String getFREEZE_CAPITAL() {
		return DIR + FREEZE_CAPITAL;
	}
	public static final void setDIR(String dIR) {
		DIR = dIR;
	}
	public static final String getZZ() {
		return ZZ;
	}

	

	
}

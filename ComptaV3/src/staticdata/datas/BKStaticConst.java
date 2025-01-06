package staticdata.datas;

import java.util.Arrays;
import java.util.List;

import basicmethods.BasicString;
import staticdata.dateloaders.BKStaticDateComptaLoader;

public class BKStaticConst {

	/*
	 * DATES
	 */
	private static int DATE_START_COMPTA_V3 = 20200831;	// we don't create mirror transactions before this date
	private static int DATE_PROY_BECOMES_CLIENT = 20210327;
	private static int DATE_START_LEASING_CONDOR_TYPE = 20210501;
	private static int FREEZE_START_NB_MONTHS_AFTER_LAST_FY = 6;
	private static int FREEZE_TRANSACTION_START = 20210331;
	/*
	 * DATES loaded by CONF files
	 */
	private static int DATE_STOP_COUNTING_IN_TRANSACTIONS;
	private static int DATE_FIRST_CHANGED_TRANSACTIONS = Integer.MAX_VALUE;
	private static long TIME_STAMP_FILE_PREVIOUS_COMPTA;
	private static int DATE_FREEZE_COMPTA_MIN;
	/*
	 * Accounts
	 */
	private static String ACCOUNT_BUNKER = "contact@bunker-group.com";
	private static String ACCOUNT_PROY = "pierre.roy@hotmail.com";
	private static String ACCOUNT_CAROUSELL = "carousell@bunker-group.com";
	/*
	 * Errors acceptable
	 */
	private static double ERROR_ACCEPTABLE_COMPTA = 0.1;
	private static double ERROR_ACCEPTABLE_WEIGHT_BARS = 0.1;
	private static double RECONCILIATION_ERROR_ACCEPTABLE_WEIGHT_PER_BAR = 0.005;
	private static double ERROR_ACCEPTABLE_NAV = 0.1;
	private static double ERROR_ACCEPTABLE_LOAN_BACK_TO_PROY = 0.1;
	private static double ERROR_ACCEPTABLE_RECONCILIATION_ASSETS = 0.001;
	private static double ERROR_ACCEPTABLE_FYINCOME_PAST = 0.5;
	private static long ERROR_ACCEPTABLE_TIME_STAMP_FILES = 24 * 60 * 60 * 1000;
	/*
	 * CONST
	 */
	private static double OZ = 31.1034768;
	private static String TAB = BasicString.repeteString(" ", 8);
	private static String NTAB = "\n" + TAB;
	private static String NAME_BKASSET_OIL = "XBCO";
	private static List<String> LIST_NAMES_FAKE_BARS = Arrays.asList(new String[] {"FAKE", "FORWARD", "FUTURE", "BAR_PROCESSED_AT_REFINERY"});
	/*
	 * DEBUG
	 */
	private static boolean IS_SKIP_RECONCILIATION_PLATORM = false;
	private static boolean IS_SKIP_WRITE_FILES_CLIENTS = false;
	/*
	 * ENUM
	 */
	public enum mode_nav {PHYSICAL, PAPER}
	public enum type_entity {PHYSICAL, TRANSFER}
	public enum com_file_written {TransactionsComputedByCompta, Debug, Reconciliator, OutputFiles, TransactionsGeneratedByCompta}
	/*
	 * BKIncome
	 */
	private static String BKINCOME_INCOMING_FUNDS = "Operations_Incoming_funds_from_client";
	private static String BKINCOME_BARS_OUTSIDE_PLATFORM = "Bars_outside_of_platform";
	private static String BKINCOME_CAPITAL = "Capital";
	private static String BKINCOME_OANDA_TRADES = "Hedging OANDA Trades";
	private static String BKINCOME_OANDA_CASH_IN = "Hedging OANDA Cash wire in";
	private static String BKINCOME_OANDA_COMMISSIONS = "Hedging OANDA Commissions";
	private static String BKINCOME_OANDA_INTERESTS = "Hedging OANDA Interests";
	private static String BKINCOME_IB_TRADES = "Hedging InteractiveBrokers Trades";
	private static String BKINCOME_IB_CASH_IN = "Hedging InteractiveBrokers Cash wire in";
	private static String BKINCOME_IB_COMMISSIONS = "Hedging InteractiveBrokers Commissions";
	private static String BKINCOME_IB_INTERESTS = "Hedging InteractiveBrokers Interests";
	private static String BKINCOME_OPERATIONS_BARS_HELD_BY_BUNKER = "Operations_Bars_held_by_Bunker";
	private static String BKINCOME_LOAN = "Loan ";
	private static String BKINCOME_STORAGE = "Operations_Storage_incoming_from_clients";
	private static String BKINCOME_CAROUSELL_BARS = "Operations_Carousell_Bars";
	private static String BKINCOME_BARS = "Bars";
	private static String BKINCOME_HOLDING_FROZEN = "Holding_at_start_of_year";
	private static String BKINCOME_PROVISION = "Provision_";
	private static String BKINCOME_LEASING_GAIN = "Operations_Gain_Leasing_";
	private static String BKINCOME_COMMERCIAL_DRAW = "Cost_Draw_Commercial";
	private static String BKINCOME_COMMERCIAL_COST = "Cost_Commercial";
	/*
	 * BKEntity
	 */
	private static String BKENTITY_UOB = "Bank_UOB";
	private static String BKENTITY_CONDOR_PROY = "Condor_PRoy";
	private static String BKENTITY_OANDA = "Broker_OANDA";
	private static String BKENTITY_IB = "Broker_InteractiveBroker";
	private static String BKENTITY_PREFIX_VAULTS = "Vault_";
	private static String BKENTITY_PREFIX_REFINERS = "Refiner_";
	private static String BKENTITY_LEASING = "_Leasing";
	private static String BKENTITY_LEASING_RELATED = "_related";
	/*
	 * Headers
	 */
	private static String HEADER_BALANCE_EXPECTED_PHYSICAL = "BKASset (as per names in the conf file 'Prices_histo_assets.csv')"
			+ ",Holding";
	private static String HEADER_BALANCE_EXPECTED_SIMPLIFIED = "BKASset (as per names in the conf file 'Prices_histo_assets.csv')" 
			+ ",Holding,Price executed average";
	private static String HEADER_BALANCE_EXPECTED_FULL = "BKASset (as per names in the conf file 'Prices_histo_assets.csv')"
			+ ",Holding positive,Price executed average positive"
			+ ",Holding negative,Price executed average negative";
	private static String HEADER_BALANCE_NAV = "NAV (US$) and BKAsset,NAV value (US$) and Amount of BKAsset,Price of BKAsset to value NAV";
	private static String HEADER_FILE_TRANSACTIONS = "Date of the transaction,BKASset (as per names in the conf file 'Prices_histo_assets.csv'),Comment,Quantity,Price (for physical assets write NaN; for paper assets we must have a price executed),BKAccount (email as per the emails in the conf file 'Accounts and currency.csv'),BKIncome (as per names in the conf file 'BKIncome.csv')";
	private static String HEADER_FILE_FISCAL_YEAR_FROZEN = "Date of the transaction,BKAsset,Quantity,Price,BKAccount email,BKAccount currency,Comment,BKIncome,BKEntity name,BKEntity type";
	/*
	 * FYE
	 */
	private static String FYE_PROVISION_GROUP = "Provision";
	
	/*
	 * Getters & Setters
	 */
	public static final String getACCOUNT_BUNKER() {
		return ACCOUNT_BUNKER;
	}
	public static final String getACCOUNT_PROY() {
		return ACCOUNT_PROY;
	}
	public static final double getOZ() {
		return OZ;
	}
	public static final double getERROR_ACCEPTABLE_COMPTA() {
		return ERROR_ACCEPTABLE_COMPTA;
	}
	public static final String getTAB() {
		return TAB;
	}
	public static final String getNTAB() {
		return NTAB;
	}
	public static final int getDATE_START_COMPTA_V3() {
		return DATE_START_COMPTA_V3;
	}
	public static final double getERROR_ACCEPTABLE_WEIGHT_BARS() {
		return ERROR_ACCEPTABLE_WEIGHT_BARS;
	}
	/**
	 * @return the end date of the previous month when we run the COMPTA
	 */
	public static final int getDATE_STOP_COUNTING_IN_TRANSACTIONS() {
		BKStaticDateComptaLoader.loadDateStopCountingInTransactions();
		return DATE_STOP_COUNTING_IN_TRANSACTIONS;
	}
	public static final String getHEADER_FILE_TRANSACTIONS() {
		return HEADER_FILE_TRANSACTIONS;
	}
	public static final String getBKINCOME_INCOMING_FUNDS() {
		return BKINCOME_INCOMING_FUNDS;
	}
	public static final String getBKINCOME_BARS_OUTSIDE_PLATFORM() {
		return BKINCOME_BARS_OUTSIDE_PLATFORM;
	}
	public static final String getACCOUNT_CAROUSELL() {
		return ACCOUNT_CAROUSELL;
	}
	/**
	 * At then end of the COMPTA, the program will create a file with the date of the COMPTA<br>
	 * If the program crashes before, the file will not be written, the time stamp will be zero, and the first date will be 0<br>
	 * If a file transaction has been changed since the last time-stamp, then this will trigger an earlier date of first transaction<br>
	 * <br>
	 * The first date of transaction serves for the reconciliation. We don't reconcile prior to this first date of transaction<br>
	 * <br>
	 * overall this date is not very used at the moment and has little impact<br>
	 * All transactions are loaded and computed anyway<br>
	 * @return
	 */
	public static final int getDATE_FIRST_CHANGED_TRANSACTIONS() {
		return DATE_FIRST_CHANGED_TRANSACTIONS;
	}
	public static final void setDATE_FIRST_CHANGED_TRANSACTIONS(int _sDATE_FIRST_CHANGED_TRANSACTIONS) {
		DATE_FIRST_CHANGED_TRANSACTIONS = _sDATE_FIRST_CHANGED_TRANSACTIONS;
	}
	public static final long getTIME_STAMP_FILE_PREVIOUS_COMPTA() {
		return TIME_STAMP_FILE_PREVIOUS_COMPTA;
	}
	public static final void setTIME_STAMP_FILE_PREVIOUS_COMPTA(long _sTIME_STAMP_FILE_PREVIOUS_COMPTA) {
		TIME_STAMP_FILE_PREVIOUS_COMPTA = _sTIME_STAMP_FILE_PREVIOUS_COMPTA;
	}
	public static final void setDATE_STOP_COUNTING_IN_TRANSACTIONS(int _sDATE_STOP_COUNTING_IN_TRANSACTIONS) {
		DATE_STOP_COUNTING_IN_TRANSACTIONS = _sDATE_STOP_COUNTING_IN_TRANSACTIONS;
	}
	public static final double getRECONCILIATION_ERROR_ACCEPTABLE_WEIGHT_PER_BAR() {
		return RECONCILIATION_ERROR_ACCEPTABLE_WEIGHT_PER_BAR;
	}
	public static final String getBKINCOME_CAPITAL() {
		return BKINCOME_CAPITAL;
	}
	public static final String getBKENTITY_UOB() {
		return BKENTITY_UOB;
	}
	public static final String getBKENTITY_OANDA() {
		return BKENTITY_OANDA;
	}
	public static final String getBKENTITY_IB() {
		return BKENTITY_IB;
	}
	public static final String getNAME_BKASSET_OIL() {
		return NAME_BKASSET_OIL;
	}
	public static final String getBKINCOME_OANDA_TRADES() {
		return BKINCOME_OANDA_TRADES;
	}
	public static final String getBKINCOME_OANDA_CASH_IN() {
		return BKINCOME_OANDA_CASH_IN;
	}
	public static final String getBKINCOME_OANDA_COMMISSIONS() {
		return BKINCOME_OANDA_COMMISSIONS;
	}
	public static final String getBKINCOME_OANDA_INTERESTS() {
		return BKINCOME_OANDA_INTERESTS;
	}
	public static final String getBKINCOME_IB_TRADES() {
		return BKINCOME_IB_TRADES;
	}
	public static final String getBKINCOME_IB_CASH_IN() {
		return BKINCOME_IB_CASH_IN;
	}
	public static final String getBKINCOME_IB_COMMISSIONS() {
		return BKINCOME_IB_COMMISSIONS;
	}
	public static final String getBKINCOME_IB_INTERESTS() {
		return BKINCOME_IB_INTERESTS;
	}
	public static final double getERROR_ACCEPTABLE_NAV() {
		return ERROR_ACCEPTABLE_NAV;
	}
	public static final String getHEADER_BALANCE_EXPECTED_PHYSICAL() {
		return HEADER_BALANCE_EXPECTED_PHYSICAL;
	}
	public static final String getHEADER_BALANCE_EXPECTED_SIMPLIFIED() {
		return HEADER_BALANCE_EXPECTED_SIMPLIFIED;
	}
	public static final String getHEADER_BALANCE_EXPECTED_FULL() {
		return HEADER_BALANCE_EXPECTED_FULL;
	}
	public static final String getHEADER_BALANCE_NAV() {
		return HEADER_BALANCE_NAV;
	}
	public static final String getBKINCOME_OPERATIONS_BARS_HELD_BY_BUNKER() {
		return BKINCOME_OPERATIONS_BARS_HELD_BY_BUNKER;
	}
	public static final String getBKINCOME_LOAN() {
		return BKINCOME_LOAN;
	}
	public static final double getERROR_ACCEPTABLE_LOAN_BACK_TO_PROY() {
		return ERROR_ACCEPTABLE_LOAN_BACK_TO_PROY;
	}
	public static final String getBKENTITY_CONDOR_PROY() {
		return BKENTITY_CONDOR_PROY;
	}
	public static final List<String> getLIST_NAMES_FAKE_BARS() {
		return LIST_NAMES_FAKE_BARS;
	}
	public static final int getDATE_PROY_BECOMES_CLIENT() {
		return DATE_PROY_BECOMES_CLIENT;
	}
	public static final String getBKINCOME_STORAGE() {
		return BKINCOME_STORAGE;
	}
	public static final double getERROR_ACCEPTABLE_RECONCILIATION_ASSETS() {
		return ERROR_ACCEPTABLE_RECONCILIATION_ASSETS;
	}
	public static final double getERROR_ACCEPTABLE_FYINCOME_PAST() {
		return ERROR_ACCEPTABLE_FYINCOME_PAST;
	}
	public static final int getDATE_FREEZE_COMPTA_MIN() {
		return DATE_FREEZE_COMPTA_MIN;
	}
	public static final void setDATE_FREEZE_COMPTA_MIN(int _sDATE_FREEZE_COMPTA_MIN) {
		DATE_FREEZE_COMPTA_MIN = _sDATE_FREEZE_COMPTA_MIN;
	}
	public static final boolean getIS_SKIP_RECONCILIATION_PLATORM() {
		return IS_SKIP_RECONCILIATION_PLATORM;
	}
	public static final boolean getIS_SKIP_WRITE_FILES_CLIENTS() {
		return IS_SKIP_WRITE_FILES_CLIENTS;
	}
	public static final void setIS_SKIP_RECONCILIATION_PLATORM(boolean _sIS_SKIP_RECONCILIATION_PLATORM) {
		IS_SKIP_RECONCILIATION_PLATORM = _sIS_SKIP_RECONCILIATION_PLATORM;
	}
	public static final void setIS_SKIP_WRITE_FILES_CLIENTS(boolean _sIS_SKIP_WRITE_FILES_CLIENTS) {
		IS_SKIP_WRITE_FILES_CLIENTS = _sIS_SKIP_WRITE_FILES_CLIENTS;
	}
	public static final int getDATE_START_LEASING_CONDOR_TYPE() {
		return DATE_START_LEASING_CONDOR_TYPE;
	}
	public static final String getBKINCOME_CAROUSELL_BARS() {
		return BKINCOME_CAROUSELL_BARS;
	}
	public static final String getBKINCOME_BARS() {
		return BKINCOME_BARS;
	}
	public static final String getHEADER_FILE_FISCAL_YEAR_FROZEN() {
		return HEADER_FILE_FISCAL_YEAR_FROZEN;
	}
	public static final int getFREEZE_START_NB_MONTHS_AFTER_LAST_FY() {
		return FREEZE_START_NB_MONTHS_AFTER_LAST_FY;
	}
	public static final int getFREEZE_TRANSACTION_START() {
		return FREEZE_TRANSACTION_START;
	}
	public static final String getBKINCOME_HOLDING_FROZEN() {
		return BKINCOME_HOLDING_FROZEN;
	}
	public static final String getBKENTITY_PREFIX_VAULTS() {
		return BKENTITY_PREFIX_VAULTS;
	}
	public static final String getBKENTITY_PREFIX_REFINERS() {
		return BKENTITY_PREFIX_REFINERS;
	}
	public static final String getBKENTITY_LEASING() {
		return BKENTITY_LEASING;
	}
	public static final String getBKINCOME_PROVISION() {
		return BKINCOME_PROVISION;
	}
	public static final String getBKINCOME_LEASING_GAIN() {
		return BKINCOME_LEASING_GAIN;
	}
	public static final String getBKENTITY_LEASING_RELATED() {
		return BKENTITY_LEASING_RELATED;
	}
	public static final long getERROR_ACCEPTABLE_TIME_STAMP_FILES() {
		return ERROR_ACCEPTABLE_TIME_STAMP_FILES;
	}
	public static final String getBKINCOME_COMMERCIAL_DRAW() {
		return BKINCOME_COMMERCIAL_DRAW;
	}
	public static final String getBKINCOME_COMMERCIAL_COST() {
		return BKINCOME_COMMERCIAL_COST;
	}
	public static final String getFYE_PROVISION_GROUP() {
		return FYE_PROVISION_GROUP;
	}

	
}

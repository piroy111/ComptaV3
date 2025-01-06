package step08_output_files.bkincome;

import java.util.ArrayList;
import java.util.List;

import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_BKIncome_to_drop extends BKOutputAbstract {

	public BKOutput_BKIncome_to_drop(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		addNewHeader("BKIncome,BKIncomeGroup,BKIncomeFY");
		List<BKIncome> lListBKIncomeFound = new ArrayList<>();
		for (BKTransaction lBKTransaction : BKTransactionManager.getpListBKTransactionSorted()) {
			if (!lListBKIncomeFound.contains(lBKTransaction.getpBKIncome())) {
				lListBKIncomeFound.add(lBKTransaction.getpBKIncome());
			}
		}
		for (BKIncome lBKIncome : BKIncomeManager.getpTreeMapNameToBKIncome().values()) {
			if (!lListBKIncomeFound.contains(lBKIncome)) {
				String lLine = lBKIncome.getpName()
						+ "," + lBKIncome.getpBKIncomeGroup()
						+ "," + lBKIncome.getpBKIncomeFY().getpName()
						+ "," + lBKIncome.getpBKIncomeFY().getpBKIncomeFYGroup().getpName();
				addNewLineToWrite(lLine);
			}
		}
	}

	
	
	
	
}

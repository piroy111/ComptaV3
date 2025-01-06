package step01_objects_from_conf_files.account.sample;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;

public class BKSampleBKAccount {

	public static void main(String[] _sArgs) {
		for (BKAccount lBKAccount : BKAccountManager.getpListBKAccount()) {
			String lLine = "> Email= " + lBKAccount.getpEmail()
				+ "; Currency= " + lBKAccount.getpBKAssetCurrency()
				+ "; Owner= " + lBKAccount.getpOwner()
				+ "; Joint= " + lBKAccount.getpJoint()
				+ "; Source= " + lBKAccount.getpSource();
			System.out.println(lLine);
		}
	}
	
}

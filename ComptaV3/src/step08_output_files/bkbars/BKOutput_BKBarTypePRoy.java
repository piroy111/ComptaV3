package step08_output_files.bkbars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.asset.bar.BKBarType;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_BKBarTypePRoy extends BKOutputAbstract {

	public BKOutput_BKBarTypePRoy(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		BKAccount lBKAccount = BKAccountManager.getpBKAccountPRoy();
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(lDate);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			return;
		}
		BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(BKPartitionPerBKAccount.getKey(lBKAccount));
		if (lBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * Count all the BKBars of PROY
		 */
		OTBarManager lOTBarManager = new OTBarManager();
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			for (BKBar lBKBar : lBKAssetMetal.getpMapIDToBKBar().values()) {
				if (lBKAccount.equals(lBKBar.getpBKAccountOwner(lDate))) {
					OTBar lOTBar = lOTBarManager.getpOrCreateOTBar(lBKBar.getpBKBarType());
					lOTBar.declareNewBKBar(lBKBar);
				}
			}
		}
		/*
		 * Write file
		 */
		List<BKBarType> lListBKBartType = new ArrayList<>(lOTBarManager.getpMapBKBarTypeToOTBar().keySet());
		Collections.sort(lListBKBartType);
		for (BKBarType lBKBarType : lListBKBartType) {
			OTBar lOTBar = lOTBarManager.getpOrCreateOTBar(lBKBarType);
			String lLine = lBKBarType.getpName()
					+ "," + lOTBar.getpNumberBars()
					+ "," + lOTBar.getpWeightOz()
					+ "," + (lOTBar.getpWeightOz() * lBKBarType.getpBKAssetMetal().getpPriceUSD(lDate));
			addNewLineToWrite(lLine);
		}
		/*
		 * Header
		 */
		addNewHeader("BKBarType,Number of bars owned by PRoy,Weight in Oz owned by PRoy,Value in US$");		
	}



}

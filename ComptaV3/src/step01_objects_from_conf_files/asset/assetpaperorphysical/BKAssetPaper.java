package step01_objects_from_conf_files.asset.assetpaperorphysical;

import staticdata.datas.BKStaticConst.mode_nav;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

public class BKAssetPaper extends BKAsset {

	public BKAssetPaper(String _sName, int _sIdxForSort) {
		super(_sName, mode_nav.PAPER, _sIdxForSort);
	}

}

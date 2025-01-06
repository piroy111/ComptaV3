package step08_output_files.leasing;

import step01_objects_from_conf_files.asset.asset.BKAssetLeasing;

class AssetLeasing extends Item {

	protected AssetLeasing(BKAssetLeasing _sBKAssetLeasing) {
		super(_sBKAssetLeasing);
	}

	/*
	 * Data
	 */
	private BKAssetLeasing pBKAssetLeasing;

	/*
	 * Getters & Setters
	 */
	public final BKAssetLeasing getpBKAssetLeasing() {
		return pBKAssetLeasing;
	}
}

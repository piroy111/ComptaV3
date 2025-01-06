package step09_fiscal_year_end.step04_outputfiles.abstracts;

import java.util.List;

import basicmethods.BasicFileOutput;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import step09_fiscal_year_end.FYManager;

public abstract class FYOutputFileAbstract extends BasicFileOutput {

	public FYOutputFileAbstract(FYOutputFileManager _sFYOutputFileManager) {
		super(BKStaticDir.getFY_OUTPUT());
		pFYOutputFileManager = _sFYOutputFileManager;
		/*
		 * 
		 */
		pFYManager = pFYOutputFileManager.getpFYManager();
	}
	
	/*
	 * Data
	 */
	protected FYOutputFileManager pFYOutputFileManager;
	protected FYManager pFYManager;
	
	/*
	 * Overrides
	 */
	public String getCompulsoryBeginForNameClass() {
		return "FYOutput_";
	}
	public void writeFile(Object _sSender, String _sDir, 
			String _sNameFile, 
			String _sHeader,
			List<String> _sListLineToWrite) {
		BKComOnFilesWritten.writeFile(com_file_written.OutputFiles, _sDir, _sNameFile, _sHeader, _sListLineToWrite);
	}
	
}

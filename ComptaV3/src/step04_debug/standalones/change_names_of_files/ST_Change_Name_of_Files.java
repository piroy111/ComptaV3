package step04_debug.standalones.change_names_of_files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicFichiersNio;

public class ST_Change_Name_of_Files {

	public static void main(String[] _sArgs) {
		/*
		 * 
		 */
		String lDir = "G:/My Drive/Compta_bunker_v3/09_Output_files/Compta/Client_NAV_v2/";
		String lTextToFind = "20220921";
		String lTextToUseInstead = "20220831";
		/*
		 * 
		 */
		List<String> lListNameFile = BasicFichiersNio.getListFilesInDirectory(lDir);
		for (String lNameFile : lListNameFile) {
			String lNewNameFile = lNameFile.replaceAll(lTextToFind, lTextToUseInstead);
			if (!lNewNameFile.equals(lNameFile)) {
				Path lPathSource = Paths.get(lDir + lNameFile);
				BasicFichiersNio.renameFile(lPathSource, lNewNameFile, true);
			}
		}
	}
	
}

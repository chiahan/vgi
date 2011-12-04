
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author JLiu
 */
public class TAFKit implements TAFKitInterface {
	
	private File pmTafKitPath;
	
	public TAFKit(String tafKitPathString) throws FileNotFoundException {
		this.setTafKitPathString(tafKitPathString);
	}  // End public TAFKit(String tafKitFolderPathStr)

	public String getTafKitPathString() {
		return pmTafKitPath.getAbsolutePath();
	}  // End public File getTafKitFolder ()

	public void setTafKitPathString(
			String tafKitFolderPathStr)
			throws
			FileNotFoundException {
		File folder = new File(tafKitFolderPathStr);
		if (!folder.exists()) {
			throw new FileNotFoundException("The specified TAF-Kit path \"" + tafKitFolderPathStr + "\" does not exist!");
		}
		if (!folder.isDirectory()) {
			throw new FileNotFoundException("The specified TAF-Kit path \"" + tafKitFolderPathStr + "\" is not a folder!");
		}
		File file = new File(folder, "vcsn-char-b");
		if (!file.exists()) {
			throw new FileNotFoundException("The specified TAF-Kit path \"" + tafKitFolderPathStr + "\" does not have the most basic TAF-Kit executable \"vcsn-char-b\" so it is probably an incorrect TAF-Kit path.");
		}
		pmTafKitPath = folder;
	}  /* End public void setTafKitPathString(
	 * String tafKitFolderPathStr)
	 * throws
	 * FileNotFoundException
	 */
	
	
	public List<VcsnAlgorithm> listVcsnAlgorithms(
			String tafKitSuffix)
			throws
			FileNotFoundException {
		
		if (!(new File(pmTafKitPath, "vcsn-" + tafKitSuffix).exists())) {
			throw new FileNotFoundException("The specified TAF-Kit executable \"" + this.getTafKitPathString() + File.separator + "vcsn-" + tafKitSuffix + "\" does not exist!");
		}
		
		Process process;
		try {
			process = Runtime.getRuntime().exec("./vcsn-" + tafKitSuffix + " --list-all-commands-json",
					null, pmTafKitPath);
			int exitValue = process.waitFor();
			
			switch (exitValue) {
				case 0:
					break;
				default:
					InputStream inputStream = process.getErrorStream();
					Error error = new Error(convertInputStreamToString(inputStream));
					inputStream.close();
					throw error;
			}  // End switch (exitValue)

			
		} catch (Exception exception) {
			throw new Error(exception);
		}
		
		JSONArray jaCategories;
		try {
			jaCategories = new JSONArray(new JSONTokener(process.getInputStream()));
		} catch (JSONException jsonException) {
			throw new Error("Output format of --list-all-commands-json probably changed.", jsonException);
		}
		
		ArrayList<VcsnAlgorithm> vcsnAlgorithmsList = new ArrayList<VcsnAlgorithm>();
		
		for (int index = 0; index < jaCategories.length(); index++) {
			
			JSONObject joCategory = jaCategories.optJSONObject(index);
			if (joCategory == null) {
				throw new Error("Output format of --list-all-commands-json probably changed.");
			}
			
			String string = joCategory.optString("category", null);
			if (string == null) {
				throw new Error("Output format of --list-all-commands-json probably changed.");
			}
			if (string.toLowerCase().contains("input/output")) {
				continue;
			}
			
			JSONArray jaAlgorithms = joCategory.optJSONArray("algorithms");
			if (jaAlgorithms == null) {
				throw new Error("Output format of --list-all-commands-json probably changed.");
			}
			
			VcsnAlgorithm vcsnAlgorithm = new VcsnAlgorithm();
			vcsnAlgorithm.name = "category";
			vcsnAlgorithm.inputsInfo = null;
			vcsnAlgorithm.outputsInfo = null;
			vcsnAlgorithm.description = string;
			vcsnAlgorithmsList.add(vcsnAlgorithm);
			
			for (int index2 = 0; index2 < jaAlgorithms.length(); index2++) {
				
				JSONObject joAlgorithm = jaAlgorithms.optJSONObject(index2);
				if (joAlgorithm == null) {
					throw new Error("Output format of --list-all-commands-json probably changed.");
				}
				
				boolean expert = joAlgorithm.optBoolean("expert", false);
				if (expert) {
					continue;
				}
				
				vcsnAlgorithm = new VcsnAlgorithm();
				vcsnAlgorithm.name = joAlgorithm.optString("name", null);
				vcsnAlgorithm.description = joAlgorithm.optString("description", null);
				
				JSONArray jaInputs = joAlgorithm.optJSONArray("input");
				if (jaInputs == null) {
					throw new Error("Output format of --list-all-commands-json probably changed.");
				}
				
				ArrayList<VcsnAlgorithm.IoInfo> ioInfos = new ArrayList<VcsnAlgorithm.IoInfo>();
				
				for (int index3 = 0; index3 < jaInputs.length(); index3++) {
					
					JSONObject joInput = jaInputs.optJSONObject(index3);
					if (joInput == null) {
						throw new Error("Output format of --list-all-commands-json probably changed.");
					}
					
					VcsnAlgorithm.IoInfo ioInfo = new VcsnAlgorithm.IoInfo();
					ioInfo.context = joInput.optString("context", null);
					string = joInput.optString("type", null);
					if (string.compareToIgnoreCase("aut") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.AUTOMATON;
					} else if (string.compareToIgnoreCase("bool") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.BOOLEAN;
					} else if (string.compareToIgnoreCase("int") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.INTEGER;
					} else if (string.compareToIgnoreCase("exp") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.REGULAR_EXPRESSION;
					} else if (string.compareToIgnoreCase("text") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.TEXT;
					} else if (string.compareToIgnoreCase("weight") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.WEIGHT;
					} else if (string.compareToIgnoreCase("word") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.WORD;
					} else {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.UNKNOWN;
					}
					ioInfos.add(ioInfo);
					
				}  // End for (int index3 = 0; index3 < jaInputs.length(); index3++)

				vcsnAlgorithm.inputsInfo = ioInfos;
				JSONArray jaOutputs = joAlgorithm.optJSONArray("output");
				if (jaOutputs == null) {
					throw new Error("Output format of --list-all-commands-json probably changed.");
				}
				
				ioInfos = new ArrayList<VcsnAlgorithm.IoInfo>();
				
				for (int index3 = 0; index3 < jaOutputs.length(); index3++) {
					
					JSONObject joOutput = jaOutputs.optJSONObject(index3);
					if (joOutput == null) {
						throw new Error("Output format of --list-all-commands-json probably changed.");
					}
					
					VcsnAlgorithm.IoInfo ioInfo = new VcsnAlgorithm.IoInfo();
					ioInfo.context = joOutput.optString("context", null);
					string = joOutput.optString("type", null);
					if (string.compareToIgnoreCase("aut") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.AUTOMATON;
					} else if (string.compareToIgnoreCase("bool") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.BOOLEAN;
					} else if (string.compareToIgnoreCase("int") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.INTEGER;
					} else if (string.compareToIgnoreCase("exp") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.REGULAR_EXPRESSION;
					} else if (string.compareToIgnoreCase("text") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.TEXT;
					} else if (string.compareToIgnoreCase("weight") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.WEIGHT;
					} else if (string.compareToIgnoreCase("word") == 0) {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.WORD;
					} else {
						ioInfo.type = VcsnAlgorithm.IoInfo.Type.UNKNOWN;
					}
					ioInfos.add(ioInfo);
					
				}  // End for (int index3 = 0; index3 < jaOutputs.length(); index3++)

				vcsnAlgorithm.outputsInfo = ioInfos;
				vcsnAlgorithmsList.add(vcsnAlgorithm);
				
			}  // End for (int index2 = 0; index2 < jaAlgorithms.length(); index2++)

		}  // End for (int index = 0; index < jaCategories.length(); index++)

		return vcsnAlgorithmsList;
	}  /* End public List<VcsnAlgorithm> listVcsnAlgorithms(
	 * String tafKitSuffix)
	 * throws
	 * FileNotFoundException
	 */
	
	
	public List<Object> runVcsnAlgorithm(
			String tafKitSuffix,
			VcsnAlgorithm algorithm,
			List<Object> inputs)
			throws
			FileNotFoundException,
			IllegalArgumentException {
		
		throw new UnsupportedOperationException("Not supported yet.");
		
	}  /* End public List<Object> runVcsnAlgorithm(
	 *	String tafKitSuffix,
	 *	VcsnAlgorithm algorithm,
	 *	List<Object> inputs)
	 *	throws
	 *	FileNotFoundException,
	 *	IllegalArgumentException
	 */
	
	
	public static String convertInputStreamToString(InputStream inputSream) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputSream));
		StringBuilder stringBuilder = new StringBuilder();
		
		while (true) {
			Integer integer;
			try {
				integer = reader.read();
				if (integer == -1) {
					break;
				}
			} catch (IOException ioException) {
				break;
			}
			stringBuilder.append(Character.toChars(integer.intValue()));
		}  // End while (true)

		return stringBuilder.toString();
		
	}  // End public static String convertInputStreamToString(InputStream inputSream)

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		
		TAFKit tafKit = null;
		try {
			tafKit = new TAFKit("/Users/JLiu/vaucanson-1.4a/taf-kit/tests");
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
		
		List<VcsnAlgorithm> vcsnAlgorithmsList;
		try {
			vcsnAlgorithmsList = tafKit.listVcsnAlgorithms("char-b");
			if (vcsnAlgorithmsList == null) {
				return;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
		
		for (int index = 0; index < vcsnAlgorithmsList.size(); index++) {
			
			VcsnAlgorithm vcsnAlgorithm = vcsnAlgorithmsList.get(index);
			if (vcsnAlgorithm.name.compareToIgnoreCase("category") == 0) {
				System.out.println();
				System.out.println(vcsnAlgorithm.name + ":  " + vcsnAlgorithm.description);
				System.out.println();
			} else {  // End if (vcsnAlgorithm.name.compareToIgnoreCase("category") == 0)
				System.out.println(vcsnAlgorithm.name + ":  " + vcsnAlgorithm.description);
				
				List<VcsnAlgorithm.IoInfo> ioInfos = vcsnAlgorithm.inputsInfo;
				System.out.print("  Input: ");
				
				for (int index2 = 0; index2 < ioInfos.size(); index2++) {
					VcsnAlgorithm.IoInfo ioInfo = ioInfos.get(index2);
					System.out.print((index2 + 1) + ". ");
					if (ioInfo.context != null) {
						System.out.print("context: " + ioInfo.context + ", ");
					}
					System.out.print("type: " + ioInfo.type.toString() + "  ");
				}  // End for (int index2 = 0; index2 < ioInfos.size(); index2++)

				System.out.println();
				
				ioInfos = vcsnAlgorithm.outputsInfo;
				System.out.print("  Output: ");
				
				for (int index2 = 0; index2 < ioInfos.size(); index2++) {
					VcsnAlgorithm.IoInfo ioInfo = ioInfos.get(index2);
					System.out.print((index2 + 1) + ". ");
					if (ioInfo.context != null) {
						System.out.print("context: " + ioInfo.context + ", ");
					}
					System.out.print("type: " + ioInfo.type.toString() + "  ");
				}  // End for (int index2 = 0; index2 < ioInfos.size(); index2++)

				System.out.println();
				
			}  // End else part of if (vcsnAlgorithm.name.compareToIgnoreCase("category") == 0)

		}  // End for (int index = 0; index < vcsnAlgorithmsList.size(); index++)

	}  // End public static void main(String args[])
}  // End public class TAFKit


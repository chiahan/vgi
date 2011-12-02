
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author JLiu
 */
public class TAFKit implements TAFKitInterface {

	private File pmTafKitFolder;

	public TAFKit() {
		pmTafKitFolder = null;
	}  // End public TAFKit()

	public TAFKit(String tafKitFolderPathStr) {
		this();
		this.setTafKitFolder(tafKitFolderPathStr);
	}  // End public TAFKit(String tafKitFolderPathStr)

	public File getTafKitFolder() {
		return pmTafKitFolder;
	}  // End public File getTafKitFolder ()

	public void setTafKitFolder(String tafKitFolderPathStr) {
		pmTafKitFolder = new File(tafKitFolderPathStr);
	}  // End public void setTafKitFolder (String tafKitFolderPathStr)

	public ArrayList<VcsnAlgorithm> listVcsnAlgorithms(String tafKitSuffix) {

		ArrayList<VcsnAlgorithm> vcsnAlgorithmsList = new ArrayList<VcsnAlgorithm>();

		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("./vcsn-" + tafKitSuffix + " --list-all-commands-json",
					null, pmTafKitFolder);

			JSONArray jaCategories = new JSONArray(new JSONTokener(process.getInputStream()));

			int exitValue = process.waitFor();
//			System.out.println("Exited with error code " + exitValue);
			switch (exitValue) {
				case 0:
					break;
				default:
					String tafKitErrorStr = convertInputStreamToString(process.getErrorStream());
					Exception exception = new Exception(tafKitErrorStr);
					throw exception;
			}  // End switch (exitValue)

			for (int index = 0; index < jaCategories.length(); index++) {

				JSONObject joCategory = jaCategories.optJSONObject(index);
				String tempStr = joCategory.optString("category", null);
				if ((tempStr == null)
						|| (tempStr.toLowerCase().contains("input/output"))) {
					continue;
				}

				JSONArray jaAlgorithms = joCategory.optJSONArray("algorithms");
				VcsnAlgorithm vcsnAlgorithm = new VcsnAlgorithm();
				vcsnAlgorithm.name = "category";
				vcsnAlgorithm.inputs = null;
				vcsnAlgorithm.outputs = null;
				vcsnAlgorithm.description = joCategory.optString("category", null);
				vcsnAlgorithmsList.add(vcsnAlgorithm);

				for (int index2 = 0; index2 < jaAlgorithms.length(); index2++) {

					JSONObject joAlgorithm = jaAlgorithms.optJSONObject(index2);
					boolean expert = joAlgorithm.optBoolean("expert", false);
					if (expert) {
						continue;
					}

					vcsnAlgorithm = new VcsnAlgorithm();
					vcsnAlgorithm.name = joAlgorithm.optString("name", null);
					vcsnAlgorithm.description = joAlgorithm.optString("description", null);

					JSONArray jaInputs = joAlgorithm.optJSONArray("input");
					ArrayList<VcsnAlgorithm.IoInfo> ioInfos = new ArrayList<VcsnAlgorithm.IoInfo>();

					for (int index3 = 0; index3 < jaInputs.length(); index3++) {

						JSONObject joInput = jaInputs.optJSONObject(index3);
						VcsnAlgorithm.IoInfo ioInfo = new VcsnAlgorithm.IoInfo();
						ioInfo.context = joInput.optString("context", null);
						tempStr = joInput.optString("type", null);
						if (tempStr.compareToIgnoreCase("aut") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.AUTOMATON;
						} else if (tempStr.compareToIgnoreCase("bool") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.BOOLEAN;
						} else if (tempStr.compareToIgnoreCase("int") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.INTEGER;
						} else if (tempStr.compareToIgnoreCase("exp") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.REGULAR_EXPRESSION;
						} else if (tempStr.compareToIgnoreCase("text") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.TEXT;
						} else if (tempStr.compareToIgnoreCase("weight") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.WEIGHT;
						} else if (tempStr.compareToIgnoreCase("word") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.WORD;
						} else {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.UNKNOWN;
						}
						ioInfos.add(ioInfo);

					}  // End for (int index3 = 0; index3 < jaInputs.length(); index3++)

					vcsnAlgorithm.inputs = ioInfos;
					JSONArray jaOutputs = joAlgorithm.optJSONArray("output");
					ioInfos = new ArrayList<VcsnAlgorithm.IoInfo>();

					for (int index3 = 0; index3 < jaOutputs.length(); index3++) {

						JSONObject joOutput = jaOutputs.optJSONObject(index3);
						VcsnAlgorithm.IoInfo ioInfo = new VcsnAlgorithm.IoInfo();
						ioInfo.context = joOutput.optString("context", null);
						tempStr = joOutput.optString("type", null);
						if (tempStr.compareToIgnoreCase("aut") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.AUTOMATON;
						} else if (tempStr.compareToIgnoreCase("bool") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.BOOLEAN;
						} else if (tempStr.compareToIgnoreCase("int") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.INTEGER;
						} else if (tempStr.compareToIgnoreCase("exp") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.REGULAR_EXPRESSION;
						} else if (tempStr.compareToIgnoreCase("text") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.TEXT;
						} else if (tempStr.compareToIgnoreCase("weight") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.WEIGHT;
						} else if (tempStr.compareToIgnoreCase("word") == 0) {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.WORD;
						} else {
							ioInfo.type = VcsnAlgorithm.IoInfo.Type.UNKNOWN;
						}
						ioInfos.add(ioInfo);

					}  // End for (int index3 = 0; index3 < jaOutputs.length(); index3++)

					vcsnAlgorithm.outputs = ioInfos;
					vcsnAlgorithmsList.add(vcsnAlgorithm);

				}  // End for (int index2 = 0; index2 < jaAlgorithms.length(); index2++)

			}  // End for (int index = 0; index < jsonArrayCategories.length(); index++)
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

		return vcsnAlgorithmsList;
	}  // End public ArrayList<VcsnAlgorithm> listVcsnAlgorithms(String tafKitSuffix) throws Exception

	public ArrayList<Object> runVcsnAlgorithm(String tafKitSuffix, VcsnAlgorithm algorithm, ArrayList<Object> inputs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}  // End public ArrayList<Object> runVcsnAlgorithm(String tafKitSuffix, VcsnAlgorithm algorithm, ArrayList<Object> inputs)

	public static String convertInputStreamToString(InputStream inputSream) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputSream));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}
		inputSream.close();
		return stringBuilder.toString();
	}  // public static String convertInputStreamToString(InputStream inputSream) throws Exception

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {

		TAFKit tafKit = new TAFKit("/Users/JLiu/vaucanson-1.4a/taf-kit/tests");
		if (tafKit == null) {
			System.out.println("tafKit object is null.");
			return;
		}

		ArrayList<VcsnAlgorithm> vcsnAlgorithmsList;
		vcsnAlgorithmsList = tafKit.listVcsnAlgorithms("char-b");
		if (vcsnAlgorithmsList == null) {
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

				ArrayList<VcsnAlgorithm.IoInfo> ioInfos = vcsnAlgorithm.inputs;
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

				ioInfos = vcsnAlgorithm.outputs;
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

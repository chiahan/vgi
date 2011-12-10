
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

	public TAFKit(String tafKitPath)
			throws
			FileNotFoundException {
		this.setTafKitPath(tafKitPath);
	}  // End public TAFKit(String tafKitFolderPathStr)

	public String getTafKitPath() {
		return pmTafKitPath.getAbsolutePath();
	}  // End public File getTafKitFolder ()

	public void setTafKitPath(
			String tafKitFolderPath)
			throws
			FileNotFoundException {

		File folder = new File(tafKitFolderPath);
		if (!folder.exists()) {
			throw new FileNotFoundException("The specified TAF-Kit path \"" + tafKitFolderPath + "\" does not exist!");
		}
		if (!folder.isDirectory()) {
			throw new FileNotFoundException("The specified TAF-Kit path \"" + tafKitFolderPath + "\" is not a folder!");
		}
		File file = new File(folder, "vcsn-char-b");
		if (!file.isFile()) {
			throw new FileNotFoundException("The specified TAF-Kit path \"" + tafKitFolderPath + "\" does not have the most basic TAF-Kit executable \"vcsn-char-b\" so it is probably an incorrect TAF-Kit path.");
		}
		pmTafKitPath = folder;

	}  // End public void setTafKitPath(...)

	public List<VcsnAlgorithm> listVcsnAlgorithms(
			String tafKitSuffix)
			throws
			FileNotFoundException,
			TAFKitException {

		if (!(new File(pmTafKitPath, "vcsn-" + tafKitSuffix).exists())) {
			throw new FileNotFoundException("The specified TAF-Kit executable \"" + this.getTafKitPath() + File.separator + "vcsn-" + tafKitSuffix + "\" does not exist!");
		}

		Process process;
		int exitValue;
		try {
			process = Runtime.getRuntime().exec("./vcsn-" + tafKitSuffix + " --list-all-commands-json",
					null, pmTafKitPath);
			exitValue = process.waitFor();
		} catch (IOException ioException) {
			throw new Error(ioException);
		} catch (InterruptedException interruptedException) {
			throw new Error(interruptedException);
		}


		switch (exitValue) {
			case 0:
				break;
			default:
				InputStream inputStream = process.getInputStream();
				String string = convertInputStreamToString(inputStream);
				try {
					inputStream.close();
				} catch (IOException ioException) {
					throw new Error(ioException);
				}
				inputStream = process.getErrorStream();
				string = string + convertInputStreamToString(inputStream);
				TAFKitException tafKitException = new TAFKitException(string);
				try {
					inputStream.close();
				} catch (IOException ioException) {
					throw new Error(ioException);
				}
				throw tafKitException;
		}  // End switch (exitValue)

		JSONArray jaCategories;
		try {
			jaCategories = new JSONArray(new JSONTokener(process.getInputStream()));
		} catch (JSONException jsonException) {
			throw new TAFKitException("Output format of --list-all-commands-json probably changed.", jsonException);
		}

		ArrayList<VcsnAlgorithm> vcsnAlgorithmsList = new ArrayList<VcsnAlgorithm>();

		for (int index = 0; index < jaCategories.length(); index++) {

			JSONObject joCategory = jaCategories.optJSONObject(index);
			if (joCategory == null) {
				throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
			}

			String string = joCategory.optString("category", null);
			if (string == null) {
				throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
			}
			if (string.toLowerCase().contains("input/output")) {
				continue;
			}

			JSONArray jaAlgorithms = joCategory.optJSONArray("algorithms");
			if (jaAlgorithms == null) {
				throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
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
					throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
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
					throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
				}

				ArrayList<VcsnAlgorithm.IoInfo> ioInfos = new ArrayList<VcsnAlgorithm.IoInfo>();

				for (int index3 = 0; index3 < jaInputs.length(); index3++) {

					JSONObject joInput = jaInputs.optJSONObject(index3);
					if (joInput == null) {
						throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
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
					throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
				}

				ioInfos = new ArrayList<VcsnAlgorithm.IoInfo>();

				for (int index3 = 0; index3 < jaOutputs.length(); index3++) {

					JSONObject joOutput = jaOutputs.optJSONObject(index3);
					if (joOutput == null) {
						throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
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

	}	// End public List<VcsnAlgorithm> listVcsnAlgorithms(...)

	public List<Object> runVcsnAlgorithm(
			String tafKitSuffix,
			VcsnAlgorithm algorithm,
			List<Object> inputs)
			throws
			FileNotFoundException,
			IllegalArgumentException,
			TAFKitException {

		if (!(new File(pmTafKitPath, "vcsn-" + tafKitSuffix).exists())) {
			throw new FileNotFoundException("The specified TAF-Kit executable \"" + this.getTafKitPath() + File.separator + "vcsn-" + tafKitSuffix + "\" does not exist!");
		}

		if ((algorithm == null)
				|| (algorithm.name == null)
				|| (algorithm.name.isEmpty())
				|| (algorithm.inputsInfo == null)
				|| (algorithm.inputsInfo.isEmpty())
				|| (algorithm.outputsInfo == null)
				|| (algorithm.outputsInfo.isEmpty())
				|| (inputs == null)
				|| (inputs.size() != algorithm.inputsInfo.size())) {
			throw new IllegalArgumentException("The VcsnAlgorithm algorithm argument is invalid.");
		}

		String commandStr = "./vcsn-" + tafKitSuffix + " -v " + algorithm.name;

		for (int index = 0; index < inputs.size(); index++) {

			Object object = inputs.get(index);
			switch (algorithm.inputsInfo.get(index).type) {

				case AUTOMATON:
					if (object instanceof Automata) {
						// TODO:  Prepare temporary XML file and append the file name to command.
					} else if (object instanceof String) {
						String string = (String) object;
						if (!(string.toLowerCase().endsWith(".xml"))) {
							throw new IllegalArgumentException("The " + (index + 1) + "th argument should be an XML file representing an automaton, but it is not!");
						}
						commandStr = commandStr + " " + string;
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be an automaton, but it is not!");
					}
					break;

				case BOOLEAN:
					throw new IllegalArgumentException("The " + (index + 1) + "th argument is boolean, but no argument should be boolean.");

				case INTEGER:
					if (object instanceof Integer) {
						Integer integer = (Integer) object;
						commandStr = commandStr + " " + integer.toString();
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be an integer, but it is not!");
					}
					break;

				case REGULAR_EXPRESSION:
					if (object instanceof String) {
						String string = (String) object;
						commandStr = commandStr + " " + string;
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be a regular expression, but it is not!");
					}
					break;

				case TEXT:
					if (object instanceof String) {
						String string = (String) object;
						commandStr = commandStr + " " + string;
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be text, but it is not!");
					}
					break;

				case WEIGHT:
					throw new UnsupportedOperationException("The " + (index + 1) + "th argument should be a weight, but VGI does not support weight arguments yet!");

				case WORD:
					if (object instanceof String) {
						String string = (String) object;
						commandStr = commandStr + " " + string;
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be a word, but it is not!");
					}
					break;

				default:
					throw new IllegalArgumentException("The " + (index + 1) + "th argument does not have a recognizable type.");

			}  // End switch (algorithm.inputsInfo.get(index).type)

		}  // End for (int index = 0; index < inputs.size(); index++)

		Process process;
		int exitValue;
		try {
			process = Runtime.getRuntime().exec(commandStr, null, pmTafKitPath);
			exitValue = process.waitFor();
		} catch (IOException ioException) {
			throw new Error(ioException);
		} catch (InterruptedException interruptedException) {
			throw new Error(interruptedException);
		}

		switch (exitValue) {
			case 0:
				break;
			default:
				boolean AtLeastOneOutputIsBoolean = false;
				for (int index = 0; index < algorithm.outputsInfo.size(); index++) {
					if (algorithm.outputsInfo.get(index).type == VcsnAlgorithm.IoInfo.Type.BOOLEAN) {
						AtLeastOneOutputIsBoolean = true;
						break;  // Leave for loop
					}
				}  // End for (int index = 0; index < algorithm.outputsInfo.size(); index++)
				if (AtLeastOneOutputIsBoolean) {
					break;  // Leave switch block
				}
				InputStream inputStream = process.getInputStream();
				String string = convertInputStreamToString(inputStream);
				try {
					inputStream.close();
				} catch (IOException ioException) {
					throw new Error(ioException);
				}
				inputStream = process.getErrorStream();
				string = string + convertInputStreamToString(inputStream);
				TAFKitException tafKitException = new TAFKitException(string);
				try {
					inputStream.close();
				} catch (IOException ioException) {
					throw new Error(ioException);
				}
				throw tafKitException;
		}  // End switch (exitValue)

		InputStream stream = process.getInputStream();
		ArrayList<Object> outputs = new ArrayList<Object>();

		for (int index = 0; index < algorithm.outputsInfo.size(); index++) {

			switch (algorithm.outputsInfo.get(index).type) {

				case AUTOMATON:
					// TODO:  Convert stream to Automata.
					break;

				case BOOLEAN:
					if (exitValue == 0) {
						outputs.add(new Boolean(true));
					} else {
						outputs.add(new Boolean(false));
					}
					break;

				case INTEGER:
					throw new TAFKitException("The " + (index + 1) + "th output is an integer, but no output should be an integer.");

				case REGULAR_EXPRESSION:
					String string = this.convertInputStreamToString(stream);
					outputs.add(string);
					break;

				case TEXT:
					string = this.convertInputStreamToString(stream);
					outputs.add(string);
					break;

				case WEIGHT:
					throw new UnsupportedOperationException("The " + (index + 1) + "th output should be a weight, but VGI does not support weight outputs yet!");

				case WORD:
					string = this.convertInputStreamToString(stream);
					outputs.add(string);
					break;

				default:
					throw new TAFKitException("The " + (index + 1) + "th output does not have a recognizable type.");

			}  // End switch (algorithm.outputsInfo.get(index).type)

		}  // End for (int index = 0; index < algorithm.outputsInfo.size(); index++)

		try {
			stream.close();
		} catch (IOException ioException) {
			throw new Error(ioException);
		}

		return outputs;

	}  // End public List<Object> runVcsnAlgorithm(...)

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
			tafKit = new TAFKit("../../vaucanson-1.4a/taf-kit/tests");
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}

		List<VcsnAlgorithm> vcsnAlgorithmsList;
		String tafKitSuffix = "char-b";
		try {
			vcsnAlgorithmsList = tafKit.listVcsnAlgorithms(tafKitSuffix);
			if (vcsnAlgorithmsList == null) {
				return;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}

		VcsnAlgorithm algorithmToRun = null;

		for (int index = 0; index < vcsnAlgorithmsList.size(); index++) {

			VcsnAlgorithm vcsnAlgorithm = vcsnAlgorithmsList.get(index);
			if (vcsnAlgorithm.name.compareToIgnoreCase("category") == 0) {
				System.out.println();
				System.out.println(vcsnAlgorithm.name + ":  " + vcsnAlgorithm.description);
				System.out.println();
			} else {  // End if (vcsnAlgorithm.name.compareToIgnoreCase("category") == 0)

				if (vcsnAlgorithm.name.equals("eval")) {
					algorithmToRun = vcsnAlgorithm;
				}

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

		List<Object> outputs;
		try {
			ArrayList<Object> inputs = new ArrayList<Object>();
			inputs.add("a1.xml");
			inputs.add("ab");
			outputs = tafKit.runVcsnAlgorithm(tafKitSuffix, algorithmToRun, inputs);
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}

		for (int index = 0; index < algorithmToRun.outputsInfo.size(); index++) {

			Object object = outputs.get(index);
			switch (algorithmToRun.outputsInfo.get(index).type) {

				case AUTOMATON:
					// TODO:  Convert stream to a FSM XML file and open it in VGI.
					break;

				case BOOLEAN:
					if (object instanceof Boolean) {
						Boolean bool = (Boolean) object;
						System.out.println(bool);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th output should be an integer, but it is not!");
					}
					break;

				case INTEGER:
					if (object instanceof Integer) {
						Integer integer = (Integer) object;
						System.out.println(integer);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th output should be an integer, but it is not!");
					}
					break;

				case REGULAR_EXPRESSION:
					if (object instanceof String) {
						String string = (String) object;
						System.out.println(string);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th output should be a regular expression, but it is not!");
					}
					break;

				case TEXT:
					if (object instanceof String) {
						String string = (String) object;
						System.out.println(string);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th output should be text, but it is not!");
					}
					break;

				case WEIGHT:
					throw new UnsupportedOperationException("The " + (index + 1) + "th output should be a weight, but VGI does not support weight outputs yet!");

				case WORD:
					if (object instanceof String) {
						String string = (String) object;
						System.out.println(string);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th output should be a word, but it is not!");
					}
					break;

				default:
					throw new IllegalArgumentException("The " + (index + 1) + "th output does not have a recognizable type.");

			}  // End switch (algorithmToRun.outputsInfo.get(index).type)

		}  // End for (int index = 0; index < algorithmToRun.outputsInfo.size(); index++)

	}  // End public static void main(String args[])
}  // End public class TAFKit

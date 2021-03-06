package vgi.fsmxml;

import vgi.fsmxml.FsmXml;
import vgi.automata.AutomataInterface;
import vgi.automata.Automata;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vgi.fsmxml.FsmXmlInterface.FsmXmlException;

/**
 *
 * @author JLiu
 */
public class TAFKit implements TAFKitInterface {

	private File pmTafKitPath;

	public TAFKit(File tafKitPath)
			throws
			IllegalArgumentException {
		this.setTafKitPath(tafKitPath);
	}  // End public TAFKit(String tafKitFolderPathStr)

	@Override
	public File getTafKitPath() {
		return pmTafKitPath;
	}  // End public File getTafKitPath()

	@Override
	public void setTafKitPath(
			File tafKitFolderPath)
			throws
			IllegalArgumentException {

		if (!tafKitFolderPath.exists()) {
			throw new IllegalArgumentException("The specified path \"" + tafKitFolderPath.getAbsolutePath() + "\" does not exist!");
		}
		if (!tafKitFolderPath.isDirectory()) {
			throw new IllegalArgumentException("The specified path \"" + tafKitFolderPath.getAbsolutePath() + "\" is not a folder!");
		}
		File file = new File(tafKitFolderPath, "vcsn-char-b");
		if ((!file.isFile()) || (!file.canExecute())) {
			throw new IllegalArgumentException("The specified path \"" + tafKitFolderPath.getAbsolutePath() + "\" does not have the most basic TAF-Kit executable \"vcsn-char-b\" so it is probably an incorrect TAF-Kit path.");
		}
		Process process;
		int exitValue;
		try {
			process = Runtime.getRuntime().exec("./vcsn-char-b data a1.xml", null, tafKitFolderPath);
			exitValue = process.waitFor();
		} catch (IOException ioException) {
			throw new Error(ioException);
		} catch (InterruptedException interruptedException) {
			throw new Error(interruptedException);
		}
		if (exitValue != 0) {
			throw new IllegalArgumentException("The most basic TAF-Kit executable \"vcsn-char-b\" in the specified path \"" + tafKitFolderPath.getAbsolutePath() + "\" did not execute successfully so this is probably an incorrect TAF-Kit path.");
		}
		pmTafKitPath = tafKitFolderPath;

	}  // End public void setTafKitPath(...)

	@Override
	public List<VcsnAlgorithm> listVcsnAlgorithms(
			AutomataType automataType)
			throws
			FileNotFoundException,
			TAFKitException {

		String tafKitExecutableFileName = automataType.toExecutableFileName();
		if (!(new File(pmTafKitPath, tafKitExecutableFileName).exists())) {
			throw new FileNotFoundException("The specified TAF-Kit executable \"" + this.getTafKitPath() + File.separator + tafKitExecutableFileName + "\" does not exist!");
		}

		Process process;
		InputStream inputStream;
		String string;
		int exitValue;
		try {
			process = Runtime.getRuntime().exec("./" + tafKitExecutableFileName + " --list-all-commands-json",
					null, pmTafKitPath);
			inputStream = process.getInputStream();
			string = convertInputStreamToString(inputStream);
			inputStream.close();
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
			jaCategories = new JSONArray(string);
		} catch (JSONException jsonException) {
			throw new TAFKitException("Output format of --list-all-commands-json probably changed.", jsonException);
		}

		ArrayList<VcsnAlgorithm> vcsnAlgorithmsList = new ArrayList<VcsnAlgorithm>();

		for (int index = 0; index < jaCategories.length(); index++) {

			JSONObject joCategory = jaCategories.optJSONObject(index);
			if (joCategory == null) {
				throw new TAFKitException("Output format of --list-all-commands-json probably changed.");
			}

			string = joCategory.optString("category", null);
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

	@Override
	public List<Object> runVcsnAlgorithm(
			AutomataType automataType,
			VcsnAlgorithm algorithm,
			List<Object> inputs)
			throws
			FileNotFoundException,
			IllegalArgumentException,
			TAFKitException {

		String tafKitExecutableFileName = automataType.toExecutableFileName();
		if (!(new File(pmTafKitPath, tafKitExecutableFileName).exists())) {
			throw new FileNotFoundException("The specified TAF-Kit executable \"" + this.getTafKitPath() + File.separator + tafKitExecutableFileName + "\" does not exist!");
		}

		if ((algorithm == null)
				|| (algorithm.name == null)
				|| (algorithm.name.isEmpty())
				|| (algorithm.inputsInfo == null)
				|| ((algorithm.inputsInfo.isEmpty()) && (!(inputs.get(0) instanceof AutomataInterface.Alphabet)))
				|| (algorithm.outputsInfo == null)
				|| (algorithm.outputsInfo.isEmpty())
				|| (inputs == null)
				|| (inputs.size() < algorithm.inputsInfo.size())) {
			throw new IllegalArgumentException("The VcsnAlgorithm algorithm argument is invalid.");
		}

		List<String> commandTokens = new ArrayList<String>();
		commandTokens.add("./" + tafKitExecutableFileName);
		commandTokens.add("-v");
		commandTokens.add(algorithm.name);
		List<Automata> automataList = new ArrayList<Automata>();
		List<File> tempFileList = new ArrayList<File>();

		for (int index = 0, infoIndex = 0;
				infoIndex < algorithm.inputsInfo.size();
				index++, infoIndex++) {

			Object object = inputs.get(index);
			switch (algorithm.inputsInfo.get(infoIndex).type) {

				case AUTOMATON:
					File file;
					if (object instanceof Automata) {
						automataList.add((Automata) object);
						file = saveAutomataToTempFile(automataList);
					} else if (object instanceof File) {
						file = saveAutomataToTempFile((File) object);
					} else if (object instanceof String) {
						file = saveAutomataToTempFile((String) object);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be an automaton, but it is not!");
					}
					tempFileList.add(file);
					commandTokens.add(file.getAbsolutePath());
					break;

				case BOOLEAN:
					throw new IllegalArgumentException("The " + (index + 1) + "th argument is boolean, but no argument should be boolean.");

				case INTEGER:
					if (object instanceof Integer) {
						Integer integer = (Integer) object;
						commandTokens.add(integer.toString());
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be an integer, but it is not!");
					}
					break;

				case REGULAR_EXPRESSION:
					if (!(object instanceof AutomataInterface.Alphabet)) {
						break;
					}
					AutomataInterface.Alphabet alphabet = (AutomataInterface.Alphabet) object;
					String specifyAlphabet = "-a";
					for (Object symbol : alphabet.allSymbols) {
						specifyAlphabet = specifyAlphabet + symbol.toString();
					}
					commandTokens.add(specifyAlphabet);
					object = inputs.get(index + 1);
					if (object instanceof String) {
						String string = (String) object;
						commandTokens.add(string);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be a regular expression, but it is not!");
					}
					index++;
					break;

				case TEXT:
					if (object instanceof String) {
						String string = (String) object;
						commandTokens.add(string);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be text, but it is not!");
					}
					break;

				case WEIGHT:
					throw new UnsupportedOperationException("The " + (index + 1) + "th argument should be a weight, but VGI does not support weight arguments yet!");

				case WORD:
					if (object instanceof String) {
						String string = (String) object;
						commandTokens.add(string);
					} else {
						throw new IllegalArgumentException("The " + (index + 1) + "th argument should be a word, but it is not!");
					}
					break;

				default:
					throw new IllegalArgumentException("The " + (index + 1) + "th argument does not have a recognizable type.");

			}  // End switch (algorithm.inputsInfo.get(index).type)

		}  // End for (int index = 0, infoIndex = 0;
		//		infoIndex < algorithm.inputsInfo.size();
		//		index++, infoIndex++)

		if (algorithm.inputsInfo.isEmpty()) {
			Object object = inputs.get(0);
			if (object instanceof AutomataInterface.Alphabet) {
				AutomataInterface.Alphabet alphabet = (AutomataInterface.Alphabet) object;
				String specifyAlphabet = "-a";
				for (Object symbol : alphabet.allSymbols) {
					specifyAlphabet = specifyAlphabet + symbol.toString();
				}
				commandTokens.add(specifyAlphabet);
			}
		}  // End if (algorithm.inputsInfo.isEmpty())

		Process process;
		InputStream inputStream;
		String string;
		int exitValue;
		try {
			String[] cmdArray = new String[commandTokens.size()];
			commandTokens.toArray(cmdArray);
			process = Runtime.getRuntime().exec(cmdArray, null, pmTafKitPath);
//			if (!(automataList.isEmpty())) {
//				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(process.getOutputStream());
//				FsmXml fsmXml = new FsmXml();
//				fsmXml.write(automataList, bufferedOutputStream);
//				bufferedOutputStream.close();
//			}  // End if (!(automataList.isEmpty()))
			inputStream = process.getInputStream();
			string = convertInputStreamToString(inputStream);
			inputStream.close();
			exitValue = process.waitFor();
		} catch (IOException ioException) {
			throw new Error(ioException);
		} catch (InterruptedException interruptedException) {
			throw new Error(interruptedException);
//		} catch (FsmXmlException fsmXmlException) {
//			throw new Error(fsmXmlException);
		}

		Iterator<File> iterateFile = tempFileList.iterator();
		while (iterateFile.hasNext()) {
			iterateFile.next().delete();
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

		ArrayList<Object> outputs = new ArrayList<Object>();

		for (int index = 0; index < algorithm.outputsInfo.size(); index++) {

			switch (algorithm.outputsInfo.get(index).type) {

				case AUTOMATON:
					inputStream = new ByteArrayInputStream(string.getBytes());
					FsmXml fsmXml = new FsmXml();
					try {
						automataList = fsmXml.read(inputStream);
						outputs.add(automataList.get(0));
					} catch (FsmXmlException fsmXmlException) {
						throw new TAFKitException(fsmXmlException);
					} finally {
						fsmXml = null;  // FsmXml fsmXml = new FsmXml();
						inputStream = null;  // inputStream = new ByteArrayInputStream(string.getBytes());
					}
					break;

				case BOOLEAN:
					if (exitValue == 0) {
						outputs.add(true);
					} else {
						outputs.add(false);
					}
					break;

				case INTEGER:
					throw new TAFKitException("The " + (index + 1) + "th output is an integer, but no output should be an integer.");

				case REGULAR_EXPRESSION:
					outputs.add(string);
					break;

				case TEXT:
					outputs.add(string);
					break;

				case WEIGHT:
					throw new UnsupportedOperationException("The " + (index + 1) + "th output should be a weight, but VGI does not support weight outputs yet!");

				case WORD:
					outputs.add(string);
					break;

				default:
					throw new TAFKitException("The " + (index + 1) + "th output does not have a recognizable type.");

			}  // End switch (algorithm.outputsInfo.get(index).type)

		}  // End for (int index = 0; index < algorithm.outputsInfo.size(); index++)

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

	protected static File saveAutomataToTempFile(String filePath)
			throws
			TAFKitException {
		File file = new File(filePath);
		return saveAutomataToTempFile(file);
	}  // End protected File saveAutomataToTempFile(String filePath)

	protected static File saveAutomataToTempFile(File file)
			throws
			TAFKitException {

		FsmXml fsmXml = new FsmXml();
		try {
			List<Automata> automataList = fsmXml.read(file);
			return saveAutomataToTempFile(automataList);
		} catch (IOException ioException) {
			throw new TAFKitException(ioException);
		} catch (FsmXmlException fsmXmlException) {
			throw new TAFKitException(fsmXmlException);
		}

	}  // End protected File saveAutomataToTempFile(File file)

	protected static File saveAutomataToTempFile(List<Automata> automataList)
			throws
			TAFKitException {

		File file = null;
		try {
			file = File.createTempFile("vgi_aut", "." + FsmXmlInterface.fileNameExtensionFilter.getExtensions()[0]);
		} catch (IOException ioException) {
			throw new TAFKitException(ioException);
		}
		if (file == null) {
			throw new TAFKitException("Failed to create temporary files.");
		}
		FsmXml fsmXml = new FsmXml();
		try {
			fsmXml.write(automataList, file, false);
		} catch (IOException ioException) {
			file.delete();
			throw new TAFKitException(ioException);
		} catch (FsmXmlException fsmXmlException) {
			file.delete();
			throw new TAFKitException(fsmXmlException);
		}
		file.deleteOnExit();

		return file;
	}  // End protected File saveAutomataToTempFile(Automata automata)

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {

		TAFKit tafKit = null;
		try {
			tafKit = new TAFKit(new File("../../vaucanson-1.4a/taf-kit/tests"));
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}

		List<VcsnAlgorithm> vcsnAlgorithmsList;
		AutomataType automataType = new AutomataType(
				AutomataType.Semiring.B_BOOLEAN,
				AutomataType.AlphabetDataType.CHAR,
				null);
		try {
			vcsnAlgorithmsList = tafKit.listVcsnAlgorithms(automataType);
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
			outputs = tafKit.runVcsnAlgorithm(automataType, algorithmToRun, inputs);
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

		try {
			automataType = new AutomataType( // vcsn-char-char-b
					AutomataType.Semiring.B_BOOLEAN,
					AutomataType.AlphabetDataType.CHAR_CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-char-z
					AutomataType.Semiring.Z_INTEGER,
					AutomataType.AlphabetDataType.CHAR_CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-f2
					AutomataType.Semiring.F2_TWO_ELEMENT_FIELD,
					AutomataType.AlphabetDataType.CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-fmp-b
					AutomataType.Semiring.B_BOOLEAN,
					AutomataType.AlphabetDataType.CHAR,
					AutomataType.AlphabetDataType.CHAR);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-fmp-z
					AutomataType.Semiring.Z_INTEGER,
					AutomataType.AlphabetDataType.CHAR,
					AutomataType.AlphabetDataType.CHAR);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-int-b
					AutomataType.Semiring.B_BOOLEAN,
					AutomataType.AlphabetDataType.CHAR_INT,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-q
					AutomataType.Semiring.Q_RATIONAL,
					AutomataType.AlphabetDataType.CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-r
					AutomataType.Semiring.R_REAL,
					AutomataType.AlphabetDataType.CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-z
					AutomataType.Semiring.Z_INTEGER,
					AutomataType.AlphabetDataType.CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-zmax
					AutomataType.Semiring.ZMAX_MAX_TROPICAL,
					AutomataType.AlphabetDataType.CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-char-zmin
					AutomataType.Semiring.ZMIN_MIN_TROPICAL,
					AutomataType.AlphabetDataType.CHAR,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-int-b
					AutomataType.Semiring.B_BOOLEAN,
					AutomataType.AlphabetDataType.INT,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-int-fmp-b
					AutomataType.Semiring.B_BOOLEAN,
					AutomataType.AlphabetDataType.INT,
					AutomataType.AlphabetDataType.INT);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-int-fmp-z
					AutomataType.Semiring.Z_INTEGER,
					AutomataType.AlphabetDataType.INT,
					AutomataType.AlphabetDataType.INT);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-int-int-b
					AutomataType.Semiring.B_BOOLEAN,
					AutomataType.AlphabetDataType.INT_INT,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-int-int-z
					AutomataType.Semiring.Z_INTEGER,
					AutomataType.AlphabetDataType.INT_INT,
					null);
			tafKit.listVcsnAlgorithms(automataType);
			automataType = new AutomataType( // vcsn-int-z
					AutomataType.Semiring.Z_INTEGER,
					AutomataType.AlphabetDataType.INT,
					null);
			tafKit.listVcsnAlgorithms(automataType);
		} catch (Exception exception) {
			exception.printStackTrace();
			return;
		}

	}  // End public static void main(String args[])
}  // End public class TAFKit

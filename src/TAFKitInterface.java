
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface TAFKitInterface {

	public class VcsnAlgorithm {

		public static class IoInfo {

			public enum Type {

				AUTOMATON, BOOLEAN, INTEGER, REGULAR_EXPRESSION, TEXT, WEIGHT, WORD, UNKNOWN
			}  // End public enum Type
			Type type;
			String context;
		}  // End public class InputOutput
		public String name;
		public List<IoInfo> inputsInfo;
		public List<IoInfo> outputsInfo;
		public String description;
	}  // End public class VcsnAlgorithm

	public class AutomataType {

		public enum Semiring {

			B_BOOLEAN, Z_INTEGER, Q_RATIONAL, R_REAL, F2_TWO_ELEMENT_FIELD,
			ZMIN_MIN_TROPICAL, ZMAX_MAX_TROPICAL
		}

		public enum AlphabetDataType {

			CHAR, INT
		}
		Semiring semiring;
		AlphabetDataType alphabetDataType;
		AlphabetDataType secondAlphabetDataType;
		boolean isTransducer;

		public AutomataType() {
			this.semiring = Semiring.B_BOOLEAN;
			this.alphabetDataType = AlphabetDataType.CHAR;
			this.secondAlphabetDataType = null;
			this.isTransducer = false;
		}

		public AutomataType(
				Semiring semiring,
				AlphabetDataType alphabetDataType,
				AlphabetDataType secondAlphabetDataType,
				boolean isTransducer) {
			this.semiring = semiring;
			this.alphabetDataType = alphabetDataType;
			this.secondAlphabetDataType = secondAlphabetDataType;
			this.isTransducer = isTransducer;
		}

		public AutomataType(Automata automata) {
			this();
			this.semiring = automata.getWeight().semiring;
			this.alphabetDataType = automata.getAlphabet().dataType;
			if (automata.getSecondAlphabet() != null) {
				this.secondAlphabetDataType = automata.getSecondAlphabet().dataType;
			}
			this.isTransducer = automata.isTransducer();
		}

		public String toExecutableFileName() {

			String string = "vcsn-";

			switch (this.alphabetDataType) {
				case CHAR:
					string = string + "char-";
					break;
				case INT:
					string = string + "int-";
					break;
				default:
					throw new IllegalArgumentException("Invalid alphabet type for TAF-Kit instance.");
			}  // End switch (this.alphabetDataType)

			if (this.isTransducer) {
				string = string + "fmp-";
			} else if (this.secondAlphabetDataType != null) {

				switch (this.alphabetDataType) {
					case CHAR:
						string = string + "char-";
						break;
					case INT:
						string = string + "int-";
						break;
					default:
						throw new IllegalArgumentException("Unrecognized alphabet data type for TAF-Kit instance.");
				}  // End switch (this.alphabetDataType)

			}  // End else if (this.secondAlphabetDataType != null)

			switch (this.semiring) {

				case B_BOOLEAN:
					string = string + "b";
					break;

				case Z_INTEGER:
					string = string + "z";
					break;

				case Q_RATIONAL:
					string = string + "q";
					break;

				case R_REAL:
					string = string + "r";
					break;

				case F2_TWO_ELEMENT_FIELD:
					string = string + "f2";
					break;

				case ZMIN_MIN_TROPICAL:
					string = string + "zmin";
					break;

				case ZMAX_MAX_TROPICAL:
					string = string + "zmax";
					break;

				default:
					throw new IllegalArgumentException("Unrecognized semiring for TAF-Kit instance.");

			}  // End switch (this.semiring)

			return string;

		}  // End public public String toExecutableFileName()
	}  // End public class AutomataType

	public class TAFKitException extends Exception {

		public TAFKitException() {
			super();
		}

		public TAFKitException(String message) {
			super(message);
		}

		public TAFKitException(String message, Throwable cause) {
			super(message, cause);
		}

		public TAFKitException(Throwable cause) {
			super(cause);
		}
	}  // End public class TAFKitException extends Exception

	public File getTafKitPath();

	public void setTafKitPath(
			File tafKitFolderPath)
			throws
			IllegalArgumentException;

	public List<VcsnAlgorithm> listVcsnAlgorithms(
			AutomataType automataType)
			throws
			FileNotFoundException,
			TAFKitException;

	public List<Object> runVcsnAlgorithm(
			AutomataType automataType,
			VcsnAlgorithm algorithm,
			List<Object> inputs)
			throws
			FileNotFoundException,
			IllegalArgumentException,
			TAFKitException;
}  // End public interface TAFKitInterface

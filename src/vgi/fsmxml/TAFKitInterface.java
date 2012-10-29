package vgi;

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

			CHAR, INT,
			CHAR_CHAR, CHAR_INT, INT_CHAR, INT_INT
		}
		Semiring semiring;
		AlphabetDataType alphabetDataType;
		AlphabetDataType outputAlphabetDataType;

		public AutomataType() {
			this.semiring = Semiring.B_BOOLEAN;
			this.alphabetDataType = AlphabetDataType.CHAR;
			this.outputAlphabetDataType = null;
		}

		public AutomataType(
				Semiring semiring,
				AlphabetDataType alphabetDataType,
				AlphabetDataType outputAlphabetDataType) {
			this.semiring = semiring;
			this.alphabetDataType = alphabetDataType;
			this.outputAlphabetDataType = outputAlphabetDataType;
		}

		public AutomataType(Automata automata) {
			this();
			this.semiring = automata.getWeight().semiring;
			this.alphabetDataType = automata.getAlphabet().dataType;
			if (automata.getOutputAlphabet() != null) {
				this.outputAlphabetDataType = automata.getOutputAlphabet().dataType;
			} else {
				this.outputAlphabetDataType = null;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AutomataType)) {
				return false;
			}
			AutomataType automataType = (AutomataType) obj;
			if (this.semiring == null) {
				if (automataType.semiring != null) {
					return false;
				}
			} else {
				if (!(this.semiring.equals(automataType.semiring))) {
					return false;
				}
			}
			if (this.alphabetDataType == null) {
				if (automataType.alphabetDataType != null) {
					return false;
				}
			} else {
				if (!(this.alphabetDataType.equals(automataType.alphabetDataType))) {
					return false;
				}
			}
			if (this.outputAlphabetDataType == null) {
				return (automataType.outputAlphabetDataType == null);
			} else {
				return this.outputAlphabetDataType.equals(automataType.outputAlphabetDataType);
			}
		}  // End public boolean equals(Object obj)

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 41 * hash + (this.semiring != null ? this.semiring.hashCode() : 0);
			hash = 41 * hash + (this.alphabetDataType != null ? this.alphabetDataType.hashCode() : 0);
			hash = 41 * hash + (this.outputAlphabetDataType != null ? this.outputAlphabetDataType.hashCode() : 0);
			return hash;
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
				case CHAR_CHAR:
					string = string + "char-char-";
					break;
				case CHAR_INT:
					string = string + "char-int-";
					break;
				case INT_CHAR:
					string = string + "int-char-";
					break;
				case INT_INT:
					string = string + "int-int-";
					break;
				default:
					throw new IllegalArgumentException("Invalid alphabet type for TAF-Kit instance.");
			}  // End switch (this.alphabetDataType)

			if (this.outputAlphabetDataType != null) {
				string = string + "fmp-";
			}

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

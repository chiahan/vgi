
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

	public String getTafKitPath();

	public void setTafKitPath(
			String tafKitFolderPath)
			throws
			FileNotFoundException;

	public List<VcsnAlgorithm> listVcsnAlgorithms(
			String tafKitSuffix)
			throws
			FileNotFoundException,
			TAFKitException;

	public List<Object> runVcsnAlgorithm(
			String tafKitSuffix,
			VcsnAlgorithm algorithm,
			List<Object> inputs)
			throws
			FileNotFoundException,
			IllegalArgumentException,
			TAFKitException;
}  // End public interface TAFKitInterface

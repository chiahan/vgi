
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

	public String getTafKitPathString();

	public void setTafKitPathString(
			String tafKitFolderPathStr)
			throws
			FileNotFoundException;

	public List<VcsnAlgorithm> listVcsnAlgorithms(
			String tafKitSuffix)
			throws
			FileNotFoundException;

	public List<Object> runVcsnAlgorithm(
			String tafKitSuffix,
			VcsnAlgorithm algorithm,
			List<Object> inputs)
			throws
			FileNotFoundException,
			IllegalArgumentException;
}  // End public interface TAFKitInterface

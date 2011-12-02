
import java.io.File;
import java.util.ArrayList;

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
		public ArrayList<IoInfo> inputs;
		public ArrayList<IoInfo> outputs;
		public String description;
	}  // End public class VcsnAlgorithm

	public File getTafKitFolder();

	public void setTafKitFolder(String tafKitFolderPathStr);

	public ArrayList<VcsnAlgorithm> listVcsnAlgorithms(String tafKitSuffix);

	public ArrayList<Object> runVcsnAlgorithm(String tafKitSuffix, VcsnAlgorithm algorithm, ArrayList<Object> inputs);
}  // End public interface TAFKitInterface

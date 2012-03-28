package vgi;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author JLiu
 */
public interface FsmXmlInterface {

	public static FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("FSM XML files", "xml");

	public class FsmXmlException extends Exception {

		public FsmXmlException() {
			super();
		}

		public FsmXmlException(String message) {
			super(message);
		}

		public FsmXmlException(String message, Throwable cause) {
			super(message, cause);
		}

		public FsmXmlException(Throwable cause) {
			super(cause);
		}
	}  // End public class FsmXmlException extends Exception

	public List<Automata> read(File fsmXmlFile)
			throws
			FileNotFoundException,
			IOException,
			FsmXmlException;

	public List<Automata> read(InputStream inputStream)
			throws
			FsmXmlException;

	public void write(List<Automata> automataList, File fsmXmlFile)
			throws
			IOException,
			FsmXmlException;

	public void write(List<Automata> automataList, OutputStream outputStream)
			throws
			FsmXmlException;
}  // End public interface FsmXmlInterface


import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author JLiu
 */
public class FsmXml implements FsmXmlInterface {

	private static class Tag {

		public enum Type {

			START, END
		}
		public String localName;
		public Type type;

		public Tag() {
			this.localName = null;
			this.type = null;
		}

		public Tag(String localName) {
			this();
			this.localName = localName;
		}

		public Tag(String localName, Type type) {
			this(localName);
			this.type = type;
		}

		@Override
		public boolean equals(Object obj) {
			if ((obj == null)
					|| (!(Tag.class.isAssignableFrom(obj.getClass())))) {
				return false;
			}
			Tag tag = (Tag) obj;
			if (!(this.localName.equals(tag.localName))) {
				return false;
			}
			if (this.type != tag.type) {
				return false;
			}
			return true;
		}  // End public boolean equals(Object obj)

		public boolean equals(String localName, Type type) {
			if ((localName == null) || (type == null)) {
				return false;
			}
			if (!(this.localName.equals(localName))) {
				return false;
			}
			if (this.type != type) {
				return false;
			}
			return true;
		}  // End public boolean equals(String localName, Type type)
	}  // End private class Tag
	private static final String TAG_FSMXML = "fsmxml";
	private static final String VAL_FSMXML_NAMESPACE = "http://vaucanson.lrde.epita.fr";
	private static final String VAL_FSMXML_VERSION_NUMBER = "1.0";
	private static final String TAG_AUTOMATON = "automaton";
	private static final String TAG_VALUE_TYPE = "valueType";
	private static final String TAG_WRITING_DATA = "writingData";
	private static final String ATR_CLOSE_PAR = "closePar";
	private static final String ATR_OPEN_PAR = "openPar";
	private static final String ATR_PLUS_SYM = "plusSym";
	private static final String ATR_SPACES_SYM = "spacesSym";
	private static final String ATR_STAR_SYM = "starSym";
	private static final String ATR_TIMES_SYM = "timesSym";
	private static final String ATR_WEIGHT_CLOSING = "weightClosing";
	private static final String ATR_WEIGHT_OPENING = "weightOpening";
	private static final String ATR_ZERO_SYM = "zeroSym";
	private static final String ATR_IDENTITY_SYM = "identitySym";
	private static final String TAG_SEMIRING = "semiring";
	private static final String ATR_SET = "set";
	private static final String VAL_B = "B";
	private static final String VAL_Z = "Z";
	private static final String VAL_Q = "Q";
	private static final String VAL_R = "R";
	private static final String ATR_OPERATIONS = "operations";
	private static final String VAL_CLASSICAL = "classical";
	private static final String VAL_FIELD = "field";
	private static final String VAL_MIN_PLUS = "minPlus";
	private static final String VAL_MAX_PLUS = "maxPlus";
	private static final String VAL_NUMERICAL = "numerical";
	private static final String TAG_MONOID = "monoid";
	private static final String ATR_GEN_DESCRIP = "genDescrip";
	private static final String VAL_ENUM = "enum";
	private static final String ATR_GEN_KIND = "genKind";
	private static final String VAL_SIMPLE = "simple";
	private static final String VAL_TUPLE = "tuple";
	private static final String ATR_GEN_SORT = "genSort";
	private static final String VAL_LETTERS = "letters";
	private static final String VAL_DIGITS = "digits";
	private static final String VAL_ALPHANUMS = "alphanums";
	private static final String VAL_INTEGERS = "integers";
	private static final String ATR_TYPE = "type";
	private static final String VAL_UNIT = "unit";
	private static final String VAL_FREE = "free";
	private static final String VAL_PRODUCT = "product";
	private static final String ATR_GEN_DIM = "genDim";
	private static final String ATR_PROD_DIM = "prodDim";
	private static final String TAG_MON_GEN = "monGen";
	private static final String ATR_VALUE = "value";
	private static final String TAG_GEN_SORT = "genSort";
	private static final String TAG_GEN_COMP_SORT = "genCompSort";
	private static final String TAG_MON_COMP_GEN = "monCompGen";
	private static final String TAG_AUTOMATON_STRUCT = "automatonStruct";
	private static final String TAG_STATES = "states";
	private static final String TAG_STATE = "state";
	private static final String ATR_ID = "id";
	private static final String ATR_NAME = "name";
	private static final String TAG_GEOMETRIC_DATA = "geometricData";
	private static final String ATR_X = "x";
	private static final String ATR_Y = "y";
	private static final String TAG_TRANSITIONS = "transitions";
	private static final String TAG_TRANSITION = "transition";
	private static final String ATR_SOURCE = "source";
	private static final String ATR_TARGET = "target";
	private static final String TAG_LABEL = "label";
	private static final String TAG_CONTROL_POINT = "controlPoint";
	private static final String TAG_ZERO = "zero";
	private static final String TAG_ONE = "one";
	private static final String TAG_MON_ELMT = "monElmt";
	private static final String TAG_SUM = "sum";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_STAR = "star";
	private static final String TAG_LEFT_EXT_MUL = "leftExtMul";
	private static final String TAG_RIGHT_EXT_MUL = "rightExtMul";
	private static final String TAG_WEIGHT = "weight";
	private static final String TAG_INITIAL = "initial";
	private static final String TAG_FINAL = "final";
	private static final String ATR_STATE = "state";

	@Override
	public List<Automata> read(File fsmXmlFile)
			throws
			FileNotFoundException,
			FsmXmlException {
		InputStream inputStream = new FileInputStream(fsmXmlFile);
		return this.read(inputStream);
	}  // End public List<Automata> read(File fsmXmlFile)

	private Tag nextStartOrEndTag(XMLStreamReader xmlStreamReader)
			throws XMLStreamException {
		Tag tag = null;
		while (xmlStreamReader.hasNext()) {
			int eventType = xmlStreamReader.next();
			if (eventType == XMLStreamReader.START_ELEMENT) {
				tag = new Tag(xmlStreamReader.getLocalName(), Tag.Type.START);
				break;
			} else if (eventType == XMLStreamReader.END_ELEMENT) {
				tag = new Tag(xmlStreamReader.getLocalName(), Tag.Type.END);
				break;
			}
		}  // End while (xmlStreamReader.hasNext())
		return tag;
	}  // End private String nextStartTag(XMLStreamReader xmlStreamReader)

	private boolean findNextSpecifiedTag(XMLStreamReader xmlStreamReader, String localName, Tag.Type type)
			throws
			XMLStreamException {
		while (xmlStreamReader.hasNext()) {
			int eventType = xmlStreamReader.next();
			if (((eventType == XMLStreamReader.START_ELEMENT) && (type == Tag.Type.START))
					|| ((eventType == XMLStreamReader.END_ELEMENT) && (type == Tag.Type.END))) {
				String name = xmlStreamReader.getLocalName();
				if (localName.equals(name)) {
					return true;
				}
			}
		}  // End while (xmlStreamReader.hasNext())
		return false;
	}  // End private boolean findNextSpecifiedTag(String localName, Tag.Type type)

	private boolean findNextSpecifiedTag(XMLStreamReader xmlStreamReader, Tag tag)
			throws
			XMLStreamException {
		if ((tag == null) || (tag.localName == null) || (tag.type == null)) {
			throw new IllegalArgumentException();
		}
		return this.findNextSpecifiedTag(xmlStreamReader, tag.localName, tag.type);
	}  // End private boolean findNextSpecifiedTag(Tag tag)

	private void assertTag(String localName, Tag.Type type) throws FsmXmlException {

		if ((localName == null) || (type == null)) {
			throw new IllegalArgumentException();
		}

		String message = "Expected \"" + localName;
		if (type == Tag.Type.START) {
			message = message + " start\"";
		} else {
			message = message + " end\"";
		}

		throw new FsmXmlException(message + " tag is not found.");

	}  // End private void assertTag(String localName, Tag.Type type) throws FsmXmlException

	private void assertTag(Tag tagExpected, Tag tagFound) throws FsmXmlException {

		if (tagExpected.equals(tagFound)) {
			return;
		}

		String message = "Expected \"" + tagExpected.localName;
		if (tagExpected.type == Tag.Type.START) {
			message = message + " start\"";
		} else {
			message = message + " end\"";
		}
		if (tagFound == null) {
			message = message + " tag is not found.";
		} else {
			message = message + " tag but found \"" + tagFound.localName;
			if (tagFound.type == Tag.Type.START) {
				message = message + " start\" tag.";
			} else {
				message = message + " end\" tag.";
			}
		}

		throw new FsmXmlException(message);

	}  // End private void assertTag(Tag tagExpected, Tag tagFound) throws FsmXmlException

	@Override
	public List<Automata> read(InputStream inputStream)
			throws
			FileNotFoundException,
			FsmXmlException {

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlStreamReader = null;
		List<Automata> automataList = null;

		try {
			xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
			int eventType = xmlStreamReader.getEventType();
			if (eventType != XMLStreamReader.START_DOCUMENT) {
				throw new FsmXmlException("Unrecognizable FSM XML file.");
			}
			if (!(xmlStreamReader.hasNext())) {
				throw new FsmXmlException("Unrecognizable FSM XML file.");
			}
			eventType = xmlStreamReader.next();
			if (eventType != XMLStreamReader.START_ELEMENT) {
				throw new FsmXmlException("Unrecognizable FSM XML file.");
			}
			String localName = xmlStreamReader.getLocalName();
			//
			// Check that <fsmxml xmlns="http://vaucanson.lrde.epita.fr" version="1.0"> is at the root level.
			//
			if (!(localName.equals(TAG_FSMXML))) {
				throw new FsmXmlException("Root tag is not " + TAG_FSMXML + " so this is likely an invalid FSM XML file.");
			} else if (!(xmlStreamReader.getNamespaceURI().equals(VAL_FSMXML_NAMESPACE))) {
				throw new FsmXmlException("Namespace is not " + VAL_FSMXML_NAMESPACE + " so this is likely an invalid FSM XML file.");
			} else if (!(xmlStreamReader.getVersion().equals(VAL_FSMXML_VERSION_NUMBER))) {
				throw new FsmXmlException("VGI only supports FSM XML files whose version is " + VAL_FSMXML_VERSION_NUMBER + ".");
			}
			automataList = new ArrayList<Automata>();

			while (xmlStreamReader.hasNext()) {

				eventType = xmlStreamReader.next();
				if (eventType == XMLStreamReader.START_ELEMENT) {

					localName = xmlStreamReader.getLocalName();
					if (localName.equals(TAG_AUTOMATON)) {
						Automata automata = parseAutomatonTag(xmlStreamReader);
						automataList.add(automata);
					}

				} // End if (eventType == XMLStreamReader.START_ELEMENT)
				else if (eventType == XMLStreamReader.END_ELEMENT) {

					localName = xmlStreamReader.getLocalName();
					if (localName.equals(TAG_FSMXML)) {
						break;
					}

				}  // End if (eventType == XMLStreamReader.END_ELEMENT)

			}  // End while (xmlStreamReader.hasNext())

		} catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
		} finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				} catch (XMLStreamException xmlStreamException) {
					throw new FsmXmlException(xmlStreamException);
				}
			}  // End if (xmlStreamReader != null)
		}

		if ((automataList != null) && (automataList.isEmpty())) {
			automataList = null;
		}

		return automataList;
	}  // End public List<Automata> read(InputStream inputStream)

	private Automata parseAutomatonTag(XMLStreamReader xmlStreamReader)
			throws XMLStreamException,
			FsmXmlException {

		Automata automata = new Automata();

		while (xmlStreamReader.hasNext()) {

			int eventType = xmlStreamReader.next();
			if (eventType == XMLStreamReader.START_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_VALUE_TYPE)) {
					parseValueTypeTag(xmlStreamReader, automata);
				} else if (localName.equals(TAG_AUTOMATON_STRUCT)) {
					parseAutomatonStructTag(xmlStreamReader, automata);
				}

			} // End if (eventType == XMLStreamReader.START_ELEMENT)
			else if (eventType == XMLStreamReader.END_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_AUTOMATON)) {
					break;
				}

			}  // End if (eventType == XMLStreamReader.END_ELEMENT)

		}  // End while (xmlStreamReader.hasNext())

		return automata;
	}

	private void parseValueTypeTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		while (xmlStreamReader.hasNext()) {

			int eventType = xmlStreamReader.next();
			if (eventType == XMLStreamReader.START_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_WRITING_DATA)) {

					if (automata.getWritingData() == null) {
						AutomataInterface.WritingData writingData = new AutomataInterface.WritingData();
						writingData.closePar = xmlStreamReader.getAttributeValue(null, ATR_CLOSE_PAR).charAt(0);
						writingData.openPar = xmlStreamReader.getAttributeValue(null, ATR_OPEN_PAR).charAt(0);
						writingData.plusSym = xmlStreamReader.getAttributeValue(null, ATR_PLUS_SYM).charAt(0);
						writingData.spacesSym = xmlStreamReader.getAttributeValue(null, ATR_SPACES_SYM).charAt(0);
						writingData.starSym = xmlStreamReader.getAttributeValue(null, ATR_STAR_SYM).charAt(0);
						writingData.timesSym = xmlStreamReader.getAttributeValue(null, ATR_TIMES_SYM).charAt(0);
						writingData.weightClosing = xmlStreamReader.getAttributeValue(null, ATR_WEIGHT_CLOSING).charAt(0);
						writingData.weightOpening = xmlStreamReader.getAttributeValue(null, ATR_WEIGHT_OPENING).charAt(0);
						writingData.zeroSym = xmlStreamReader.getAttributeValue(null, ATR_ZERO_SYM).charAt(0);
						automata.setWritingData(writingData);
					}

				} else if (localName.equals(TAG_SEMIRING)) {

					AutomataInterface.Weight weight = new AutomataInterface.Weight();
					String set = xmlStreamReader.getAttributeValue(null, ATR_SET);
					String operations = xmlStreamReader.getAttributeValue(null, ATR_OPERATIONS);

					if (set.equals(VAL_B)) {

						if (operations.equals(VAL_CLASSICAL)) {
							weight.semiring = TAFKitInterface.AutomataType.Semiring.B_BOOLEAN;
						} else if (operations.equals(VAL_FIELD)) {
							weight.semiring = TAFKitInterface.AutomataType.Semiring.F2_TWO_ELEMENT_FIELD;
						} else {
							throw new FsmXmlException("Unrecognizable semiring operation.");
						}

					} else if (set.equals(VAL_Z)) {

						if (operations.equals(VAL_CLASSICAL)) {
							weight.semiring = TAFKitInterface.AutomataType.Semiring.Z_INTEGER;
						} else if (operations.equals(VAL_MIN_PLUS)) {
							weight.semiring = TAFKitInterface.AutomataType.Semiring.ZMIN_MIN_TROPICAL;
						} else if (operations.equals(VAL_MAX_PLUS)) {
							weight.semiring = TAFKitInterface.AutomataType.Semiring.ZMAX_MAX_TROPICAL;
						} else {
							throw new FsmXmlException("Unrecognizable semiring operation.");
						}

					} else if (set.equals(VAL_Q)) {
						weight.semiring = TAFKitInterface.AutomataType.Semiring.Q_RATIONAL;
					} else if (set.equals(VAL_R)) {
						weight.semiring = TAFKitInterface.AutomataType.Semiring.R_REAL;
					} else {
						throw new FsmXmlException("Unrecognizable semiring set.");
					}

					automata.setWeight(weight);

				} else if (localName.equals(TAG_MONOID)) {
					parseMonoidTag(xmlStreamReader, automata);
				}

			} // End if (eventType == XMLStreamReader.START_ELEMENT)
			else if (eventType == XMLStreamReader.END_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_VALUE_TYPE)) {
					break;
				}

			}  // End if (eventType == XMLStreamReader.END_ELEMENT)

		}  // End while (xmlStreamReader.hasNext())

	}  // End private void parseValueTypeTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseMonoidTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		AutomataInterface.Alphabet alphabet = null;
		String type = xmlStreamReader.getAttributeValue(null, ATR_TYPE);

		if (type.equals(VAL_UNIT)) {

			throw new FsmXmlException("VGI does not currently support the monoid type \"unit\".");

		} else if (type.equals(VAL_FREE)) {

			alphabet = new AutomataInterface.Alphabet();
			String genKind = xmlStreamReader.getAttributeValue(null, ATR_GEN_KIND);

			if (genKind.equals(VAL_SIMPLE)) {

				String genSort = xmlStreamReader.getAttributeValue(null, ATR_GEN_SORT);
				if ((genSort.equals(VAL_LETTERS))
						|| (genSort.equals(VAL_DIGITS))
						|| (genSort.equals(VAL_ALPHANUMS))) {
					alphabet.dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR;
				} else if (genSort.equals(VAL_INTEGERS)) {
					alphabet.dataType = TAFKitInterface.AutomataType.AlphabetDataType.INT;
				} else {
					throw new FsmXmlException("Unrecognizable value of the \"" + ATR_GEN_SORT + "\" attribute of the monoid tag.");
				}

			} // End if (genSort != null)
			else if (!(genKind.equals(VAL_TUPLE))) {
				throw new FsmXmlException("Expected the \"" + ATR_GEN_KIND + "\" attribute to be \"" + VAL_TUPLE + "\" but found \"" + genKind + "\".");
			}

		} else if (type.equals(VAL_PRODUCT)) {
			// This is a transducer.
		}

		if (alphabet != null) {
			if (automata.getAlphabet() == null) {
				automata.setAlphabet(alphabet);
			} else if (automata.getOutputAlphabet() == null) {
				automata.setOutputAlphabet(alphabet);
			}
		}  // End if (alphabet != null)

		while (xmlStreamReader.hasNext()) {

			int eventType = xmlStreamReader.next();
			if (eventType == XMLStreamReader.START_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if ((localName.equals(TAG_WRITING_DATA)) && (alphabet != null)) {
					alphabet.identitySymbol = xmlStreamReader.getAttributeValue(null, ATR_IDENTITY_SYM);
					alphabet.timesSymbol = xmlStreamReader.getAttributeValue(null, ATR_TIMES_SYM);
				} else if ((localName.equals(TAG_MON_GEN)) && (alphabet != null)) {
					alphabet.allSymbols.add(parseMonGenTag(xmlStreamReader, automata));
				} else if (localName.equals(TAG_MONOID)) {
					parseMonoidTag(xmlStreamReader, automata);
				} else if (localName.equals(TAG_GEN_SORT)) {
					alphabet.dataType = parseGenSortTag(xmlStreamReader, automata);
				}

			} // End if (eventType == XMLStreamReader.START_ELEMENT)
			else if (eventType == XMLStreamReader.END_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_MONOID)) {
					break;
				}

			}  // End if (eventType == XMLStreamReader.END_ELEMENT)

		}  // End while (xmlStreamReader.hasNext())

	}  // End private void parseMonoidTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Object parseMonGenTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		String value = xmlStreamReader.getAttributeValue(null, ATR_VALUE);
		if (value != null) {
			return value;
		}

		AutomataInterface.SymbolPair<Object> pair = new AutomataInterface.SymbolPair<Object>();

		while (xmlStreamReader.hasNext()) {

			int eventType = xmlStreamReader.next();
			if (eventType == XMLStreamReader.START_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_MON_COMP_GEN)) {

					value = xmlStreamReader.getAttributeValue(null, ATR_VALUE);
					Object symbol;
					switch (automata.getAlphabet().dataType) {
						case CHAR_CHAR:
							symbol = new Character(value.charAt(0));
							break;
						case CHAR_INT:
							if (pair.isEmpty()) {
								symbol = new Character(value.charAt(0));
							} else {
								symbol = new Integer(value);
							}
							break;
						case INT_CHAR:
							if (pair.isEmpty()) {
								symbol = new Integer(value);
							} else {
								symbol = new Character(value.charAt(0));
							}
							break;
						case INT_INT:
							symbol = new Integer(value);
							break;
						default:
							throw new FsmXmlException("Unexpected value of the alphabet data type.");
					}  // End switch (automata.getAlphabet().dataType)
					pair.add(symbol);

				}  // End if (localName.equals(TAG_MON_COMP_GEN))

			} // End if (eventType == XMLStreamReader.START_ELEMENT)
			else if (eventType == XMLStreamReader.END_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_MON_GEN)) {
					break;
				}

			}  // End if (eventType == XMLStreamReader.END_ELEMENT)

		}  // End while (xmlStreamReader.hasNext())

		if (pair.isEmpty()) {
			pair = null;
			throw new FsmXmlException("Parsing \"" + TAG_MON_GEN + "\" tag yields no result.");
		}
		return pair;
	}  // End private Object parseMonGenTag(XMLStreamReader xmlStreamReader, Automata automata)

	private TAFKitInterface.AutomataType.AlphabetDataType parseGenSortTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		TAFKitInterface.AutomataType.AlphabetDataType dataType = null;

		while (xmlStreamReader.hasNext()) {

			int eventType = xmlStreamReader.next();
			if (eventType == XMLStreamReader.START_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_GEN_COMP_SORT)) {

					String genCompSort = xmlStreamReader.getAttributeValue(null, ATR_VALUE);
					if ((genCompSort.equals(VAL_LETTERS))
							|| (genCompSort.equals(VAL_DIGITS))
							|| (genCompSort.equals(VAL_ALPHANUMS))) {

						if (dataType == null) {
							dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR;
						} else if (dataType == TAFKitInterface.AutomataType.AlphabetDataType.CHAR) {
							dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR_CHAR;
						} else if (dataType == TAFKitInterface.AutomataType.AlphabetDataType.INT) {
							dataType = TAFKitInterface.AutomataType.AlphabetDataType.INT_CHAR;
						}

					} else if (genCompSort.equals(VAL_INTEGERS)) {

						if (dataType == null) {
							dataType = TAFKitInterface.AutomataType.AlphabetDataType.INT;
						} else if (dataType == TAFKitInterface.AutomataType.AlphabetDataType.CHAR) {
							dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR_INT;
						} else if (dataType == TAFKitInterface.AutomataType.AlphabetDataType.INT) {
							dataType = TAFKitInterface.AutomataType.AlphabetDataType.INT_INT;
						}

					} else {
						throw new FsmXmlException("Unrecognizable value of the \"" + ATR_VALUE + "\" attribute of the \"" + TAG_GEN_COMP_SORT + "\" tag.");
					}

				}

			} // End if (eventType == XMLStreamReader.START_ELEMENT)
			else if (eventType == XMLStreamReader.END_ELEMENT) {

				String localName = xmlStreamReader.getLocalName();
				if (localName.equals(TAG_GEN_SORT)) {
					break;
				}

			}  // End if (eventType == XMLStreamReader.END_ELEMENT)

		}  // End while (xmlStreamReader.hasNext())

		if (dataType == null) {
			throw new FsmXmlException("Parsing \"" + TAG_GEN_SORT + "\" tag yields no result.");
		}
		return dataType;
	}  // End private TAFKitInterface.AutomataType.AlphabetDataType parseGenSortTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseAutomatonStructTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		if (!(this.findNextSpecifiedTag(xmlStreamReader, TAG_STATES, Tag.Type.START))) {
			assertTag(TAG_STATES, Tag.Type.START);
		}
		Map<String, State> statesMap = parseStatesTag(xmlStreamReader, automata);

		if (!(this.findNextSpecifiedTag(xmlStreamReader, TAG_TRANSITIONS, Tag.Type.START))) {
			assertTag(TAG_TRANSITIONS, Tag.Type.START);
		}
		parseTransitionsTag(xmlStreamReader, automata, statesMap);

		if (!(this.findNextSpecifiedTag(xmlStreamReader, TAG_AUTOMATON_STRUCT, Tag.Type.END))) {
			assertTag(TAG_AUTOMATON_STRUCT, Tag.Type.END);
		}

	}  // End private void parseAutomatonStructTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Map<String, State> parseStatesTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Map<String, State> statesMap = new HashMap<String, State>();

		Tag tag = this.nextStartOrEndTag(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_STATES, Tag.Type.END)))) {

			if (tag.equals(TAG_STATE, Tag.Type.START)) {
				String id = xmlStreamReader.getAttributeValue(null, ATR_ID);
				if (id == null) {
					throw new FsmXmlException("Missing required \"" + ATR_ID + "\" attribute of a \"" + tag.localName + "\" tag.");
				}
				State state = new State();
				state.setName(xmlStreamReader.getAttributeValue(null, ATR_NAME));
				automata.addState(state);
				statesMap.put(id, state);
			} // End if (tag.equals(TAG_STATE, Tag.Type.START))
			else if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START)) {
				parseStateGeometricData(xmlStreamReader, automata);
			}  // End if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START))

			tag = this.nextStartOrEndTag(xmlStreamReader);

		}  // End while ((tag != null) && (!(tag.equals(TAG_STATES, Tag.Type.END))))

		if (statesMap.isEmpty()) {
			statesMap = null;
			throw new FsmXmlException("Parsing \"" + TAG_STATES + "\" tag yields no result.");
		}
		return statesMap;

	}  // End private Map<String, State> parseStatesTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseStateGeometricData(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Tag tag;

		do {  //  while ((tag != null) && (!(tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.END))))

			if ((xmlStreamReader.isStartElement()) && (xmlStreamReader.getLocalName().equals(TAG_GEOMETRIC_DATA))) {
				Double x = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_X));
				Double y = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_Y));
				List<State> allStates = automata.getAllStates();
				State state = allStates.get(allStates.size() - 1);
				state.getGeometricData().location = new Point2D.Double(x, y);
			}

			tag = this.nextStartOrEndTag(xmlStreamReader);

		} while ((tag != null) && (!(tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.END))));

	}  // End private void parseStateGeometricData(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseTransitionsTag(XMLStreamReader xmlStreamReader, Automata automata, Map<String, State> statesMap)
			throws XMLStreamException,
			FsmXmlException {

		Tag tag = this.nextStartOrEndTag(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_TRANSITIONS, Tag.Type.END)))) {

			if (tag.equals(TAG_TRANSITION, Tag.Type.START)) {

				String sourceId = xmlStreamReader.getAttributeValue(null, ATR_SOURCE);
				if (sourceId == null) {
					throw new FsmXmlException("Missing required \"" + ATR_SOURCE + "\" attribute of a \"" + tag.localName + "\" tag.");
				}
				String targetId = xmlStreamReader.getAttributeValue(null, ATR_TARGET);
				if (targetId == null) {
					throw new FsmXmlException("Missing required \"" + ATR_TARGET + "\" attribute of a \"" + tag.localName + "\" tag.");
				}
				State sourceState = statesMap.get(sourceId);
				if (sourceState == null) {
					throw new FsmXmlException("Missing state with id \"" + sourceId + "\", which is referenced by a transition.");
				}
				State targetState = statesMap.get(targetId);
				if (targetState == null) {
					throw new FsmXmlException("Missing state with id \"" + targetId + "\", which is referenced by a transition.");
				}
				if (!(this.findNextSpecifiedTag(xmlStreamReader, TAG_LABEL, Tag.Type.START))) {
					this.assertTag(TAG_LABEL, Tag.Type.START);
				}
				WeightedRegularExpression label = parseLabelTag(xmlStreamReader, automata);
				Transition transition = new Transition();
				transition.setSourceState(sourceState);
				transition.setTargetState(targetState);
				transition.setLabel(label);
				automata.addTransition(transition);

			} // End if (tag.equals(TAG_TRANSITION, Tag.Type.START))
			else if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START)) {

				parseTransitionGeometricData(xmlStreamReader, automata);

			} // End if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START))
			else if (tag.equals(TAG_INITIAL, Tag.Type.START)) {

				String id = xmlStreamReader.getAttributeValue(null, ATR_STATE);
				if (id == null) {
					throw new FsmXmlException("Missing required \"" + ATR_STATE + "\" attribute of a \"" + tag.localName + "\" tag.");
				}
				State state = statesMap.get(id);
				if (state == null) {
					throw new FsmXmlException("Missing state with id \"" + id + "\", which is referenced by a \"" + tag.localName + "\" tag.");
				}
				state.setInitialWeight(true);

			} // End if (tag.equals(TAG_INITIAL, Tag.Type.START))
			else if (tag.equals(TAG_FINAL, Tag.Type.START)) {

				String id = xmlStreamReader.getAttributeValue(null, ATR_STATE);
				if (id == null) {
					throw new FsmXmlException("Missing required \"" + ATR_STATE + "\" attribute of a \"" + tag.localName + "\" tag.");
				}
				State state = statesMap.get(id);
				if (state == null) {
					throw new FsmXmlException("Missing state with id \"" + id + "\", which is referenced by a \"" + tag.localName + "\" tag.");
				}
				state.setFinalWeight(true);

			}  // End if (tag.equals(TAG_FINAL, Tag.Type.START))

			tag = this.nextStartOrEndTag(xmlStreamReader);

		}  // End while ((tag != null) && (!(tag.equals(TAG_TRANSITIONS, Tag.Type.END))))

	}  // End private void parseTransitionsTag(XMLStreamReader xmlStreamReader, Automata automata, Map<String, State> statesMap)

	private WeightedRegularExpression parseLabelTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		WeightedRegularExpression label = null;
		WeightedRegularExpression firstChild = null;
		WeightedRegularExpression secondChild = null;
		Object weight = null;

		if (!(xmlStreamReader.isStartElement())) {
			throw new FsmXmlException("The first input of parseLabelTag() is not a start tag!");
		}

		String tagName = xmlStreamReader.getLocalName();

		while (xmlStreamReader.hasNext()) {
			xmlStreamReader.next();

			if (xmlStreamReader.isStartElement()) {
				WeightedRegularExpression expression = null;
				String localName = xmlStreamReader.getLocalName();

				if (TAG_ZERO.equals(localName)) {
					expression = new WeightedRegularExpression.Zero();
				} else if (TAG_ONE.equals(localName)) {
					expression = new WeightedRegularExpression.One();
				} else if (TAG_MON_ELMT.equals(localName)) {
					expression = new WeightedRegularExpression.Atomic(parseMonElmtTag(xmlStreamReader, automata));
				} else if (TAG_SUM.equals(localName)) {
					expression = parseLabelTag(xmlStreamReader, automata);
				} else if (TAG_PRODUCT.equals(localName)) {
					expression = parseLabelTag(xmlStreamReader, automata);
				} else if (TAG_STAR.equals(localName)) {
					expression = parseLabelTag(xmlStreamReader, automata);
				} else if (TAG_LEFT_EXT_MUL.equals(localName)) {
					expression = parseLabelTag(xmlStreamReader, automata);
				} else if (TAG_RIGHT_EXT_MUL.equals(localName)) {
					expression = parseLabelTag(xmlStreamReader, automata);
				} else if (TAG_WEIGHT.equals(localName)) {
					weight = parseWeightTag(xmlStreamReader, automata);
				}

				if (expression != null) {
					if (firstChild == null) {
						firstChild = expression;
					} else if (secondChild == null) {
						secondChild = expression;
					}
				}

			} // End if (xmlStreamReader.isStartElement())
			else if (xmlStreamReader.isEndElement()) {
				String localName = xmlStreamReader.getLocalName();

				if (TAG_ZERO.equals(localName)) {
					label = firstChild;
				} else if (TAG_ONE.equals(localName)) {
					label = firstChild;
				} else if (TAG_MON_ELMT.equals(localName)) {
					label = firstChild;
				} else if (TAG_SUM.equals(localName)) {
					if ((firstChild != null) && (secondChild != null)) {
						label = new WeightedRegularExpression.Sum(firstChild, secondChild);
					}
				} else if (TAG_PRODUCT.equals(localName)) {
					if ((firstChild != null) && (secondChild != null)) {
						label = new WeightedRegularExpression.Product(firstChild, secondChild);
					}
				} else if (TAG_STAR.equals(localName)) {
					if (firstChild != null) {
						label = new WeightedRegularExpression.Star(firstChild);
					}
				} else if (TAG_LEFT_EXT_MUL.equals(localName)) {
					if ((weight != null) && (firstChild != null)) {
						label = new WeightedRegularExpression.LeftMultiply(weight, firstChild);
					}
				} else if (TAG_RIGHT_EXT_MUL.equals(localName)) {
					if ((firstChild != null) && (weight != null)) {
						label = new WeightedRegularExpression.RightMultiply(secondChild, weight);
					}
				} else if (TAG_LABEL.equals(localName)) {
					label = firstChild;
				}
				if (tagName.equals(localName)) {
					break;
				}
			}  // End if (xmlStreamReader.isEndElement())

		}  // End while (xmlStreamReader.hasNext())

		if (label == null) {
			throw new FsmXmlException("Parsing \"" + tagName + "\" tag yields no result.");
		}
		label.setAlphabet(automata.getAlphabet());
		label.setWeight(automata.getWeight());
		label.setWritingData(automata.getWritingData());
		return label;
	}  // End private WeightedRegularExpression parseLabelTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Object parseMonElmtTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Object returnObject = null;

		while (xmlStreamReader.hasNext()) {

			xmlStreamReader.next();
			if (xmlStreamReader.isEndElement() && TAG_MON_ELMT.equals(xmlStreamReader.getLocalName())) {
				break;
			}

			if (xmlStreamReader.isStartElement()) {
				String localName = xmlStreamReader.getLocalName();
				Object object = null;

				if (TAG_MON_GEN.equals(localName)) {
					object = parseMonGenTag(xmlStreamReader, automata);
				} else if (TAG_MON_ELMT.equals(localName)) {
					object = parseMonElmtTag(xmlStreamReader, automata);
				} else if (TAG_ZERO.equals(localName)) {
					object = new WeightedRegularExpression.Zero();
				} else if (TAG_ONE.equals(localName)) {
					object = new WeightedRegularExpression.One();
				}

				if (returnObject == null) {
					returnObject = object;
				} else if (List.class.isInstance(returnObject)) {
					((List) returnObject).add(object);
				} else {
					List<Object> list = new ArrayList<Object>();
					list.add(returnObject);
					list.add(object);
					returnObject = list;
				}
			}  // End if (xmlStreamReader.isStartElement())

		}  // End while (xmlStreamReader.hasNext())

		if (returnObject == null) {
			throw new FsmXmlException("Parsing \"" + TAG_MON_ELMT + "\" tag yields no result.");
		}
		return returnObject;
	}  // End private Object parseMonElmtTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Object parseWeightTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Object object = null;//xmlStreamReader.getAttributeValue(null, ATR_VALUE);
		switch (automata.getWeight().semiring) {
			case Z_INTEGER:
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				object = Integer.valueOf(xmlStreamReader.getAttributeValue(null, ATR_VALUE));
				break;
			case Q_RATIONAL:
			case R_REAL:
				object = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_VALUE));
				break;
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
				throw new FsmXmlException("The boolean semiring and the two-element field should not have any weight tags.");
			default:
				throw new FsmXmlException("Unrecognizable semiring set.");
		}  // End switch (automata.getWeight().semiring)

		while (xmlStreamReader.hasNext()) {

			xmlStreamReader.next();
			if (xmlStreamReader.isEndElement() && TAG_WEIGHT.equals(xmlStreamReader.getLocalName())) {
				break;
			}

		}  // End while (xmlStreamReader.hasNext())

		if (object == null) {
			throw new FsmXmlException("Parsing \"" + TAG_WEIGHT + "\" tag yields no result.");
		}
		return object;
	}  // End private Object parseWeightTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseTransitionGeometricData(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Tag tag;

		do {  //  while ((tag != null) && (!(tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.END))))

			if ((xmlStreamReader.isStartElement()) && (xmlStreamReader.getLocalName().equals(TAG_CONTROL_POINT))) {
				Double x = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_X));
				Double y = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_Y));
				List<Transition> allTransitions = automata.getAllTransitions();
				Transition transition = allTransitions.get(allTransitions.size() - 1);
				transition.getGeometricData().controlPoints.add(new Point2D.Double(x, y));
			}  // End if ((xmlStreamReader.isStartElement()) && (xmlStreamReader.getLocalName().equals(TAG_CONTROL_POINT)))

			tag = this.nextStartOrEndTag(xmlStreamReader);

		} while ((tag != null) && (!(tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.END))));

	}  // End private void parseTransitionGeometricData(XMLStreamReader xmlStreamReader, Automata automata)

	@Override
	public void write(List<Automata> automataList, File fsmXmlFile)
			throws
			IOException,
			FsmXmlException {
		if (!(fsmXmlFile.exists())) {
			fsmXmlFile.getParentFile().mkdirs();
			fsmXmlFile.createNewFile();
		}
		OutputStream outputStream = new FileOutputStream(fsmXmlFile);
		this.write(automataList, outputStream);
	}  // End public void write(List<Automata> automataList, File fsmXmlFile)

	@Override
	public void write(List<Automata> automataList, OutputStream outputStream)
			throws
			FsmXmlException {

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream byteArrayInputStream = null;

		try {
			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(byteArrayOutputStream);
			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement(TAG_FSMXML);
			xmlStreamWriter.writeAttribute("xmlns", VAL_FSMXML_NAMESPACE);
			xmlStreamWriter.writeAttribute("version", VAL_FSMXML_VERSION_NUMBER);

			Iterator<Automata> automataIterator = automataList.iterator();
			while (automataIterator.hasNext()) {
				Automata automata = automataIterator.next();
				xmlStreamWriter.writeStartElement(TAG_AUTOMATON);
				writeValueTypeTag(xmlStreamWriter, automata);
				xmlStreamWriter.writeStartElement(TAG_AUTOMATON_STRUCT);
				writeStatesTag(xmlStreamWriter, automata);
				writeTransitionsTag(xmlStreamWriter, automata);
				xmlStreamWriter.writeEndElement();  // TAG_AUTOMATON_STRUCT
				xmlStreamWriter.writeEndElement();  // TAG_AUTOMATON
			}  // End while (automataIterator.hasNext())

			xmlStreamWriter.writeEndElement();  // TAG_FSMXML
			xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.flush();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//			transformer.setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
//			No idea why {http://xml.apache.org/xalan}line-separator does not work
//			so the horrible line.separator work around is used in order to specify
//			Unix line endings in the resulting XML file.
			byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			Source source = new StreamSource(byteArrayInputStream);
			Result result = new StreamResult(outputStream);
			String originalLineSeparator = System.getProperty("line.separator");
			System.setProperty("line.separator", "\n");
			transformer.transform(source, result);
			System.setProperty("line.separator", originalLineSeparator);
		} catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
		} catch (TransformerConfigurationException transformerConfigurationException) {
			throw new FsmXmlException(transformerConfigurationException);
		} catch (TransformerException transformerException) {
			throw new FsmXmlException(transformerException);
		} finally {
			if (xmlStreamWriter != null) {
				try {
					xmlStreamWriter.close();
				} catch (XMLStreamException xmlStreamException) {
					throw new FsmXmlException(xmlStreamException);
				}
			}  // End if (xmlStreamReader != null)
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException iOException) {
					throw new FsmXmlException(iOException);
				}
			}  // End if (byteArrayInputStream != null)
			try {
				byteArrayOutputStream.close();
			} catch (IOException iOException) {
				throw new FsmXmlException(iOException);
			}
		}  // End finally

	}  // End public void write(List<Automata> automataList, OutputStream outputStream)

	private void writeValueTypeTag(XMLStreamWriter xmlStreamWriter, Automata automata)
			throws
			XMLStreamException,
			FsmXmlException {

		xmlStreamWriter.writeStartElement(TAG_VALUE_TYPE);

		AutomataInterface.WritingData writingData = automata.getWritingData();
		if (writingData != null) {
			xmlStreamWriter.writeStartElement(TAG_WRITING_DATA);
			xmlStreamWriter.writeAttribute(ATR_CLOSE_PAR, writingData.closePar.toString());
			xmlStreamWriter.writeAttribute(ATR_OPEN_PAR, writingData.openPar.toString());
			xmlStreamWriter.writeAttribute(ATR_PLUS_SYM, writingData.plusSym.toString());
			xmlStreamWriter.writeAttribute(ATR_SPACES_SYM, writingData.spacesSym.toString());
			xmlStreamWriter.writeAttribute(ATR_STAR_SYM, writingData.starSym.toString());
			xmlStreamWriter.writeAttribute(ATR_TIMES_SYM, writingData.timesSym.toString());
			xmlStreamWriter.writeAttribute(ATR_WEIGHT_CLOSING, writingData.weightClosing.toString());
			xmlStreamWriter.writeAttribute(ATR_WEIGHT_OPENING, writingData.weightOpening.toString());
			xmlStreamWriter.writeAttribute(ATR_ZERO_SYM, writingData.zeroSym.toString());
			xmlStreamWriter.writeEndElement();  // End TAG_WRITING_DATA
		}  // End if (writingData != null)

		AutomataInterface.Weight weight = automata.getWeight();
		xmlStreamWriter.writeStartElement(TAG_SEMIRING);
		switch (weight.semiring) {
			case B_BOOLEAN:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_CLASSICAL);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_B);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
			case Z_INTEGER:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_CLASSICAL);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_Z);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
			case Q_RATIONAL:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_CLASSICAL);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_Q);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
			case R_REAL:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_CLASSICAL);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_R);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
			case F2_TWO_ELEMENT_FIELD:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_FIELD);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_B);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
			case ZMIN_MIN_TROPICAL:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_MIN_PLUS);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_Z);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
			case ZMAX_MAX_TROPICAL:
				xmlStreamWriter.writeAttribute(ATR_OPERATIONS, VAL_MAX_PLUS);
				xmlStreamWriter.writeAttribute(ATR_SET, VAL_Z);
				xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_NUMERICAL);
				break;
		}  // End switch (weight.semiring)
		xmlStreamWriter.writeEndElement();  // End TAG_SEMIRING

		writeMonoidTag(xmlStreamWriter, automata);

		xmlStreamWriter.writeEndElement();  // End TAG_VALUE_TYPE

	}  // End private void writeValueTypeTag(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeMonoidTag(XMLStreamWriter xmlStreamWriter, Automata automata)
			throws
			XMLStreamException,
			FsmXmlException {

		AutomataInterface.Alphabet alphabet = automata.getAlphabet();
		AutomataInterface.Alphabet outputAlphabet = automata.getOutputAlphabet();

		if (outputAlphabet != null) {
			//
			// If this IS a transducer
			//
			xmlStreamWriter.writeStartElement(TAG_MONOID);
			xmlStreamWriter.writeAttribute(ATR_PROD_DIM, Integer.toString(2));
			xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_PRODUCT);
			xmlStreamWriter.writeStartElement(TAG_WRITING_DATA);
			xmlStreamWriter.writeAttribute(ATR_IDENTITY_SYM, "1");
			xmlStreamWriter.writeEndElement();  // End TAG_WRITING_DATA
			writeMonoidTag(xmlStreamWriter, alphabet);
			writeMonoidTag(xmlStreamWriter, outputAlphabet);
			xmlStreamWriter.writeEndElement();  // End TAG_MONOID
		} else {
			//
			// If this is NOT a transducer
			//
			writeMonoidTag(xmlStreamWriter, alphabet);
		}

	}  // End private void writeMonoidTag(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeMonoidTag(XMLStreamWriter xmlStreamWriter, AutomataInterface.Alphabet alphabet)
			throws
			XMLStreamException,
			FsmXmlException {

		xmlStreamWriter.writeStartElement(TAG_MONOID);

		xmlStreamWriter.writeAttribute(ATR_GEN_DESCRIP, VAL_ENUM);
		switch (alphabet.dataType) {
			case CHAR:
				xmlStreamWriter.writeAttribute(ATR_GEN_KIND, VAL_SIMPLE);
				xmlStreamWriter.writeAttribute(ATR_GEN_SORT, VAL_LETTERS);
				break;
			case INT:
				xmlStreamWriter.writeAttribute(ATR_GEN_KIND, VAL_SIMPLE);
				xmlStreamWriter.writeAttribute(ATR_GEN_SORT, VAL_INTEGERS);
				break;
			default:
				xmlStreamWriter.writeAttribute(ATR_GEN_DIM, Integer.toString(2));
				xmlStreamWriter.writeAttribute(ATR_GEN_KIND, VAL_TUPLE);
				break;
		}  // End switch (alphabet.dataType)
		xmlStreamWriter.writeAttribute(ATR_TYPE, VAL_FREE);

		if (alphabet.identitySymbol != null) {
			xmlStreamWriter.writeStartElement(TAG_WRITING_DATA);
			xmlStreamWriter.writeAttribute(ATR_IDENTITY_SYM, alphabet.identitySymbol.toString());
			if (alphabet.timesSymbol != null) {
				xmlStreamWriter.writeAttribute(ATR_TIMES_SYM, alphabet.timesSymbol.toString());
			}
			xmlStreamWriter.writeEndElement();  // End TAG_WRITING_DATA
		}  // End if (alphabet.identitySymbol != null)

		switch (alphabet.dataType) {
			case CHAR_CHAR:
				xmlStreamWriter.writeStartElement(TAG_GEN_SORT);
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_LETTERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_LETTERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_SORT
				break;
			case CHAR_INT:
				xmlStreamWriter.writeStartElement(TAG_GEN_SORT);
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_LETTERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_INTEGERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_SORT
				break;
			case INT_CHAR:
				xmlStreamWriter.writeStartElement(TAG_GEN_SORT);
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_INTEGERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_LETTERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_SORT
				break;
			case INT_INT:
				xmlStreamWriter.writeStartElement(TAG_GEN_SORT);
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_INTEGERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeStartElement(TAG_GEN_COMP_SORT);
				xmlStreamWriter.writeAttribute(ATR_VALUE, VAL_INTEGERS);
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_COMP_SORT
				xmlStreamWriter.writeEndElement();  // End TAG_GEN_SORT
				break;
		}  // End switch (alphabet.dataType)

		Iterator<Object> monGenIterator = alphabet.allSymbols.iterator();
		while (monGenIterator.hasNext()) {
			Object symbol = monGenIterator.next();
			writeMonGenTag(xmlStreamWriter, symbol);
		}  // End while (monGenIterator.hasNext())

		xmlStreamWriter.writeEndElement();  // End TAG_MONOID

	}  // End private void writeMonoidTag(XMLStreamWriter xmlStreamWriter, AutomataInterface.Alphabet alphabet)

	private void writeMonGenTag(XMLStreamWriter xmlStreamWriter, Object symbol)
			throws
			XMLStreamException,
			FsmXmlException {

		xmlStreamWriter.writeStartElement(TAG_MON_GEN);

		if (symbol instanceof String) {
			xmlStreamWriter.writeAttribute(ATR_VALUE, (String) symbol);
		} else if (symbol instanceof AutomataInterface.SymbolPair) {
			Iterator symbolIterator = ((AutomataInterface.SymbolPair) symbol).iterator();
			while (symbolIterator.hasNext()) {
				Object object = symbolIterator.next();
				xmlStreamWriter.writeStartElement(TAG_MON_COMP_GEN);
				xmlStreamWriter.writeAttribute(ATR_VALUE, object.toString());
				xmlStreamWriter.writeEndElement();  // End TAG_MON_COMP_GEN
			}  // End while (symbolIterator.hasNext())
		}

		xmlStreamWriter.writeEndElement();  // End TAG_MON_GEN

	}  // End private void writeMonGenTag(XMLStreamWriter xmlStreamWriter, Object symbol)

	private void writeStatesTag(XMLStreamWriter xmlStreamWriter, Automata automata)
			throws
			XMLStreamException,
			FsmXmlException {

		List<State> allStates = automata.getAllStates();
		Iterator<State> allStatesIterator = allStates.iterator();
		xmlStreamWriter.writeStartElement(TAG_STATES);

		while (allStatesIterator.hasNext()) {
			State state = allStatesIterator.next();
			xmlStreamWriter.writeStartElement(TAG_STATE);
			xmlStreamWriter.writeAttribute(ATR_ID, "s" + allStates.indexOf(state));
			String name = state.getName();
			if (name != null) {
				xmlStreamWriter.writeAttribute(ATR_NAME, name);
			}
			Point2D point2d = state.getGeometricData().location;
			if (point2d != null) {
				xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
				xmlStreamWriter.writeAttribute(ATR_X, String.valueOf(point2d.getX()));
				xmlStreamWriter.writeAttribute(ATR_Y, String.valueOf(point2d.getY()));
				xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
			}
			xmlStreamWriter.writeEndElement();  // End TAG_STATE
		}  // End while (allStatesIterator.hasNext())

		xmlStreamWriter.writeEndElement();  // End TAG_STATES

	}  // End private void writeStateTags(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeTransitionsTag(XMLStreamWriter xmlStreamWriter, Automata automata)
			throws
			XMLStreamException,
			FsmXmlException {

		List<Transition> allTransitions = automata.getAllTransitions();
		Iterator<Transition> allTransitionsIterator = allTransitions.iterator();
		List<State> allStates = automata.getAllStates();
		xmlStreamWriter.writeStartElement(TAG_TRANSITIONS);

		while (allTransitionsIterator.hasNext()) {
			Transition transition = allTransitionsIterator.next();
			xmlStreamWriter.writeStartElement(TAG_TRANSITION);
			State state = transition.getSourceState();
			xmlStreamWriter.writeAttribute(ATR_SOURCE, "s" + allStates.indexOf(state));
			state = transition.getTargetState();
			xmlStreamWriter.writeAttribute(ATR_TARGET, "s" + allStates.indexOf(state));
			WeightedRegularExpression label = transition.getLabel();
			if (label != null) {
				xmlStreamWriter.writeStartElement(TAG_LABEL);
				writeWeightedRegularExpression(xmlStreamWriter, label);
				xmlStreamWriter.writeEndElement();  // End TAG_LABEL
			}
			List<Point2D> controlPoints = transition.getGeometricData().controlPoints;
			if (!(controlPoints.isEmpty())) {
				xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
				Iterator<Point2D> iteratePoints = controlPoints.iterator();
				while (iteratePoints.hasNext()) {
					Point2D point2d = iteratePoints.next();
					xmlStreamWriter.writeStartElement(TAG_CONTROL_POINT);
					xmlStreamWriter.writeAttribute(ATR_X, String.valueOf(point2d.getX()));
					xmlStreamWriter.writeAttribute(ATR_Y, String.valueOf(point2d.getY()));
					xmlStreamWriter.writeEndElement();  // End TAG_CONTROL_POINT
				}  // End while (iteratePoints.hasNext())
				xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
			}  // End if (controlPoints != null)
			xmlStreamWriter.writeEndElement();  // End TAG_TRANSITION
		}  // End while (allTransitionsIterator.hasNext())

		Iterator<State> allStatesIterator = allStates.iterator();
		while (allStatesIterator.hasNext()) {
			State state = allStatesIterator.next();
			if (state.getInitialWeight() != null) {
				xmlStreamWriter.writeStartElement(TAG_INITIAL);
				xmlStreamWriter.writeAttribute(ATR_STATE, "s" + allStates.indexOf(state));
				xmlStreamWriter.writeEndElement();  // End TAG_INITIAL
			}
		}  // End while (allStatesIterator.hasNext())

		allStatesIterator = allStates.iterator();
		while (allStatesIterator.hasNext()) {
			State state = allStatesIterator.next();
			if (state.getFinalWeight() != null) {
				xmlStreamWriter.writeStartElement(TAG_FINAL);
				xmlStreamWriter.writeAttribute(ATR_STATE, "s" + allStates.indexOf(state));
				xmlStreamWriter.writeEndElement();  // End TAG_FINAL
			}
		}  // End while (allStatesIterator.hasNext())

		xmlStreamWriter.writeEndElement();  // End TAG_TRANSITIONS

	}  // End private void writeTransitionTags(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeWeightedRegularExpression(XMLStreamWriter xmlStreamWriter, WeightedRegularExpression expression)
			throws
			XMLStreamException,
			FsmXmlException {

		if (WeightedRegularExpression.Zero.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_ZERO);
			xmlStreamWriter.writeEndElement();  // End TAG_ZERO
		} else if (WeightedRegularExpression.One.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_ONE);
			xmlStreamWriter.writeEndElement();  // End TAG_ONE
		} else if (WeightedRegularExpression.Atomic.class.isInstance(expression)) {
			WeightedRegularExpression.Atomic atomic = (WeightedRegularExpression.Atomic) expression;
			writeMonElmtTag(xmlStreamWriter, atomic.getSymbol());
		} else if (WeightedRegularExpression.Sum.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_SUM);
			WeightedRegularExpression.Sum sum = (WeightedRegularExpression.Sum) expression;
			writeWeightedRegularExpression(xmlStreamWriter, sum.getLeftExpression());
			writeWeightedRegularExpression(xmlStreamWriter, sum.getRightExpression());
			xmlStreamWriter.writeEndElement();  // End TAG_SUM
		} else if (WeightedRegularExpression.Product.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_PRODUCT);
			WeightedRegularExpression.Product product = (WeightedRegularExpression.Product) expression;
			writeWeightedRegularExpression(xmlStreamWriter, product.getLeftExpression());
			writeWeightedRegularExpression(xmlStreamWriter, product.getRightExpression());
			xmlStreamWriter.writeEndElement();  // End TAG_PRODUCT
		} else if (WeightedRegularExpression.Star.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_STAR);
			WeightedRegularExpression.Star star = (WeightedRegularExpression.Star) expression;
			writeWeightedRegularExpression(xmlStreamWriter, star.getExpression());
			xmlStreamWriter.writeEndElement();  // End TAG_STAR
		} else if (WeightedRegularExpression.LeftMultiply.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_LEFT_EXT_MUL);
			WeightedRegularExpression.LeftMultiply leftMultiply = (WeightedRegularExpression.LeftMultiply) expression;
			writeWeightTag(xmlStreamWriter, leftMultiply.getWeightValue());
			writeWeightedRegularExpression(xmlStreamWriter, leftMultiply.getExpression());
			xmlStreamWriter.writeEndElement();  // End TAG_LEFT_EXT_MUL
		} else if (WeightedRegularExpression.RightMultiply.class.isInstance(expression)) {
			xmlStreamWriter.writeStartElement(TAG_RIGHT_EXT_MUL);
			WeightedRegularExpression.RightMultiply rightMultiply = (WeightedRegularExpression.RightMultiply) expression;
			writeWeightedRegularExpression(xmlStreamWriter, rightMultiply.getExpression());
			writeWeightTag(xmlStreamWriter, rightMultiply.getWeightValue());
			xmlStreamWriter.writeEndElement();  // End TAG_RIGHT_EXT_MUL
		}

	}  // End private void writeWeightedRegularExpression(XMLStreamWriter xmlStreamWriter, WeightedRegularExpression expression)

	private void writeMonElmtTag(XMLStreamWriter xmlStreamWriter, Object monElmt)
			throws
			XMLStreamException,
			FsmXmlException {

		if (WeightedRegularExpression.Zero.class.isInstance(monElmt)) {
			xmlStreamWriter.writeStartElement(TAG_ZERO);
			xmlStreamWriter.writeEndElement();  // End TAG_ZERO
			return;
		} else if (WeightedRegularExpression.One.class.isInstance(monElmt)) {
			xmlStreamWriter.writeStartElement(TAG_ONE);
			xmlStreamWriter.writeEndElement();  // End TAG_ONE
			return;
		}

		xmlStreamWriter.writeStartElement(TAG_MON_ELMT);

		if (List.class.isInstance(monElmt)) {
			if (AutomataInterface.SymbolPair.class.isInstance(monElmt)) {
				writeMonGenTag(xmlStreamWriter, monElmt);
			} else {
				Iterator iterator = ((List) monElmt).iterator();
				while (iterator.hasNext()) {
					writeMonElmtTag(xmlStreamWriter, iterator.next());
				}  // End while (iterator.hasNext())
			}
		} else {  // End if (List.class.isInstance(monElmt))
			writeMonGenTag(xmlStreamWriter, monElmt);
		}

		xmlStreamWriter.writeEndElement();  // End TAG_MON_ELMT

	}

	private void writeWeightTag(XMLStreamWriter xmlStreamWriter, Object weight)
			throws
			XMLStreamException,
			FsmXmlException {

		xmlStreamWriter.writeStartElement(TAG_WEIGHT);
		if (String.class.isInstance(weight)) {
			xmlStreamWriter.writeAttribute(ATR_VALUE, ((String) weight));
		} else if (Integer.class.isInstance(weight)) {
			xmlStreamWriter.writeAttribute(ATR_VALUE, ((Integer) weight).toString());
		} else if (Double.class.isInstance(weight)) {
			DecimalFormat decimalFormat = new DecimalFormat("");
			String output = decimalFormat.format((Double) weight);
			xmlStreamWriter.writeAttribute(ATR_VALUE, output);
		}
		xmlStreamWriter.writeEndElement();  // End TAG_WEIGHT

	}  // End private void writeWeightTag(XMLStreamWriter xmlStreamWriter, Object weight)

	private static void testFsmXmlFile(String filePathToRead, String filePathToWrite)
			throws
			IOException,
			FsmXmlException {
		FsmXml fsmXml = new FsmXml();
		File file = new File(filePathToRead);
		System.out.println("Reading " + file.getAbsolutePath());
		List<Automata> automataList = fsmXml.read(file);
		fsmXml.write(automataList, System.out);
		if (filePathToWrite != null) {
			file = new File(filePathToWrite);
			fsmXml.write(automataList, file);
		}
	}  // End private static void testFsmXmlFile(String filePathToRead, String filePathToWrite)

	public static void main(String args[]) {
		String testInputFolderPath = "./test input/";
		String testOutputFolderPath = "./test output/";
		try {
			testFsmXmlFile(testInputFolderPath + "char-b/a1.xml", testOutputFolderPath + "char-b/a1.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/a1g.xml", testOutputFolderPath + "char-b/a1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/b1.xml", testOutputFolderPath + "char-b/b1.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/b1g.xml", testOutputFolderPath + "char-b/b1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/div3base2.xml", testOutputFolderPath + "char-b/div3base2.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/div3base2g.xml", testOutputFolderPath + "char-b/div3base2g.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/double-3-1.xml", testOutputFolderPath + "char-b/double-3-1.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/double-3-1g.xml", testOutputFolderPath + "char-b/double-3-1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/evena.xml", testOutputFolderPath + "char-b/evena.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/evenag.xml", testOutputFolderPath + "char-b/evenag.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/ladybird-6.xml", testOutputFolderPath + "char-b/ladybird-6.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/ladybird-6g.xml", testOutputFolderPath + "char-b/ladybird-6g.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/oddb.xml", testOutputFolderPath + "char-b/oddb.xml");
			testFsmXmlFile(testInputFolderPath + "char-b/oddbg.xml", testOutputFolderPath + "char-b/oddbg.xml");
			testFsmXmlFile(testInputFolderPath + "char-char-b/ex-pair1.xml", testOutputFolderPath + "char-char-b/ex-pair1.xml");
			testFsmXmlFile(testInputFolderPath + "char-char-b/ex-pair1g.xml", testOutputFolderPath + "char-char-b/ex-pair1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-f2/ring-7-0-2-3.xml", testOutputFolderPath + "char-f2/ring-7-0-2-3.xml");
			testFsmXmlFile(testInputFolderPath + "char-f2/ring-7-0-2-3g.xml", testOutputFolderPath + "char-f2/ring-7-0-2-3g.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/fibred_left.xml", testOutputFolderPath + "char-fmp-b/fibred_left.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/fibred_leftg.xml", testOutputFolderPath + "char-fmp-b/fibred_leftg.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/fibred_right.xml", testOutputFolderPath + "char-fmp-b/fibred_right.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/fibred_rightg.xml", testOutputFolderPath + "char-fmp-b/fibred_rightg.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/quot3base2.xml", testOutputFolderPath + "char-fmp-b/quot3base2.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/quot3base2g.xml", testOutputFolderPath + "char-fmp-b/quot3base2g.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/t1.xml", testOutputFolderPath + "char-fmp-b/t1.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/t1g.xml", testOutputFolderPath + "char-fmp-b/t1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/u1.xml", testOutputFolderPath + "char-fmp-b/u1.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-b/u1g.xml", testOutputFolderPath + "char-fmp-b/u1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-z/t1.xml", testOutputFolderPath + "char-fmp-z/t1.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-z/t1g.xml", testOutputFolderPath + "char-fmp-z/t1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-z/u1.xml", testOutputFolderPath + "char-fmp-z/u1.xml");
			testFsmXmlFile(testInputFolderPath + "char-fmp-z/u1g.xml", testOutputFolderPath + "char-fmp-z/u1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-q/b1.xml", testOutputFolderPath + "char-q/b1.xml");
			testFsmXmlFile(testInputFolderPath + "char-q/b1g.xml", testOutputFolderPath + "char-q/b1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-q/c1.xml", testOutputFolderPath + "char-q/c1.xml");
			testFsmXmlFile(testInputFolderPath + "char-q/c1g.xml", testOutputFolderPath + "char-q/c1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-q/d1.xml", testOutputFolderPath + "char-q/d1.xml");
			testFsmXmlFile(testInputFolderPath + "char-q/d1g.xml", testOutputFolderPath + "char-q/d1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-r/b1.xml", testOutputFolderPath + "char-r/b1.xml");
			testFsmXmlFile(testInputFolderPath + "char-r/b1g.xml", testOutputFolderPath + "char-r/b1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-r/c1.xml", testOutputFolderPath + "char-r/c1.xml");
			testFsmXmlFile(testInputFolderPath + "char-r/c1g.xml", testOutputFolderPath + "char-r/c1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-r/d1.xml", testOutputFolderPath + "char-r/d1.xml");
			testFsmXmlFile(testInputFolderPath + "char-r/d1g.xml", testOutputFolderPath + "char-r/d1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-z/b1.xml", testOutputFolderPath + "char-z/b1.xml");
			testFsmXmlFile(testInputFolderPath + "char-z/b1g.xml", testOutputFolderPath + "char-z/b1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-z/c1.xml", testOutputFolderPath + "char-z/c1.xml");
			testFsmXmlFile(testInputFolderPath + "char-z/c1g.xml", testOutputFolderPath + "char-z/c1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-z/d1.xml", testOutputFolderPath + "char-z/d1.xml");
			testFsmXmlFile(testInputFolderPath + "char-z/d1g.xml", testOutputFolderPath + "char-z/d1g.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmax/maxab.xml", testOutputFolderPath + "char-zmax/maxab.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmax/maxabg.xml", testOutputFolderPath + "char-zmax/maxabg.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmax/maxblocka.xml", testOutputFolderPath + "char-zmax/maxblocka.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmax/maxblockag.xml", testOutputFolderPath + "char-zmax/maxblockag.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmin/minab.xml", testOutputFolderPath + "char-zmin/minab.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmin/minabg.xml", testOutputFolderPath + "char-zmin/minabg.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmin/minblocka.xml", testOutputFolderPath + "char-zmin/minblocka.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmin/minblockag.xml", testOutputFolderPath + "char-zmin/minblockag.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmin/slowgrow.xml", testOutputFolderPath + "char-zmin/slowgrow.xml");
			testFsmXmlFile(testInputFolderPath + "char-zmin/slowgrowg.xml", testOutputFolderPath + "char-zmin/slowgrowg.xml");
			testFsmXmlFile(testInputFolderPath + "int-b/coins.xml", testOutputFolderPath + "int-b/coins.xml");
			testFsmXmlFile(testInputFolderPath + "int-b/coinsg.xml", testOutputFolderPath + "int-b/coinsg.xml");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}  // End public static void main(String args[])
}  // End public class FsmXml implements FsmXmlInterface

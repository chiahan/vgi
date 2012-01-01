
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Deque;
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

	private class Tag {

		public String localName;
		public Object object;

		public Tag() {
			this.localName = null;
			this.object = null;
		}

		public Tag(String localName) {
			this();
			this.localName = localName;
		}

		public Tag(String localName, Object object) {
			this(localName);
			this.object = object;
		}
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
	private static final String TAG_TRANSITIONS = "transitions";
	private static final String TAG_TRANSITION = "transition";
	private static final String ATR_SOURCE = "source";
	private static final String ATR_TARGET = "target";
	private static final String TAG_LABEL = "label";
	private static final String TAG_MON_ELMT = "monElmt";
	private static final String TAG_INITIAL = "initial";
	private static final String TAG_FINAL = "final";
	private static final String ATR_STATE = "state";
	private Deque<Tag> pmTagStack;
	private List<Automata> pmAutomataList;
	private Map<String, State> pmMapIdState;

	public FsmXml() {
		this.pmAutomataList = null;
		this.pmTagStack = null;
	}

	@Override
	public List<Automata> read(File fsmXmlFile)
			throws
			FileNotFoundException,
			FsmXmlException {
		InputStream inputStream = new FileInputStream(fsmXmlFile);
		return this.read(inputStream);
	}  // End public List<Automata> read(File fsmXmlFile)

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
		} catch (FsmXmlException fsmXmlException) {
			throw fsmXmlException;
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
					alphabet.timesSymbol =  xmlStreamReader.getAttributeValue(null, ATR_TIMES_SYM);
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

		ArrayList<Object> pair = new ArrayList<Object>();

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

		return dataType;
	}  // End private TAFKitInterface.AutomataType.AlphabetDataType parseGenSortTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseAutomatonStructTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {
	}  // End private void parseAutomatonStructTag(XMLStreamReader xmlStreamReader, Automata automata)

	@Override
	public void write(List<Automata> automataList, File fsmXmlFile)
			throws
			FileNotFoundException,
			FsmXmlException {
		OutputStream outputStream = new FileOutputStream(fsmXmlFile);
		this.write(automataList, outputStream);
	}  // End public void write(List<Automata> automataList, File fsmXmlFile)

	@Override
	public void write(List<Automata> automataList, OutputStream outputStream)
			throws
			FileNotFoundException,
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
				writeInitialFinalTags(xmlStreamWriter, automata);
				xmlStreamWriter.writeEndElement();  // TAG_AUTOMATON_STRUCT
				xmlStreamWriter.writeEndElement();  // TAG_AUTOMATON
			}  // End while (automataIterator.hasNext())

			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.flush();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			Source source = new StreamSource(byteArrayInputStream);
			Result result = new StreamResult(outputStream);
			transformer.transform(source, result);
		} catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
		} catch (TransformerConfigurationException transformerConfigurationException) {
			throw new FsmXmlException(transformerConfigurationException);
		} catch (TransformerException transformerException) {
			throw new FsmXmlException(transformerException);
//		} catch (FsmXmlException fsmXmlException) {
//			throw fsmXmlException;
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

		xmlStreamWriter.writeStartElement(TAG_WRITING_DATA);
		xmlStreamWriter.writeAttribute(ATR_IDENTITY_SYM, alphabet.identitySymbol.toString());
		if (alphabet.timesSymbol != null) {
			xmlStreamWriter.writeAttribute(ATR_TIMES_SYM, alphabet.timesSymbol.toString());
		}
		xmlStreamWriter.writeEndElement();  // End TAG_WRITING_DATA

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
		} else if (symbol instanceof List) {
			Iterator symbolIterator = ((List) symbol).iterator();
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

		xmlStreamWriter.writeStartElement(TAG_STATES);
		xmlStreamWriter.writeStartElement(TAG_STATE);
		xmlStreamWriter.writeEndElement();  // End TAG_STATE
		xmlStreamWriter.writeEndElement();  // End TAG_STATES

	}  // End private void writeStateTags(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeTransitionsTag(XMLStreamWriter xmlStreamWriter, Automata automata)
			throws
			XMLStreamException,
			FsmXmlException {

		xmlStreamWriter.writeStartElement(TAG_TRANSITIONS);
		xmlStreamWriter.writeStartElement(TAG_TRANSITION);
		xmlStreamWriter.writeEndElement();  // End TAG_TRANSITION
		xmlStreamWriter.writeEndElement();  // End TAG_TRANSITIONS

	}  // End private void writeTransitionTags(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeInitialFinalTags(XMLStreamWriter xmlStreamWriter, Automata automata)
			throws
			XMLStreamException,
			FsmXmlException {

		xmlStreamWriter.writeStartElement(TAG_INITIAL);
		xmlStreamWriter.writeEndElement();  // End TAG_INITIAL

	}  // End private void writeInitialFinalTags(XMLStreamWriter xmlStreamWriter, Automata automata)

	private static void testFsmXmlFile(String fileToRead, String fileToWrite)
			throws
			FileNotFoundException,
			FsmXmlException {
		FsmXml fsmXml = new FsmXml();
		File file = new File(fileToRead);
		System.out.println("Reading " + file.getAbsolutePath());
		List<Automata> automataList = fsmXml.read(file);
		file = new File(fileToWrite);
		fsmXml.write(automataList, System.out);
	}  // End private void testOneFile(String fileToRead, String fileToWrite)

	public static void main(String args[]) {
		String automataRepositoryPath = "../../vaucanson-1.4a/data/automata/";
		try {
			testFsmXmlFile(automataRepositoryPath + "char-b/a1.xml", automataRepositoryPath + "char-b/a1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-b/b1.xml", automataRepositoryPath + "char-b/b1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-b/div3base2.xml", automataRepositoryPath + "char-b/div3base2w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-b/double-3-1.xml", automataRepositoryPath + "char-b/double-3-1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-b/evena.xml", automataRepositoryPath + "char-b/evenaw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-b/ladybird-6.xml", automataRepositoryPath + "char-b/ladybird-6w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-b/oddb.xml", automataRepositoryPath + "char-b/oddbw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-char-b/ex-pair1.xml", automataRepositoryPath + "char-char-b/ex-pair1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-f2/ring-7-0-2-3.xml", automataRepositoryPath + "char-f2/ring-7-0-2-3w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-b/fibred_left.xml", automataRepositoryPath + "char-fmp-b/fibred_leftw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-b/fibred_right.xml", automataRepositoryPath + "char-fmp-b/fibred_rightw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-b/quot3base2.xml", automataRepositoryPath + "char-fmp-b/quot3base2w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-b/t1.xml", automataRepositoryPath + "char-fmp-b/t1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-b/u1.xml", automataRepositoryPath + "char-fmp-b/u1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-z/t1.xml", automataRepositoryPath + "char-fmp-z/t1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-fmp-z/u1.xml", automataRepositoryPath + "char-fmp-z/u1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-q/b1.xml", automataRepositoryPath + "char-q/b1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-q/c1.xml", automataRepositoryPath + "char-q/c1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-q/d1.xml", automataRepositoryPath + "char-q/d1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-r/b1.xml", automataRepositoryPath + "char-r/b1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-r/c1.xml", automataRepositoryPath + "char-r/c1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-r/d1.xml", automataRepositoryPath + "char-r/d1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-z/b1.xml", automataRepositoryPath + "char-z/b1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-z/c1.xml", automataRepositoryPath + "char-z/c1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-z/d1.xml", automataRepositoryPath + "char-z/d1w.xml");
			testFsmXmlFile(automataRepositoryPath + "char-zmax/maxab.xml", automataRepositoryPath + "char-zmax/maxabw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-zmax/maxblocka.xml", automataRepositoryPath + "char-zmax/maxblockaw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-zmin/minab.xml", automataRepositoryPath + "char-zmin/minabw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-zmin/minblocka.xml", automataRepositoryPath + "char-zmin/minblockaw.xml");
			testFsmXmlFile(automataRepositoryPath + "char-zmin/slowgrow.xml", automataRepositoryPath + "char-zmin/slowgroww.xml");
			testFsmXmlFile(automataRepositoryPath + "int-b/coins.xml", automataRepositoryPath + "int-b/coinsw.xml");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}  // End public static void main(String args[])
}  // End public class FsmXml implements FsmXmlInterface

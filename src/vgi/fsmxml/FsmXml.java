package vgi.fsmxml;

import vgi.automata.AutomataInterface;
import vgi.automata.InitialFinalWeight;
import vgi.automata.Transition;
import vgi.automata.TransitionInterface;
import vgi.automata.StateInterface;
import vgi.automata.State;
import vgi.automata.Automata;
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
import java.text.NumberFormat;
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
import vgi.automata.*;

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

		public static Tag nextStartOrEnd(XMLStreamReader xmlStreamReader)
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

		public static boolean findNextSpecified(XMLStreamReader xmlStreamReader, String localName, Tag.Type type)
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

		public static boolean findNextSpecified(XMLStreamReader xmlStreamReader, Tag tag)
				throws
				XMLStreamException {
			if ((tag == null) || (tag.localName == null) || (tag.type == null)) {
				throw new IllegalArgumentException();
			}
			return findNextSpecified(xmlStreamReader, tag.localName, tag.type);
		}  // End private boolean findNextSpecifiedTag(Tag tag)

		public static void assertTag(String localName, Tag.Type type) throws FsmXmlException {

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

		public static void assertTag(Tag tagExpected, Tag tagFound) throws FsmXmlException {

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
	private static final String ATR_WIDTH = "width";
	private static final String VAL_DEFAULT_WIDTH = "50.0";
	private static final String ATR_HEIGHT = "height";
	private static final String VAL_DEFAULT_HEIGHT = "50.0";
	private static final String ATR_SHAPE="shape";
	
        //\/\/\// for IniFinGeometricData
        private static final String ATR_DIRECTION="direction";
        private static final String ATR_LENGTHRATIO="lengthratio";
        //\/\/\//
        
	private static final String ATR_LABEL_POS = "labelPos";
	private static final String ATR_LABEL_DIST = "labelDist";
	private static final String ATR_LABEL_OFFSET_X = "labelOffsetX";
	private static final String ATR_LABEL_OFFSET_Y = "labelOffsetY";
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
        
        
        private static final String TAG_DRAWING_DATA="drawingData";
        private static final String ATR_FILL_COLOR="fillColor";
        private static final String ATR_STROKE_COLOR="strokeColor";
        private static final String ATR_STROKE_WIDTH="strokeWidth";
        private static final String ATR_START_ARROW="startArrow";
        private static final String ATR_END_ARROW="endArrow";
        

	@Override
	public List<Automata> read(File fsmXmlFile)
			throws
			FileNotFoundException,
			IOException,
			FsmXmlException {
		InputStream inputStream = new FileInputStream(fsmXmlFile);
		List<Automata> automataList = this.read(inputStream);
		return automataList;
	}  // End public List<Automata> read(File fsmXmlFile)

	@Override
	public List<Automata> read(InputStream inputStream)
			throws
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
			//
			// Check that <fsmxml xmlns="http://vaucanson.lrde.epita.fr" version="1.0"> is at the root level.
			//
			Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
                        System.out.println(xmlStreamReader.getVersion());
			if ((tag == null) || (!(tag.equals(TAG_FSMXML, Tag.Type.START)))) {
				throw new FsmXmlException("Root tag is not " + TAG_FSMXML + " so this is likely an invalid FSM XML file.");
			} else if (!(xmlStreamReader.getNamespaceURI().equals(VAL_FSMXML_NAMESPACE))) {
				throw new FsmXmlException("Namespace is not " + VAL_FSMXML_NAMESPACE + " so this is likely an invalid FSM XML file.");
			} 
                        //else if (!(xmlStreamReader.getVersion().equals(VAL_FSMXML_VERSION_NUMBER))) {
			//	throw new FsmXmlException("VGI only supports FSM XML files whose version is " + VAL_FSMXML_VERSION_NUMBER + ".");
			//}
			automataList = new ArrayList<Automata>();
                        System.out.println(automataList);
			tag = Tag.nextStartOrEnd(xmlStreamReader);
			while ((tag != null) && (!(tag.equals(TAG_FSMXML, Tag.Type.END)))) {

				if (tag.equals(TAG_AUTOMATON, Tag.Type.START)) {
					automataList.add(parseAutomatonTag(xmlStreamReader));
				}

				tag = Tag.nextStartOrEnd(xmlStreamReader);
			}  // End while ((tag != null) && (!(tag.equals(TAG_FSMXML, Tag.Type.END))))

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

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_AUTOMATON, Tag.Type.END)))) {

			if (tag.equals(TAG_VALUE_TYPE, Tag.Type.START)) {
				parseValueTypeTag(xmlStreamReader, automata);
			} else if (tag.equals(TAG_AUTOMATON_STRUCT, Tag.Type.START)) {
				parseAutomatonStructTag(xmlStreamReader, automata);
			}

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_AUTOMATON, Tag.Type.END))))

		if (automata == null) {
			throw new FsmXmlException("Parsing \"" + TAG_AUTOMATON + "\" tag yields no result.");
		}
		return automata;
	}

	private void parseValueTypeTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_VALUE_TYPE, Tag.Type.END)))) {

			if (tag.equals(TAG_WRITING_DATA, Tag.Type.START)) {

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

			} else if (tag.equals(TAG_SEMIRING, Tag.Type.START)) {

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

			} else if (tag.equals(TAG_MONOID, Tag.Type.START)) {
				parseMonoidTag(xmlStreamReader, automata);
			}

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_VALUE_TYPE, Tag.Type.END))))

		if (automata.getAlphabet() == null) {
			throw new FsmXmlException("Missing alphabet definition (i.e. <" + TAG_MONOID + "> tag) in the FSM XML file.");
		}
		if (automata.getWeight() == null) {
			throw new FsmXmlException("Missing weight definition (i.e. <" + TAG_SEMIRING + "> tag) in the FSM XML file.");
		}
		if (automata.getWritingData() == null) {
			throw new FsmXmlException("Missing writing data definition (i.e. <" + TAG_WRITING_DATA + "> tag) in the FSM XML file.");
		}

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

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_MONOID, Tag.Type.END)))) {

			if ((tag.equals(TAG_WRITING_DATA, Tag.Type.START)) && (alphabet != null)) {
				alphabet.identitySymbol = xmlStreamReader.getAttributeValue(null, ATR_IDENTITY_SYM);
				alphabet.timesSymbol = xmlStreamReader.getAttributeValue(null, ATR_TIMES_SYM);
			} else if ((tag.equals(TAG_MON_GEN, Tag.Type.START)) && (alphabet != null)) {
				alphabet.allSymbols.add(parseMonGenTag(xmlStreamReader, automata));
			} else if (tag.equals(TAG_MONOID, Tag.Type.START)) {
				parseMonoidTag(xmlStreamReader, automata);
			} else if (tag.equals(TAG_GEN_SORT, Tag.Type.START)) {
				alphabet.dataType = parseGenSortTag(xmlStreamReader, automata);
			}

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_MONOID, Tag.Type.END))))

	}  // End private void parseMonoidTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Object parseMonGenTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {
                
		String value = xmlStreamReader.getAttributeValue(null, ATR_VALUE);
		if (value != null) {
                	return value;
		}

		AutomataInterface.SymbolPair<Object> pair = new AutomataInterface.SymbolPair<Object>();

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_MON_GEN, Tag.Type.END)))) {

			if (tag.equals(TAG_MON_COMP_GEN, Tag.Type.START)) {
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
			}  // End if (tag.equals(TAG_MON_COMP_GEN, Tag.Type.START))

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_MON_GEN, Tag.Type.END))))

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

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_GEN_SORT, Tag.Type.END)))) {

			if (tag.equals(TAG_GEN_COMP_SORT, Tag.Type.START)) {
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
			}  // End if (tag.equals(TAG_GEN_COMP_SORT, Tag.Type.START))

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_GEN_SORT, Tag.Type.END))))

		if (dataType == null) {
			throw new FsmXmlException("Parsing \"" + TAG_GEN_SORT + "\" tag yields no result.");
		}
		return dataType;
	}  // End private TAFKitInterface.AutomataType.AlphabetDataType parseGenSortTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseAutomatonStructTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		if (!(Tag.findNextSpecified(xmlStreamReader, TAG_STATES, Tag.Type.START))) {
			Tag.assertTag(TAG_STATES, Tag.Type.START);
		}
		Map<String, State> statesMap = parseStatesTag(xmlStreamReader, automata);

		if (!(Tag.findNextSpecified(xmlStreamReader, TAG_TRANSITIONS, Tag.Type.START))) {
			Tag.assertTag(TAG_TRANSITIONS, Tag.Type.START);
		}
		parseTransitionsTag(xmlStreamReader, automata, statesMap);

		if (!(Tag.findNextSpecified(xmlStreamReader, TAG_AUTOMATON_STRUCT, Tag.Type.END))) {
			Tag.assertTag(TAG_AUTOMATON_STRUCT, Tag.Type.END);
		}

	}  // End private void parseAutomatonStructTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Map<String, State> parseStatesTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Map<String, State> statesMap = new HashMap<String, State>();

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
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
                        else if(tag.equals(TAG_DRAWING_DATA, Tag.Type.START)){
                            parseStateDrawingData(xmlStreamReader,automata);
                        }
			tag = Tag.nextStartOrEnd(xmlStreamReader);
                        
		}  // End while ((tag != null) && (!(tag.equals(TAG_STATES, Tag.Type.END))))

//		if (statesMap.isEmpty()) {
//			statesMap = null;
//			throw new FsmXmlException("Parsing \"" + TAG_STATES + "\" tag yields no result.");
//		}
		return statesMap;

	}  // End private Map<String, State> parseStatesTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseStateGeometricData(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Double x = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_X));
		Double y = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_Y));
		List<State> allStates = automata.getAllStates();
		State state = allStates.get(allStates.size() - 1);
		//state.getGeometricData().setLocation (new Point2D.Double(x, y));
                //System.out.println("parse state geodata: "+x+" , "+y);
		String string = xmlStreamReader.getAttributeValue(null, ATR_WIDTH);
		if (string == null) {
			string = VAL_DEFAULT_WIDTH;
		}
		Double w = Double.valueOf(string);
		string = xmlStreamReader.getAttributeValue(null, ATR_HEIGHT);
		if (string == null) {
			string = VAL_DEFAULT_HEIGHT;
		}
		Double h = Double.valueOf(string);
		//state.getGeometricData().setSize(new Point2D.Double(w, h));
		String shape_=xmlStreamReader.getAttributeValue(null,ATR_SHAPE);
		//state.setShape(shape_);

                StateGeometricData stategeodata=new StateGeometricData(x,y,w,h,shape_);
                automata.setStateGeometricData(state,stategeodata);
                
		if (!(Tag.findNextSpecified(xmlStreamReader, TAG_GEOMETRIC_DATA, Tag.Type.END))) {
			Tag.assertTag(TAG_GEOMETRIC_DATA, Tag.Type.END);
		}

	}  // End private void parseStateGeometricData(XMLStreamReader xmlStreamReader, Automata automata)

        private void parseStateDrawingData(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {
                
                String fillcolor=xmlStreamReader.getAttributeValue(null, ATR_FILL_COLOR);
            
                String strokecolor=xmlStreamReader.getAttributeValue(null, ATR_STROKE_COLOR);
                float strokewidth=Float.valueOf(xmlStreamReader.getAttributeValue(null, ATR_STROKE_WIDTH));
            
                
                List<State> allStates = automata.getAllStates();
		State state = allStates.get(allStates.size() - 1);
		
//                StateInterface.DrawingData drawingdata=new StateInterface.DrawingData();
//                
//                drawingdata.fillColor=fillcolor;
//                drawingdata.strokeColor=strokecolor;
//                drawingdata.strokeWidth=strokewidth;
//                state.setDrawingData(drawingdata);
//               
                StateDrawingData drawingdata=new StateDrawingData();
                
                drawingdata.setFillColor(fillcolor);
                drawingdata.setStrokeColor(strokecolor);
                drawingdata.setStrokeWidth(strokewidth);
                //state.setDrawingData(drawingdata);
                automata.setStateDrawingData(state, drawingdata);
                
                
                if (!(Tag.findNextSpecified(xmlStreamReader, TAG_DRAWING_DATA, Tag.Type.END))) {
			Tag.assertTag(TAG_DRAWING_DATA, Tag.Type.END);
		}

	}  // End private void parseStateGeometricData(XMLStreamReader xmlStreamReader, Automata automata)

        
	private void parseTransitionsTag(XMLStreamReader xmlStreamReader, Automata automata, Map<String, State> statesMap)
			throws XMLStreamException,
			FsmXmlException {

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
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
//				if (!(Tag.findNextSpecified(xmlStreamReader, TAG_LABEL, Tag.Type.START))) {
//					Tag.assertTag(TAG_LABEL, Tag.Type.START);
//				}
//				WeightedRegularExpression label = parseLabelTag(xmlStreamReader, automata);
				//Transition transition = new Transition();
				//transition.setSourceState(sourceState);
				//transition.setTargetState(targetState);
//				transition.setLabel(label);
				//automata.addTransition(transition);
                                automata.addTransition(sourceState, targetState);
                                
			} // End if (tag.equals(TAG_TRANSITION, Tag.Type.START))
			else if (tag.equals(TAG_LABEL, Tag.Type.START)) {

				WeightedRegularExpression label = parseLabelTag(xmlStreamReader, automata);
				List<Transition> allTransitions = automata.getAllTransitions();
				Transition transition = allTransitions.get(allTransitions.size() - 1);
				//transition.setLabel(label);
                                automata.setTransitionLabel(transition, label);
                                
			}  // else if (tag.equals(TAG_LABEL, Tag.Type.START))
			else if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START)) {

				parseTransitionGeometricData(xmlStreamReader, automata);

			} // End if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START))
			else if (tag.equals(TAG_INITIAL, Tag.Type.START)) {

				parseInitialFinalTag(xmlStreamReader, automata, statesMap, true);

			} // End if (tag.equals(TAG_INITIAL, Tag.Type.START))
			else if (tag.equals(TAG_FINAL, Tag.Type.START)) {

				parseInitialFinalTag(xmlStreamReader, automata, statesMap, false);

			}  // End if (tag.equals(TAG_FINAL, Tag.Type.START))
                        else if(tag.equals(TAG_DRAWING_DATA, Tag.Type.START)){
                                parseTransitionDrawingData(xmlStreamReader, automata);
                        }
			tag = Tag.nextStartOrEnd(xmlStreamReader);

		}  // End while ((tag != null) && (!(tag.equals(TAG_TRANSITIONS, Tag.Type.END))))

	}  // End private void parseTransitionsTag(XMLStreamReader xmlStreamReader, Automata automata, Map<String, State> statesMap)

	private WeightedRegularExpression parseLabelTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {
                //System.out.println("......parsing label");
				
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
                                        //System.out.println("expression: "+((WeightedRegularExpression.Atomic)expression).getSymbol());
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
						label = new WeightedRegularExpression.RightMultiply(firstChild, weight);
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
                //System.out.println("......parsing MonElmt");
		        
		Object returnObject = null;

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_MON_ELMT, Tag.Type.END)))) {
			Object object = null;

			if (tag.equals(TAG_MON_GEN, Tag.Type.START)) {
				object = parseMonGenTag(xmlStreamReader, automata);
			} else if (tag.equals(TAG_MON_ELMT, Tag.Type.START)) {
				object = parseMonElmtTag(xmlStreamReader, automata);
			} else if (tag.equals(TAG_ZERO, Tag.Type.START)) {
				WeightedRegularExpression expression = new WeightedRegularExpression.Zero();
				expression.setAlphabet(automata.getAlphabet());
				expression.setWeight(automata.getWeight());
				expression.setWritingData(automata.getWritingData());
				object = expression;
			} else if (tag.equals(TAG_ONE, Tag.Type.START)) {
				WeightedRegularExpression expression = new WeightedRegularExpression.One();
				expression.setAlphabet(automata.getAlphabet());
				expression.setWeight(automata.getWeight());
				expression.setWritingData(automata.getWritingData());
				object = expression;
			}

			if (object != null) {
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
			}  // End if (object != null)

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_MON_ELMT, Tag.Type.END))))

		if (returnObject == null) {
			throw new FsmXmlException("Parsing \"" + TAG_MON_ELMT + "\" tag yields no result.");
		}
                
                //System.out.println(returnObject);
                
		return returnObject;
	}  // End private Object parseMonElmtTag(XMLStreamReader xmlStreamReader, Automata automata)

	private Object parseWeightTag(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		Object object = xmlStreamReader.getAttributeValue(null, ATR_VALUE);
//		switch (automata.getWeight().semiring) {
//			case Z_INTEGER:
//			case ZMIN_MIN_TROPICAL:
//			case ZMAX_MAX_TROPICAL:
//				object = Integer.valueOf(xmlStreamReader.getAttributeValue(null, ATR_VALUE));
//				break;
//			case Q_RATIONAL:
//			case R_REAL:
//				object = Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_VALUE));
//				break;
//			case B_BOOLEAN:
//			case F2_TWO_ELEMENT_FIELD:
//				throw new FsmXmlException("The boolean semiring and the two-element field should not have any weight tags.");
//			default:
//				throw new FsmXmlException("Unrecognizable semiring set.");
//		}  // End switch (automata.getWeight().semiring)

		if (!(Tag.findNextSpecified(xmlStreamReader, TAG_WEIGHT, Tag.Type.END))) {
			Tag.assertTag(TAG_WEIGHT, Tag.Type.END);
		}

		if (object == null) {
			throw new FsmXmlException("Parsing \"" + TAG_WEIGHT + "\" tag yields no result.");
		}
		return object;
	}  // End private Object parseWeightTag(XMLStreamReader xmlStreamReader, Automata automata)

	private void parseTransitionGeometricData(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {

		TransitionGeometricData geometricData = new TransitionGeometricData();
		String xStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_POS);
		String yStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_DIST);
		if ((xStr != null) && (yStr != null)) {
			double x = Double.valueOf(xStr);
			double y = Double.valueOf(yStr);
			geometricData.labelPosAndDist = new Point2D.Double(x, y);
		}  // End if ((xStr != null) && (yStr != null))
		xStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_X);
		yStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_Y);
		if ((xStr != null) && (yStr != null)) {
			double x = Double.valueOf(xStr);
			double y = Double.valueOf(yStr);
			geometricData.labelOffset = new Point2D.Double(x, y);
		}  // End if ((xStr != null) && (yStr != null))

		Tag tag = Tag.nextStartOrEnd(xmlStreamReader);
		while ((tag != null) && (!(tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.END)))) {

			if (tag.equals(TAG_CONTROL_POINT, Tag.Type.START)) {
				xStr = xmlStreamReader.getAttributeValue(null, ATR_X);
				yStr = xmlStreamReader.getAttributeValue(null, ATR_Y);
				if ((xStr != null) && (yStr != null)) {
					double x = Double.valueOf(xStr);
					double y = Double.valueOf(yStr);
					geometricData.controlPoints.add(new Point2D.Double(x, y));
                                        
				}  // End if ((xStr != null) && (yStr != null))
			}  // End if (tag.equals(TAG_CONTROL_POINT, Tag.Type.START))

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while ((tag != null) && (!(tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.END))))

		List<Transition> allTransitions = automata.getAllTransitions();
		Transition transition = allTransitions.get(allTransitions.size() - 1);
		//transition.setGeometricData(geometricData);
                automata.setTransitionGeometricData(transition, geometricData);
                //System.out.println("control pt: "+transition.getGeometricData().controlPoints);
	}  // End private void parseTransitionGeometricData(XMLStreamReader xmlStreamReader, Automata automata)

        private void parseTransitionDrawingData(XMLStreamReader xmlStreamReader, Automata automata)
			throws XMLStreamException,
			FsmXmlException {
            TransitionDrawingData drawingdata=new TransitionDrawingData();
            
            String strokecolor=xmlStreamReader.getAttributeValue(null, ATR_STROKE_COLOR);
            float strokewidth=Float.valueOf(xmlStreamReader.getAttributeValue(null, ATR_STROKE_WIDTH));
            String startArrow=xmlStreamReader.getAttributeValue(null, ATR_START_ARROW);
            String endArrow=xmlStreamReader.getAttributeValue(null, ATR_END_ARROW);
            
            drawingdata.setStrokeColor(strokecolor);
            drawingdata.setStrokeWidth(strokewidth);
            drawingdata.setStartArrow(startArrow);
            drawingdata.setEndArrow(endArrow);
            
            
            List<Transition> allTransitions = automata.getAllTransitions();
            Transition transition = allTransitions.get(allTransitions.size() - 1);
            //transition.setDrawingData(drawingdata);
            automata.setTransitionDrawingData(transition, drawingdata);       
            
            
            
        }
        
        
        
	protected void parseInitialFinalTag(
			XMLStreamReader xmlStreamReader,
			Automata automata,
			Map<String, State> statesMap,
			boolean isInitial)
			throws XMLStreamException,
			FsmXmlException {

		Tag tag;
		if (isInitial) {
			tag = new Tag(TAG_INITIAL, Tag.Type.START);
		} else {
			tag = new Tag(TAG_FINAL, Tag.Type.START);
		}

		String id = xmlStreamReader.getAttributeValue(null, ATR_STATE);
		if (id == null) {
			throw new FsmXmlException("Missing required \"" + ATR_STATE + "\" attribute of a \"" + tag.localName + "\" tag.");
		}
		State state = statesMap.get(id);
		if (state == null) {
			throw new FsmXmlException("Missing state with id \"" + id + "\", which is referenced by a \"" + tag.localName + "\" tag.");
		}

		//InitialFinalWeight initialFinalWeight = new InitialFinalWeight();
		//initialFinalWeight.setValue(getSemiringIdentityValue(automata.getWeight().semiring));
                Object value=getSemiringIdentityValue(automata.getWeight().semiring);
                IniFinGeometricData geodata=new IniFinGeometricData();
                            
		tag = Tag.nextStartOrEnd(xmlStreamReader);
		while (!((isInitial && (tag.equals(TAG_INITIAL, Tag.Type.END))) || (!isInitial && (tag.equals(TAG_FINAL, Tag.Type.END))))) {

			if (tag.equals(TAG_WEIGHT, Tag.Type.START)) {
				value=parseWeightTag(xmlStreamReader, automata);
			} else if (tag.equals(TAG_GEOMETRIC_DATA, Tag.Type.START)) {
                            
                            String xStr = xmlStreamReader.getAttributeValue(null, ATR_LENGTHRATIO);
                            if(xStr!=null){
                                double lr=Double.valueOf(xStr);
                                geodata.lengthRatio=lr;
                            }
                            String yStr = xmlStreamReader.getAttributeValue(null, ATR_DIRECTION);
                            if(yStr!=null){
                                double dir=Double.valueOf(yStr);
                                geodata.direction=dir;
                            }
                            xStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_POS);
                            yStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_DIST);
                            if ((xStr != null) && (yStr != null)) {
                                    double x = Double.valueOf(xStr);
                                    double y = Double.valueOf(yStr);
                                    geodata.labelPosAndDist = new Point2D.Double(x, y);
                            } 
                            xStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_X);
                            yStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_Y);
                            if ((xStr != null) && (yStr != null)) {
                                    double x = Double.valueOf(xStr);
                                    double y = Double.valueOf(yStr);
                                    geodata.labelOffset = new Point2D.Double(x, y);
                            }
                            
                            
//				InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
//				String xStr = xmlStreamReader.getAttributeValue(null, ATR_X);
//				String yStr = xmlStreamReader.getAttributeValue(null, ATR_Y);
//				if ((xStr != null) && (yStr != null)) {
//					double x = Double.valueOf(xStr);
//					double y = Double.valueOf(yStr);
//					geometricData.offset = new Point2D.Double(x, y);
//				}  // End if ((xStr != null) && (yStr != null))
//				xStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_POS);
//				yStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_DIST);
//				if ((xStr != null) && (yStr != null)) {
//					double x = Double.valueOf(xStr);
//					double y = Double.valueOf(yStr);
//					geometricData.labelPosAndDist = new Point2D.Double(x, y);
//				}  // End if ((xStr != null) && (yStr != null))
//				xStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_X);
//				yStr = xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_Y);
//				if ((xStr != null) && (yStr != null)) {
//					double x = Double.valueOf(xStr);
//					double y = Double.valueOf(yStr);
//					geometricData.labelOffset = new Point2D.Double(x, y);
//				}  // End if ((xStr != null) && (yStr != null))
//				if ((geometricData.offset != null)
//						|| (geometricData.labelPosAndDist != null)
//						|| (geometricData.labelOffset != null)) {
//					initialFinalWeight.setGeometricData(geometricData);
//				}
//				geometricData = null;  // InitialFinalWeight.GeometricData geometricData = new InitialFinalWeight.GeometricData();
			}

			tag = Tag.nextStartOrEnd(xmlStreamReader);
		}  // End while (!((isInitial && (tag.equals(TAG_INITIAL, Tag.Type.END))) || (!isInitial && (tag.equals(TAG_FINAL, Tag.Type.END)))))

		if (isInitial) {
			//state.setInitialWeight(initialFinalWeight);
                        automata.setInitialWeight(state, value);
                        automata.setIniFinGeometricData(state, geodata, true);
		} else {
			//state.setFinalWeight(initialFinalWeight);
                        automata.setFinalWeight(state, value);
                        automata.setIniFinGeometricData(state, geodata, false);
		}

	}  // End protected void parseInitialFinalTag()

	public static Object getSemiringIdentityValue(TAFKitInterface.AutomataType.Semiring semiring) {
		switch (semiring) {
			case Z_INTEGER:
			case ZMIN_MIN_TROPICAL:
			case ZMAX_MAX_TROPICAL:
				return new Integer(1);
			case Q_RATIONAL:
			case R_REAL:
				return new Double(1);
			case B_BOOLEAN:
			case F2_TWO_ELEMENT_FIELD:
				return true;
			default:
				throw new IllegalArgumentException("Unrecognizable semiring set.");
		}  // End switch (automata.getWeight().semiring)
	} // End public Object getSemiringIdentityValue(TAFKitInterface.AutomataType.Semiring semiring)

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
		this.write(automataList, outputStream, true);
		outputStream.close();
	}  // End public void write(List<Automata> automataList, File fsmXmlFile)

	protected void write(List<Automata> automataList, File fsmXmlFile, boolean writeGeometricAndDrawingData)
			throws
			IOException,
			FsmXmlException {
		if (!(fsmXmlFile.exists())) {
			fsmXmlFile.getParentFile().mkdirs();
			fsmXmlFile.createNewFile();
		}
		OutputStream outputStream = new FileOutputStream(fsmXmlFile);
		this.write(automataList, outputStream, writeGeometricAndDrawingData);
		outputStream.close();
	}  // End public void write(List<Automata> automataList, File fsmXmlFile)

	@Override
	public void write(List<Automata> automataList, OutputStream outputStream)
			throws
			FsmXmlException {
		this.write(automataList, outputStream, true);
	}

	protected void write(List<Automata> automataList, OutputStream outputStream, boolean writeGeometricAndDrawingData)
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
				writeStatesTag(xmlStreamWriter, automata, writeGeometricAndDrawingData);
				writeTransitionsTag(xmlStreamWriter, automata, writeGeometricAndDrawingData);
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

	private void writeStatesTag(XMLStreamWriter xmlStreamWriter, Automata automata, boolean writeGeometricAndDrawingData)
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
			if (writeGeometricAndDrawingData) {
				Point2D point2d = state.getGeometricData().getLocation();
				xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
				if (point2d != null) {
                                    xmlStreamWriter.writeAttribute(ATR_X, String.valueOf(point2d.getX()));
                                    xmlStreamWriter.writeAttribute(ATR_Y, String.valueOf(point2d.getY()));
				    
                                        
				}
                                Point2D sizepoint2d = state.getGeometricData().getSize();
				if (sizepoint2d != null) {
					xmlStreamWriter.writeAttribute(ATR_WIDTH, String.valueOf(sizepoint2d.getX()));
					xmlStreamWriter.writeAttribute(ATR_HEIGHT, String.valueOf(sizepoint2d.getY()));
				        
				}
                                String shape=state.getShape();
                                if(shape!=null)
                                    xmlStreamWriter.writeAttribute(ATR_SHAPE, shape);
                                
                                xmlStreamWriter.writeEndElement();  
                                    
                            // End TAG_GEOMETRIC_DATA
                                
                            
                            // TAG_DRAWING_DATA
                            xmlStreamWriter.writeStartElement(TAG_DRAWING_DATA);
//                            xmlStreamWriter.writeAttribute(ATR_FILL_COLOR, state.getDrawingData().fillColor);
//                            xmlStreamWriter.writeAttribute(ATR_STROKE_COLOR, state.getDrawingData().strokeColor);
//                            xmlStreamWriter.writeAttribute(ATR_STROKE_WIDTH, String.valueOf(state.getDrawingData().strokeWidth));
                            xmlStreamWriter.writeAttribute(ATR_FILL_COLOR, automata.getStateDrawingData(state).getFillColor());
                            xmlStreamWriter.writeAttribute(ATR_STROKE_COLOR, automata.getStateDrawingData(state).getStrokeColor());
                            xmlStreamWriter.writeAttribute(ATR_STROKE_WIDTH, String.valueOf(automata.getStateDrawingData(state).getStrokeWidth()));
                            
                            xmlStreamWriter.writeEndElement();   
                        
                        
                        } // End if (writeGeometricAndDrawingData)
                        
                        
                        
                        
                        
                        xmlStreamWriter.writeEndElement();  // End TAG_STATE
		}  // End while (allStatesIterator.hasNext())

		xmlStreamWriter.writeEndElement();  // End TAG_STATES

	}  // End private void writeStateTags(XMLStreamWriter xmlStreamWriter, Automata automata)

	private void writeTransitionsTag(XMLStreamWriter xmlStreamWriter, Automata automata, boolean writeGeometricAndDrawingData)
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
			if (writeGeometricAndDrawingData) {
				TransitionGeometricData geometricData = transition.getGeometricData();
				if (geometricData != null) {
					boolean isStartGeometricDataTagWritten = false;
					if (geometricData.labelPosAndDist != null) {
						xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
						isStartGeometricDataTagWritten = true;
						xmlStreamWriter.writeAttribute(ATR_LABEL_POS, String.valueOf(geometricData.labelPosAndDist.getX()));
						xmlStreamWriter.writeAttribute(ATR_LABEL_DIST, String.valueOf(geometricData.labelPosAndDist.getY()));
					}  // End if (geometricData.labelPosAndDist != null)
					if (geometricData.labelOffset != null) {
						if (!isStartGeometricDataTagWritten) {
							xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
							isStartGeometricDataTagWritten = true;
						}
						xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_X, String.valueOf(geometricData.labelOffset.getX()));
						xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_Y, String.valueOf(geometricData.labelOffset.getY()));
					}  // End if (geometricData.labelOffset != null)
					if ((geometricData.controlPoints != null) && !(geometricData.controlPoints.isEmpty())) {
						if (!isStartGeometricDataTagWritten) {
							xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
							isStartGeometricDataTagWritten = true;
						}
						Iterator<Point2D> iteratePoints = geometricData.controlPoints.iterator();
						while (iteratePoints.hasNext()) {
							Point2D point2d = iteratePoints.next();
							xmlStreamWriter.writeStartElement(TAG_CONTROL_POINT);
							xmlStreamWriter.writeAttribute(ATR_X, String.valueOf(point2d.getX()));
							xmlStreamWriter.writeAttribute(ATR_Y, String.valueOf(point2d.getY()));
							xmlStreamWriter.writeEndElement();  // End TAG_CONTROL_POINT
						}  // End while (iteratePoints.hasNext())
					}  // End if ((geometricData.controlPoints != null) && !(geometricData.controlPoints.isEmpty()))
					if (isStartGeometricDataTagWritten) {
						xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
					}
				}  // End if (geometricData != null)
                                
                                // write drawing data
                        
                            xmlStreamWriter.writeStartElement(TAG_DRAWING_DATA);
                            xmlStreamWriter.writeAttribute(ATR_STROKE_COLOR, automata.getTransitionDrawingData(transition).getStrokeColor());
                            xmlStreamWriter.writeAttribute(ATR_STROKE_WIDTH, String.valueOf(automata.getTransitionDrawingData(transition).getStrokeWidth()));
                        
                            String startarrow=automata.getTransitionDrawingData(transition).getStartArrow();
                            if(startarrow!=null)
                                xmlStreamWriter.writeAttribute(ATR_START_ARROW, startarrow);
                            xmlStreamWriter.writeAttribute(ATR_END_ARROW, automata.getTransitionDrawingData(transition).getEndArrow());
                            xmlStreamWriter.writeEndElement();   
			} // End if (writeGeometricAndDrawingData)
			WeightedRegularExpression label = transition.getLabel();
			if (label != null) {
				xmlStreamWriter.writeStartElement(TAG_LABEL);
				writeWeightedRegularExpression(xmlStreamWriter, label);
				xmlStreamWriter.writeEndElement();  // End TAG_LABEL
			}
			xmlStreamWriter.writeEndElement();  // End TAG_TRANSITION
		}  // End while (allTransitionsIterator.hasNext())

		Iterator<State> allStatesIterator = allStates.iterator();
		while (allStatesIterator.hasNext()) {
			State state = allStatesIterator.next();
//			InitialFinalWeight initialFinalWeight = state.getInitialWeight();
                        Initial ini=state.getInitial();
                        if (ini != null) {
				xmlStreamWriter.writeStartElement(TAG_INITIAL);
				xmlStreamWriter.writeAttribute(ATR_STATE, "s" + allStates.indexOf(state));
				if (writeGeometricAndDrawingData) {
					IniFinGeometricData geometricData = automata.getIniFinGeometricData(state, true);
					if (geometricData != null) {
						xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
							xmlStreamWriter.writeAttribute(ATR_DIRECTION, String.valueOf(geometricData.direction));
							xmlStreamWriter.writeAttribute(ATR_LENGTHRATIO, String.valueOf(geometricData.lengthRatio));
                                                        xmlStreamWriter.writeAttribute(ATR_LABEL_POS, String.valueOf(geometricData.labelPosAndDist.getX()));
							xmlStreamWriter.writeAttribute(ATR_LABEL_DIST, String.valueOf(geometricData.labelPosAndDist.getY()));
                                                        xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_X, String.valueOf(geometricData.labelOffset.getX()));
							xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_Y, String.valueOf(geometricData.labelOffset.getY()));
                                                xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
					
					}  // End if (geometricData != null)
				} // End if (writeGeometricAndDrawingData)
//			if (initialFinalWeight != null) {
//				xmlStreamWriter.writeStartElement(TAG_INITIAL);
//				xmlStreamWriter.writeAttribute(ATR_STATE, "s" + allStates.indexOf(state));
//				if (writeGeometricAndDrawingData) {
//					InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
//					if (geometricData != null) {
//						boolean isStartGeometricDataTagWritten = false;
//						if (geometricData.offset != null) {
//							xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
//							isStartGeometricDataTagWritten = true;
//							xmlStreamWriter.writeAttribute(ATR_X, String.valueOf(geometricData.offset.getX()));
//							xmlStreamWriter.writeAttribute(ATR_Y, String.valueOf(geometricData.offset.getY()));
//						}  // End if (geometricData.offset != null)
//						if (geometricData.labelPosAndDist != null) {
//							if (!isStartGeometricDataTagWritten) {
//								xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
//								isStartGeometricDataTagWritten = true;
//							}
//							xmlStreamWriter.writeAttribute(ATR_LABEL_POS, String.valueOf(geometricData.labelPosAndDist.getX()));
//							xmlStreamWriter.writeAttribute(ATR_LABEL_DIST, String.valueOf(geometricData.labelPosAndDist.getY()));
//						}  // End if (geometricData.labelPosAndDist != null)
//						if (geometricData.labelOffset != null) {
//							if (!isStartGeometricDataTagWritten) {
//								xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
//								isStartGeometricDataTagWritten = true;
//							}
//							xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_X, String.valueOf(geometricData.labelOffset.getX()));
//							xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_Y, String.valueOf(geometricData.labelOffset.getY()));
//						}  // End if (geometricData.labelOffset != null)
//						if (isStartGeometricDataTagWritten) {
//							xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
//						}
//					}  // End if (geometricData != null)
//				} // End if (writeGeometricAndDrawingData)
//				Object object = initialFinalWeight.getValue();
                                Object object=ini.getWeight();
				if (!(object instanceof Boolean)) {
					xmlStreamWriter.writeStartElement(TAG_LABEL);
					xmlStreamWriter.writeStartElement(TAG_LEFT_EXT_MUL);
					writeWeightTag(xmlStreamWriter, object);
					xmlStreamWriter.writeStartElement(TAG_ONE);
					xmlStreamWriter.writeEndElement();  // End TAG_ONE
					xmlStreamWriter.writeEndElement();  // End TAG_LEFT_EXT_MUL
					xmlStreamWriter.writeEndElement();  // End TAG_LABEL
				}  // End if (!(object instanceof Boolean))
				xmlStreamWriter.writeEndElement();  // End TAG_INITIAL
			}  // End if (initialFinalWeight != null)
		}  // End while (allStatesIterator.hasNext())

		allStatesIterator = allStates.iterator();
		while (allStatesIterator.hasNext()) {
			State state = allStatesIterator.next();
//			InitialFinalWeight initialFinalWeight = state.getFinalWeight();
                        Final fin=state.getFinal();
			if (fin != null) {
				xmlStreamWriter.writeStartElement(TAG_FINAL);
				xmlStreamWriter.writeAttribute(ATR_STATE, "s" + allStates.indexOf(state));
				if (writeGeometricAndDrawingData) {
                                        IniFinGeometricData geometricData = automata.getIniFinGeometricData(state, false);
					if (geometricData != null) {
						xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
							xmlStreamWriter.writeAttribute(ATR_DIRECTION, String.valueOf(geometricData.direction));
							xmlStreamWriter.writeAttribute(ATR_LENGTHRATIO, String.valueOf(geometricData.lengthRatio));
                                                        xmlStreamWriter.writeAttribute(ATR_LABEL_POS, String.valueOf(geometricData.labelPosAndDist.getX()));
							xmlStreamWriter.writeAttribute(ATR_LABEL_DIST, String.valueOf(geometricData.labelPosAndDist.getY()));
                                                        xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_X, String.valueOf(geometricData.labelOffset.getX()));
							xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_Y, String.valueOf(geometricData.labelOffset.getY()));
                                                xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
					
                                        
//					InitialFinalWeight.GeometricData geometricData = initialFinalWeight.getGeometricData();
//					if (geometricData != null) {
//						boolean isStartGeometricDataTagWritten = false;
//						if (geometricData.offset != null) {
//							xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
//							isStartGeometricDataTagWritten = true;
//							xmlStreamWriter.writeAttribute(ATR_X, String.valueOf(geometricData.offset.getX()));
//							xmlStreamWriter.writeAttribute(ATR_Y, String.valueOf(geometricData.offset.getY()));
//						}  // End if (geometricData.offset != null)
//						if (geometricData.labelPosAndDist != null) {
//							if (!isStartGeometricDataTagWritten) {
//								xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
//								isStartGeometricDataTagWritten = true;
//							}
//							xmlStreamWriter.writeAttribute(ATR_LABEL_POS, String.valueOf(geometricData.labelPosAndDist.getX()));
//							xmlStreamWriter.writeAttribute(ATR_LABEL_DIST, String.valueOf(geometricData.labelPosAndDist.getY()));
//						}  // End if (geometricData.labelPosAndDist != null)
//						if (geometricData.labelOffset != null) {
//							if (!isStartGeometricDataTagWritten) {
//								xmlStreamWriter.writeStartElement(TAG_GEOMETRIC_DATA);
//								isStartGeometricDataTagWritten = true;
//							}
//							xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_X, String.valueOf(geometricData.labelOffset.getX()));
//							xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_Y, String.valueOf(geometricData.labelOffset.getY()));
//						}  // End if (geometricData.labelOffset != null)
//						if (isStartGeometricDataTagWritten) {
//							xmlStreamWriter.writeEndElement();  // End TAG_GEOMETRIC_DATA
//						}
					}  // End if (geometricData != null)
				} // End if (writeGeometricAndDrawingData)
//				Object object = initialFinalWeight.getValue();
                                Object object=fin.getWeight();
				if (!(object instanceof Boolean)) {
					xmlStreamWriter.writeStartElement(TAG_LABEL);
					xmlStreamWriter.writeStartElement(TAG_LEFT_EXT_MUL);
					writeWeightTag(xmlStreamWriter, object);
					xmlStreamWriter.writeStartElement(TAG_ONE);
					xmlStreamWriter.writeEndElement();  // End TAG_ONE
					xmlStreamWriter.writeEndElement();  // End TAG_LEFT_EXT_MUL
					xmlStreamWriter.writeEndElement();  // End TAG_LABEL
				}  // End if (!(object instanceof Boolean))
				xmlStreamWriter.writeEndElement();  // End TAG_FINAL
			}  // End if (initialFinalWeight != null)
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
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setGroupingUsed(false);
			String output = numberFormat.format((Double) weight);
			xmlStreamWriter.writeAttribute(ATR_VALUE, output);
		}else{
                    xmlStreamWriter.writeAttribute(ATR_VALUE, weight.toString());
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

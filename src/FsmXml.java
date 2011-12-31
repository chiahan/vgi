
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

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
	private static final String TAG_MONOID = "monoid";
	private static final String ATR_GEN_SORT = "genSort";
	private static final String VAL_LETTERS = "letters";
	private static final String VAL_DIGITS = "digits";
	private static final String VAL_ALPHANUMS = "alphanums";
	private static final String VAL_INTEGERS = "integers";
	private static final String ATR_TYPE = "type";
	private static final String VAL_UNIT = "unit";
	private static final String VAL_FREE = "free";
	private static final String VAL_PRODUCT = "product";
	private static final String TAG_MON_GEN = "monGen";
	private static final String ATR_VALUE = "value";
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

	private void processStartElement(XMLStreamReader xmlStreamReader)
			throws FsmXmlException {

		String localName = xmlStreamReader.getLocalName();

		//
		// Check that <fsmxml xmlns="http://vaucanson.lrde.epita.fr" version="1.0"> is at the root level.
		//
		if (this.pmTagStack.isEmpty()) {
			if (!(localName.equals(TAG_FSMXML))) {
				throw new FsmXmlException("Root tag is not " + TAG_FSMXML + " so this is likely an invalid FSM XML file.");
			} else if (!(xmlStreamReader.getNamespaceURI().equals(VAL_FSMXML_NAMESPACE))) {
				throw new FsmXmlException("Namespace is not " + VAL_FSMXML_NAMESPACE + " so this is likely an invalid FSM XML file.");
			} else if (!(xmlStreamReader.getVersion().equals(VAL_FSMXML_VERSION_NUMBER))) {
				throw new FsmXmlException("VGI only supports FSM XML files whose version is " + VAL_FSMXML_VERSION_NUMBER + ".");
			}
		}  // End if (this.pmTagStack.isEmpty())

//		System.out.print(localName + " start");
//		for (int index = 0; index < xmlStreamReader.getAttributeCount(); index++) {
//			System.out.print("; " + xmlStreamReader.getAttributeLocalName(index) + " = " + xmlStreamReader.getAttributeValue(index));
//		}  // End for (int index = 0; index < xmlStreamReader.getAttributeCount(); index++)
//		System.out.println();

		Tag tag = new Tag(localName);
		Tag parentTag = this.pmTagStack.peek();

		if (localName.equals(TAG_AUTOMATON)) {

			tag.object = new Automata();

		} // End if (localName.equals(TAG_AUTOMATON))
		else if (localName.equals(TAG_WRITING_DATA)) {

			if (parentTag.localName.equals(TAG_MONOID)) {

				tag.object = xmlStreamReader.getAttributeValue(null, ATR_IDENTITY_SYM);

			} else {  // End if (parentTag.localName.equals(TAG_MONOID))
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
				tag.object = writingData;
			}  // End else part of if (parentTag.localName.equals(TAG_MONOID))

		} // End if (localName.equals(TAG_WRITING_DATA))
		else if (localName.equals(TAG_SEMIRING)) {

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

			tag.object = weight;

		} // End if (localName.equals(TAG_SEMIRING))
		else if (localName.equals(TAG_MONOID)) {

			String type = xmlStreamReader.getAttributeValue(null, ATR_TYPE);

			if (type.equals(VAL_UNIT)) {

				throw new FsmXmlException("VGI does not currently support the monoid type \"unit\".");

			} else if (type.equals(VAL_FREE)) {

				AutomataInterface.Alphabet alphabet = new AutomataInterface.Alphabet();
				String genSort = xmlStreamReader.getAttributeValue(null, ATR_GEN_SORT);

				if ((genSort.equals(VAL_LETTERS))
						|| (genSort.equals(VAL_DIGITS))
						|| (genSort.equals(VAL_ALPHANUMS))) {
					alphabet.dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR;
				} else if (genSort.equals(VAL_INTEGERS)) {
					alphabet.dataType = TAFKitInterface.AutomataType.AlphabetDataType.INT;
				} else {
					throw new FsmXmlException("Unrecognizable value of the genSort attribute of the monoid tag.");
				}

				tag.object = alphabet;

			} else if (type.equals(VAL_PRODUCT)) {

				if (!(parentTag.object instanceof Automata)) {
					throw new FsmXmlException("Unexpected parent tag.object type.");
				}

				((Automata) parentTag.object).setIsTransducer(true);
				tag.object = parentTag.object;

			}  // End if (type.equals(VAL_PRODUCT))

		} // End if (localName.equals(TAG_MONOID))
		else if (localName.equals(TAG_MON_GEN)) {

			tag.object = xmlStreamReader.getAttributeValue(null, ATR_VALUE);

		} // End if (localName.equals(TAG_MON_GEN))
		else if (localName.equals(TAG_STATE)) {

			String id = xmlStreamReader.getAttributeValue(null, ATR_ID);
			if (id == null) {
				throw new FsmXmlException("Missing required \"" + ATR_ID + "\" attribute of a \"" + localName + "\" tag.");
			}

			State state = new State();
			this.pmMapIdState.put(id, state);
			tag.object = state;

		} // End if (localName.equals(TAG_STATE))
		else if (localName.equals(TAG_TRANSITION)) {

			String sourceId = xmlStreamReader.getAttributeValue(null, ATR_SOURCE);
			if (sourceId == null) {
				throw new FsmXmlException("Missing required \"" + ATR_SOURCE + "\" attribute of a \"" + localName + "\" tag.");
			}
			String targetId = xmlStreamReader.getAttributeValue(null, ATR_TARGET);
			if (targetId == null) {
				throw new FsmXmlException("Missing required \"" + ATR_TARGET + "\" attribute of a \"" + localName + "\" tag.");
			}
			if (!(parentTag.object instanceof Automata)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}
			Automata automata = (Automata) parentTag.object;
			State sourceState = this.pmMapIdState.get(sourceId);
			if (sourceState == null) {
				throw new FsmXmlException("Missing state with id \"" + sourceId + "\", which is referenced by a transition.");
			}
			State targetState = this.pmMapIdState.get(targetId);
			if (targetState == null) {
				throw new FsmXmlException("Missing state with id \"" + targetId + "\", which is referenced by a transition.");
			}

			Transition transition = new Transition();
			transition.setSourceState(sourceState);
			transition.setTargetState(targetState);
			tag.object = transition;

		} // End if (localName.equals(TAG_TRANSITION))
		else if (localName.equals(TAG_LABEL)) {

			tag.object = new String();

		} // End if (localName.equals(TAG_LABEL))
		else if (localName.equals(TAG_INITIAL)) {

			String id = xmlStreamReader.getAttributeValue(null, ATR_STATE);
			if (id == null) {
				throw new FsmXmlException("Missing required \"" + ATR_STATE + "\" attribute of a \"" + localName + "\" tag.");
			}
			if (!(parentTag.object instanceof Automata)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}
			Automata automata = (Automata) parentTag.object;
			State state = this.pmMapIdState.get(id);
			if (state == null) {
				throw new FsmXmlException("Missing state with id \"" + id + "\", which is referenced by a \"" + localName + "\" tag.");
			}

			tag.object = state;

		} // End if (localName.equals(TAG_INITIAL))
		else if (localName.equals(TAG_FINAL)) {

			String id = xmlStreamReader.getAttributeValue(null, ATR_STATE);
			if (id == null) {
				throw new FsmXmlException("Missing required \"" + ATR_STATE + "\" attribute of a \"" + localName + "\" tag.");
			}
			if (!(parentTag.object instanceof Automata)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}
			Automata automata = (Automata) parentTag.object;
			State state = this.pmMapIdState.get(id);
			if (state == null) {
				throw new FsmXmlException("Missing state with id \"" + id + "\", which is referenced by a \"" + localName + "\" tag.");
			}

			tag.object = state;

		} // End if (localName.equals(TAG_FINAL))
		else if (parentTag != null) {

			tag.object = parentTag.object;

		}

		this.pmTagStack.push(tag);
	}  // End private void processStartElement(XMLStreamReader xmlStreamReader)

	private void processEndElement(XMLStreamReader xmlStreamReader)
			throws FsmXmlException {

		String localName = xmlStreamReader.getLocalName();
//		System.out.println(localName + " end");

		Tag tag = this.pmTagStack.pop();
		if (!(tag.localName.equals(localName))) {
			throw new FsmXmlException(tag.localName + " start tage is being matched with a different end tag, " + localName + ".");
		}

		Tag parentTag = this.pmTagStack.peek();

		if (localName.equals(TAG_AUTOMATON)) {

			if (!(tag.object instanceof Automata)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			}

			this.pmAutomataList.add((Automata) tag.object);
			this.pmMapIdState.clear();

		} // End if (localName.equals(TAG_AUTOMATON))
		else if (localName.equals(TAG_WRITING_DATA)) {

			if (parentTag.localName.equals(TAG_MONOID)) {

				if (parentTag.object instanceof AutomataInterface.Alphabet) {
					AutomataInterface.Alphabet alphabet = (AutomataInterface.Alphabet) parentTag.object;
					alphabet.identitySymbol = tag.object;
				}

			} else {  // End if (parentTag.localName.equals(TAG_MONOID))

				if (!(tag.object instanceof AutomataInterface.WritingData)) {
					throw new FsmXmlException("Mismatched tag.object type.");
				} else if (parentTag.object instanceof Automata) {
					Automata automata = (Automata) parentTag.object;
					automata.setWritingData((AutomataInterface.WritingData) tag.object);
				}

			}  // End else part of if (parentTag.localName.equals(TAG_MONOID))

		} // End if (localName.equals(TAG_WRITING_DATA))
		else if (localName.equals(TAG_SEMIRING)) {

			if (!(tag.object instanceof AutomataInterface.Weight)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			} else if (!(parentTag.localName.equals(TAG_VALUE_TYPE))) {
				throw new FsmXmlException("Unexpected parent tag.");
			} else if (!(parentTag.object instanceof Automata)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}

			Automata automata = (Automata) parentTag.object;
			automata.setWeight((AutomataInterface.Weight) tag.object);

		} // End if (localName.equals(TAG_SEMIRING))
		else if (localName.equals(TAG_MONOID)) {

			if ((tag.object instanceof AutomataInterface.Alphabet)
					&& (parentTag.object instanceof Automata)) {
				Automata automata = (Automata) parentTag.object;
				if (automata.getAlphabet() == null) {
					automata.setAlphabet((AutomataInterface.Alphabet) tag.object);
				} else if (automata.getSecondAlphabet() == null) {
					automata.setSecondAlphabet((AutomataInterface.Alphabet) tag.object);
				}
			}  // End if ((tag.object instanceof AutomataInterface.Alphabet)
			//	&& (parentTag.object instanceof Automata))

		} // End if (localName.equals(TAG_MONOID))
		else if (localName.equals(TAG_MON_GEN)) {

			if (!(tag.object instanceof String)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			} else if (parentTag.object instanceof AutomataInterface.Alphabet) {
				((AutomataInterface.Alphabet) parentTag.object).allSymbols.add(tag.object);
			} else if (parentTag.object instanceof String) {
				String label = (String) parentTag.object;
				label = label + (String) tag.object;
			}

		} // End if (localName.equals(TAG_MON_GEN))
		else if (localName.equals(TAG_STATE)) {

			if (!(tag.object instanceof State)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			} else if (!(parentTag.object instanceof Automata)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}

			((Automata) parentTag.object).addState((State) tag.object);

		} // End if (localName.equals(TAG_STATE))
		else if (localName.equals(TAG_TRANSITION)) {

			if (!(tag.object instanceof Transition)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			} else if (!(parentTag.object instanceof Automata)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}

			Automata automata = (Automata) parentTag.object;
			automata.addTransition((Transition) tag.object);

		} // End if (localName.equals(TAG_TRANSITION))
		else if (localName.equals(TAG_LABEL)) {

			if (!(tag.object instanceof String)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			} else if (!(parentTag.object instanceof Transition)) {
				throw new FsmXmlException("Unexpected parent tag.object type.");
			}

			Transition transition = (Transition) parentTag.object;
			transition.setLabel((String) tag.object);

		} // End if (localName.equals(TAG_LABEL))
		else if (localName.equals(TAG_INITIAL)) {

			if (!(tag.object instanceof State)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			}

			((State) tag.object).setInitialWeight(true);

		} // End if (localName.equals(TAG_INITIAL))
		else if (localName.equals(TAG_FINAL)) {

			if (!(tag.object instanceof State)) {
				throw new FsmXmlException("Mismatched tag.object type.");
			}

			((State) tag.object).setFinalWeight(true);

		}  // End if (localName.equals(TAG_FINAL))

		parentTag = null;
		tag.localName = null;
		tag.object = null;
		tag = null;
	}  // End private void processEndElement(XMLStreamReader xmlStreamReader)

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
			this.pmTagStack = new ArrayDeque<Tag>();
			this.pmAutomataList = new ArrayList<Automata>();
			this.pmMapIdState = new HashMap<String, State>();

			while (xmlStreamReader.hasNext()) {
				eventType = xmlStreamReader.next();
				switch (eventType) {
					case XMLStreamReader.START_ELEMENT:
						this.processStartElement(xmlStreamReader);
						break;
					case XMLStreamReader.END_ELEMENT:
						this.processEndElement(xmlStreamReader);
						break;
					case XMLStreamReader.CHARACTERS:
						if (!xmlStreamReader.isWhiteSpace()) {
//							System.out.println("XML event CHARACTERS: " + xmlStreamReader.getText());
						}
					default:
//						System.out.println("eventType: " + eventType);
				}  // End switch (eventType)
			}  // End while (xmlStreamReader.hasNext())

		} catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
		} catch (FsmXmlException fsmXmlException) {
			throw fsmXmlException;
		} finally {
			if (this.pmTagStack != null) {
				this.pmTagStack = null;
			}
			automataList = this.pmAutomataList;
			this.pmAutomataList = null;
			this.pmMapIdState.clear();
			this.pmMapIdState = null;
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				} catch (XMLStreamException xmlStreamException) {
					throw new FsmXmlException(xmlStreamException);
				}
			}  // End if (xmlStreamReader != null)
		}

		return automataList;

	}  // End public List<Automata> read(InputStream inputStream)

	@Override
	public void write(List<Automata> automataList, File fsmXmlFile) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static void main(String args[]) {
		String automataRepositoryPath = "../../vaucanson-1.4a/data/automata/";
		FsmXml fsmXml = new FsmXml();
		try {
			File file = new File(automataRepositoryPath + "char-b/a1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-b/b1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-b/div3base2.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-b/double-3-1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-b/evena.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-b/ladybird-6.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-b/oddb.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-f2/ring-7-0-2-3.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-b/fibred_left.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-b/fibred_right.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-b/quot3base2.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-b/t1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-b/u1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-z/t1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-fmp-z/u1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-q/b1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-q/c1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-q/d1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-r/b1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-r/c1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-r/d1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-z/b1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-z/c1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-z/d1.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-zmax/maxab.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-zmax/maxblocka.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-zmin/minab.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-zmin/minblocka.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "char-zmin/slowgrow.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
			file = new File(automataRepositoryPath + "int-b/coins.xml");
			System.out.println("Reading " + file.getAbsolutePath());
			fsmXml.read(file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}  // End public static void main(String args[])
}  // End public class FsmXml implements FsmXmlInterface

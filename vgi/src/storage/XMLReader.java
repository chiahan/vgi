package storage;

import java.awt.geom.Point2D;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.Automata2;
import model.Drawing2;
import model.Geometry2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * this class provides the function of reading an FSM XML file and converting it into data objects used by VGI;
 * to use this class, first construct a class object and call function parseXML(),
 * then use the various getter function to extract the appropriate data objects;
 * contains various public getter functions and private parser functions
 * code referenced from http://www.totheriver.com/learn/xml/xmltutorial.html
 * @author polar
 */
public class XMLReader {

    /** for debugging */
	private static final boolean DEBUG = true;

    /** file which stores XML data */
	private Document dom;

    /** private member used for temporary storage of automaton data */
	private Automata2 automata;

    /** private member used for temporary storage of automaton data */
	private Geometry2 geometry;

    /** private member used for temporary storage of automaton data */
	private Drawing2 drawing;

    /** private member used for temporary storage of automaton data */
	private Automata2.FreeMonoid.GenKindE generatorKind = Automata2.FreeMonoid.GenKindE.SIMPLE;

    /** private member used for temporary storage of automaton data */
	private Automata2.Semiring.TypeE semiringType = Automata2.Semiring.TypeE.NUMERICAL;

    /** private member used for temporary storage of automaton data */
	private Automata2.Monoid.TypeE monoidType = Automata2.Monoid.TypeE.FREE;

    /** private member used for temporary storage of automaton data */
	private Hashtable<Integer, Point2D.Float> StatePositions = new Hashtable();

    /** private member used for some condition checking */
	private boolean containsGeoInfo = false;
    
    /** private member used for some condition checking */
	boolean isProductMonoid = false;

    /**
     * constructor
     */
	public XMLReader() {
		automata = new Automata2();
		geometry = new Geometry2();
		drawing = new Drawing2();
	}

    /**
     * main function which provides class utility;
     * extract data objects through public getter functions
     * @param address path of XML file
     */
	public void parseXML(String address) {
		//** read xml file
		loadXML(address);
		//** do the parsing
		parseDocument();
	}

    /**
     * public function used to return respective data types
     * @return automata object
     */
	public Automata2 getAutomata() {
		return automata;
	}

    /**
     * public function used to return respective data types
     * @return drawing object
     */
	public Drawing2 getDrawing() {
		return drawing;
	}

    /**
     * public function used to return respective data types
     * @return geometry
     */
	public Geometry2 getGeometry() {
		return geometry;
	}

    /**
     * public function used to return respective data types
     * @return hashtable that maps integer state id to a 2d position
     */
	public Hashtable<Integer, Point2D.Float> getStatePositionH() {
		return StatePositions;
	}

    /**
     * public functions which tells whether xml files contains geometric data or not
     * @return
     */
	public boolean containsGeoInfo() {
		return containsGeoInfo;
	}

    /**
     * prepares XML file for processing
     * @param address file path
     */
	private void loadXML(String address) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(address);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

    /** parse DOM object into appropriate data members */
	private void parseDocument() {

		String ans = removeExtraBracket("((aaa)*)·(b)");
		Element docEle = dom.getDocumentElement();

		//** ValueType
		Automata2.ValueType valueType = new Automata2.ValueType();
		NodeList nlValueType = docEle.getElementsByTagName("valueType");
		if (nlValueType != null && nlValueType.getLength() > 0) {
			Element el = (Element) nlValueType.item(0);
			valueType = parseValueType(el);
		}

		//** States
		Automata2.States stateV = new Automata2.States();
		Geometry2.States stateGeoV = new Geometry2.States();
		Drawing2.States stateDrawV = new Drawing2.States(); //TODO
		NodeList nlState = docEle.getElementsByTagName("state");
		if (nlState != null && nlState.getLength() > 0) {
			for (int i = 0; i < nlState.getLength(); i++) {
				Element ele = (Element) nlState.item(i);

				Automata2.State state = parseState(ele);
				stateV.addState(state);

				Geometry2.State stateGeo = parseStateGeometricData(ele);
				stateGeoV.getStateH().put(stateGeo.getId(), stateGeo);
			}
		}

		//** Transitions
		Automata2.Transitions transV = new Automata2.Transitions();
		Geometry2.Transitions transGeoV = new Geometry2.Transitions();
		Drawing2.Transitions transDrawV = new Drawing2.Transitions(); //TODO
		NodeList nlTrans = docEle.getElementsByTagName("transition");
		if (nlTrans != null && nlTrans.getLength() > 0) {
			for (int i = 0; i < nlTrans.getLength(); i++) {
				Element ele = (Element) nlTrans.item(i);
				String id = Integer.toString(i);
				Automata2.Transition trans = parseTransition(id, ele);
				transV.getTransitionH().put(id, trans);
			}
		}

		NodeList nlInitial = docEle.getElementsByTagName("initial");
		if (nlInitial != null && nlInitial.getLength() > 0) {
			for (int i = 0; i < nlInitial.getLength(); i++) {
				Element ele = (Element) nlInitial.item(i);
				Automata2.Initial initial = parseInitial(ele);
				String id = initial.getStateId();
				transV.getInitialH().put(id, initial);
			}
		}

		NodeList nlFinal = docEle.getElementsByTagName("final");
		if (nlFinal != null && nlFinal.getLength() > 0) {
			for (int i = 0; i < nlFinal.getLength(); i++) {
				Element ele = (Element) nlFinal.item(i);
				Automata2.Final final_ = parseFinal(ele);
				String id = final_.getStateId();
				transV.getFinalH().put(id, final_);
			}
		}

		//** xml header
		String xmlns = "";
		float version = 1;
		NodeList nlFsmxml = docEle.getElementsByTagName("fsmxml");
		if (nlFsmxml != null && nlFsmxml.getLength() > 0) {
			Element fsmxml = (Element) nlFsmxml.item(0);
			xmlns = docEle.getAttribute("xmlns");
			version = Float.valueOf(fsmxml.getAttribute("version"));
		}

		//** construct dataModel from class constituents
		String name = "automaton1";
		Automata2.Automaton.ReadingDirE readDir = Automata2.Automaton.ReadingDirE.LEFT;
		NodeList nlAutomaton = docEle.getElementsByTagName("automaton");
		if (nlAutomaton != null && nlAutomaton.getLength() > 0) {
			Element automaton = (Element) nlAutomaton.item(0);
			name = automaton.getAttribute("name");
			//readDir = Automata2.Automaton.ReadingDirE.LEFT;
		}

		Automata2.AutomatonStruct autoStruct = new Automata2.AutomatonStruct(stateV, transV);
		Automata2.Automaton automatonObj = new Automata2.Automaton(name, readDir, valueType, autoStruct);
		Vector<Automata2.Automaton> automatonV = new Vector<Automata2.Automaton>();
		automatonV.add(automatonObj);

		Automata2.Fsmxml fsmxmlObj = new Automata2.Fsmxml();
		fsmxmlObj = new Automata2.Fsmxml(xmlns, version, automatonV);
		automata = new Automata2(fsmxmlObj);

		Geometry2.AutomatonStruct autoStructGeo = new Geometry2.AutomatonStruct(stateGeoV, transGeoV);
		Geometry2.Automaton autoGeo = new Geometry2.Automaton(autoStructGeo);
		Vector<Geometry2.Automaton> autoGeoV = new Vector<Geometry2.Automaton>();
		autoGeoV.add(autoGeo);
		Geometry2.Fsmxml fsmxmlGeo = new Geometry2.Fsmxml(autoGeoV);
		geometry = new Geometry2(fsmxmlGeo);
	}

    /** private parser function */
	private Automata2.ValueType parseValueType(Element ele) {

		//semiring
		Object semiring = new Object();
		try {
			NodeList nlSem = ele.getElementsByTagName("semiring");
			if (nlSem != null && nlSem.getLength() > 0) {
				Element eleSem = (Element) nlSem.item(0);
				semiring = parseSemiring(eleSem);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("err: no semiring data found, default value inserted");
			semiring = new Automata2.Semiring();
		}

		//monoid
		Object monoid = new Object();
		try {
			NodeList nlMon = ele.getElementsByTagName("monoid");
			if (nlMon != null && nlMon.getLength() > 0) {
				Element eleMon = (Element) nlMon.item(0);
				monoid = parseMonoid(eleMon);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("err: no monoid data found");
			monoid = new Automata2.Monoid();
		}

		Automata2.ValueType valueType = new Automata2.ValueType();
		switch (semiringType) {
			case NUMERICAL:
				switch (monoidType) {
					case FREE:
						valueType = new Automata2.ValueType((Automata2.NumericalSemiring) semiring,
										(Automata2.FreeMonoid) monoid);
						break;
					case PRODUCT:
						valueType = new Automata2.ValueType((Automata2.NumericalSemiring) semiring,
										(Automata2.ProductMonoid) monoid);
						break;
					case UNIT:
						valueType = new Automata2.ValueType((Automata2.NumericalSemiring) semiring,
										(Automata2.UnitMonoid) monoid);
						break;
					default:
						System.out.println("unresolved monoid type for numerical numerical found");
						break;
				}
				break;
			case SERIES:
				switch (monoidType) {
					case FREE:
						valueType = new Automata2.ValueType((Automata2.SeriesSemiring) semiring,
										(Automata2.FreeMonoid) monoid);
						break;
					case PRODUCT:
						valueType = new Automata2.ValueType((Automata2.SeriesSemiring) semiring,
										(Automata2.ProductMonoid) monoid);
						break;
					case UNIT:
						valueType = new Automata2.ValueType((Automata2.SeriesSemiring) semiring,
										(Automata2.UnitMonoid) monoid);
						break;
					default:
						System.out.println("unresolved monoid type for numerical series found");
						break;
				}
				break;
			default:
				System.out.println("unresolved semiring type found");
				break;
		}
		return valueType;
	}

    /** private parser function */
	private Object parseSemiring(Element ele) {
		Object semiring = new Object();
		semiring = new Automata2.Semiring();
		String type = ele.getAttribute("type");

		if (type.equalsIgnoreCase("numerical")) {
			semiringType = Automata2.Semiring.TypeE.NUMERICAL;
			String set = ele.getAttribute("set");
			String operations = ele.getAttribute("operations");
			Automata2.NumericalSemiring.SetE setObj = Automata2.NumericalSemiring.SetE.B;
			Automata2.NumericalSemiring.OperationE oprObj = Automata2.NumericalSemiring.OperationE.CLASSICAL;

			if (set.equalsIgnoreCase("B")) {
				setObj = Automata2.NumericalSemiring.SetE.B;
			} else if (set.equalsIgnoreCase("N")) {
				setObj = Automata2.NumericalSemiring.SetE.N;
			} else if (set.equalsIgnoreCase("Z")) {
				setObj = Automata2.NumericalSemiring.SetE.Z;
			} else if (set.equalsIgnoreCase("Q")) {
				setObj = Automata2.NumericalSemiring.SetE.Q;
				System.out.println("err: unable to handle semiring with set = Q");
			} else if (set.equalsIgnoreCase("R")) {
				setObj = Automata2.NumericalSemiring.SetE.R;
				System.out.println("err: unable to handle semiring with set = R");
			} else if (set.equalsIgnoreCase("C")) {
				setObj = Automata2.NumericalSemiring.SetE.C;
				System.out.println("err: unable to handle semiring with set = C");
			} else {
				System.out.println("error parsing set of numerical semiring");
			}

			if (operations.equalsIgnoreCase("classical")) {
				oprObj = Automata2.NumericalSemiring.OperationE.CLASSICAL;
			} else if (operations.equalsIgnoreCase("minPlus")) {
				oprObj = Automata2.NumericalSemiring.OperationE.MINPLUS;
			} else if (operations.equalsIgnoreCase("maxPlus")) {
				oprObj = Automata2.NumericalSemiring.OperationE.MAXPLUS;
			} else {
				System.out.println("error parsing operation of numerical semiring");
			}

			semiring = new Automata2.NumericalSemiring(setObj, oprObj);

		} else if (type.equalsIgnoreCase("series")) {
			semiringType = Automata2.Semiring.TypeE.SERIES;
			Automata2.Semiring nestedSem = new Automata2.Semiring();
			Automata2.Monoid nestedMon = new Automata2.Monoid();

			NodeList nlNestedSem = ele.getElementsByTagName("semiring");
			if (nlNestedSem != null && nlNestedSem.getLength() > 0) {
				Element eleNestedSem = (Element) nlNestedSem.item(0);
				nestedSem = (Automata2.Semiring) parseSemiring(eleNestedSem);
			}

			NodeList nlNestedMon = ele.getElementsByTagName("monoid");
			if (nlNestedMon != null && nlNestedMon.getLength() > 1) {
				Element eleNestedMon = (Element) nlNestedMon.item(1);
				nestedMon = (Automata2.Monoid) parseMonoid(eleNestedMon);
			}

			semiring = new Automata2.SeriesSemiring(nestedSem, nestedMon);

		} else {
			System.out.println("error parsing type of semiring");
		}

		return semiring;
	}

    /** private parser function */
	private Object parseMonoid(Element ele) {
		Object monoid = new Object();
		monoid = new Automata2.Monoid();
		String type = ele.getAttribute("type");

		if (type.equalsIgnoreCase("unit")) {
			monoidType = Automata2.Monoid.TypeE.UNIT;
			monoid = new Automata2.UnitMonoid();
		} else if (type.equalsIgnoreCase("product")) {
			int prodDim = 2;
			try {
				prodDim = Integer.parseInt(ele.getAttribute("prodDim"));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("error parsing prodDim, default value 2 assigned");
				prodDim = 2;
			}
			Vector<Automata2.FreeMonoid> freeMonoidV = new Vector<Automata2.FreeMonoid>();

			NodeList nlNestedMon = ele.getElementsByTagName("monoid");
			if (nlNestedMon != null && nlNestedMon.getLength() > 0) {
				for (int i = 0; i < nlNestedMon.getLength(); i++) {
					Element tempMon = (Element) nlNestedMon.item(i);
					Automata2.FreeMonoid tempMonObj = (Automata2.FreeMonoid) parseMonoid(tempMon);
					freeMonoidV.add(tempMonObj);
				}
			}
			monoid = new Automata2.ProductMonoid(prodDim, freeMonoidV);
			monoidType = Automata2.Monoid.TypeE.PRODUCT;
			isProductMonoid = true;
		} else if (type.equalsIgnoreCase("free")) {
			monoidType = Automata2.Monoid.TypeE.FREE;
			String genKind = ele.getAttribute("genKind");
			String genDescript = ele.getAttribute("genDescrip");
			Automata2.FreeMonoid.GenKindE genKindObj = Automata2.FreeMonoid.GenKindE.SIMPLE;
			Automata2.FreeMonoid.GenDescriptE genDescObj = Automata2.FreeMonoid.GenDescriptE.ENUM;
			Automata2.FreeMonoid.GenSortE genSortObj = Automata2.FreeMonoid.GenSortE.LETTERS;
			Vector<Automata2.MonGen> monGenV = new Vector<Automata2.MonGen>();
			int genDim = 2;

			if (genDescript.equalsIgnoreCase("enum")) {
				genDescObj = Automata2.FreeMonoid.GenDescriptE.ENUM;
			} else if (genDescript.equalsIgnoreCase("range")) {
				genDescObj = Automata2.FreeMonoid.GenDescriptE.RANGE;
			} else if (genDescript.equalsIgnoreCase("set")) {
				genDescObj = Automata2.FreeMonoid.GenDescriptE.SET;
			} else {
				System.out.println("error parsing genDescript");
			}

			if (genKind.equalsIgnoreCase("simple")) {
				genKindObj = Automata2.FreeMonoid.GenKindE.SIMPLE;
				generatorKind = Automata2.FreeMonoid.GenKindE.SIMPLE;

				String genSort = ele.getAttribute("genSort");
				if (genSort.equalsIgnoreCase("letters")) {
					genSortObj = Automata2.FreeMonoid.GenSortE.LETTERS;
				} else if (genSort.equalsIgnoreCase("digits")) {
					genSortObj = Automata2.FreeMonoid.GenSortE.DIGITS;
				} else if (genSort.equalsIgnoreCase("integer")) {
					genSortObj = Automata2.FreeMonoid.GenSortE.INTEGER;
				} else if (genSort.equalsIgnoreCase("alphanum")) {
					genSortObj = Automata2.FreeMonoid.GenSortE.ALPHANUM;
				} else {
					System.out.println("error parsing genSort for free monoid");
				}

				NodeList nlNestedMonGen = ele.getElementsByTagName("monGen");
				for (int i = 0; i < nlNestedMonGen.getLength(); i++) {
					Element monGen = (Element) nlNestedMonGen.item(i);
					String value = monGen.getAttribute("value");
					Automata2.MonGen monGenObj = new Automata2.MonGen(value);
					monGenV.add(monGenObj);
				}

				monoid = new Automata2.FreeMonoid(genDescObj, genSortObj, monGenV);

			} else if (genKind.equalsIgnoreCase("tuple")) {
				genKindObj = Automata2.FreeMonoid.GenKindE.TUPLE;
				generatorKind = Automata2.FreeMonoid.GenKindE.TUPLE;
				try {
					genDim = Integer.parseInt(ele.getAttribute("genDim"));
					if (genDim != 2) {
						System.out.println("genDim other than 2 is not supported, default value 2 assigned");
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("error parsing genDim, default value 2 assigned");
					genDim = 2;
				}
				Vector<Automata2.GenCompSort> genCompSortV = new Vector<Automata2.GenCompSort>();

				Automata2.GenSort nestedGenSortObj = new Automata2.GenSort();
				try {
					NodeList nlNestedGenSort = ele.getElementsByTagName("genSort");
					Element nestedGenSort = (Element) nlNestedGenSort.item(0);
					NodeList nlGenCompSort = nestedGenSort.getElementsByTagName("genCompSort");
					for (int k = 0; k < 2; k++) {
						Element genCompSort = (Element) nlGenCompSort.item(k);
						String stringValue = genCompSort.getAttribute("value");
						Automata2.GenCompSort.ValueE value = Automata2.GenCompSort.ValueE.LETTERS;
						if (stringValue.equalsIgnoreCase("letters")) {
							value = Automata2.GenCompSort.ValueE.LETTERS;
						} else if (stringValue.equalsIgnoreCase("digits")) {
							value = Automata2.GenCompSort.ValueE.DIGITS;
						} else if (stringValue.equalsIgnoreCase("integer")) {
							value = Automata2.GenCompSort.ValueE.INTEGER;
						} else {
							System.out.println("error parsing genCompSort");
						}
						Automata2.GenCompSort tempGenCompSort = new Automata2.GenCompSort(value);
						genCompSortV.add(tempGenCompSort);
					}
					nestedGenSortObj = new Automata2.GenSort(genCompSortV);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("error parsing genSort from xml, default value assigned");
					Automata2.GenCompSort tempGenCompSort1 = new Automata2.GenCompSort(Automata2.GenCompSort.ValueE.LETTERS);
					Automata2.GenCompSort tempGenCompSort2 = new Automata2.GenCompSort(Automata2.GenCompSort.ValueE.LETTERS);
					genCompSortV.add(tempGenCompSort1);
					genCompSortV.add(tempGenCompSort2);
					nestedGenSortObj = new Automata2.GenSort(genCompSortV);
				}

				NodeList nlNestedMonGen = ele.getElementsByTagName("monGen");
				if (nlNestedMonGen != null && nlNestedMonGen.getLength() > 0) {
					for (int i = 0; i < nlNestedMonGen.getLength(); i++) {
						Element monGen = (Element) nlNestedMonGen.item(i);
						NodeList nlMonCompGen = monGen.getElementsByTagName("monCompGen");
						Vector<Automata2.MonCompGen> monCompGenV = new Vector<Automata2.MonCompGen>();
						for (int k = 0; k < genDim; k++) {
							Element monCompGen = (Element) nlMonCompGen.item(k);
							String value = monCompGen.getAttribute("value");
							Automata2.MonCompGen monCompGenObj = new Automata2.MonCompGen(value);
							monCompGenV.add(monCompGenObj);
						}
						Automata2.MonGen monGenObj = new Automata2.MonGen(monCompGenV);
						monGenV.add(monGenObj);
					}
				}
				monoid = new Automata2.FreeMonoid(genDescObj, genDim, nestedGenSortObj, monGenV);
				//end case genKind = tuple
			} else {
				System.out.println("error parsing genKind for free monoid");
			}
		} else {
			System.out.println("unable to resolve type of monoid");
		}
		return monoid;
	}

    /** private parser function */
	private Automata2.State parseState(Element ele) {
		String id = ele.getAttribute("id");
		String name = "";
		if (ele.hasAttribute("name")) {
			name = ele.getAttribute("name");
			//check if ID is in the form of "sXXX"
		} else if (id.toLowerCase().startsWith("s")) {
			name = id;
			id = id.toLowerCase().replace("s", "");
		}
		Automata2.State state = new Automata2.State(id, name);
		return state;
	}

    /** private parser function */
	private Geometry2.State parseStateGeometricData(Element ele) {
		String id = ele.getAttribute("id");
		//check if ID is in the form of "sXXX"
		if (id.toLowerCase().startsWith("s")) {
			id = id.toLowerCase().replace("s", "");
		}

		Geometry2.StatesGeometricData geoData = new Geometry2.StatesGeometricData();

		NodeList nlGeo = ele.getElementsByTagName("geometricData");
		if (nlGeo != null && nlGeo.getLength() > 0) {
			containsGeoInfo = true;
			Element eleGeo = (Element) nlGeo.item(0);
			float x = Float.valueOf(eleGeo.getAttribute("x"));
			float y = Float.valueOf(eleGeo.getAttribute("y"));
			geoData = new Geometry2.StatesGeometricData(x, y);
			StatePositions.put(Integer.parseInt(id), new Point2D.Float(x, y));
		}
		Geometry2.State state = new Geometry2.State(id, geoData);
		return state;
	}

    /** private parser function */
	private Drawing2.State parseStateDrawingData(Element ele) {
		//TODO
		return null;
	}

    /** private parser function */
	private String parseMonElmtValue(Automata2.RegExpBody exp) {
		Automata2.FreeMonElmt freeMonElmt = (Automata2.FreeMonElmt) exp;
		Vector<Automata2.MonGen> monGenV = freeMonElmt.getMonGenV();
		String value = "";
		for (int i = 0; i < monGenV.size(); i++) {
			switch (generatorKind) {
				case SIMPLE:
					value = value + monGenV.get(i).getValue();
					break;
				case TUPLE:
					try {
						Vector<Automata2.MonCompGen> monCompGenV = monGenV.get(i).getMonCompGenV();
						value = value + "[";
						for (int k = 0; k < 2; k++) {
							Automata2.MonCompGen monCompGen = monCompGenV.get(k);
							value = value + monCompGen.getValue();
							if (k == 0) {
								value = value + ", ";
							}
						}
						value = value + "]";
					} catch (Exception e) {
						System.out.println(e.getMessage());
						System.out.println("error parsing monCompGen");
					}
					break;
				default:
					System.out.println("unable to resolve genKind");
					break;
			}
		}
		return value;
	}

    /** private parser function */
	private String parseTransitionLabelValue(Automata2.RegExpBody exp) {
		String value = "";
		switch (exp.getDataType()) {
			case MON_ELMT:
				switch (monoidType) {
					case FREE:
						value = parseMonElmtValue(exp);
						break;
					case PRODUCT:
						Automata2.ProductMonElmt prodMonElmt = (Automata2.ProductMonElmt) exp;
						Vector<Automata2.RegExpBody> prodMonElmtChild = prodMonElmt.getMonElmtOrOneV();
						value = "(";
						for (int i = 0; i < prodMonElmtChild.size(); i++) {
							switch (prodMonElmtChild.get(i).getDataType()) {
								case ONE:
									value = value + automata.getValueType().getOneSymbol();
									break;
								case MON_ELMT:
									value = value + parseMonElmtValue(prodMonElmtChild.get(i));
									break;
								default:
									System.out.println("invalid oneOrMonElmt found");
									break;
							}
							if (i != prodMonElmtChild.size() - 1) {
								value = value + ", ";
							}
						}
						value = value + ")";
						break;
					case UNIT:
						System.out.println("unit monoid is not supported");
						break;
					default:
						System.out.println("unresolved monoid type found");
						break;
				}
				break;
			case LEFT_EXT_MUL:
				Automata2.LeftExtMul leftExtMul = (Automata2.LeftExtMul) exp;
				Automata2.NumericalSemiringWeight leftWeight =
								(Automata2.NumericalSemiringWeight) leftExtMul.getWeight();
				value = "{" + leftWeight.getValue() + "}" +
								parseTransitionLabelValue(leftExtMul.getRegExpBody());
				break;
			case RIGHT_EXT_MUL:
				Automata2.RightExtMul rightExtMul = (Automata2.RightExtMul) exp;
				Automata2.NumericalSemiringWeight rightWeight =
								(Automata2.NumericalSemiringWeight) rightExtMul.getWeight();
				value = parseTransitionLabelValue(rightExtMul.getRegExpBody()) +
								"{" + rightWeight.getValue() + "}";
				break;
			case ONE:
				value = automata.getValueType().getOneSymbol();
				break;
			case ZERO:
				value = automata.getValueType().getZeroSymbol();
				break;
			case PRODUCT:
				Automata2.Product product = (Automata2.Product) exp;
				value = "(" + parseTransitionLabelValue(product.getRegExpBody1()) + ")·(" +
								parseTransitionLabelValue(product.getRegExpBody2()) + ")";
				break;
			case SUM:
				Automata2.Sum sum = (Automata2.Sum) exp;
				value = "(" + parseTransitionLabelValue(sum.getRegExpBody1()) + ")+(" +
								parseTransitionLabelValue(sum.getRegExpBody2()) + ")";
				break;
			case STAR:
				Automata2.Star star = (Automata2.Star) exp;
				value = "(" + parseTransitionLabelValue(star.getRegExpBody()) + ")*";
				break;
			default:
				System.out.println("unable to resolve regExpBody type");
				break;
		}
		return value;
	}

    /** private parser function */
	private Automata2.Transition parseTransition(String id, Element ele) {
        String source = "";
        if (ele.hasAttribute("src")) {
            source = ele.getAttribute("src").toLowerCase().replace("s", "");
        } else if (ele.hasAttribute("source")) {
            source = ele.getAttribute("source").toLowerCase().replace("s", "");
        } else {
            System.out.println("input XML file does not have valid source attribute for transition elements");
        }
		String target = ele.getAttribute("target").toLowerCase().replace("s", "");

		if (DEBUG) {
			System.out.println("transition id: " + id + " :: " + source + " -> " + target);
		}

		Automata2.Label labelObj = new Automata2.Label("");
		try {
			NodeList nl = ele.getElementsByTagName("label");
			Element labelEle = (Element) nl.item(0);
			nl = labelEle.getElementsByTagName("*");
			Element labelEleChild = (Element) nl.item(0);
			Automata2.RegExpBody regExpBody = parseRegExpBody(labelEleChild);
			Automata2.TypedRegExp typeRegExp = new Automata2.TypedRegExp(regExpBody);
			String value = parseTransitionLabelValue(regExpBody);
			value = removeExtraBracket(value);
			if (isProductMonoid) {
				value = "(" + value + ")";
			}
			labelObj = new Automata2.Label(typeRegExp, value);
//			labelObj = new Automata2.Label(value);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error parsing transition label");
		}
		Automata2.Transition trans = new Automata2.Transition(id, source, target, labelObj);
		return trans;
	} // end parseTransition

    /** private parser function */
	private Geometry2.Transition parseTransitionGeometricData(Element ele) {
		//TODO
		return null;
	}

    /** private parser function */
	private Automata2.Initial parseInitial(Element ele) {
		String stateID = ele.getAttribute("state").toLowerCase().replace("s", "");
		Automata2.Initial initial = new Automata2.Initial(stateID);
		return initial;
	}

    /** private parser function */
	private Automata2.Final parseFinal(Element ele) {
		String stateID = ele.getAttribute("state").toLowerCase().replace("s", "");
		Automata2.Final final_ = new Automata2.Final(stateID);
		return final_;
	}

    /** private parser function */
	private Automata2.RegExpBody parseRegExpBody(Element ele) {
		String name = ele.getNodeName();
		Automata2.RegExpBody regExpBody = null;
		try {
			if (name.equalsIgnoreCase("sum")) {
				Node one = ele.getFirstChild().getNextSibling();
				if (DEBUG) {
					String nameOne = one.getNodeName();
				}
				Node two = one.getNextSibling().getNextSibling();
				if (DEBUG) {
					String nameTwo = two.getNodeName();
				}
				regExpBody = new Automata2.Sum(
								parseRegExpBody((Element) one),
								parseRegExpBody((Element) two));
			} else if (name.equalsIgnoreCase("product")) {
				Node one = ele.getFirstChild().getNextSibling();
				if (DEBUG) {
					String nameOne = one.getNodeName();
				}
				Node two = one.getNextSibling().getNextSibling();
				if (DEBUG) {
					String nameTwo = two.getNodeName();
				}
				regExpBody = new Automata2.Product(
								parseRegExpBody((Element) one),
								parseRegExpBody((Element) two));
			} else if (name.equalsIgnoreCase("star")) {
				Node one = ele.getFirstChild().getNextSibling();
				if (DEBUG) {
					String nameOne = one.getNodeName();
				}
				regExpBody = new Automata2.Star(
								parseRegExpBody((Element) one));
			} else if (name.equalsIgnoreCase("leftExtMul")) {
				Node one = ele.getFirstChild().getNextSibling();
				if (DEBUG) {
					String nameOne = one.getNodeName();
				}
				Node two = one.getNextSibling().getNextSibling();
				if (DEBUG) {
					String nameTwo = two.getNodeName();
				}
				regExpBody = new Automata2.LeftExtMul(
								parseWeight((Element) one),
								parseRegExpBody((Element) two));
			} else if (name.equalsIgnoreCase("rightExtMul")) {
				Node one = ele.getFirstChild().getNextSibling();
				if (DEBUG) {
					String nameOne = one.getNodeName();
				}
				Node two = one.getNextSibling().getNextSibling();
				if (DEBUG) {
					String nameTwo = two.getNodeName();
				}
				regExpBody = new Automata2.RightExtMul(
								parseWeight((Element) one),
								parseRegExpBody((Element) two));
			} else if (name.equalsIgnoreCase("monElmt")) {
				switch (monoidType) {
					case FREE:
						regExpBody = parseMonElmt(ele);
						break;
					case PRODUCT:
						NodeList nl = ele.getElementsByTagName("*");
						Vector<Automata2.RegExpBody> productV = new Vector<Automata2.RegExpBody>();
						for (int i = 0; i < nl.getLength(); i++) {
							Element child = (Element) nl.item(i);
							if (child.getNodeName().equalsIgnoreCase("monElmt")) {
								productV.add(parseMonElmt(child));
							} else if (child.getNodeName().equalsIgnoreCase("monGen")) {
								continue;
							} else if (child.getNodeName().equalsIgnoreCase("one")) {
								productV.add(new Automata2.One());
							} else {
								System.out.println("unable to resolve child of product monElmt");
							}
						}
						regExpBody = new Automata2.ProductMonElmt(productV);
						break;
					default:
						System.out.println("unable to resolve monoid type ");
						break;
				}
			} else if (name.equalsIgnoreCase("one")) {
				regExpBody = new Automata2.One();
			} else {
				System.out.println("unable to determine regExpBody type");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error parsing regExpBody");
		}
		return regExpBody;
	} // end parseRegExpBody

	//TODO seriesSemiringWeight... someday
    /** private parser function */
	private Automata2.NumericalSemiringWeight parseWeight(Element ele) {
		Automata2.NumericalSemiringWeight weight = new Automata2.NumericalSemiringWeight("");
		try {
			String value = ele.getAttribute("value");
			weight.setValue(value);
		} catch (Exception e) {
			System.out.println("unable to parse value of weight");
			System.out.println(e.getMessage());
		}
		return weight;
	}

	//parse monElmt
	//TODO need to parse monCompGen too
    /** private parser function */
	private Automata2.RegExpBody parseMonElmt(Element ele) {
		Automata2.RegExpBody regExpBody = null;
		try {
			Vector<Automata2.MonGen> monGenV = new Vector<Automata2.MonGen>();
			NodeList nl = ele.getElementsByTagName("monGen");
			for (int i = 0; i < nl.getLength(); i++) {
				Element monGen = (Element) nl.item(i);
				if (generatorKind == Automata2.FreeMonoid.GenKindE.SIMPLE) {
					monGenV.add(new Automata2.MonGen(monGen.getAttribute("value")));
				} else if (generatorKind == Automata2.FreeMonoid.GenKindE.TUPLE) {
					Vector<Automata2.MonCompGen> monCompGenV = new Vector<Automata2.MonCompGen>();
					NodeList nlMCG = monGen.getElementsByTagName("monCompGen");
					for (int j = 0; j < nlMCG.getLength(); j++) {
						Element monCompGen = (Element) nlMCG.item(j);
						monCompGenV.add(new Automata2.MonCompGen(monCompGen.getAttribute("value")));
					}
					monGenV.add(new Automata2.MonGen(monCompGenV));
				} else {
					System.out.println("unable to resolve genKind while parsing monElmt");
				}
			}
			regExpBody = new Automata2.FreeMonElmt(monGenV);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error while parsing monElmt");
		}
		return regExpBody;
	}

    /** utility function for data processing */
	private String removeExtraBracket(String inputString) {
		Vector<String> outputV = toStringVector(inputString.replace("()", ""));
		outputV.add(")"); //sentinel
		int i = 0;
		while (i < outputV.size()) {
			if (outputV.get(i).equals("(")) {
				try {
					if (outputV.get(i + 2).equals(")") && !outputV.get(i + 3).equals("*")) {
						outputV.remove(i);	//remove "("
						outputV.remove(i + 1); //remove ")"
					}
					if (outputV.get(i + 1).equals("{")) {
						if (outputV.get(i + 5).equals(")")) {
							outputV.remove(i + 5); //remove ")"
							outputV.remove(i);	//remove "("
						}
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("error trying to remove bracket of the type (a) or ({2}a)");
				}
			}
			i++;
		}
		i = 0;
		while (i < outputV.size()) {
			if (outputV.get(i).equals("·")) {
				try {
					if (outputV.get(i - 1).equals(")") && outputV.get(i - 2).equals("*") && outputV.get(i - 5).equals("(") && outputV.get(i - 6).equals("(")) {
						outputV.remove(i - 1);
						outputV.remove(i - 6);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("error trying to remove bracket of the type ((a)*)·b");
				}
			}
			i++;
		}

		if (!outputV.remove(outputV.size() - 1).equals(")")) {
			System.err.println("sentinel \")\" not detected");
		}

		String value = "";
		for (int k = 0; k < outputV.size(); k++) {
			value = value + outputV.get(k);
		}
		return value;
	}

    /** utility function for data processing */
	private Vector<String> toStringVector(String input) {
//		String output = input.replace(" ", "") + ")";
		String output = input + ")";
		Vector<String> outputV = new Vector();
		int i = 0;
		int j = 0;
		while (i < output.length()) {
			char buf = output.charAt(i);
			if (isOperatorOrBracket(buf)) {
				if (j < i) {
					outputV.add(output.substring(j, i));
				}
				outputV.add(String.valueOf(buf));
				j = i + 1;
			}
			i++;
		}
		outputV.remove(outputV.size() - 1);
		return outputV;
	}

    /** utility function for data processing */
	private boolean isOperatorOrBracket(char input) {
		boolean ans = false;
		switch (input) {
			case '+':
			case '·':
			case '*':
			case '(':
			case ')':
			case '{':
			case '}':
			case '[':
			case ']':
				return true;
			default:
				break;
		}
		return ans;
	}
}

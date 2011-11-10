package storage;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.Automata2;
import model.Automata2.FreeMonoid;
import model.Automata2.GenCompSort;
import model.Automata2.GenSort;
import model.Automata2.MonGen;
import model.Automata2.Monoid;
import model.Automata2.NumericalSemiring;
import model.Automata2.ProductMonoid;
import model.Automata2.Semiring;
import model.DataModelInterface;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * this class provides the function of reading automaton data from
 * dataModelInterface and writing it into an XML file;
 * to use this class, first construct a class object and call function run();
 * contains various private functions which converts automaton objects into its appropriate XML form
 * @author polar
 */
public class XMLCreator {

    /** file which stores XML data */
	private Document dom;

    /** data object which provides the means for automaton manipulation */
	private DataModelInterface model;

    /** private member used for temporary storage of automaton data */
	private Automata2.ValueType valueType;

    /** private member used for temporary storage of automaton data */
	private Vector<Integer> stateIDs = new Vector<Integer>();

    /** private member used for temporary storage of automaton data */
	private Vector<Integer> transitionIDs;

    /** private member used for temporary storage of automaton data */
	private Vector<Integer> initialStateIDs;

    /** private member used for temporary storage of automaton data */
	private Vector<Integer> finalStateIDs;

    /** private member used for temporary storage XML file path */
	private String filename;

    /**
     * constructor
     * @param modelInput DataModelInterface which contains all automaton data and tools
     * @param fileName XML file to be written with automaton data
     */
	public XMLCreator(DataModelInterface modelInput, String fileName) {
		model = modelInput;
		stateIDs = model.getAllStates();
		transitionIDs = model.getAllTransitions();
		finalStateIDs = model.getFinalStates();
		initialStateIDs = model.getInitialStates();

		filename = fileName;
		//Get a DOM object
		createDocument();
	}

    /**
     * reads automaton data from dataModelInterface and write it into an XML file
     * this is the only function to be called by other classes besides constructor
     */
	public void run() {
		int index = Storage.getInstance().getSelectedGraphIndex();
		this.valueType = Storage.getInstance().getController(index).getDataModel().getValueType();
		System.out.println("Started Saving XML ");
		createDOMTree();
		printToFile();
		System.out.println("Generated XML file successfully.");
	}

	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 */
	private void createDocument() {
		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			//get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//create an instance of DOM
			dom = db.newDocument();
		} catch (ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
	}

    /**
     * creates the DOM tree which stores automaton XML data
     */
	private void createDOMTree() {

		// Reference from vaucanson-developer-manual Figure 2.1: The automaton A1
		//create the root element <automaton>
		Element fsmxml = dom.createElement("fsmxml");
		fsmxml.setAttribute("xmlns", "http://vaucanson.lrde.epita.fr");
		fsmxml.setAttribute("version", "1.0");
		dom.appendChild(fsmxml);
		Element automaton = dom.createElement("automaton");
		fsmxml.appendChild(automaton);
		Element valueTypeEle = dom.createElement("valueType");
		automaton.appendChild(valueTypeEle);

		Element semiring = dom.createElement("semiring");
		Semiring semiringData = valueType.getSemiring();
		if (semiringData != null) {
			if (semiringData.getTypeE() == Automata2.Semiring.TypeE.NUMERICAL) {
				NumericalSemiring numericalSemiring = (NumericalSemiring) semiringData;
				//semiring.setAttribute("type", numericalSemiring.getTypeE().toString().toLowerCase());
				semiring.setAttribute("type", "numerical");
				semiring.setAttribute("set", numericalSemiring.getSetE().toString().toUpperCase());
				semiring.setAttribute("operations", "classical");
				//TODO implement other operations some day
			} else if (semiringData.getTypeE() == Automata2.Semiring.TypeE.SERIES) {
				// not supported now
				System.out.println("error: parsing semiring type other than numerical");
			}
		}
		valueTypeEle.appendChild(semiring);

		Element monoid = dom.createElement("monoid");
		Monoid monoidData = valueType.getMonoid();
		Monoid.TypeE monoidType = monoidData.getTypeE();
		FreeMonoid freeMonoidData = null;
		try {
			switch (monoidType) {
				case FREE:
					freeMonoidData = (FreeMonoid) monoidData;
					monoid = createFreeMonoid(freeMonoidData);
					break;
				case PRODUCT:
					ProductMonoid productMonoidData = (ProductMonoid) valueType.getMonoid();
					monoid.setAttribute("type", "product");
					monoid.setAttribute("prodDim", Integer.toString(productMonoidData.getProdDim()));
					for (Enumeration it = productMonoidData.getFreeMonoidV().elements(); it.hasMoreElements();) {
						freeMonoidData = (FreeMonoid) it.nextElement();
						Element freeMonoid = dom.createElement("monoid");
						freeMonoid = createFreeMonoid(freeMonoidData);
						monoid.appendChild(freeMonoid);
					}
					break;
				case UNIT:
					System.out.println("unit monoid not supported");
					break;
				default:
					System.out.println("unable to resolve monoid type");
					break;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("error while creating monoid attributes");
		}
		valueTypeEle.appendChild(monoid);

		Element automatonStruct = dom.createElement("automatonStruct");
		automaton.appendChild(automatonStruct);

		Element states = dom.createElement("states");
		automatonStruct.appendChild(states);

		Element transitions = dom.createElement("transitions");
		automatonStruct.appendChild(transitions);

		for (int i = 0; i < stateIDs.size(); i++) {
			Element state = dom.createElement("state");
			state.setAttribute("id", stateIDs.get(i).toString());
			state.setAttribute("name", model.getStateName(stateIDs.get(i)));
			//add geometric data
			Point2D.Float position = model.getStatePosition(stateIDs.get(i));
			Element geo = dom.createElement("geometricData");
			geo.setAttribute("x", String.valueOf(position.getX()));
			geo.setAttribute("y", String.valueOf(position.getY()));
			state.appendChild(geo);
			states.appendChild(state);
		}

		for (int i = 0; i < transitionIDs.size(); i++) {
			Element transition = createTransitionElement(transitionIDs.get(i));
			transitions.appendChild(transition);
		}

		for (int i = 0; i < initialStateIDs.size(); i++) {
			Element initial = dom.createElement("initial");
			initial.setAttribute("state", initialStateIDs.get(i).toString());
			transitions.appendChild(initial);
		}

		for (int i = 0; i < finalStateIDs.size(); i++) {
			Element _final = dom.createElement("final");
			_final.setAttribute("state", finalStateIDs.get(i).toString());
			transitions.appendChild(_final);
		}

		//TODO
		//initial
		//final

	}

    /**
     * converts automaton objects into its appropriate XML form
     * @param freeMonoidData
     * @return
     */
	private Element createFreeMonoid(FreeMonoid freeMonoidData) {
		Element monoidEle = dom.createElement("monoid");
		monoidEle.setAttribute("type", "free");
		monoidEle.setAttribute("genKind", freeMonoidData.getGenKindE().toString().toLowerCase());
		monoidEle.setAttribute("genDescrip", "enum");
		switch (freeMonoidData.getGenKindE()) {
			case SIMPLE:
				if (freeMonoidData.getGenSortE() != null) {
					monoidEle.setAttribute("genSort", freeMonoidData.getGenSortE().toString().toLowerCase());
				} else {
					System.out.println("err: no genSort type found");
					monoidEle.setAttribute("genSort", "letters");
				}
				this.createMonGen(monoidEle, freeMonoidData.getMonGenV(), Automata2.FreeMonoid.GenKindE.SIMPLE);
				break;
			case TUPLE:
				monoidEle.setAttribute("genDim", Integer.toString(freeMonoidData.getGenDim()));
				this.createGenSort(monoidEle, freeMonoidData.getGenSort());
				this.createMonGen(monoidEle, freeMonoidData.getMonGenV(), Automata2.FreeMonoid.GenKindE.TUPLE);
				break;
			default:
				System.out.println("unable to resolve genKind");
				break;
		}
		return monoidEle;
	}

    /**
     * converts automaton objects into its appropriate XML form
     * @param freeMonoidData
     * @return
     */
	private void createGenSort(Element parent, GenSort genSortData) {
		Element genSort = dom.createElement("genSort");
		for (Enumeration e = genSortData.getGenCompSortV().elements(); e.hasMoreElements();) {
			GenCompSort genCompSortData = (GenCompSort) e.nextElement();
			Element genCompSort = dom.createElement("genCompSort");
			if (genCompSortData.getValueE() != null) {
				genCompSort.setAttribute("value", genCompSortData.getValueE().toString().toLowerCase());
			} else {
				System.out.println("err: value not found");
				genCompSort.setAttribute("value", "letters");
			}
			genSort.appendChild(genCompSort);
		}
		parent.appendChild(genSort);
	}

    /**
     * converts automaton objects into its appropriate XML form
     * @param freeMonoidData
     * @return
     */
	private void createMonGen(Element parent, Vector<MonGen> monGenList,
					Automata2.FreeMonoid.GenKindE genKind) {
		for (Enumeration e = monGenList.elements(); e.hasMoreElements();) {
			MonGen monGenData = (MonGen) e.nextElement();
			Element monGen = dom.createElement("monGen");
			switch (genKind) {
				case SIMPLE:
					monGen.setAttribute("value", monGenData.getValue());
					break;
				case TUPLE:
					Vector<Automata2.MonCompGen> monCompGenV = monGenData.getMonCompGenV();
					for (int i = 0; i < 2; i++) {
						String value = monCompGenV.get(i).getValue();
						Element eleMonCompGen = dom.createElement("monCompGen");
						eleMonCompGen.setAttribute("value", value);
						monGen.appendChild(eleMonCompGen);
					}
//					this.createMonCompGen(monGen, monGenData.getMonCompGenV());
					break;
				default:
					System.out.println("unable to resolve genKind type");
					break;
			}
			parent.appendChild(monGen);
		}
	}

//	private void createMonCompGen(Element parent, Vector<MonCompGen> monCompGenList) {
//		for (Enumeration e = monCompGenList.elements(); e.hasMoreElements();) {
//			MonCompGen monCompGenData = (MonCompGen) e.nextElement();
//			Element monCompGen = dom.createElement("monCompGen");
//			monCompGen.setAttribute("value", monCompGenData.getValue());
//			parent.appendChild(monCompGen);
//		}
//	}
    
    /**
     * converts automaton objects into its appropriate XML form
     * @param freeMonoidData
     * @return
     */
	private Element createTransitionElement(int transID) {
		Integer sourceIdTemp = model.getTransitionSource(transID);
		Integer targetIdTemp = model.getTransitionTarget(transID);

		Element transition = dom.createElement("transition");
//		transition.setAttribute("src", sourceIdTemp.toString());
		transition.setAttribute("source", sourceIdTemp.toString());
		transition.setAttribute("target", targetIdTemp.toString());

		Element label = dom.createElement("label");
		transition.appendChild(label);
		Automata2.RegExpBody exp = model.getTransitionRegExpBody(transID);
		Element expBody = createRegExpBody(exp);
		label.appendChild(expBody);

		return transition;
	}

     /**
     * converts automaton objects into its appropriate XML form
     * @param freeMonoidData
     * @return
     */
	private Element createRegExpBody(Automata2.RegExpBody exp) {
		Element ele = null;
		switch (exp.getDataType()) {
			case MON_ELMT:
				switch (model.getValueType().getMonoidType()) {
					case FREE:
						ele = createMonoidElement(exp);
						break;
					case PRODUCT:
						ele = dom.createElement("monElmt");
						Automata2.ProductMonElmt prodMonElmt = (Automata2.ProductMonElmt) exp;
						Vector<Automata2.RegExpBody> regExpBodyV = prodMonElmt.getMonElmtOrOneV();
						for (int i = 0; i < regExpBodyV.size(); i++) {
							Element prodMonElmtChild = createMonoidElement(regExpBodyV.get(i));
							ele.appendChild(prodMonElmtChild);
						}
						break;
					case UNIT:
						//TODO
						System.out.println("unit monoid unsupported");
						break;
					default:
						System.out.println("unresolved monoid type found");
				}
				break;
			case ONE:
				ele = dom.createElement("one");
				break;
			case ZERO:
				ele = dom.createElement("zero");
				break;
			case LEFT_EXT_MUL:
				ele = dom.createElement("leftExtMul");
				Automata2.LeftExtMul leftExtMul = (Automata2.LeftExtMul) exp;

				Automata2.NumericalSemiringWeight leftWeight = (Automata2.NumericalSemiringWeight) leftExtMul.getWeight();
				String leftWeightValue = leftWeight.getValue();
				Element leftWEle = dom.createElement("weight");
				leftWEle.setAttribute("value", leftWeightValue);
				ele.appendChild(leftWEle);

				Automata2.RegExpBody leftBody = leftExtMul.getRegExpBody();
				Element leftBodyEle = createRegExpBody(leftBody);
				ele.appendChild(leftBodyEle);
				break;
			case RIGHT_EXT_MUL:
				Automata2.RightExtMul rightExtMul = (Automata2.RightExtMul) exp;

				Automata2.NumericalSemiringWeight rightWObj = (Automata2.NumericalSemiringWeight) rightExtMul.getWeight();
				String rightWValue = rightWObj.getValue();
				Element rightWEle = dom.createElement("weight");
				rightWEle.setAttribute("value", rightWValue);

				Element rightBodyEle = createRegExpBody(rightExtMul.getRegExpBody());

				ele = dom.createElement("rightExtMul");
				ele.appendChild(rightWEle);
				ele.appendChild(rightBodyEle);
				break;
			case PRODUCT:
				Automata2.Product product = (Automata2.Product) exp;
				Element prodFirstArgu = createRegExpBody(product.getRegExpBody1());
				Element prodSecondArgu = createRegExpBody(product.getRegExpBody2());
				ele = dom.createElement("product");
				ele.appendChild(prodFirstArgu);
				ele.appendChild(prodSecondArgu);
				break;
			case SUM:
				Automata2.Sum sum = (Automata2.Sum) exp;
				Element sumFirstArgu = createRegExpBody(sum.getRegExpBody1());
				Element sumSecondArgu = createRegExpBody(sum.getRegExpBody2());
				ele = dom.createElement("sum");
				ele.appendChild(sumFirstArgu);
				ele.appendChild(sumSecondArgu);
				break;
			case STAR:
				Automata2.Star star = (Automata2.Star) exp;
				Element starArg = createRegExpBody(star.getRegExpBody());
				ele = dom.createElement("star");
				ele.appendChild(starArg);
				break;
			default:
				System.out.println("unresolved regExpBody type found");
				break;
		}
		return ele;
	}

    /**
     * converts automaton objects into its appropriate XML form
     * @param freeMonoidData
     * @return
     */
	private Element createMonoidElement(Automata2.RegExpBody exp) {
		Element ele = null;
		switch (exp.getDataType()) {
			case MON_ELMT:
				ele = dom.createElement("monElmt");
				Automata2.FreeMonElmt freeMonElmt = (Automata2.FreeMonElmt) exp;
				Vector<Automata2.MonGen> monGenV = freeMonElmt.getMonGenV();
				for (int i = 0; i < monGenV.size(); i++) {
					Element monGen = dom.createElement("monGen");
					Automata2.FreeMonoid.GenKindE genKindData = FreeMonoid.GenKindE.SIMPLE;
					switch (valueType.getMonoidType()) {
						case FREE:
							Automata2.FreeMonoid freeMonoidData = (Automata2.FreeMonoid) valueType.getMonoid();
							genKindData = freeMonoidData.getGenKindE();
							break;
						case PRODUCT:
							//TODO query for actual genKind type, set to be simple for product monoid for now
							genKindData = FreeMonoid.GenKindE.SIMPLE;
							break;
						case UNIT:
							System.out.println("unit monoid not supported");
							break;
						default:
							System.out.println("unable to resolve monoid type");
							break;
					}
					Automata2.MonGen monGenData = monGenV.get(i);
					switch (genKindData) {
						case SIMPLE:
							monGen.setAttribute("value", monGenData.getValue());
							break;
						case TUPLE:
							Vector<Automata2.MonCompGen> monCompGenV = monGenData.getMonCompGenV();
							for (int k = 0; k < 2; k++) {
								String value = monCompGenV.get(k).getValue();
								Element eleMonCompGen = dom.createElement("monCompGen");
								eleMonCompGen.setAttribute("value", value);
								monGen.appendChild(eleMonCompGen);
							}
							break;
						default:
							System.out.println("unable to resolve genKind");
							break;
					}
					ele.appendChild(monGen);
				}
				break;
			case ONE:
				ele = dom.createElement("one");
				break;
			default:
				System.out.println("unresolved one or monElmt element found");
				break;
		}
		return ele;
	}

    /**
     * write to file the appropriate XML data
     */
	private void printToFile() {

		try {
			//print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			//to generate output to console use this serializer
			//XMLSerializer serializer = new XMLSerializer(System.out, format);


			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = new XMLSerializer(
							new FileOutputStream(new File(filename)), format);

			serializer.serialize(dom);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}


package model;

import debug.Debug;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * refer to Vaucanson FSMXML spec sheet for an overview on the data types used
 * @author mysterious, rasiel
 */
public class Automata2 {

    private Fsmxml fsmxml;

    public Automata2() {
        this.fsmxml = new Fsmxml();
    }

    public Automata2(Fsmxml fsmxml) {
        this.fsmxml = fsmxml;
    }

    public void addState(String stateId, String name) {
        addState(0, stateId, name);
    }

    public void addState(int automatonID, String stateId, String name) {
        State state = new State(stateId, name);
        this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH.put(stateId, state);
    }

    public void deleteState(String stateId) {
        deleteState(0, stateId);

    }

    public void deleteState(int automatonID, String stateId) {
        this.fsmxml.automatonV.get(automatonID).automatonStruct.states.removeState(stateId);
    //Hashtable hashTemp = this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH;
    //hashTemp.remove(hashTemp.get(stateId));
    }

    public Vector<String> getAllStates() {
        return getAllStates(0);
    }

    public Vector<String> getAllStates(int automatonID) {
        Vector<String> statesId = new Vector<String>();
        Hashtable<String, State> states = this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH;

        for (Enumeration e = states.elements(); e.hasMoreElements();) {
            statesId.add(((State) (e.nextElement())).getId());
        }

        return statesId;
    }

    public ValueType getValueType() {
        return getValueType(0);
    }

    public ValueType getValueType(int automatonID) {
        return this.fsmxml.automatonV.get(automatonID).valueType;
    }

    public void setValueType(ValueType valueType) {
        setValueType(0, valueType);
    }

    public void setValueType(int automatonID, ValueType valueType) {
        this.fsmxml.automatonV.get(automatonID).valueType = valueType;
    }

    public void setInitialState(String stateId) {
        setInitialState(0, stateId);
    }

    public void setInitialState(int automatonID, String stateId) {
        Initial initial = new Initial(stateId);
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH.put(stateId, initial);
        System.out.println("");
    }

    public void removeInitialState(String stateId) {
        removeInitialState(0, stateId);
    }

    public void removeInitialState(int automatonID, String stateId) {
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH.remove(stateId);

    }

    public void setAllInitialState(Hashtable<String, Initial> initialH) {
        setAllInitialState(0, initialH);
    }

    public void setAllInitialState(int automatonID, Hashtable<String, Initial> InitialH) {
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH = InitialH;
    }

    public Hashtable<String, Initial> getAllInitialStates() {
        return getAllInitialStates(0);
    }

    public Hashtable<String, Initial> getAllInitialStates(int automatonID) {
        return this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH;
    }

    public void setFinalState(String stateId) {
        setFinalState(0, stateId);
    }

    public void setFinalState(int automatonID, String stateId) {
        Final final_ = new Final(stateId);
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.finalH.put(stateId, final_);
    }

    public void removeFinalState(String stateId) {
        removeFinalState(0, stateId);
    }

    public void removeFinalState(int automatonID, String stateId) {
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.finalH.remove(stateId);
    }

    public void setAllFinalState(Hashtable<String, Final> finalH) {
        setAllFinalState(0, finalH);
    }

    public void setAllFinalState(int automatonID, Hashtable<String, Final> finalH) {
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.finalH = finalH;
    }

    public Hashtable<String, Final> getAllFinalStates() {
        return getAllFinalStates(0);
    }

    public Hashtable<String, Final> getAllFinalStates(int automatonID) {
        return this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.finalH;
    }

    public void changeStateName(String stateID, String newname) {
        changeStateName(0, stateID, newname);
    }

    public void changeStateName(int automatonID, String stateID, String newname) {
        ((State) (this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH.get(stateID))).name = newname;
    }

    public String getStateName(String stateID) {
        return getStateName(0, stateID);
    }

    public String getStateName(int automatonID, String stateID) {
        return ((State) (this.fsmxml.automatonV.get(automatonID).automatonStruct.states.stateH.get(stateID))).name;
    }

    //FIXME:::::
    public void addTransition(String transitionId, String stateId1, String stateId2, String label) {
        Label label_ = new Label(label);
        addTransition(0, transitionId, stateId1, stateId2, label_);
    }

    public void addTransition(String transitionId, String stateId1, String stateId2, Label label) {
        addTransition(0, transitionId, stateId1, stateId2, label);
    }

    public void addTransition(int automatonID, String transitionId, String stateId1, String stateId2, Label label) {
        Transition transition = new Transition(transitionId, stateId1, stateId2, label);
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.addTransition(transition);
    }

    public void deleteTransition(String transitionId) {
        deleteTransition(0, transitionId);
    }

    public void deleteTransition(int automatonID, String transitionId) {
        this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.removeTrasition(transitionId);
    }

    public Vector<String> getAllTransitions() {
        return getAllTransitions(0);
    }

    public Vector<String> getAllTransitions(int automatonID) {
        Vector<String> transitionsId = new Vector<String>();
        Hashtable<String, Transition> transitions = this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.transitionH;

        for (Enumeration e = transitions.elements(); e.hasMoreElements();) {
            transitionsId.add(((Transition) (e.nextElement())).getId());
        }

        return transitionsId;
    }

    public Vector<String> getAllInitialTransitionsStateId() {
        return getAllInitialTransitionsStateId(0);
    }

    public Vector<String> getAllInitialTransitionsStateId(int automatonID) {
        Vector<String> transitionsId = new Vector<String>();
        Hashtable<String, Initial> transitions = this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.initialH;

        for (Enumeration e = transitions.elements(); e.hasMoreElements();) {
            transitionsId.add(((Initial) (e.nextElement())).getStateId());
        }

        return transitionsId;
    }

    public Vector<String> getAllFinalTransitionsStateId() {
        return getAllFinalTransitionsStateId(0);
    }

    public Vector<String> getAllFinalTransitionsStateId(int automatonID) {
        Vector<String> transitionsId = new Vector<String>();
        Hashtable<String, Final> transitions = this.fsmxml.automatonV.get(automatonID).automatonStruct.transitions.finalH;

        for (Enumeration e = transitions.elements(); e.hasMoreElements();) {
            transitionsId.add(((Final) (e.nextElement())).getStateId());
        }

        return transitionsId;
    }

//	public void setTransitionLabel(String transitionID, Label label, String text) {
//		sds
//	}

    //Fix ME:::::::::
    public void changeTransitionLabel(String transitionId, String label) {
        Label label_ = new Label(label);
        changeTransitionLabel(0, transitionId, label_);
    }

    public void changeTransitionLabel(String transitionID, String value, String weight) {
//		Label label = new Label(weight, value);
//		changeTransitionLabel(0, transitionID, label);
    }

    public void changeTransitionLabel(String transitionId, Label label) {
        changeTransitionLabel(0, transitionId, label);
    }

    public void changeTransitionLabel(int automatonId, String transitionId, Label label) {
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).label = label;
    }

    public RegExpBody getTransitionRegExpBody(int transitionID) {
        String transID = Integer.toString(transitionID);
        return this.fsmxml.automatonV.get(0).automatonStruct.transitions.transitionH.get(transID).getLabel().getTypedRegExp().getRegExpBody();
    }

    public String getTransitionLabel(String transitionId) {
        return getTransitionLabel(0, transitionId);
    }

    public String getTransitionLabel(int automatonId, String transitionId) {
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).label.getLabel();
    }

    public String getTransitionSource(String transitionId) {
        return getTransitionSource(0, transitionId);
    }

    public String getTransitionSource(int automatonId, String transitionId) {
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).source;
    }

    public String getTransitionTarget(String transitionId) {
        return getTransitionTarget(0, transitionId);
    }

    public String getTransitionTarget(int automatonId, String transitionId) {
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.get(transitionId).target;
    }

    public void changeInitialTransitionLabel(String stateId, Label label) {
        changeInitialTransitionLabel(0, stateId, label);
    }

    public void changeInitialTransitionLabel(int automatonId, String stateId, Label label) {
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.initialH.get(stateId).label = label;
    }

    public String getInitialTransitionLabel(String transitionId) {
        return getInitialTransitionLabel(0, transitionId);
    }

    public String getInitialTransitionLabel(int automatonId, String stateId) {
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.initialH.get(stateId).label.getLabel();
    }

    public void changeFinalTransitionLabel(String stateId, Label label) {
        changeFinalTransitionLabel(0, stateId, label);
    }

    public void changeFinalTransitionLabel(int automatonId, String stateId, Label label) {
        this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.finalH.get(stateId).label = label;
    }

    public String getFinalTransitionLabel(String transitionId) {
        return getInitialTransitionLabel(0, transitionId);
    }

    public String getFinalTransitionLabel(int automatonId, String stateId) {
        return this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.finalH.get(stateId).label.getLabel();
    }

    public Vector<String> getSourcePortTransitions(String stateId) {
        return getSourcePortTransitions(0, stateId);
    }

    public Vector<String> getSourcePortTransitions(int automatonId, String stateId) {
        Hashtable<String, Transition> transitionsTemp =
                this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH;
        Vector<String> vs = new Vector<String>();

        for (Enumeration e = transitionsTemp.keys(); e.hasMoreElements();) {
            String transitionId = (String) e.nextElement();
            String sourceId = transitionsTemp.get(transitionId).getSource();
            if (sourceId.equals(stateId)) {
                vs.add(transitionId);
            }
        }

        return vs;
    }

    public Vector<String> getTargetPortTransitions(String stateId) {
        return getTargetPortTransitions(0, stateId);
    }

    public Vector<String> getTargetPortTransitions(int automatonId, String stateId) {
        Hashtable<String, Transition> transitionsTemp =
                this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH;

        Vector<String> vs = new Vector<String>();

        for (Enumeration e = transitionsTemp.keys(); e.hasMoreElements();) {
            String transitionId = (String) e.nextElement();
            String targetId = transitionsTemp.get(transitionId).getTarget();
            if (targetId.equals(stateId)) {
                vs.add(transitionId);
            }
        }

        return vs;
    }

    public Vector<String> getConnectedTransitions(String stateId) {
        return getConnectedTransitions(0, stateId);
    }

    public Vector<String> getConnectedTransitions(int automatonId, String stateId) {
        Hashtable<String, Transition> transitionsTemp =
                this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH;

        Vector<String> vs = null;

        for (int i = 0; i < transitionsTemp.size(); i++) {
            if (transitionsTemp.get(i).source.equals(stateId) || transitionsTemp.get(i).target.equals(stateId)) {
                vs.add(transitionsTemp.get(i).id);
            }
        }
        return vs;
    }

    public Vector<String> getConnectedInitialTransitions(String stateId) {
        return getConnectedInitialTransitions(0, stateId);
    }

    public Vector<String> getConnectedInitialTransitions(int automatonId, String stateId) {
        Hashtable<String, Initial> initialTransitionsTemp =
                this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.initialH;
        Vector<String> vs = null;

        for (int j = 0; j < initialTransitionsTemp.size(); j++) {
            if (initialTransitionsTemp.get(j).equals(stateId)) {
                vs.add(initialTransitionsTemp.get(j).stateId);
            }
        }

        return vs;
    }

    public Vector<String> getConnectedFinalTransitions(String stateId) {
        return getConnectedFinalTransitions(0, stateId);
    }

    public Vector<String> getConnectedFinalTransitions(int automatonId, String stateId) {
        Hashtable<String, Final> finalTransitionsTemp =
                this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.finalH;

        Vector<String> vs = null;
        for (int j = 0; j < finalTransitionsTemp.size(); j++) {
            if (finalTransitionsTemp.get(j).equals(stateId)) {
                vs.add(finalTransitionsTemp.get(j).stateId);
            }
        }
        return vs;
    }

    public int getNumberOfStates() {
        return getNumberOfStates(0);
    }

    public int getNumberOfStates(int automatonId) {
        int number = 0;
        number = this.fsmxml.automatonV.get(automatonId).automatonStruct.states.stateH.size();
        return number;
    }

    public int getNumberOfTransition() {
        return getNumberOfTransition(0);
    }

    public int getNumberOfTransition(int automatonId) {
        int number = 0;
        number = this.fsmxml.automatonV.get(automatonId).automatonStruct.transitions.transitionH.size();
        return number;
    }

    public static class Fsmxml {

        private String xmlns;
        private float version;
        private Vector<RegExp> regExpV;
        private Vector<Automaton> automatonV;

        public Fsmxml() {
            this.xmlns = null; //FIXME:::
            this.version = 0;//FIXME:::
            this.automatonV = new Vector<Automaton>();
            this.automatonV.add(new Automaton());

            this.regExpV = new Vector<RegExp>();
        }

        public Fsmxml(String xmlns, float version) {
            this.xmlns = xmlns;
            this.version = version;
            this.regExpV = new Vector<RegExp>();
            this.automatonV = new Vector<Automaton>();
        }

        public Fsmxml(String xmlns, float version, Vector<Automaton> automatonV) {
            this.xmlns = xmlns;
            this.version = version;
            this.automatonV = automatonV;
        }

        public void setAutomaton(Vector<Automaton> automatonV) {
            this.automatonV = automatonV;
        }

        public void setRegExp(Vector<RegExp> regExpV) {
            this.regExpV = regExpV;
        }

        public void setVersion(float version) {
            this.version = version;
        }

        public void setXmlns(String xmlns) {
            this.xmlns = xmlns;
        }

        public Vector<Automaton> getAutomaton() {
            return automatonV;
        }

        public Vector<RegExp> getRegExp() {
            return regExpV;
        }

        public float getVersion() {
            return version;
        }

        public String getXmlns() {
            return xmlns;
        }
    }

    public static class ValueType {

        //private Semiring semiring;
        private NumericalSemiring numSemiring;
        private SeriesSemiring serSemiring;
        private String zeroSymbol = "0";
        private String oneSymbol = "1";

        public enum SemiringTypeE {

            NUMERICAL, SERIES
        };
        private SemiringTypeE semiringType;        //private Monoid monoid;
        private UnitMonoid unitMonoid;
        private FreeMonoid freeMonoid;
        private ProductMonoid prodMonoid;

        public enum MonoidTypeE {

            UNIT, FREE, PRODUCT
        };
        private MonoidTypeE monoidType;

        public ValueType() {
            MonGen monGen1 = new MonGen("a");
            MonGen monGen2 = new MonGen("b");
            Vector<MonGen> monGens = new Vector<MonGen>();
            monGens.add(monGen1);
            monGens.add(monGen2);
            this.setNumericalSemiring(new NumericalSemiring(NumericalSemiring.SetE.B, NumericalSemiring.OperationE.CLASSICAL));
            this.setFreeMonoid(new FreeMonoid(FreeMonoid.GenDescriptE.ENUM, FreeMonoid.GenSortE.LETTERS, monGens));
        }

        public ValueType(NumericalSemiring numSemiring, UnitMonoid unitMonoid) {
            this.numSemiring = numSemiring;
            this.unitMonoid = unitMonoid;
            this.semiringType = SemiringTypeE.NUMERICAL;
            this.monoidType = MonoidTypeE.UNIT;
        }

        public ValueType(NumericalSemiring numSemiring, FreeMonoid freeMonoid) {
            this.numSemiring = numSemiring;
            this.freeMonoid = freeMonoid;
            this.semiringType = SemiringTypeE.NUMERICAL;
            this.monoidType = MonoidTypeE.FREE;
        }

        public ValueType(NumericalSemiring numSemiring, ProductMonoid prodMonoid) {
            this.numSemiring = numSemiring;
            this.prodMonoid = prodMonoid;
            this.semiringType = SemiringTypeE.NUMERICAL;
            this.monoidType = MonoidTypeE.PRODUCT;
        }

        public ValueType(SeriesSemiring serSemiring, UnitMonoid unitMonoid) {
            this.serSemiring = serSemiring;
            this.unitMonoid = unitMonoid;
            this.semiringType = SemiringTypeE.SERIES;
            this.monoidType = MonoidTypeE.UNIT;
        }

        public ValueType(SeriesSemiring serSemiring, FreeMonoid freeMonoid) {
            this.serSemiring = serSemiring;
            this.freeMonoid = freeMonoid;
            this.semiringType = SemiringTypeE.SERIES;
            this.monoidType = MonoidTypeE.FREE;
        }

        public ValueType(SeriesSemiring serSemiring, ProductMonoid prodMonoid) {
            this.serSemiring = serSemiring;
            this.prodMonoid = prodMonoid;
            this.semiringType = SemiringTypeE.SERIES;
            this.monoidType = MonoidTypeE.PRODUCT;
        }
        
        public void setZeroSymbol(String symbol) {
            zeroSymbol = symbol;
        }
        
        public String getZeroSymbol() {
            return zeroSymbol;
        }
        
        public void setOneSymbol(String symbol) {
            oneSymbol = symbol;
        }
        
        public String getOneSymbol() {
            return oneSymbol;
        }

        public Monoid getMonoid() {
            if (this.monoidType == MonoidTypeE.UNIT) {
                return unitMonoid;
            } else if (this.monoidType == MonoidTypeE.FREE) {
                return freeMonoid;
            } else if (this.monoidType == MonoidTypeE.PRODUCT) {
                return prodMonoid;
            } else {
                return null;
            }
        }

        public Semiring getSemiring() {
            if (this.semiringType == SemiringTypeE.NUMERICAL) {
                return numSemiring;
            } else if (this.semiringType == SemiringTypeE.SERIES) {
                return serSemiring;
            } else {
                return null;
            }
        }

        public void setUnitMonoid(UnitMonoid monoid) {
            this.unitMonoid = monoid;
            this.monoidType = MonoidTypeE.UNIT;
            this.freeMonoid = null;
            this.prodMonoid = null;
        }

        public void setFreeMonoid(FreeMonoid monoid) {
            this.freeMonoid = monoid;
            this.monoidType = MonoidTypeE.FREE;
            this.unitMonoid = null;
            this.prodMonoid = null;
        }

        public void setProductMonoid(ProductMonoid monoid) {
            this.prodMonoid = monoid;
            this.monoidType = MonoidTypeE.PRODUCT;
            this.freeMonoid = null;
            this.unitMonoid = null;
        }

        public void setSeriesSemiring(SeriesSemiring semiring) {
            this.serSemiring = semiring;
            this.semiringType = SemiringTypeE.SERIES;
            this.numSemiring = null;
        }

        public void setNumericalSemiring(NumericalSemiring semiring) {
            this.numSemiring = semiring;
            this.semiringType = SemiringTypeE.NUMERICAL;
            this.serSemiring = null;
        }

        public MonoidTypeE getMonoidType() {
            return monoidType;
        }

        public SemiringTypeE getSemiringType() {
            return semiringType;
        }
    }

    public static class Monoid {

        public enum TypeE {

            UNIT, FREE, PRODUCT
        };
        protected TypeE typeE;

        public TypeE getTypeE() {
            return typeE;
        }

        public void setTypeE(TypeE typeE) {
            this.typeE = typeE;
        }
    }

    public static class UnitMonoid extends Monoid {

        public UnitMonoid() {
            this.typeE = Automata2.Monoid.TypeE.UNIT;
        }
    }

    public static class FreeMonoid extends Monoid {

        public enum GenKindE {

            SIMPLE, TUPLE
        };
        private GenKindE genKindE;

        public enum GenDescriptE {

            ENUM, RANGE, SET
        };
        private GenDescriptE genDescriptE;
        private WritingData writingData;

        public enum GenSortE {

            LETTERS, DIGITS, INTEGER, ALPHANUM
        };
        private GenSortE genSortE;
        private Vector<MonGen> monGenV;
        private int genDim;
        private GenSort genSort;

        //For genKind = simple
        public FreeMonoid(GenDescriptE genDescript, GenSortE genSortE, Vector<MonGen> monGenV) {
            if (monGenV.size() > 0 && !monGenV.get(0).value.equals(null)) {//&& MonGen Should fit genSort  restrction.
                this.typeE = Automata2.Monoid.TypeE.FREE;
                this.genKindE = GenKindE.SIMPLE;
                this.genDescriptE = genDescript;
                this.genSortE = genSortE;
                this.monGenV = monGenV;
            }
        }

        //For genKind = tuple
        public FreeMonoid(GenDescriptE genDescript, int genDim, GenSort genSort, Vector<MonGen> monGenV) {
            if (genSort.genCompSortV.size() == genDim) {
                this.typeE = Automata2.Monoid.TypeE.FREE;
                this.genKindE = GenKindE.TUPLE;
                this.genDescriptE = genDescript;
                this.genDim = genDim;
                this.genSort = genSort;
                this.monGenV = monGenV;
            } else {
                Debug.echo(" File Structure Error!!! tuple FreeMonoid need genDim times genComSort");
            }
        }

        public int getGenDim() {
            return genDim;
        }

        public void setGenDim(int genDim) {
            this.genDim = genDim;
        }

        public GenKindE getGenKindE() {
            return genKindE;
        }

        public void setGenKindE(GenKindE genKindE) {
            this.genKindE = genKindE;
        }

        public GenDescriptE getGenDescriptE() {
            return genDescriptE;
        }

        public void setGenDescriptE(GenDescriptE genDescriptE) {
            this.genDescriptE = genDescriptE;
        }

        public GenSort getGenSort() {
            return genSort;
        }

        public void setGenSort(GenSort genSort) {
            this.genSort = genSort;
        }

        public GenSortE getGenSortE() {
            return genSortE;
        }

        public void setGenSortE(GenSortE genSortE) {
            this.genSortE = genSortE;
        }

        public Vector<MonGen> getMonGenV() {
            return monGenV;
        }

        public void setMonGenV(Vector<MonGen> monGenV) {
            this.monGenV = monGenV;
        }

        public WritingData getWritingData() {
            return writingData;
        }

        public void setWritingData(WritingData writingData) {
            this.writingData = writingData;
        }
    }

    public static class ProductMonoid extends Monoid {

        private int prodDim;
        private WritingData writingData;
        private Vector<FreeMonoid> freeMonoidV;

        public ProductMonoid(int prodDim, Vector<FreeMonoid> freeMonoidV) {
            if (freeMonoidV.size() == prodDim) {
                this.typeE = Automata2.Monoid.TypeE.PRODUCT;
                this.prodDim = prodDim;
                this.freeMonoidV = freeMonoidV;
            } else {
                Debug.echo(" File Structure Error!!! ProductMonoid need prodDim times FreeMonoid");
            }
        }

        public Vector<FreeMonoid> getFreeMonoidV() {
            return freeMonoidV;
        }

        public void setFreeMonoidV(Vector<FreeMonoid> freeMonoidV) {
            this.freeMonoidV = freeMonoidV;
        }

        public int getProdDim() {
            return prodDim;
        }

        public void setProdDim(int prodDim) {
            this.prodDim = prodDim;
        }

        public WritingData getWritingData() {
            return writingData;
        }

        public void setWritingData(WritingData writingData) {
            this.writingData = writingData;
        }
    }

    public static class GenSort {

        private Vector<GenCompSort> genCompSortV;

        public GenSort() {
        }

        public GenSort(Vector<GenCompSort> genCompSortV) {
            this.genCompSortV = genCompSortV;
        }

        public Vector<GenCompSort> getGenCompSortV() {
            return genCompSortV;
        }

        public void setGenCompSortV(Vector<GenCompSort> genCompSortV) {
            this.genCompSortV = genCompSortV;
        }
    }

    public static class GenCompSort {

        public enum ValueE {

            LETTERS, DIGITS, INTEGER
        };
        private ValueE valueE;

        public GenCompSort(ValueE valueE) {
            this.valueE = valueE;
        }

        public ValueE getValueE() {
            return valueE;
        }

        public void setValueE(ValueE valueE) {
            this.valueE = valueE;
        }
    }

    public static class MonGen {
        //For "enum" generator of "simple" sort
        private String value = null;
        //For "enum" generator of "tupple" sort
        private Vector<MonCompGen> monCompGenV;//FIXE ME:Occurs genDim times.

        //For "range" and "set" generators
        public MonGen(String value) {
            this.value = value;
        }

        public MonGen(char value) {
            this.value = "" + value;
        }

        public MonGen(Vector<MonCompGen> monCompGenV) {
            this.monCompGenV = monCompGenV;
        }

        public Vector<MonCompGen> getMonCompGenV() {
            return monCompGenV;
        }

        public void setMonCompGenV(Vector<MonCompGen> monCompGenV) {
            this.monCompGenV = monCompGenV;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class MonCompGen {

        private String value = null;

        public MonCompGen(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Semiring {

        public enum TypeE {

            NUMERICAL, SERIES
        };
        protected TypeE typeE;
        protected WritingData writingData;

        public Semiring() {
        }

        public Semiring(TypeE typeE) {
            this.typeE = typeE;
        }

        public TypeE getTypeE() {
            return typeE;
        }

        public void setTypeE(TypeE typeE) {
            this.typeE = typeE;
        }

        public WritingData getWritingData() {
            return writingData;
        }

        public void setWritingData(WritingData writingData) {
            this.writingData = writingData;
        }       
    }

    public static class NumericalSemiring extends Semiring {

        public enum SetE {

            B, N, Z, Q, R, C
        };
        private SetE setE;

        public enum OperationE {

            CLASSICAL, MINPLUS, MAXPLUS
        };
        private OperationE operationE;

        public NumericalSemiring(SetE setE, OperationE operationE) {
            this.typeE = Semiring.TypeE.NUMERICAL;
            this.setE = setE;
            this.operationE = operationE;
        }

        public NumericalSemiring(WritingData writingData, SetE setE, OperationE operationE) {
            this.typeE = Semiring.TypeE.NUMERICAL;
            this.writingData = writingData;
            this.setE = setE;
            this.operationE = operationE;
        }

        public OperationE getOperationE() {
            return operationE;
        }

        public void setOperationE(OperationE operationE) {
            this.operationE = operationE;
        }

        public SetE getSetE() {
            return setE;
        }

        public void setSetE(SetE setE) {
            this.setE = setE;
        }
    }

    public static class SeriesSemiring extends Semiring {

        private Semiring semiring;
        private Monoid monoid;

        public SeriesSemiring(Semiring semiring, Monoid monoid) {
            this.typeE = Semiring.TypeE.SERIES;
            this.semiring = semiring;
            this.monoid = monoid;
        }

        public SeriesSemiring(WritingData writingData, Semiring semiring, Monoid monoid) {
            this.typeE = Semiring.TypeE.SERIES;
            this.writingData = writingData;
            this.semiring = semiring;
            this.monoid = monoid;
        }

        public Monoid getMonoid() {
            return monoid;
        }

        public void setMonoid(Monoid monoid) {
            this.monoid = monoid;
        }

        public Semiring getSemiring() {
            return semiring;
        }

        public void setSemiring(Semiring semiring) {
            this.semiring = semiring;
        }
    }

    public static class RegExp {

        private ValueType value;
        private TypedRegExp typedRegExp;

        public RegExp(ValueType value, TypedRegExp typeRegExp) {
            this.value = value;
            this.typedRegExp = typeRegExp;
        }

        public TypedRegExp getTypeRegExp() {
            return typedRegExp;
        }

        public void setTypeRegExp(TypedRegExp typedRegExp) {
            this.typedRegExp = typedRegExp;
        }

        public ValueType getValue() {
            return value;
        }

        public void setValue(ValueType value) {
            this.value = value;
        }
    }

    public static class TypedRegExp {

        private RegExpBody regExpBody;

        public TypedRegExp(RegExpBody regExpBody) {
            this.regExpBody = regExpBody;
        }

        public RegExpBody getRegExpBody() {
            return regExpBody;
        }

        public void setRegularExpression(RegExpBody regExpBody) {
            this.regExpBody = regExpBody;
        }
    }

    public static abstract class RegExpBody {

        public enum DataTypeE {

            SUM, PRODUCT, STAR, RIGHT_EXT_MUL, LEFT_EXT_MUL, MON_ELMT, ZERO, ONE
        };
        protected DataTypeE dataTypeE;

        public RegExpBody(DataTypeE dataTypeE) {
            this.dataTypeE = dataTypeE;
        }

        public DataTypeE getDataType() {
            return dataTypeE;
        }
    }

    public static class Sum extends RegExpBody {

        RegExpBody regExpBody1;
        RegExpBody regExpBody2;

        public Sum(RegExpBody regExpBody1, RegExpBody regExpBody2) {
            super(RegExpBody.DataTypeE.SUM);
            this.regExpBody1 = regExpBody1;
            this.regExpBody2 = regExpBody2;
        }

        public RegExpBody getRegExpBody1() {
            return regExpBody1;
        }

        public void setRegExpBody1(RegExpBody regExpBody1) {
            this.regExpBody1 = regExpBody1;
        }

        public RegExpBody getRegExpBody2() {
            return regExpBody2;
        }

        public void setRegExpBody2(RegExpBody regExpBody2) {
            this.regExpBody2 = regExpBody2;
        }
    }

    public static class Product extends RegExpBody {

        RegExpBody regExpBody1;
        RegExpBody regExpBody2;

        public Product(RegExpBody regExpBody1, RegExpBody regExpBody2) {
            super(RegExpBody.DataTypeE.PRODUCT);
            this.regExpBody1 = regExpBody1;
            this.regExpBody2 = regExpBody2;
        }

        public RegExpBody getRegExpBody1() {
            return regExpBody1;
        }

        public void setRegExpBody1(RegExpBody regExpBody1) {
            this.regExpBody1 = regExpBody1;
        }

        public RegExpBody getRegExpBody2() {
            return regExpBody2;
        }

        public void setRegExpBody2(RegExpBody regExpBody2) {
            this.regExpBody2 = regExpBody2;
        }
    }

    public static class Star extends RegExpBody {

        RegExpBody regExpBody;

        public Star(RegExpBody regExpBody) {
            super(RegExpBody.DataTypeE.STAR);
            this.regExpBody = regExpBody;
        }

        public RegExpBody getRegExpBody() {
            return regExpBody;
        }

        public void setRegExpBody(RegExpBody regExpBody) {
            this.regExpBody = regExpBody;
        }
    }

    public static class ExtMul extends RegExpBody {

        protected Weight weight;
        protected RegExpBody regExpBody;

        public ExtMul(RegExpBody.DataTypeE dataTypeE, Weight weight, RegExpBody regExpBody) {
            super(dataTypeE);
            this.weight = weight;
            this.regExpBody = regExpBody;
        }

        public RegExpBody getRegExpBody() {
            return regExpBody;
        }

        public void setRegExpBody(RegExpBody regExpBody) {
            this.regExpBody = regExpBody;
        }

        public Weight getWeight() {
            return weight;
        }

        public void setWeight(Weight weight) {
            this.weight = weight;
        }
    }

    public static class RightExtMul extends ExtMul {

        public RightExtMul(Weight weight, RegExpBody regExpBody) {
            super(RegExpBody.DataTypeE.RIGHT_EXT_MUL, weight, regExpBody);
        }
    }

    public static class LeftExtMul extends ExtMul {

        public LeftExtMul(Weight weight, RegExpBody regExpBody) {
            super(RegExpBody.DataTypeE.LEFT_EXT_MUL, weight, regExpBody);
        }
    }

    public static abstract class MonElmt extends RegExpBody {

        MonElmt() {
            super(RegExpBody.DataTypeE.MON_ELMT);
        }
    }

    public static class FreeMonElmt extends MonElmt {

        private Vector<MonGen> monGenV;

        public FreeMonElmt() {
        }

        public FreeMonElmt(Vector<MonGen> monGenV) {
            this.monGenV = monGenV;
        }

        public Vector<MonGen> getMonGenV() {
            return monGenV;
        }

        public void setMonGenV(Vector<MonGen> monGenV) {
            this.monGenV = monGenV;
        }
    }

    public static class ProductMonElmt extends MonElmt {

//		private Vector<MonElmt> monElmtV;
//		private Vector<One> oneV;
        private Vector<RegExpBody> monElmtOrOneV;

        public ProductMonElmt() {
        }

        public ProductMonElmt(Vector<RegExpBody> monElmtOrOneV) {
            this.monElmtOrOneV = monElmtOrOneV;
        }

        public Vector<RegExpBody> getMonElmtOrOneV() {
            return monElmtOrOneV;
        }

        public void setMonElmtOrOneV(Vector<RegExpBody> monElmtOrOneV) {
            this.monElmtOrOneV = monElmtOrOneV;
        }        //** the codes below are replaced by the ones above
        //FIXME: If this is a product monoid monElmt.size()+one.size() should equal to prodDim.
//		public ProductMonElmt(Vector<MonElmt> monElmtV) {
//			this.monElmtV = monElmtV;
//		}
//
//		public ProductMonElmt(Vector<One> oneV) {
//			this.oneV = oneV;
//		}
//
//		public ProductMonElmt(Vector<MonElmt> monElmtV, Vector<One> oneV) {
//			this.monElmtV = monElmtV;
//			this.oneV = oneV;
//		}
//
//		public Vector<MonElmt> getMonElmtV() {
//			return monElmtV;
//		}
//
//		public void setMonElmtV(Vector<MonElmt> monElmtV) {
//			this.monElmtV = monElmtV;
//		}
//
//		public Vector<One> getOneV() {
//			return oneV;
//		}
//
//		public void setOneV(Vector<One> oneV) {
//			this.oneV = oneV;
//		}
    }

    public static class Zero extends RegExpBody {
        
        public Zero() {
            super(RegExpBody.DataTypeE.ZERO);
        }      
    }

    public static class One extends RegExpBody {
        
        public One() {
            super(RegExpBody.DataTypeE.ONE);
        }
    }

    public static abstract class Weight {
    }

    public static class NumericalSemiringWeight extends Weight {

        private String value;

        public NumericalSemiringWeight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class SeriesSemiringWeight extends Weight {

        private RegExpBody regExpBody;

        public SeriesSemiringWeight(RegExpBody regExpBody) {
            this.regExpBody = regExpBody;
        }

        public RegExpBody getRegExpBody() {
            return regExpBody;
        }

        public void setRegExpBody(RegExpBody regExpBody) {
            this.regExpBody = regExpBody;
        }
    }

    public static class Automaton {

        private String name;

        public enum ReadingDirE {

            LEFT, RIGHT
        };
        private ReadingDirE readingDirE;
        //private GeometricData geometricData;
        //private DrawingData drawingData;
        private ValueType valueType;
        private AutomatonStruct automatonStruct;

        public Automaton() {
            this.name = null;//FIXME :::
            this.readingDirE = ReadingDirE.LEFT;//FIXME:::
            this.valueType = new ValueType();
            this.automatonStruct = new AutomatonStruct();
        }

        public Automaton(String name, ReadingDirE readingDirE, ValueType valueType, AutomatonStruct automatonStruct) {
            this.name = name;
            this.readingDirE = readingDirE;
            this.valueType = valueType;
            this.automatonStruct = automatonStruct;
        }

        public AutomatonStruct getAutomatonStruct() {
            return automatonStruct;
        }

        public void setAutomatonStruct(AutomatonStruct automatonStruct) {
            this.automatonStruct = automatonStruct;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ReadingDirE getReadingDir() {
            return readingDirE;
        }

        public void setReadingDir(ReadingDirE readingDirE) {
            this.readingDirE = readingDirE;
        }

        public ValueType getValueType() {
            return valueType;
        }

        public void setValueType(ValueType valueType) {
            this.valueType = valueType;
        }
    }

    public static class AutomatonStruct {

        private States states;
        private Transitions transitions;

        public AutomatonStruct() {
            states = new States();
            transitions = new Transitions();
        }

        public AutomatonStruct(States states, Transitions transitions) {
            this.states = states;
            this.transitions = transitions;
        }

        public States getStates() {
            return states;
        }

        public void setStates(States states) {
            this.states = states;
        }

        public Transitions getTransitions() {
            return transitions;
        }

        public void setTransitions(Transitions transitions) {
            this.transitions = transitions;
        }
    }

    public static class States {
        //private Vector<State> stateV;
        private Hashtable<String, State> stateH = new Hashtable();

        public Hashtable getStateH() {
            return stateH;
        }

        public void setStateH(Hashtable stateH) {
            this.stateH = stateH;
        }

        public State getState(int stateId) {
            return stateH.get(stateId);
        }

        public void addState(State state) {
            this.stateH.put(state.getId(), state);
        }

        public void removeState(String stateId) {
            this.stateH.remove(stateId);
        }
    }

    public static class State {

        private String id;
        private int key;
        private String name;

        //private GeometricData geometricData ;
        //private DrawingData drawingData;
        public State(String id, String name) {
            this.id = id;
            this.name = name;
            this.key = 0;
        }

        public State(String id, int key, String name) {
            this.id = id;
            this.key = key;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Transitions {
        //private Vector<Transition> transitionV;
        private Hashtable<String, Transition> transitionH = new Hashtable();
        //private Vector<Initial> initialV;
        private Hashtable<String, Initial> initialH = new Hashtable();
        //private Vector<Final> finalV;
        private Hashtable<String, Final> finalH = new Hashtable();

        public Hashtable<String, Final> getFinalH() {
            return finalH;
        }

        public void setFinalH(Hashtable<String, Final> finalH) {
            this.finalH = finalH;
        }

        public void addFinal(Final final_) {
            this.finalH.put(final_.stateId, final_);
        }

        public Hashtable<String, Initial> getInitialH() {
            return initialH;
        }

        public void setInitialH(Hashtable<String, Initial> initialH) {
            this.initialH = initialH;
        }

        public void addInitial(Initial initial) {
            this.initialH.put(initial.stateId, initial);
        }

        public Hashtable<String, Transition> getTransitionH() {
            return transitionH;
        }

        public void setTransitionH(Hashtable<String, Transition> transitionH) {
            this.transitionH = transitionH;
        }

        public void addTransition(Transition transition) {
            this.transitionH.put(transition.id, transition);
        }

        public void removeTrasition(String transitionId) {
            this.transitionH.remove(transitionId);
        }
    }

    public static class Transition {

        private String id;
        private String source;
        private String target;
        private Label label;
        //private GeometricData geometricData;
        //private DrawingData drawing;
        public Transition(String id, String source, String target, Label label) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.label = label;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class InitialAndFinal {

        protected String stateId;
        protected Label label;
        protected String id;
        //protected GeometricData geometricData;
        //protected DrawingData drawingData;
        public InitialAndFinal(String state) {
            this.stateId = state;
        }

        public InitialAndFinal(String state, Label label) {
            this.stateId = state;
            this.label = label;
        }

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Initial extends InitialAndFinal {

        public Initial(String state) {
            super(state);
        }

        public Initial(String state, Label label) {
            super(state, label);
        }
    }

    public static class Final extends InitialAndFinal {

        public Final(String state) {
            super(state);
        }

        public Final(String state, Label label) {
            super(state, label);
        }
    }

    public static class Label {

        private String label = "";
        private TypedRegExp typedRegExp;
        private boolean typeIsString = true;

        //this should be default constructor
        public Label(TypedRegExp exp, String label) {
            typedRegExp = exp;
            this.label = label;
        }
        //TODO empty label
        public Label() {
        }

        //simple label
        public Label(String label) {
            this.label = label;
            this.typeIsString = true;
        }

        public Label(TypedRegExp typedRegExp) {
            this.typedRegExp = typedRegExp;
            this.typeIsString = false;
        }

        public String getLabel() {
            return label;
        }

        public TypedRegExp getTypedRegExp() {
            return typedRegExp;
        }

        public void setLabel(String label) {
            this.label = label;
            this.typeIsString = true;
        }

        public boolean typeIsString() {
            return typeIsString;
        }

        public void setTypedRegExp(TypedRegExp typedRegExp) {
            this.typedRegExp = typedRegExp;
            this.typeIsString = false;
        }
    }

    public static class WritingData {
    }

    public static class MonoidWritingData extends WritingData {

        private String identitySymbol;

        public MonoidWritingData(String identitySymbol) {
            this.identitySymbol = identitySymbol;
        }

        public String getIdentitySymbol() {
            return identitySymbol;
        }

        public void setIdentitySymbol(String identitySymbol) {
            this.identitySymbol = identitySymbol;
        }
    }

    public static class SemiringWritingData extends WritingData {

        private String identitySymbol;
        private String zeroSymbol;

        public SemiringWritingData(String identitySymbol, String zeroSymbol) {
            this.identitySymbol = identitySymbol;
            this.zeroSymbol = zeroSymbol;
        }

        public String getIdentitySymbol() {
            return identitySymbol;
        }

        public void setIdentitySymbol(String identitySymbol) {
            this.identitySymbol = identitySymbol;
        }

        public String getZeroSymbol() {
            return zeroSymbol;
        }

        public void setZeroSymbol(String zeroSymbol) {
            this.zeroSymbol = zeroSymbol;
        }
    }
}

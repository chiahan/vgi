package vgi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface AutomataInterface {

	public class WritingData {

		public Character closePar;
		public Character openPar;
		public Character plusSym;
		public Character spacesSym;
		public Character starSym;
		public Character timesSym;
		public Character weightClosing;
		public Character weightOpening;
		public Character zeroSym;
	}  // End public class WritingData

	public class Weight {

		public TAFKitInterface.AutomataType.Semiring semiring;
		public Object identitySymbol;
		public Object zeroSymbol;

		public Weight() {
			this.semiring = null;
			this.identitySymbol = null;
			this.zeroSymbol = null;
		}
	}  // End public class Weight

	public class Alphabet {

		public TAFKitInterface.AutomataType.AlphabetDataType dataType;
		public List<Object> allSymbols;
		public Object identitySymbol;
		public Object timesSymbol;

		public Alphabet() {
			this.dataType = null;
			this.allSymbols = new ArrayList<Object>();
			this.identitySymbol = null;
			this.timesSymbol = null;
		}
	}  // End public class Alphabet

	public class SymbolPair<E> extends ArrayList<E> {
	}

	public String getName();

	public void setName(String name);

	public WritingData getWritingData();

	public void setWritingData(WritingData writingData);

	public Weight getWeight();

	public void setWeight(Weight weight);

	public Alphabet getAlphabet();

	public void setAlphabet(Alphabet alphabet);

	public Alphabet getOutputAlphabet();

	public void setOutputAlphabet(Alphabet alphabet);

	public List<State> getAllStates();

	public void setAllStates(List<State> allStates);

	public List<State> getInitialStates();

	public List<State> getFinalStates();

	public void addState(State state);

	public List<Transition> getAllTransitions();

	public void setAllTransitions(List<Transition> allTransitions);

	public void addTransition(Transition transition);
}  // End public interface AutomataInterface

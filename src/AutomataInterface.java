
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
			this.semiring = TAFKitInterface.AutomataType.Semiring.B_BOOLEAN;
			this.identitySymbol = null;
			this.zeroSymbol = null;
		}
	}  // End public class Weight

	public class Alphabet {

		public TAFKitInterface.AutomataType.AlphabetDataType dataType;
		public List<Object> allSymbols;
		public Object identitySymbol;

		public Alphabet() {
			this.dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR;
			this.allSymbols = new ArrayList<Object>();
			this.identitySymbol = null;
		}
	}  // End public class Alphabet

	public WritingData getWritingData();

	public void setWritingData(WritingData writingData);

	public Weight getWeight();

	public void setWeight(Weight weight);

	public Alphabet getAlphabet();

	public void setAlphabet(Alphabet alphabet);

	public Alphabet getSecondAlphabet();

	public void setSecondAlphabet(Alphabet alphabet);

	public boolean isTransducer();

	public void setIsTransducer(boolean isTransducer);

	public List<State> getAllStates();

	public void setAllStates(List<State> allStates);

	public State getStateById(String id);

	public List<Transition> getAllTransitions();

	public void setAllTransitions(List<Transition> allTransitions);
}  // End public interface AutomataInterface

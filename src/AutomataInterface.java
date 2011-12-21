
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

		public enum Semiring {

			B_BOOLEAN, Z_INTEGER, Q_RATIONAL, R_REAL, F2_TWO_ELEMENT_FIELD,
			ZMIN_MIN_TROPICAL, ZMAX_MAX_TROPICAL
		}
		public Semiring semiring;
		public Character identitySymbol;
		public Character zeroSymbol;

		public Weight() {
			this.semiring = Semiring.B_BOOLEAN;
			this.identitySymbol = null;
			this.zeroSymbol = new Character('0');
		}
	}  // End public class Weight

	public class Alphabet {

		public enum SymbolType {

			CHAR, INT
		}  // End public static class Symbol
		public SymbolType symbolType;
		public List<Object> allSymbols;
		public Character identitySymbol;
	}  // End public class Alphabet

	public WritingData getWritingData();

	public void setWritingData(WritingData writingData);

	public boolean isTransducer();

	public void setIsTransducer(boolean isTransducer);

	public Weight getWeight();

	public void setWeight(Weight weight);

	public Alphabet getAlphabet();

	public void setAlphabet(Alphabet alphabet);

	public Alphabet getSecondAlphabet();

	public void setSecondAlphabet(Alphabet alphabet);

	public List<State> getAllStates();

	public void setAllStates(List<State> allStates);

	public List<Transition> getAllTransitions();

	public void setAllTransitions(List<Transition> allTransitions);

	/*
	 *  This class is used only to communicate with TAFKitInterface
	 */
	public class Type {

		public boolean isTransducer;
		public Weight.Semiring weightSemiring;
		public Alphabet.SymbolType alphabetSymbolType;
		public Alphabet.SymbolType secondAlphabetSymbolType;

		public Type() {
			this.isTransducer = false;
			this.weightSemiring = Weight.Semiring.B_BOOLEAN;
			this.alphabetSymbolType = Alphabet.SymbolType.CHAR;
			this.secondAlphabetSymbolType = null;
		}

		public Type(
				boolean isTransducer,
				Weight.Semiring weightSemiring,
				Alphabet.SymbolType alphabetDataType,
				Alphabet.SymbolType secondAlphabetDataType) {
			this.isTransducer = isTransducer;
			this.weightSemiring = weightSemiring;
			this.alphabetSymbolType = alphabetDataType;
			this.secondAlphabetSymbolType = secondAlphabetDataType;
		}
	}  // End public static class Type
}  // End public interface AutomataInterface

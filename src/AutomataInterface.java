
import java.util.List;

/**
 *
 * @author JLiu
 */
public interface AutomataInterface {

	public enum WeightSemiring {

		B_BOOLEAN, Z_INTEGER, Q_RATIONAL, R_REAL, F2_TWO_ELEMENT_FIELD,
		ZMIN_MIN_TROPICAL, ZMAX_MAX_TROPICAL
	}

	public enum AlphabetDataType {

		CHAR, INT
	}

	public static class Type {

		boolean isTransducer;
		WeightSemiring weightSemiring;
		AlphabetDataType alphabetDataType;
		AlphabetDataType secondAlphabetDataType;

		public Type() {
			this.isTransducer = false;
			this.weightSemiring = WeightSemiring.B_BOOLEAN;
			this.alphabetDataType = AlphabetDataType.CHAR;
			this.secondAlphabetDataType = null;
		}

		public Type(
				boolean isTransducer,
				WeightSemiring weightSemiring,
				AlphabetDataType alphabetDataType,
				AlphabetDataType secondAlphabetDataType) {
			this.isTransducer = isTransducer;
			this.weightSemiring = weightSemiring;
			this.alphabetDataType = alphabetDataType;
			this.secondAlphabetDataType = secondAlphabetDataType;
		}
	}  // End public static class Type

	public Type getType();

	public List<Object> getAlphabet();

	public void setAlphabet(List<Object> alphabet);

	public List<Object> getSecondAlphabet();

	public void setSecondAlphabet(List<Object> alphabet);

	public List<State> getAllStates();

	public void setAllStates(List<State> allStates);

	public void addState(State state);

	public void removeState(State state);

	public List<Transition> getAllTransitions();

	public void setAllTransitions(List<Transition> allTransitions);

	public void addTransition(Transition transition);

	public void removeTransition(Transition transition);
}  // End public interface AutomataInterface

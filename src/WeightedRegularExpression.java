
/**
 *
 * @author JLiu
 */
public class WeightedRegularExpression {

	private WeightedRegularExpression parent;
	private AutomataInterface.WritingData writingData;
	private AutomataInterface.Weight weight;
	private AutomataInterface.Alphabet alphabet;

	public WeightedRegularExpression getParent() {
		return this.parent;
	}

	public void setParent(WeightedRegularExpression parent) {
		this.parent = parent;
	}

	public AutomataInterface.WritingData getWritingData() {
		if (this.writingData == null) {
			this.setWritingData(this.getParent().getWritingData());
		}
		return this.writingData;
	}

	public void setWritingData(AutomataInterface.WritingData writingData) {
		this.writingData = writingData;
	}

	public AutomataInterface.Weight getWeight() {
		if (this.weight == null) {
			this.setWeight(this.getParent().getWeight());
		}
		return this.weight;
	}

	public void setWeight(AutomataInterface.Weight weight) {
		this.weight = weight;
	}

	public AutomataInterface.Alphabet getAlphabet() {
		if (this.alphabet == null) {
			this.setAlphabet(this.getParent().getAlphabet());
		}
		return this.alphabet;
	}

	public void setAlphabet(AutomataInterface.Alphabet alphabet) {
		this.alphabet = alphabet;
	}

	public static class Zero extends WeightedRegularExpression {

		@Override
		public String toString() {
			return this.getWritingData().zeroSym.toString();
		}
	}

	public static class One extends WeightedRegularExpression {

		@Override
		public String toString() {
			return this.getAlphabet().identitySymbol.toString();
		}
	}

	public static class Atomic extends WeightedRegularExpression {

		private Object value;

		public Atomic() {
		}

		public Atomic(Object value) {
			this.setValue(value);
		}

		public Object getValue() {
			return this.value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	public static class Sum extends WeightedRegularExpression {

		private WeightedRegularExpression leftExpression;
		private WeightedRegularExpression rightExpression;

		public Sum() {
		}

		public Sum(WeightedRegularExpression leftExpression, WeightedRegularExpression rightExpression) {
			this.setLeftExpression(leftExpression);
			this.setRightExpression(rightExpression);
		}

		public WeightedRegularExpression getLeftExpression() {
			return this.leftExpression;
		}

		public void setLeftExpression(WeightedRegularExpression leftExpression) {
			leftExpression.setParent(this);
			this.leftExpression = leftExpression;
		}

		public WeightedRegularExpression getRightExpression() {
			return this.rightExpression;
		}

		public void setRightExpression(WeightedRegularExpression rightExpression) {
			rightExpression.setParent(this);
			this.rightExpression = rightExpression;
		}

		@Override
		public String toString() {
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = this.leftExpression.toString() + writingData.plusSym.toString() + this.rightExpression.toString();
			return string;
		}
	}

	public static class Product extends WeightedRegularExpression {

		private WeightedRegularExpression leftExpression;
		private WeightedRegularExpression rightExpression;

		public Product() {
		}

		public Product(WeightedRegularExpression leftExpression, WeightedRegularExpression rightExpression) {
			this.setLeftExpression(leftExpression);
			this.setRightExpression(rightExpression);
		}

		public WeightedRegularExpression getLeftExpression() {
			return this.leftExpression;
		}

		public void setLeftExpression(WeightedRegularExpression leftExpression) {
			leftExpression.setParent(this);
			this.leftExpression = leftExpression;
		}

		public WeightedRegularExpression getRightExpression() {
			return this.rightExpression;
		}

		public void setRightExpression(WeightedRegularExpression rightExpression) {
			rightExpression.setParent(this);
			this.rightExpression = rightExpression;
		}

		@Override
		public String toString() {
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = this.leftExpression.toString();
			if (Sum.class.isAssignableFrom(this.leftExpression.getClass())) {
				string = string = writingData.openPar.toString() + string + writingData.closePar.toString();
			}
			string = string + writingData.timesSym.toString();
			if (Sum.class.isAssignableFrom(this.rightExpression.getClass())) {
				string = string + writingData.openPar.toString() + this.rightExpression.toString() + writingData.closePar.toString();
			} else {
				string = string + this.rightExpression.toString();
			}
			return string;
		}
	}

	public static class Star extends WeightedRegularExpression {

		private WeightedRegularExpression expression;

		public Star() {
		}

		public Star(WeightedRegularExpression expression) {
			this.setExpression(expression);
		}

		public WeightedRegularExpression getExpression() {
			return this.expression;
		}

		public void setExpression(WeightedRegularExpression expression) {
			expression.setParent(this);
			this.expression = expression;
		}

		@Override
		public String toString() {
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = this.expression.toString();
			if (Sum.class.isAssignableFrom(this.expression.getClass())
					|| Product.class.isAssignableFrom(this.expression.getClass())
					|| LeftMultiply.class.isAssignableFrom(this.expression.getClass())
					|| RightMultiply.class.isAssignableFrom(this.expression.getClass())) {
				string = string = writingData.openPar.toString() + string + writingData.closePar.toString();
			}
			string = string + writingData.starSym.toString();
			return string;
		}
	}

	public static class LeftMultiply extends WeightedRegularExpression {

		private Object weightValue;
		private WeightedRegularExpression expression;

		public LeftMultiply() {
		}

		public LeftMultiply(Object weightValue, WeightedRegularExpression expression) {
			this.setWeightValue(weightValue);
			this.setExpression(expression);
		}

		public Object getWeightValue() {
			return this.weightValue;
		}

		public void setWeightValue(Object weightValue) {
			this.weightValue = weightValue;
		}

		public WeightedRegularExpression getExpression() {
			return this.expression;
		}

		public void setExpression(WeightedRegularExpression expression) {
			expression.setParent(this);
			this.expression = expression;
		}

		@Override
		public String toString() {
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = writingData.weightOpening.toString() + weightValue.toString() + writingData.weightClosing.toString();
			if (Sum.class.isAssignableFrom(this.expression.getClass())
					|| Product.class.isAssignableFrom(this.expression.getClass())
					|| Star.class.isAssignableFrom(this.expression.getClass())) {
				string = string + writingData.openPar.toString() + this.expression.toString() + writingData.closePar.toString();
			} else {
				string = string + this.expression.toString();
			}
			return string;
		}
	}

	public static class RightMultiply extends WeightedRegularExpression {

		private WeightedRegularExpression expression;
		private Object weightValue;

		public RightMultiply() {
		}

		public RightMultiply(WeightedRegularExpression expression, Object weightValue) {
			this.setExpression(expression);
			this.setWeightValue(weightValue);
		}

		public WeightedRegularExpression getExpression() {
			return this.expression;
		}

		public void setExpression(WeightedRegularExpression expression) {
			expression.setParent(this);
			this.expression = expression;
		}

		public Object getWeightValue() {
			return this.weightValue;
		}

		public void setWeightValue(Object weightValue) {
			this.weightValue = weightValue;
		}

		@Override
		public String toString() {
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = this.expression.toString();
			if (Sum.class.isAssignableFrom(this.expression.getClass())
					|| Product.class.isAssignableFrom(this.expression.getClass())) {
				string = writingData.openPar.toString() + string + writingData.closePar.toString();
			}
			string = string + writingData.weightOpening.toString() + weightValue.toString() + writingData.weightClosing.toString();
			return string;
		}
	}

	public static void main(String args[]) {

		AutomataInterface.WritingData writingData = new AutomataInterface.WritingData();
		writingData.closePar = ')';
		writingData.openPar = '(';
		writingData.plusSym = '+';
		writingData.spacesSym = ' ';
		writingData.starSym = '*';
		writingData.timesSym = '.';
		writingData.weightClosing = '}';
		writingData.weightOpening = '{';
		writingData.zeroSym = '0';

		AutomataInterface.Weight weight = new AutomataInterface.Weight();
		weight.semiring = TAFKitInterface.AutomataType.Semiring.Z_INTEGER;
		weight.identitySymbol = (int) 1;
		weight.zeroSymbol = (int) 0;

		AutomataInterface.Alphabet alphabet = new AutomataInterface.Alphabet();
		alphabet.dataType = TAFKitInterface.AutomataType.AlphabetDataType.CHAR;
		alphabet.allSymbols.add('a');
		alphabet.allSymbols.add('b');
		alphabet.identitySymbol = 'e';

		Sum sum = new Sum();
		sum.setWritingData(writingData);
		sum.setWeight(weight);
		sum.setAlphabet(alphabet);

		sum.setLeftExpression(new Star(new Sum(new LeftMultiply((int) 2, new Atomic('a')), new Product(new LeftMultiply((int) 3, new Atomic('b')), new LeftMultiply((int) 4, new Atomic('a'))))));
		sum.setRightExpression(new LeftMultiply((int) 5, new Atomic('b')));
		System.out.println(sum.toString());

		Star star = new Star();
		star.setWritingData(writingData);
		star.setWeight(weight);
		star.setAlphabet(alphabet);

		star.setExpression(new Zero());
		System.out.println(star.toString());

		star.setExpression(new One());
		System.out.println(star.toString());

		star.setExpression(new Atomic('b'));
		System.out.println(star.toString());

		star.setExpression(new LeftMultiply((int) 5, new Atomic('b')));
		System.out.println(star.toString());

		star.setExpression(new RightMultiply(new Atomic('a'), (int) 9));
		System.out.println(star.toString());

		star.setExpression(new Product(new Atomic('b'), new Atomic('a')));
		System.out.println(star.toString());

	}  // End public static void main(String args[])
}  // End public class WeightedRegularExpression

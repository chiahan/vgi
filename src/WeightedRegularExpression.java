
import java.text.NumberFormat;

/**
 *
 * @author JLiu
 */
public abstract class WeightedRegularExpression {

	public class HighlightRange {

		public int start;
		public int end;

		public HighlightRange() {
		}

		public HighlightRange(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}  // End public class HighlightRange
	private WeightedRegularExpression parent;
	private AutomataInterface.Alphabet alphabet;
	private AutomataInterface.Weight weight;
	private AutomataInterface.WritingData writingData;

	public final WeightedRegularExpression getParent() {
		return this.parent;
	}

	public final void setParent(WeightedRegularExpression parent) {
		this.parent = parent;
	}

	public final AutomataInterface.Alphabet getAlphabet() {
		if (this.alphabet == null) {
			if (this.parent == null) {
				return null;
			}
			this.setAlphabet(this.parent.getAlphabet());
		}
		return this.alphabet;
	}

	public final void setAlphabet(AutomataInterface.Alphabet alphabet) {
		this.alphabet = alphabet;
		if (this.parent != null) {
			this.parent.setAlphabet(alphabet);
		}
	}

	public final AutomataInterface.Weight getWeight() {
		if (this.weight == null) {
			if (this.parent == null) {
				return null;
			}
			this.setWeight(this.parent.getWeight());
		}
		return this.weight;
	}

	public final void setWeight(AutomataInterface.Weight weight) {
		this.weight = weight;
		if (this.parent != null) {
			this.parent.setWeight(weight);
		}
	}

	public final AutomataInterface.WritingData getWritingData() {
		if (this.writingData == null) {
			if (this.parent == null) {
				return null;
			}
			this.setWritingData(this.parent.getWritingData());
		}
		return this.writingData;
	}

	public final void setWritingData(AutomataInterface.WritingData writingData) {
		this.writingData = writingData;
		if (this.parent != null) {
			this.parent.setWritingData(writingData);
		}
	}

	public WeightedRegularExpression getFirstSelectedExpression() {
		return this;
	}

	public HighlightRange getHighlightRange() {
		int startOffset = 0;
		if ((this.parent != null)
				&& (ChildEnumerable.class.isInstance(this.parent))) {
			startOffset = ((ChildEnumerable) this.parent).getHighlightStartOffsetOfThisChild(this);
		}
		return new HighlightRange(startOffset, startOffset + this.toString().length());
	}

	public WeightedRegularExpression getNextSelectedExpression() {
		if ((this.parent == null)
				|| (!(ChildEnumerable.class.isInstance(this.parent)))) {
			return null;
		}
		return ((ChildEnumerable) this.parent).getSelectedExpressionAfterThisChild(this);
	}

	public WeightedRegularExpression getPreviousSelectedExpression() {
		if ((this.parent == null)
				|| (!(ChildEnumerable.class.isInstance(this.parent)))) {
			return null;
		}
		return ((ChildEnumerable) this.parent).getSelectedExpressionBeforeThisChild(this);
	}

	public interface ChildEnumerable {

		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child);

		public WeightedRegularExpression getSelectedExpressionAfterThisChild(WeightedRegularExpression child);

		public WeightedRegularExpression getSelectedExpressionBeforeThisChild(WeightedRegularExpression child);
	}

	@Override
	public String toString() {
		if ((this.getAlphabet() == null)
				|| (this.getWeight() == null)
				|| (this.getWritingData() == null)) {
			throw new RuntimeException("Alphabet, weight, and writing data required by " + this.getClass().getName() + ".toString() are unavailable!");
		}
		return super.toString();
	}

	public static abstract class ExpressionWithOneChildExpression extends WeightedRegularExpression
			implements ChildEnumerable {

		private WeightedRegularExpression expression;

		public ExpressionWithOneChildExpression() {
			super();
		}

		public ExpressionWithOneChildExpression(WeightedRegularExpression expression) {
			this.setExpression(expression);
		}

		public final WeightedRegularExpression getExpression() {
			return this.expression;
		}

		public final void setExpression(WeightedRegularExpression expression) {
			this.expression = expression;
			if (expression != null) {
				expression.setParent(this);
			}
		}

		@Override
		public WeightedRegularExpression getFirstSelectedExpression() {
			return this.expression.getFirstSelectedExpression();
		}

		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {
			if (child == null) {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}
			WeightedRegularExpression parent = this.getParent();
			if ((parent != null)
					&& (ChildEnumerable.class.isInstance(parent))) {
				return ((ChildEnumerable) parent).getHighlightStartOffsetOfThisChild(this);
			} else {
				return 0;
			}
		}

		@Override
		public WeightedRegularExpression getNextSelectedExpression() {
			WeightedRegularExpression parent = this.getParent();
			if ((parent == null)
					|| (!(ChildEnumerable.class.isInstance(parent)))) {
				return null;
			}
			return ((ChildEnumerable) parent).getSelectedExpressionAfterThisChild(this);
		}

		@Override
		public WeightedRegularExpression getPreviousSelectedExpression() {
//			if (LeftMultiply.class.isInstance(this.expression)) {
//				return ((LeftMultiply) this.expression).getExpression();
//			}
			return this.expression;
		}

		@Override
		public WeightedRegularExpression getSelectedExpressionAfterThisChild(WeightedRegularExpression child) {
			if (this.expression.equals(child)) {
				return this;
			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}
		}

		@Override
		public WeightedRegularExpression getSelectedExpressionBeforeThisChild(WeightedRegularExpression child) {
			if (!(this.expression.equals(child))) {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}
			WeightedRegularExpression parent = this.getParent();
			if ((parent == null)
					|| (!(ChildEnumerable.class.isInstance(parent)))) {
				return null;
			}
			return ((ChildEnumerable) parent).getSelectedExpressionBeforeThisChild(this);
		}
	}  // End public static abstract class ExpressionWithOneChildExpression extends WeightedRegularExpression

	public static abstract class ExpressionWithTwoChildrenExpressions extends WeightedRegularExpression
			implements ChildEnumerable {

		private WeightedRegularExpression leftExpression;
		private WeightedRegularExpression rightExpression;

		public ExpressionWithTwoChildrenExpressions() {
			super();
		}

		public ExpressionWithTwoChildrenExpressions(WeightedRegularExpression leftExpression, WeightedRegularExpression rightExpression) {
			this.setLeftExpression(leftExpression);
			this.setRightExpression(rightExpression);
		}

		public final WeightedRegularExpression getLeftExpression() {
			return this.leftExpression;
		}

		public final void setLeftExpression(WeightedRegularExpression leftExpression) {
			this.leftExpression = leftExpression;
			if (leftExpression != null) {
				leftExpression.setParent(this);
			}
		}

		public final WeightedRegularExpression getRightExpression() {
			return this.rightExpression;
		}

		public final void setRightExpression(WeightedRegularExpression rightExpression) {
			this.rightExpression = rightExpression;
			if (rightExpression != null) {
				rightExpression.setParent(this);
			}
		}

		@Override
		public WeightedRegularExpression getFirstSelectedExpression() {
			return this.leftExpression.getFirstSelectedExpression();
		}

		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {
			if (child == null) {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}
			WeightedRegularExpression parent = this.getParent();
			if ((parent != null)
					&& (ChildEnumerable.class.isInstance(parent))) {
				return ((ChildEnumerable) parent).getHighlightStartOffsetOfThisChild(this);
			} else {
				return 0;
			}
		}

		@Override
		public WeightedRegularExpression getNextSelectedExpression() {
			WeightedRegularExpression parent = this.getParent();
			if ((parent == null)
					|| (!(ChildEnumerable.class.isInstance(parent)))) {
				return null;
			}
			return ((ChildEnumerable) parent).getSelectedExpressionAfterThisChild(this);
		}

		@Override
		public WeightedRegularExpression getPreviousSelectedExpression() {
//			if (LeftMultiply.class.isInstance(this.rightExpression)) {
//				return ((LeftMultiply) this.rightExpression).getExpression();
//			}
			return this.rightExpression;
		}

		@Override
		public WeightedRegularExpression getSelectedExpressionAfterThisChild(WeightedRegularExpression child) {
			if (this.leftExpression.equals(child)) {
				return this.rightExpression.getFirstSelectedExpression();
			} else if (this.rightExpression.equals(child)) {
				return this;
			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}
		}

		@Override
		public WeightedRegularExpression getSelectedExpressionBeforeThisChild(WeightedRegularExpression child) {
			if (this.leftExpression.equals(child)) {
				WeightedRegularExpression parent = this.getParent();
				if ((parent == null)
						|| (!(ChildEnumerable.class.isInstance(parent)))) {
					return null;
				}
				return ((ChildEnumerable) parent).getSelectedExpressionBeforeThisChild(this);
			} else if (this.rightExpression.equals(child)) {
				return this.getLeftExpression();
			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}
		}
	}  // End public static abstract class ExpressionWithTwoChildrenExpressions extends WeightedRegularExpression

	public static class Zero extends WeightedRegularExpression {

		@Override
		public String toString() {
			super.toString();
			return this.getWritingData().zeroSym.toString();
		}
	}  // End public static class Zero extends WeightedRegularExpression

	public static class One extends WeightedRegularExpression {

		@Override
		public String toString() {
			super.toString();
			return this.getAlphabet().identitySymbol.toString();
		}
	}  // End public static class One extends WeightedRegularExpression

	public static class Atomic extends WeightedRegularExpression {

		private Object symbol;

		public Atomic() {
		}

		public Atomic(Object value) {
			this.setSymbol(value);
		}

		public final Object getSymbol() {
			return this.symbol;
		}

		public final void setSymbol(Object symbol) {
			this.symbol = symbol;
		}

		@Override
		public String toString() {
			super.toString();
			return symbol.toString();
		}
	}  // End public static class Atomic extends WeightedRegularExpression

	public static class Sum extends ExpressionWithTwoChildrenExpressions {

		public Sum() {
			super();
		}

		public Sum(WeightedRegularExpression leftExpression, WeightedRegularExpression rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {

			int startOffset = super.getHighlightStartOffsetOfThisChild(child);
			WeightedRegularExpression leftExpression = this.getLeftExpression();
			WeightedRegularExpression rightExpression = this.getRightExpression();
			AutomataInterface.WritingData writingData = this.getWritingData();

			if (child.equals(leftExpression)) {
				return startOffset;
			} else if (child.equals(rightExpression)) {
				return startOffset + leftExpression.toString().length() + writingData.plusSym.toString().length();
			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}

		}  // End public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child)

		@Override
		public String toString() {
			super.toString();
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = this.getLeftExpression().toString() + writingData.plusSym.toString() + this.getRightExpression().toString();
			return string;
		}
	}  // End public static class Sum extends ExpressionWithTwoChildrenExpressions

	public static class Product extends ExpressionWithTwoChildrenExpressions {

		public Product() {
			super();
		}

		public Product(WeightedRegularExpression leftExpression, WeightedRegularExpression rightExpression) {
			super(leftExpression, rightExpression);
		}

		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {

			int startOffset = super.getHighlightStartOffsetOfThisChild(child);
			WeightedRegularExpression leftExpression = this.getLeftExpression();
			WeightedRegularExpression rightExpression = this.getRightExpression();
			AutomataInterface.WritingData writingData = this.getWritingData();

			if (child.equals(leftExpression)) {

				if (Sum.class.isInstance(leftExpression)) {
					startOffset = startOffset + writingData.openPar.toString().length();
				}

			} else if (child.equals(rightExpression)) {

				startOffset = startOffset + leftExpression.toString().length() + writingData.timesSym.toString().length();
				if (Sum.class.isInstance(leftExpression)) {
					startOffset = startOffset + writingData.openPar.toString().length() + writingData.closePar.toString().length();
				}
				if (Sum.class.isInstance(rightExpression)) {
					startOffset = startOffset + writingData.openPar.toString().length();
				}

			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}

			return startOffset;
		}  // End public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child)

		@Override
		public String toString() {
			super.toString();
			AutomataInterface.WritingData writingData = this.getWritingData();
			WeightedRegularExpression leftExpression = this.getLeftExpression();
			String string = leftExpression.toString();
			if (Sum.class.isInstance(leftExpression)) {
				string = writingData.openPar.toString() + string + writingData.closePar.toString();
			}
			string = string + writingData.timesSym.toString();
			WeightedRegularExpression rightExpression = this.getRightExpression();
			if (Sum.class.isInstance(rightExpression)) {
				string = string + writingData.openPar.toString() + rightExpression.toString() + writingData.closePar.toString();
			} else {
				string = string + rightExpression.toString();
			}
			return string;
		}
	}  // End public static class Product extends ExpressionWithTwoChildrenExpressions

	public static class Star extends ExpressionWithOneChildExpression {

		public Star() {
			super();
		}

		public Star(WeightedRegularExpression expression) {
			super(expression);
		}

		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {

			int startOffset = super.getHighlightStartOffsetOfThisChild(child);
			WeightedRegularExpression expression = this.getExpression();
			AutomataInterface.WritingData writingData = this.getWritingData();

			if (child.equals(expression)) {

				if (Sum.class.isInstance(expression)
						|| Product.class.isInstance(expression)
						|| LeftMultiply.class.isInstance(expression)
						|| RightMultiply.class.isInstance(expression)) {
					startOffset = startOffset + writingData.openPar.toString().length();
				}

			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}

			return startOffset;
		}  // End public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child)

		@Override
		public String toString() {
			super.toString();
			AutomataInterface.WritingData writingData = this.getWritingData();
			WeightedRegularExpression expression = this.getExpression();
			String string = expression.toString();
			if (Sum.class.isInstance(expression)
					|| Product.class.isInstance(expression)
					|| LeftMultiply.class.isInstance(expression)
					|| RightMultiply.class.isInstance(expression)) {
				string = writingData.openPar.toString() + string + writingData.closePar.toString();
			}
			string = string + writingData.starSym.toString();
			return string;
		}
	}  // End public static class Star extends ExpressionWithOneChildExpression

	public static class LeftMultiply extends ExpressionWithOneChildExpression {

		private Object weightValue;

		public LeftMultiply() {
			super();
		}

		public LeftMultiply(Object weightValue, WeightedRegularExpression expression) {
			super(expression);
			this.setWeightValue(weightValue);
		}

		public final Object getWeightValue() {
			return this.weightValue;
		}

		public final void setWeightValue(Object weightValue) {
			this.weightValue = weightValue;
		}

		public String getWeightString() {
			if (Double.class.isInstance(this.weightValue)) {
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setGroupingUsed(false);
				return numberFormat.format((Double) this.weightValue);
			} else {
				return this.weightValue.toString();
			}
		}

//		@Override
//		public WeightedRegularExpression getFirstSelectedExpression() {
//			return this;
//		}
//
//		@Override
//		public WeightedRegularExpression getNextSelectedExpression() {
//			return this.getExpression().getFirstSelectedExpression();
//		}
//
//		@Override
//		public WeightedRegularExpression getPreviousSelectedExpression() {
//			WeightedRegularExpression parent = this.getParent();
//			if ((parent == null)
//					|| (!(ChildEnumerable.class.isInstance(parent)))) {
//				return null;
//			}
//			WeightedRegularExpression expression = ((ChildEnumerable) parent).getSelectedExpressionBeforeThisChild(this);
//			if (LeftMultiply.class.isInstance(expression)) {
//				return ((LeftMultiply) expression).getExpression();
//			}
//			return expression;
//		}
//
//		@Override
//		public WeightedRegularExpression getSelectedExpressionAfterThisChild(WeightedRegularExpression child) {
//			if (!(this.getExpression().equals(child))) {
//				throw new IllegalArgumentException("Required input is not a child of this node!");
//			}
//			WeightedRegularExpression parent = this.getParent();
//			if ((parent == null)
//					|| (!(ChildEnumerable.class.isInstance(parent)))) {
//				return null;
//			}
//			return ((ChildEnumerable) parent).getSelectedExpressionAfterThisChild(this);
//		}
//
//		@Override
//		public WeightedRegularExpression getSelectedExpressionBeforeThisChild(WeightedRegularExpression child) {
//			return this;
//		}
//
//		@Override
//		public HighlightRange getHighlightRange() {
//			int startOffset = super.getHighlightRange().start;
//			AutomataInterface.WritingData writingData = this.getWritingData();
//			return new HighlightRange(startOffset,
//					startOffset + writingData.weightOpening.toString().length()
//					+ this.getWeightString().length()
//					+ writingData.weightClosing.toString().length());
//		}

		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {

			int startOffset = super.getHighlightStartOffsetOfThisChild(child);
			WeightedRegularExpression expression = this.getExpression();
			AutomataInterface.WritingData writingData = this.getWritingData();

			if (child.equals(expression)) {

				startOffset = startOffset + writingData.weightOpening.toString().length()
						+ this.getWeightString().length()
						+ writingData.weightClosing.toString().length();

				if (Sum.class.isInstance(expression)
						|| Product.class.isInstance(expression)
						|| Star.class.isInstance(expression)) {
					startOffset = startOffset + writingData.openPar.toString().length();
				}

			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}

			return startOffset;
		}  // End public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child)

		@Override
		public String toString() {
			super.toString();
			AutomataInterface.WritingData writingData = this.getWritingData();
			String string = writingData.weightOpening.toString() + this.getWeightString() + writingData.weightClosing.toString();
			WeightedRegularExpression expression = this.getExpression();
			if (Sum.class.isInstance(expression)
					|| Product.class.isInstance(expression)
					|| Star.class.isInstance(expression)) {
				string = string + writingData.openPar.toString() + expression.toString() + writingData.closePar.toString();
			} else {
				string = string + expression.toString();
			}
			return string;
		}
	}  // End public static class LeftMultiply extends ExpressionWithOneChildExpression

	public static class RightMultiply extends ExpressionWithOneChildExpression {

		private Object weightValue;

		public RightMultiply() {
			super();
		}

		public RightMultiply(WeightedRegularExpression expression, Object weightValue) {
			super(expression);
			this.setWeightValue(weightValue);
		}

		public final Object getWeightValue() {
			return this.weightValue;
		}

		public final void setWeightValue(Object weightValue) {
			this.weightValue = weightValue;
		}

		public String getWeightString() {
			if (Double.class.isInstance(this.weightValue)) {
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setGroupingUsed(false);
				return numberFormat.format((Double) this.weightValue);
			} else {
				return this.weightValue.toString();
			}
		}

//		@Override
//		public HighlightRange getHighlightRange() {
//
//			int startOffset = super.getHighlightRange().start;
//			WeightedRegularExpression expression = this.getExpression();
//			AutomataInterface.WritingData writingData = this.getWritingData();
//			startOffset = startOffset + expression.toString().length();
//
//			if (Sum.class.isInstance(expression)
//					|| Product.class.isInstance(expression)) {
//				startOffset = startOffset + writingData.openPar.toString().length()
//						+ writingData.closePar.toString().length();
//			}
//
//			return new HighlightRange(startOffset,
//					startOffset + writingData.weightOpening.toString().length()
//					+ this.getWeightString().length()
//					+ writingData.weightClosing.toString().length());
//		}  // End public HighlightRange getHighlightRange()
		@Override
		public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child) {

			int startOffset = super.getHighlightStartOffsetOfThisChild(child);
			WeightedRegularExpression expression = this.getExpression();
			AutomataInterface.WritingData writingData = this.getWritingData();

			if (child.equals(expression)) {

				if (Sum.class.isInstance(expression)
						|| Product.class.isInstance(expression)) {
					startOffset = startOffset + writingData.openPar.toString().length();
				}

			} else {
				throw new IllegalArgumentException("Required input is not a child of this node!");
			}

			return startOffset;
		}  // End public int getHighlightStartOffsetOfThisChild(WeightedRegularExpression child)

		@Override
		public String toString() {
			super.toString();
			AutomataInterface.WritingData writingData = this.getWritingData();
			WeightedRegularExpression expression = this.getExpression();
			String string = expression.toString();
			if (Sum.class.isInstance(expression)
					|| Product.class.isInstance(expression)) {
				string = writingData.openPar.toString() + string + writingData.closePar.toString();
			}
			string = string + writingData.weightOpening.toString() + this.getWeightString() + writingData.weightClosing.toString();
			return string;
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

			Object symbol_a = alphabet.allSymbols.get(0);
			Object symbol_b = alphabet.allSymbols.get(1);

			WeightedRegularExpression expression = new Atomic(symbol_a);
			expression = new LeftMultiply((int) 2, expression);
			expression = new Sum(expression, null);
			WeightedRegularExpression expression2 = new Atomic(symbol_b);
			expression2 = new LeftMultiply((int) 3, expression2);
			expression2 = new Product(expression2, null);
			WeightedRegularExpression expression3 = new Atomic(symbol_a);
			expression3 = new LeftMultiply((int) 4, expression3);
			((Product) expression2).setRightExpression(expression3);
			((Sum) expression).setRightExpression(expression2);
			expression = new Star(expression);
			expression = new Sum(expression, null);
			expression2 = new Atomic(symbol_b);
			expression2 = new LeftMultiply((int) 5, expression2);
			((Sum) expression).setRightExpression(expression2);
			expression2.setAlphabet(alphabet);
			expression2.setWeight(weight);
			expression2.setWritingData(writingData);
			System.out.println(expression.toString());

			expression2 = expression;
			expression = expression.getFirstSelectedExpression();
			while (expression != null) {
				System.out.println(expression.toString());
				HighlightRange highlightRange = expression.getHighlightRange();
				System.out.println("start: " + highlightRange.start + ", end: " + highlightRange.end);
				expression = expression.getNextSelectedExpression();
			}

			expression = expression2;
			while (expression != null) {
				System.out.println(expression.toString());
				expression = expression.getPreviousSelectedExpression();
			}

			Star star = new Star();
			star.setWritingData(writingData);
			star.setWeight(weight);
			star.setAlphabet(alphabet);

			star.setExpression(new Zero());
			System.out.println(star.toString());

			star.setExpression(new One());
			System.out.println(star.toString());

			star.setExpression(new Atomic(symbol_b));
			System.out.println(star.toString());

			star.setExpression(new LeftMultiply((int) 5, new Atomic(symbol_b)));
			System.out.println(star.toString());

			star.setExpression(new RightMultiply(new Atomic(symbol_a), (int) 9));
			System.out.println(star.toString());

			star.setExpression(new Product(new Atomic(symbol_b), new Atomic(symbol_a)));
			System.out.println(star.toString());

		}  // End public static void main(String args[])
	}  // End public static class RightMultiply extends ExpressionWithOneChildExpression
}  // End public class WeightedRegularExpression

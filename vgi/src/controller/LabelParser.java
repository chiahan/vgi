package controller;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JOptionPane;
import model.Automata2.FreeMonElmt;
import model.Automata2.FreeMonoid;
import model.Automata2.FreeMonoid.GenKindE;
import model.Automata2.FreeMonoid.GenSortE;
import model.Automata2.GenCompSort;
import model.Automata2.GenCompSort.ValueE;
import model.Automata2.GenSort;
import model.Automata2.Label;
import model.Automata2.LeftExtMul;
import model.Automata2.MonCompGen;
import model.Automata2.MonGen;
import model.Automata2.NumericalSemiring;
import model.Automata2.NumericalSemiring.SetE;
import model.Automata2.NumericalSemiringWeight;
import model.Automata2.One;
import model.Automata2.Product;
import model.Automata2.ProductMonElmt;
import model.Automata2.ProductMonoid;
import model.Automata2.RegExpBody;
import model.Automata2.Star;
import model.Automata2.Sum;
import model.Automata2.TypedRegExp;
import model.Automata2.ValueType;
import model.Automata2.ValueType.MonoidTypeE;
import model.Automata2.ValueType.SemiringTypeE;
import model.Automata2.Zero;
import model.DataModel;
import model.DataModelInterface;

/**
 * This class is used to parse string data into Label format.
 *
 * @author rasielchang
 */
public class LabelParser {

    DataModelInterface model;
    ValueType valueType;
    Vector<String[]> alphabetV;
    int seriesProdDim;
    int prodDim;
    boolean isWeighted;
    boolean isTuple;
    String oneSymbol;
    String zeroSymbol;
    SetE semiringSet;
    Vector<GenKindE> monoidGenKind;
    Vector monoidGenSort;

    /**
    * The constructor.
    *
    * @param model is the DataModelInterface.
    *
    * @param valueType is the value type.
    *
    * @author rasielchang
    */
    public LabelParser(DataModelInterface model, ValueType valueType) {
        if (model != null && valueType != null) {
            this.model = model;
            this.valueType = valueType;
            alphabetV = new Vector();
            parseValueType();
        } else {
            JOptionPane.showMessageDialog(null,
                    "ValueType or Model can not be null!!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
    * Use to set value type.
    *
    * @param valueType is the new value type.
    *
    * @author rasielchang
    */    
    public void setValueType(ValueType valueType) {
        if (valueType != null) {
            this.valueType = valueType;
            parseValueType();
        } else {
            JOptionPane.showMessageDialog(null,
                    "ValueType can not be null!!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
    * Use to parse the value type.
    *    
    * @author rasielchang
    */
    private void parseValueType() {
        String[] alphabet;
        zeroSymbol = valueType.getZeroSymbol();
        oneSymbol = valueType.getOneSymbol();

        if (valueType.getSemiringType() == SemiringTypeE.NUMERICAL) {
            semiringSet = ((NumericalSemiring) valueType.getSemiring()).getSetE();
            monoidGenKind = new Vector();
            monoidGenSort = new Vector();
            Vector<FreeMonoid> freeMonoidV;
            if (valueType.getMonoidType() == MonoidTypeE.PRODUCT) {
                ProductMonoid productMonoid = (ProductMonoid) valueType.getMonoid();
                freeMonoidV = productMonoid.getFreeMonoidV();
                prodDim = freeMonoidV.size();
            } else {
                freeMonoidV = new Vector();
                freeMonoidV.add((FreeMonoid) valueType.getMonoid());
                prodDim = 1;
            }
            for (int q = 0; q < freeMonoidV.size(); q++) {
                FreeMonoid Monoid = (FreeMonoid) freeMonoidV.get(q);
                Vector MonGenV = Monoid.getMonGenV();
                alphabet = new String[MonGenV.size()];
                if (Monoid.getGenKindE() == GenKindE.SIMPLE) {
                    for (int i = 0; i < MonGenV.size(); i++) {
                        MonGen temp = (MonGen) MonGenV.get(i);
                        alphabet[i] = temp.getValue();
                    }
                    monoidGenKind.add(GenKindE.SIMPLE);
                    monoidGenSort.add(Monoid.getGenSortE());
                } else if (Monoid.getGenKindE() == GenKindE.TUPLE) {
                    for (int p = 0; p < MonGenV.size(); p++) {
                        MonGen monGen = (MonGen) MonGenV.get(p);
                        Vector monCompGenV = monGen.getMonCompGenV();
                        String label = "";
                        for (int j = 0; j < monCompGenV.size(); j++) {
                            MonCompGen monCompGen = (MonCompGen) monCompGenV.get(j);
                            if (label.equals("")) {
                                label = monCompGen.getValue();
                            } else {
                                label = label + "," + monCompGen.getValue();
                            }
                        }
                        alphabet[p] = "[" + label + "]";
                    }
                    monoidGenKind.add(GenKindE.TUPLE);
                    for(int p = 0; p < Monoid.getGenSort().getGenCompSortV().size(); p++) {
                        GenCompSort genCompSort = (GenCompSort)Monoid.getGenSort().getGenCompSortV().get(p);
                        monoidGenSort.add(genCompSort.getValueE());
                    }                    
                }//end if                
                alphabetV.add(alphabet);
            }

        } else if (valueType.getSemiringType() == SemiringTypeE.SERIES) {
        }

    }

    /**
    * Use to parse the value type.
    *
    * @param transitionID indicates which transition's label has been changed.
    *
    * @author rasielchang
    */
    public void startParse(int transitionID) {
        boolean stop = false;
        Label temp;
        Vector tempV;
        if (prodDim == 1) {
            String userInput = JOptionPane.showInputDialog(null,
                    "Please keyin a regular expression.",
                    "Set Transition Label",
                    JOptionPane.PLAIN_MESSAGE);
            if (userInput == null) {
            } else if (userInput.equals("")) {
                JOptionPane.showMessageDialog(null,
                        "Please keyin a label!!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                temp = parseOneMonoidLabel(userInput);
                if (temp != null) {
                    model.setTransitionLabel(transitionID, temp, userInput);
                }
            }
        } else {
            String[] userInput = new String[prodDim];
            for (int i = 0; i < prodDim; i++) {
                userInput[i] = JOptionPane.showInputDialog(null,
                        "Please keyin a regular expression.",
                        "Set Transition Label(Monoid " + i + ")",
                        JOptionPane.PLAIN_MESSAGE);
                if (userInput[i] == null) {
                    stop = true;
                    break;
                } else if (userInput[i].equals("")) {
                    stop = true;
                    JOptionPane.showMessageDialog(null,
                            "Please keyin a label!!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
            if (!stop) {
                tempV = parseMultiMonoidLabel(userInput);
                if (tempV != null) {
                    model.setTransitionLabel(transitionID, (Label) tempV.get(0), (String) tempV.get(1));
                }
            }
        }
    }

    /**
    * Use to check if the exp contains character which is not in alphabet.
    *
    * @param exp is the input label.
    *
    * @param alphabet is the alphabet of this automata.
    *
    * @param genkind is the genkind of this automata.
    *
    * @return true means the exp is in alphabet, otherwise is not.
    *
    * @author rasielchang
    */
    private boolean isInAlphabet(String exp, String[] alphabet, GenKindE genKind) {
        String temp = exp;
        boolean isIn = false;
        temp = temp.replace("+", "");
        temp = temp.replace("*", "");
        temp = temp.replace("(", "");
        temp = temp.replace(")", "");
        temp = temp.replace("&", "");
        if (genKind == GenKindE.SIMPLE) {
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) == '{') {
                    while (temp.charAt(i) != '}') {
                        i++;
                    }
                    i++;
                }
                for (int j = 0; j < alphabet.length; j++) {
                    if (temp.charAt(i) == alphabet[j].charAt(0)) {
                        isIn = true;
                    }
                }
                if (temp.charAt(i) == zeroSymbol.charAt(0) || temp.charAt(i) == oneSymbol.charAt(0)) {
                    isIn = true;
                }
                if (!isIn) {
                    JOptionPane.showMessageDialog(null,
                            temp.charAt(i) + " is not in your alphabet!!",
                            "Alphabet Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                isIn = false;
            }
        } else if (genKind == GenKindE.TUPLE) {
            String tempAlph = "";
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) == '{') {
                    while (temp.charAt(i) != '}') {
                        i++;
                    }                    
                } else if (temp.charAt(i) == '[') {
                    while (temp.charAt(i) != ']') {
                        tempAlph = tempAlph + temp.charAt(i);
                        i++;
                    }                    
                    tempAlph = tempAlph + temp.charAt(i);
                    for (int j = 0; j < alphabet.length; j++) {
                        if (tempAlph.equals(alphabet[j])) {
                            isIn = true;
                        }
                    }
                    if (!isIn) {
                        JOptionPane.showMessageDialog(null,
                                tempAlph + " is not in your alphabet!!",
                                "Alphabet Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    isIn = false;
                    tempAlph = "";
                } else if (temp.charAt(i) == zeroSymbol.charAt(0) || temp.charAt(i) == oneSymbol.charAt(0)) {
                } else {
                    JOptionPane.showMessageDialog(null,
                            temp.charAt(i) + " is not in your alphabet!!",
                            "Alphabet Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    /**
    * Use to check if the exp is a legal regular expression.
    *
    * @param exp is the input label.
    *
    * @return true means the exp is a legal regular expression, otherwise is not.
    *
    * @author rasielchang
    */
    private boolean isRegularExpression(String exp) {
        Stack par = new Stack();
        boolean preCharIsOperator = true;

        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '+') {
                if (preCharIsOperator) {
                    JOptionPane.showMessageDialog(null,
                            exp.charAt(i) + " needs two operands!!",
                            "Syntax Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    preCharIsOperator = true;
                }

            } else if (exp.charAt(i) == '*') {
                if (preCharIsOperator) {
                    JOptionPane.showMessageDialog(null,
                            exp.charAt(i) + " needs one operand!!",
                            "Syntax Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    preCharIsOperator = false;
                }

            } else if (exp.charAt(i) == '(') {
                par.push('(');
            } else if (exp.charAt(i) == ')') {
                if (par.empty()) {
                    JOptionPane.showMessageDialog(null,
                            "( Not found!!",
                            "Syntax Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    par.pop();
                }
            } else if (exp.charAt(i) == '[') {
                preCharIsOperator = false;
                i++;
                if (exp.charAt(i) == ']') {
                    JOptionPane.showMessageDialog(null,
                            "Error happended at [ ], It cann't be empty!!",
                            "Syntax Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    String temp = "";
                    while (exp.charAt(i) != ']' && i < exp.length()) {
                        temp = temp + exp.charAt(i);
                        i++;
                    }
                    if (i == exp.length()) {// there are nor any ] till the end of the String.
                        JOptionPane.showMessageDialog(null,
                                "] Not found!!",
                                "Syntax Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    } else {
                        if (temp.contains("+") ||
                                temp.contains("*") ||
                                temp.contains("&") ||
                                temp.contains("{") ||
                                temp.contains("}") ||
                                temp.contains("(") ||
                                temp.contains(")")) {
                            JOptionPane.showMessageDialog(null,
                                    "Error happended at [" + temp + "], It cann't has any operators!!",
                                    "Syntax Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
            } else if (exp.charAt(i) == '{') {
                preCharIsOperator = true;
                i++;
                if (exp.charAt(i) == '}') {
                    JOptionPane.showMessageDialog(null,
                            "Error happended at { }, It need a weight!!",
                            "Weight Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    String temp = "";
                    while (exp.charAt(i) != '}' && i < exp.length()) {
                        temp = temp + exp.charAt(i);
                        i++;
                    }
                    if (i == exp.length()) {// there are nor any } till the end of the String.
                        JOptionPane.showMessageDialog(null,
                                "} Not found!!",
                                "Syntax Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    } else if (i == exp.length() - 1) {// this weight is the last operator in this input string
                        JOptionPane.showMessageDialog(null,
                                "you need a operand after your weight!!",
                                "Syntax Error",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    } else {
                        if (semiringSet == SetE.B) {
                            JOptionPane.showMessageDialog(null,
                                    "Error happended at { }, this is a boolean automaton!!",
                                    "Weight Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        } else if (semiringSet == SetE.Z) {
                            try {
                                Integer.parseInt(temp);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null,
                                        "Error happended at {" + temp + "}, It's not a integer!!",
                                        "Weight Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                        } else if (semiringSet == SetE.R) {
                            try {
                                Float.parseFloat(temp);
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null,
                                        "Error happended at {" + temp + "}, It's not a rational number!!",
                                        "Weight Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                        }
                    }
                }
            } else if (exp.charAt(i) == '}') {
                JOptionPane.showMessageDialog(null,
                        "{ Not found!!",
                        "Syntax Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (exp.charAt(i) == '&') {
                if (preCharIsOperator) {
                    JOptionPane.showMessageDialog(null,
                            exp.charAt(i) + " needs two operands!!",
                            "Syntax Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    preCharIsOperator = true;
                }

            } else {
                preCharIsOperator = false;
            }

        }
        if (par.empty()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                    ") Not found!!",
                    "Syntax Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }

    /**
    * Use to bind servel characters into one operands, also add & operand if it is needed.
    *
    * @param exp is the input label.
    *
    * @return a infix regular expression.
    *
    * @author rasielchang
    */
    private ArrayList bindOperands(String exp) {
        ArrayList arrayList = new ArrayList();
        String temp = "";
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '+') {
                if(!temp.equals("")) {
                    arrayList.add(temp);
                }                
                arrayList.add("+");
                temp = "";
            } else if (exp.charAt(i) == '*') {
                if(!temp.equals("")) {
                    arrayList.add(temp);
                }
                arrayList.add("*");
                temp =
                        "";
            } else if (exp.charAt(i) == '(') {
                if (exp.charAt(i - 1) == '+') {
                    arrayList.add("(");
                } else if (exp.charAt(i - 1) == '*') {
                    arrayList.add("&");
                    arrayList.add(exp.charAt(i));
                } else if (exp.charAt(i - 1) == ')') {
                    arrayList.add("&");
                    arrayList.add("(");
                } else {
                    arrayList.add(temp);
                    arrayList.add("(");
                    temp = "";
                }

            } else if (exp.charAt(i) == ')') {
                if(!temp.equals("")) {
                    arrayList.add(temp);
                }
                arrayList.add(")");
                temp = "";
            } else if (exp.charAt(i) == '[') {
                while (exp.charAt(i) == '[') {
                    while (exp.charAt(i) != ']') {
                        temp = temp + exp.charAt(i);
                        i++;
                    }
                    temp = temp + ']';
                    if(i < exp.length() - 1) {
                        i++;    
                    }                    
                } 
                arrayList.add(temp);
                temp = "";
            } else if (exp.charAt(i) == '{') {
                if (!temp.equals("")) {
                    arrayList.add(temp);
                }
                temp = "{";
            } else if (exp.charAt(i) == '}') {
                temp = temp + "}";
                arrayList.add(temp);
                temp = "";
            } else if (exp.charAt(i) == '&') {
                if(!temp.equals("")) {
                    arrayList.add(temp);
                }
                arrayList.add("&");
                temp = "";
            } else if (exp.charAt(i) == zeroSymbol.charAt(0)) {
                if (!temp.equals("")) {
                    arrayList.add(temp);
                    temp = "";
                }
                arrayList.add(zeroSymbol);
            } else if (exp.charAt(i) == oneSymbol.charAt(0)) {
                if (!temp.equals("")) {
                    arrayList.add(temp);
                    temp = "";
                }
                arrayList.add(oneSymbol);
            } else {
                temp = temp + exp.charAt(i);
            }

        }
        if (!temp.equals("")) {
            arrayList.add(temp);
        }
        return arrayList;
    }

    /**
    * Use to convert infix regular expression into postfix.
    *
    * @param infix is the infix regular expression.
    *
    * @return a postfix regular expression.
    *
    * @author rasielchang
    */
    private static ArrayList convertToPostfix(ArrayList infix) {
        ArrayList postfix = new ArrayList();
        Stack stack = new Stack();

        stack.push("(");
        for (int i = 0; i < infix.size(); i++) {
            if (infix.get(i) == "+") {
                while (stack.peek() != "(") {
                    postfix.add(stack.pop());
                }
                stack.push(infix.get(i));
            } else if (infix.get(i) == "*") {
                while (stack.peek().toString().contains("{")) {
                    postfix.add(stack.pop());
                }

                stack.push(infix.get(i));
            } else if (infix.get(i) == "(") {
                stack.push(infix.get(i));
            } else if (infix.get(i) == ")") {
                while (stack.peek() != "(") {
                    postfix.add(stack.pop());
                }

            } else if (infix.get(i).toString().contains("{")) {
                stack.push(infix.get(i));
            } else if (infix.get(i) == "&") {
                while (stack.peek() != "(" || stack.peek() != "+") {
                    postfix.add(stack.pop());
                }

                stack.push(infix.get(i));
            } else {
                postfix.add(infix.get(i));
            }

        }
        while (stack.peek() != "(") {
            postfix.add(stack.pop());
        }

        return postfix;
    }

    /**
    * Use to parse the input label with one monoid.
    *
    * @param input is the input label.
    *
    * @return the label format variable of input.
    *
    * @author rasielchang
    */
    public Label parseOneMonoidLabel(String input) {
        Label label = null;
        RegExpBody left = null;
        RegExpBody right = null;
        TypedRegExp typedRegExp = null;
        Vector<MonGen> monGenV = null;
        FreeMonElmt leftEle = new FreeMonElmt(monGenV);
        FreeMonElmt rightEle = new FreeMonElmt(monGenV);
        ArrayList output = new ArrayList();
        Stack operands = new Stack();
        Stack<Boolean> isTypedRegExp = new Stack();
        String temp = "";

        input = input.replace(" ", "");
        if (isRegularExpression(input)) {
            if (isInAlphabet(input, alphabetV.get(0), monoidGenKind.get(0))) {
                output = convertToPostfix(bindOperands(input));
                for (int i = 0; i < output.size(); i++) {
                    temp = "" + output.get(i);
                    if (temp.equals("+")) {
                        if (isTypedRegExp.pop()) {
                            typedRegExp = (TypedRegExp) operands.pop();
                            right = typedRegExp.getRegExpBody();
                        } else {
                            right = (RegExpBody) operands.pop();
                        }

                        if (isTypedRegExp.pop()) {
                            typedRegExp = (TypedRegExp) operands.pop();
                            left =
                                    typedRegExp.getRegExpBody();
                        } else {
                            left = (RegExpBody) operands.pop();
                        }

                        operands.add(new TypedRegExp(new Sum(left, right)));
                        isTypedRegExp.add(true);
                    } else if (temp.equals("*")) {
                        if (isTypedRegExp.pop()) {
                            typedRegExp = (TypedRegExp) operands.pop();
                            left =
                                    typedRegExp.getRegExpBody();
                        } else {
                            left = (RegExpBody) operands.pop();
                        }

                        operands.add(new TypedRegExp(new Star(left)));
                        isTypedRegExp.add(true);
                    } else if (temp.equals("&")) {
                        if (isTypedRegExp.pop()) {
                            typedRegExp = (TypedRegExp) operands.pop();
                            right = typedRegExp.getRegExpBody();
                        } else {
                            right = (RegExpBody) operands.pop();
                        }

                        if (isTypedRegExp.pop()) {
                            typedRegExp = (TypedRegExp) operands.pop();
                            left =
                                    typedRegExp.getRegExpBody();
                        } else {
                            left = (RegExpBody) operands.pop();
                        }

                        operands.add(new TypedRegExp(new Product(left, right)));
                        isTypedRegExp.add(true);
                    } else if (temp.toString().contains("{")) {
                        temp = temp.replace("{", "");
                        temp =
                                temp.replace("}", "");
                        if (isTypedRegExp.pop()) {
                            typedRegExp = (TypedRegExp) operands.pop();
                            left =
                                    typedRegExp.getRegExpBody();
                        } else {
                            left = (RegExpBody) operands.pop();
                        }
                        operands.add(new TypedRegExp(new LeftExtMul(new NumericalSemiringWeight(temp), left)));
                        isTypedRegExp.add(true);
                    } else if (temp.equals(zeroSymbol)) {
                        operands.push(new TypedRegExp(new Zero()));
                        isTypedRegExp.push(true);
                    } else if (temp.equals(oneSymbol)) {
                        operands.push(new TypedRegExp(new One()));
                        isTypedRegExp.push(true);
                    } else {
                        if (monoidGenKind.get(0) == GenKindE.SIMPLE) {
                            if (monoidGenSort.get(0) == GenSortE.LETTERS) {
                                monGenV = new Vector();
                                for (int j = 0; j < temp.length(); j++) {
                                    monGenV.add(new MonGen(temp.charAt(j)));
                                }
                                operands.push(new FreeMonElmt(monGenV));
                                isTypedRegExp.push(false);
                            } else if(monoidGenSort.get(0) == GenSortE.INTEGER) {
                                monGenV = new Vector();
                                monGenV.add(new MonGen(temp));
                                operands.push(new FreeMonElmt(monGenV));
                                isTypedRegExp.push(false);
                            }

                        } else if (monoidGenKind.get(0) == GenKindE.TUPLE) {
                            Vector<MonCompGen> monCompGenV = new Vector();
                            MonCompGen monCompGen = null;
                            int k = 0;
                            for(int j = 0; j < monoidGenSort.size(); j++) {
                                k++;
                                if(monoidGenSort.get(j) == ValueE.LETTERS) {                                    
                                    monCompGen = new MonCompGen("" + temp.charAt(k));
                                    monCompGenV.add(monCompGen);
                                    k++;                                    
                                } else if(monoidGenSort.get(j) == ValueE.INTEGER) {
                                    String tempInt = "";
                                    while(temp.charAt(k) != ',' || temp.charAt(k) != ']') {
                                        tempInt = tempInt + temp.charAt(k);
                                        k++;
                                    }
                                    k++;
                                    monCompGen = new MonCompGen(tempInt);
                                    monCompGenV.add(monCompGen);
                                }
                            }
                            monGenV = new Vector();
                            monGenV.add(new MonGen(monCompGenV));
                            operands.push(new FreeMonElmt(monGenV));
                            isTypedRegExp.push(false);                            
                        }
                    }
                }
                if (isTypedRegExp.pop()) {
                    label = new Label((TypedRegExp) operands.pop(), input);
                } else {
                    label = new Label(new TypedRegExp((FreeMonElmt) operands.pop()), input);
                }

                return label;
            }

        }
        return null;
    }

    /**
    * Use to parse the input label with multiple monoids.
    *
    * @param input is the input label.
    *
    * @return the label format variable of input.
    *
    * @author rasielchang
    */
    public Vector parseMultiMonoidLabel(String[] input) {
        Label label = null;
        RegExpBody left = null;
        RegExpBody right = null;
        TypedRegExp typedRegExp = null;
        Vector<MonGen> monGenV = null;
        FreeMonElmt leftEle = new FreeMonElmt(monGenV);
        FreeMonElmt rightEle = new FreeMonElmt(monGenV);
        ArrayList output = new ArrayList();
        Stack operands = new Stack();
        Stack<Boolean> isTypedRegExp = new Stack();
        String temp = "";
        Vector labelV = new Vector();

        for (int q = 0; q < input.length; q++) {
            input[q] = input[q].replace(" ", "");
            if (isRegularExpression(input[q])) {
                if (isInAlphabet(input[q], alphabetV.get(q), monoidGenKind.get(q))) {
                    output = convertToPostfix(bindOperands(input[q]));
                    for (int i = 0; i < output.size(); i++) {
                        temp = "" + output.get(i);
                        if (temp.equals("+")) {
                            if (isTypedRegExp.pop()) {
                                typedRegExp = (TypedRegExp) operands.pop();
                                right = typedRegExp.getRegExpBody();
                            } else {
                                right = (RegExpBody) operands.pop();
                            }
                            if (isTypedRegExp.pop()) {
                                typedRegExp = (TypedRegExp) operands.pop();
                                left = typedRegExp.getRegExpBody();
                            } else {
                                left = (RegExpBody) operands.pop();
                            }
                            operands.add(new TypedRegExp(new Sum(left, right)));
                            isTypedRegExp.add(true);
                        } else if (temp.equals("*")) {
                            if (isTypedRegExp.pop()) {
                                typedRegExp = (TypedRegExp) operands.pop();
                                left = typedRegExp.getRegExpBody();
                            } else {
                                left = (RegExpBody) operands.pop();
                            }
                            operands.add(new TypedRegExp(new Star(left)));
                            isTypedRegExp.add(true);
                        } else if (temp.equals("&")) {
                            if (isTypedRegExp.pop()) {
                                typedRegExp = (TypedRegExp) operands.pop();
                                right = typedRegExp.getRegExpBody();
                            } else {
                                right = (RegExpBody) operands.pop();
                            }
                            if (isTypedRegExp.pop()) {
                                typedRegExp = (TypedRegExp) operands.pop();
                                left = typedRegExp.getRegExpBody();
                            } else {
                                left = (RegExpBody) operands.pop();
                            }
                            operands.add(new TypedRegExp(new Product(left, right)));
                            isTypedRegExp.add(true);
                        } else if (temp.toString().contains("{")) {
                            temp = temp.replace("{", "");
                            temp = temp.replace("}", "");
                            if (isTypedRegExp.pop()) {
                                typedRegExp = (TypedRegExp) operands.pop();
                                left = typedRegExp.getRegExpBody();
                            } else {
                                left = (RegExpBody) operands.pop();
                            }
                            operands.add(new TypedRegExp(new LeftExtMul(new NumericalSemiringWeight(temp), left)));
                            isTypedRegExp.add(true);
                        } else if (temp.equals(zeroSymbol)) {
                            operands.push(new TypedRegExp(new Zero()));
                            isTypedRegExp.push(true);
                        } else if (temp.equals(oneSymbol)) {
                            operands.push(new TypedRegExp(new One()));
                            isTypedRegExp.push(true);
                        } else {
                            monGenV = new Vector();
                            for (int j = 0; j < temp.length(); j++) {
                                monGenV.add(new MonGen(temp.charAt(j)));
                            }
                            operands.push(new FreeMonElmt(monGenV));
                            isTypedRegExp.push(false);
                        }
                    }// End of for
                } else {
                    return null;
                }
            } else {
                return null;
            }

        }// End of for 
        Vector freeMonElmtV = new Vector();
        String labelS = "(";
        for (int i = 0; i < prodDim; i++) {
            if (isTypedRegExp.pop()) {
                typedRegExp = (TypedRegExp) operands.pop();
                freeMonElmtV.add(0, typedRegExp.getRegExpBody());
            } else {
                freeMonElmtV.add(0, (RegExpBody) operands.pop());
            }
            if (i != prodDim - 1) {
                labelS = labelS + " " + input[i] + ",";
            } else {
                labelS = labelS + " " + input[i] + ")";
            }
        }
        label = new Label(new TypedRegExp(new ProductMonElmt(freeMonElmtV)), labelS);
        labelV.add(label);
        labelV.add(labelS);
        return labelV;
    }
}

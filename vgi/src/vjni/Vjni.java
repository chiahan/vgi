package vjni;

import java.io.File;

/**
 * this class serves as the interface between VGI and Vaucanson Library;
 * for this class to work, the vaucanson vjni.so file must be provided in the
 * static section;
 * for what each functions does, refer to vaucanson library user manuals
 * @author rasiel, polar
 */
public class Vjni {


	//********* Boolean Automata Algorithms *************//

	//*  Tests and evaluation on automata:
	// takes automaton as input and return boolean as output, except for eval
	public native boolean boolAreEquivalent(String inputFile1, String inputFile2);
	public native int boolEval(String inputFile, String word);
	public native boolean boolIsAmbiquous(String inputFile);
	public native boolean boolIsComplete(String inputFile);
	public native boolean boolIsDeterministic(String inputFile);
	public native boolean boolIsEmpty(String inputFile);
	public native boolean boolHasSuccComp(String inputFile);
	public native boolean boolIsRealtime(String inputFile);
	public native boolean boolIsStandard(String inputFile);

	//*  Generic algorithms for automata:
	// takes automaton as input and return automaton as output
	// return by writing to file
	public native void boolAccessible(String inputFile, String outputFile);
	public native void boolEpsRemoval(String inputFile, String outputFile);
	public native void boolEpsRemovalSp(String inputFile, String outputFile);
	public native void boolCoAccessible(String inputFile, String outputFile);
	public native void boolComplete(String inputFile, String outputFile);
	public native void boolConcatenate(String inputFile1, String inputFile2, String outputFile);
	public native void boolPower(String inputFile, int n, String outputFile);
	public native void boolProduct(String inputFile1, String inputFile2, String outputFile);
	public native void boolQuotient(String inputFile, String outputFile);
	public native void boolRealtime(String inputFile, String outputFile);
	public native void boolStandardize(String inputFile, String outputFile);
	public native void boolUnionOfStandard(String inputFile1, String inputFile2, String outputFile);
	public native void boolConcatOfStandard(String inputFile1, String inputFile2, String outputFile);
	public native void boolStarOfStandard(String inputFile, String outputFile);
	public native void boolUnion(String inputFile1, String inputFile2, String outputFile);
	public native void boolTranspose(String inputFile, String outputFile);
	public native void boolTrim(String inputFile, String outputFile);

	//*  Boolean automaton specific algorithms:
	// takes automaton as input and return automaton as output
	// return by writing to file
	public native void boolComplement(String inputFile, String outputFile);
	public native void boolDeterminize(String inputFile, String outputFile);
	public native void boolMinimize(String inputFile, String outputFile);
	public native void boolMinimizeMoore(String inputFile, String outputFile);

	//*  Conversion between automata and expressions:
	// most functions takes an expression as input
	// unclear of the data type of expressions
	public native void boolAutToExp(String inputFile, String outputFile);
	public native void boolDerivedTerm(String alphabet, String exp, String outputFile);
	public native void boolExpToAut(String alphabet, String exp, String outputFile);
	public native void boolExpand(String alphabet, String exp, String outputFile);
	public native void boolIdentityExp(String alphabet, String exp, String outputFile);
	public native void boolStandard(String alphabet, String exp, String outputFile);
	public native void boolThompson(String alphabet, String exp, String outputFile);


	//********* Weighted Automata Algorithms *************//

	//** tests and evaluation on automata
	public native int wEval(String inputFile, String exp);
	public native boolean wIsAmbiguous(String inputFile);
	public native boolean wIsComplete(String inputFile);
	public native boolean wIsEmpty(String inputFile);
	public native boolean wHasSuccComp(String inputFile);
	public native boolean wIsRealtime(String inputFile);
	public native boolean wIsStandard(String inputFile);

	//** generic algorithms for automata
	public native void wAccessible(String inputFile, String outputFile);
	public native void wEpsRemoval(String inputFile, String outputFile);
	public native void wEpsRemovalSp(String inputFile, String outputFile);
	public native void wCoAccessible(String inputFile, String outputFile);
	public native void wComplete(String inputFile, String outputFile);
	public native void wConcatenate(String inputFile1, String inputFile2, String outputFile);
	public native void wPower(String inputFile, int n, String outputFile);
	public native void wProduct(String inputFile1, String inputFile2, String outputFile);
	public native void wQuotient(String inputFile, String outputFile);
	public native void wRealtime(String inputFile, String outputFile);
	public native void wStandardize(String inputFile, String outputFile);
	public native void wUnionOfStandard(String inputFile1, String inputFile2, String outputFile);
	public native void wConcatOfStandard(String inputFile1, String inputFile2, String outputFile);
	public native void wStarOfStandard(String inputFile, String outputFile);
	public native void wUnion(String inputFile1, String inputFile2, String outputFile);
	public native void wTranspose(String inputFile, String outputFile);
	public native void wTrim(String inputFile, String outputFile);

	//*  Conversion between automata and expressions
	public native void wAutToExp(String inputFile, String outputFile);
	public native void wDerivedTerm(String alphabet, String exp, String outputFile);
	public native void wExpToAut(String alphabet, String exp, String outputFile);
	public native void wExpand(String alphabet, String exp, String outputFile);
	public native void wIdentityExp(String alphabet, String exp, String outputFile);
	public native void wStandard(String alphabet, String exp, String outputFile);
	public native void wThompson(String alphabet, String exp, String outputFile);

	//********* Weighted Automata Algorithms *************//

	//** tests and evaluation on transducer
	public native boolean tIsEmpty(String inputFile);
	public native boolean tHasSuccComp(String inputFile);
	public native boolean tIsSubNormalized(String inputFile);

	//** generic algorithms for transducers
	public native void tEpsRemoval(String inputFile, String outputFile);
	public native void tEpsRemovalSp(String inputFile, String outputFile);
	public native void tDomain(String inputFile, String outputFile);
	public native void tEval(String inputFile, String exp, String outputFile);
	public native void tEvalAut(String inputAut1, String inputAut2, String outputFile);
	public native void tLtlToPair(String inputFile, String outputFile);
	public native void tImage(String inputFile, String outputFile);
	public native void tTranspose(String inputFile, String outputFile);
	public native void tTrim(String inputFile, String outputFile);

	//** algorithms for transducers
	public native void tSubNormalize(String inputFile, String outputFile);
	public native void tCompositionCover(String inputFile, String outputFile);
	public native void tCompositionCoCover(String inputFile, String outputFile);
	public native void tCompose(String inputAut1, String inputAut2, String outputFile);
	public native void tUCompose(String inputAut1, String inputAut2, String outputFile);
	public native void tToRw(String inputFile, String outputFile);
	public native void tInvert(String inputFile, String outputFile);
	public native void tIntersection(String inputFile, String outputFile);

    /**
     * Dynamic Library path
     * vjni.so file must be provided for Vaucanson related functions to work
     */
	static {
		// BEGIN
		// Searches for vjni.so in several possible paths.
        File file = new File("vjni.so");
		if (!file.exists()) {
			file = new File("vjni/vjni.so");
		}
		if (!file.exists()) {
			file = new File("../vjni/vjni.so");
		}
		if (file.exists()) {
			System.load(file.getAbsolutePath());
		}
		// Searches for vjni.so in several possible paths.
		// END
	}

}


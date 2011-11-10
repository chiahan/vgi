#include <vaucanson/automata/concept/automata.hh>
#include <vaucanson/boolean_automaton.hh>
#include <vaucanson/z_automaton.hh>
#include <vaucanson/fmp_transducer.hh>
#include <vaucanson/xml/contexts/fmp.hh>
#include <vaucanson/xml/XML.hh>
#include <vaucanson/tools/dot_display.hh>
#include <vaucanson/tools/io.hh>

//#include <vaucanson/algebra/implementation/series/rat/exp.hh>
#include <vaucanson/algebra/implementation/series/krat_exp_parser.hxx>
#include <vaucanson/automata/implementation/listg_graph_impl.hh>

#include <vaucanson/algorithms/accessible.hh>
#include <vaucanson/algorithms/aut_to_exp.hh>
#include <vaucanson/algorithms/complement.hh>
#include <vaucanson/algorithms/complete.hh>
#include <vaucanson/algorithms/composition_cover.hh>
//#include <vaucanson/algorithms/concatenate.hh>
#include <vaucanson/algorithms/derived_term_automaton.hh>
#include <vaucanson/algorithms/determinize.hh>
#include <vaucanson/algorithms/domain.hh>
#include <vaucanson/algorithms/equivalent.hh>
#include <vaucanson/algorithms/eval.hh>
#include <vaucanson/algorithms/eps_removal.hh>
#include <vaucanson/algorithms/eps_removal_sp.hh>
#include <vaucanson/algorithms/is_useless.hh>
#include <vaucanson/algorithms/image.hh>
#include <vaucanson/algorithms/invert.hh>
#include <vaucanson/algorithms/is_ambiguous.hh>
#include <vaucanson/algorithms/is_deterministic.hh>
#include <vaucanson/algorithms/is_empty.hh>
#include <vaucanson/algorithms/ltl_to_pair.hh>
#include <vaucanson/algorithms/minimization_hopcroft.hh>
#include <vaucanson/algorithms/minimization_moore.hh>
#include <vaucanson/algorithms/product.hh>
#include <vaucanson/algorithms/realtime.hh>
#include <vaucanson/algorithms/standard.hh>
#include <vaucanson/algorithms/standard_of.hh>
//#include <vaucanson/algorithms/sum.hh>
#include <vaucanson/algorithms/sub_normalize.hh>
#include <vaucanson/algorithms/thompson.hh>
#include <vaucanson/algorithms/transpose.hh>
#include <vaucanson/algorithms/trim.hh>
#include <vaucanson/algorithms/evaluation_fmp.hh>

#include <iostream>
#include <sstream>
#include <stdio.h>
#include <fstream>
#include <string>

#include <jni.h>
#include <vaucanson/algebra/concept/letter.hh>
#include <vector>
#include "vjni.h"

# define MUTE_TRAITS mute_ltl_to_pair<S, T>

using namespace std;

using namespace vcsn;

//using namespace vcsn::boolean_automaton;
//using namespace vcsn::xml;
//using namespace vcsn::tools;

vcsn::boolean_automaton::rat_exp_t getBooleanExp(const std::string& exp, const vcsn::boolean_automaton::alphabet_t alphabet)
{
  return vcsn::boolean_automaton::make_rat_exp(alphabet, exp);
}

vcsn::z_automaton::rat_exp_t getZExp(const std::string& exp, const vcsn::z_automaton::alphabet_t alphabet)
{

  return vcsn::z_automaton::make_rat_exp(alphabet, exp);
}

//vcsn::fmp_transducer::rat_exp_t getFMPExp(const std::string& exp, const vcsn::fmp_transducer::alphabet_t alphabet) {
//
//    vcsn::algebra::token_representation_t tok_rep;
//    tok_rep.open_par = std::string("(");
//    tok_rep.close_par = std::string(")");
//    tok_rep.one = std::string("1");
//    tok_rep.open_weight = std::string("{");
//    tok_rep.close_weight = std::string("}");
//    tok_rep.plus = std::string("+");
//    tok_rep.zero = std::string("0");
//    std::vector<string> tmp;
//    tmp.push_back(" ");
//    tok_rep.spaces = tmp;
//    tok_rep.star = std::string("*");
//    tok_rep.times = std::string("&");
//
//    return vcsn::fmp_transducer::make_rat_exp(alphabet, exp, tok_rep);;
//}


vcsn::boolean_automaton::automaton_t getBooleanAut(std::string filename, int autType = 0) {
  ifstream file(filename.c_str());
  if (file.is_open()) {
    vcsn::boolean_automaton::automaton_t aut = vcsn::boolean_automaton::make_automaton(vcsn::boolean_automaton::alphabet_t());
    file >> automaton_loader(aut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
    return aut;
  } else {
    cout << "Unable to open file containing xml data";
    exit(1);
  }
}

vcsn::z_automaton::automaton_t getZAut(std::string filename, int autType = 0) {
  ifstream file(filename.c_str());
  if (file.is_open()) {
    vcsn::z_automaton::automaton_t aut = vcsn::z_automaton::make_automaton(vcsn::z_automaton::alphabet_t());
    file >> automaton_loader(aut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
    return aut;
  } else {
    cout << "Unable to open file containing xml data";
    exit(1);
  }
}

vcsn::fmp_transducer::automaton_t getFMPTransducer(std::string filename) {
  ifstream file(filename.c_str());
  if (file.is_open()) {
    vcsn::fmp_transducer::automaton_t aut = vcsn::fmp_transducer::make_automaton(
            vcsn::fmp_transducer::first_alphabet_t(),
            vcsn::fmp_transducer::second_alphabet_t());
    file >> automaton_loader(aut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
    return aut;
  } else {
    cout << "Unable to open file containing xml data";
    exit(1);
  }
}

void saveBooleanAut(std::string filename, vcsn::boolean_automaton::automaton_t aut) {
  ofstream file(filename.c_str());
  if (file.is_open()) {
    file << automaton_saver(aut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
  } else
    cout << "Unable to open file to save to";
}

void saveZAut(std::string filename, vcsn::z_automaton::automaton_t aut) {
  ofstream file(filename.c_str());
  if (file.is_open()) {
    file << automaton_saver(aut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
  } else
    cout << "Unable to open file to save to";
}

void saveFMPTransducer(std::string filename, vcsn::fmp_transducer::automaton_t aut) {
  ofstream file(filename.c_str());
  if (file.is_open()) {
    file << automaton_saver(aut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
  } else
    cout << "Unable to open file to save to";
}

//*********************************************//
//************ Boolean Automata ***************//
//*********************************************//

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolAreEquivalent
(JNIEnv *env, jobject, jstring lhs, jstring rhs) {
  const char* str1 = env->GetStringUTFChars(lhs, false);
  const char* str2 = env->GetStringUTFChars(rhs, false);
  std::string filename1(str1);
  std::string filename2(str2);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(filename1);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(filename2);

  bool result = are_equivalent(aut1, aut2);
  return result;
}

JNIEXPORT jint JNICALL Java_vjni_Vjni_boolEval
(JNIEnv *env, jobject, jstring inputFile, jstring word) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(word, false);
  std::string fileIn(str1);
  std::string wordString(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  aut = realtime(aut);
  aut = determinize(aut);
  cout << "result of evalution is: " << eval(aut, wordString) << "" << endl;
  int result = (eval(aut, wordString)).value();

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolIsAmbiquous
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_ambiguous(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolIsComplete
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_complete(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolIsDeterministic
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_deterministic(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolIsEmpty
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_empty(aut);

  return result;
}

// VGI FIX: Rename this call from HasSuccComp to
//                                  IsUseless
JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolHasSuccComp
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_useless(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolIsRealtime
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_realtime(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_boolIsStandard
(JNIEnv *env, jobject, jstring inputFile) {
  const char* str = env->GetStringUTFChars(inputFile, false);
  std::string filename(str);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(filename);

  bool result = is_standard(aut);

  return result;
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolAccessible
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  accessible_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolEpsRemoval
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  eps_removal_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolEpsRemovalSp
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  eps_removal_here_sp(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolCoAccessible
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  coaccessible_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolComplete
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  complete_here(aut);
  saveBooleanAut(fileOut, aut);
}

/*JNIEXPORT void JNICALL Java_vjni_Vjni_boolConcatenate
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn1);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(fileIn2);
  concatenate_here(aut1, aut2);
  saveBooleanAut(fileOut, aut1);
}*/

JNIEXPORT void JNICALL Java_vjni_Vjni_boolPower
(JNIEnv *env, jobject, jstring inputFile, jint n, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);
  int degree(n);

  if (degree < 2) {
    cout << "attemping to raise to power less than 2 \n";
    exit(0);
  }
  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(fileIn);
  while (degree >= 2) {
    aut2 = product(aut1, aut2);
    degree--;
  }
  saveBooleanAut(fileOut, aut2);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolProduct
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn1);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(fileIn2);
  vcsn::boolean_automaton::automaton_t aut3 = product(aut1, aut2);
  saveBooleanAut(fileOut, aut3);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolQuotient
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(autFileIn);
  aut = quotient(aut);
  saveBooleanAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolRealtime
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  realtime_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolStandardize
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  standardize(aut);
  saveBooleanAut(fileOut, aut);
}

/*JNIEXPORT void JNICALL Java_vjni_Vjni_boolUnionOfStandard
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn1);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(fileIn2);
  union_of_standard_here(aut1, aut2);
  saveBooleanAut(fileOut, aut1);
}*/

/*
 * Class:     vjni_Vjni
 * Method:    boolConcatOfStandard
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_boolConcatOfStandard
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn1);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(fileIn2);
  concat_of_standard_here(aut1, aut2);
  saveBooleanAut(fileOut, aut1);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolStarOfStandard
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  star_of_standard_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolUnion
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn1);
  vcsn::boolean_automaton::automaton_t aut2 = getBooleanAut(fileIn2);
  union_here(aut1, aut2);
  saveBooleanAut(fileOut, aut1);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolTranspose
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn);
  vcsn::boolean_automaton::automaton_t aut2 = transpose(aut1);
  saveBooleanAut(fileOut, aut2);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolTrim
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  trim_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolComplement
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  complement_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolDeterminize
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn);
  if (is_realtime(aut1)) {
    vcsn::boolean_automaton::automaton_t aut2 = determinize(aut1);
    saveBooleanAut(fileOut, aut2);
  } else
    cout << "automata is not realtime \n";
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolMinimize
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut1 = getBooleanAut(fileIn);
  vcsn::boolean_automaton::automaton_t aut2 = minimization_hopcroft(aut1);
  saveBooleanAut(fileOut, aut2);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolMinimizeMoore
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  minimization_moore_here(aut);
  saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolAutToExp
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);
  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(fileIn);
  ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << aut_to_exp(generalized(aut));
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolDerivedTerm
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile) {
    const char* str3 = env->GetStringUTFChars(alpha, false);
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::boolean_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::boolean_automaton::automaton_t aut = vcsn::boolean_automaton::make_automaton(A);
    vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, A);
    derived_term_automaton(aut, exp);
    saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolExpToAut
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile) {
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::boolean_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::boolean_automaton::automaton_t aut = vcsn::boolean_automaton::make_automaton(A);
    vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, A);
    standard_of(aut, exp.value());
    saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolExpand
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile) {
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::boolean_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, A);
    ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << expand(exp);
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolIdentityExp
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile) {
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::boolean_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, A);
    ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << exp;
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolStandard
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile) {
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::boolean_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::boolean_automaton::automaton_t aut = vcsn::boolean_automaton::make_automaton(A);
    vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, A);
    standard_of(aut, exp.value());
    saveBooleanAut(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_boolThompson
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile) {
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::boolean_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::boolean_automaton::automaton_t aut = vcsn::boolean_automaton::make_automaton(A);
    vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, A);
    thompson_of(aut, exp.value());
    saveBooleanAut(fileOut, aut);
}

//*********************************************//
//*********** Weighted Automata ***************//
//*********************************************//

JNIEXPORT jint JNICALL Java_vjni_Vjni_wEval
(JNIEnv *env, jobject, jstring inputAutFile, jstring inputWord) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(inputWord, false);
  std::string fileIn(str1);
  std::string wordIn(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(fileIn);
//  cout << "result of evalution is: " << eval(aut, wordIn) << "" << endl;
  int result = (eval(aut, wordIn)).value();

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_wIsAmbiguous
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string autFileIn(str);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);

  bool result = is_ambiguous(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_wIsComplete
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string autFileIn(str);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);

  bool result = is_complete(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_wIsEmpty
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string autFileIn(str);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);

  bool result = is_empty(aut);

  return result;
}

// VGI FIX: Rename this call from HasSuccComp to
//                                  IsUseless
JNIEXPORT jboolean JNICALL Java_vjni_Vjni_wHasSuccComp
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string autFileIn(str);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);

  bool result = is_useless(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_wIsRealtime
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string autFileIn(str);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);

  bool result = is_realtime(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_wIsStandard
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string autFileIn(str);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);

  bool result = is_standard(aut);

  return result;
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wAccessible
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  accessible_here(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wEpsRemoval
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  eps_removal_here(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wEpsRemovalSp
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  eps_removal_here_sp(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wCoAccessible
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  coaccessible_here(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wComplete
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  complete_here(aut);
  saveZAut(autFileOut, aut);
}

/*JNIEXPORT void JNICALL Java_vjni_Vjni_wConcatenate
(JNIEnv *env, jobject, jstring inputAutFile1, jstring inputAutFile2, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile1, false);
  const char* str2 = env->GetStringUTFChars(inputAutFile2, false);
  const char* str3 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn1(str1);
  std::string autFileIn2(str2);
  std::string autFileOut(str3);

  vcsn::z_automaton::automaton_t aut1 = getZAut(autFileIn1);
  vcsn::z_automaton::automaton_t aut2 = getZAut(autFileIn1);
  concatenate_here(aut1, aut2);
  saveZAut(autFileOut, aut1);
}*/

JNIEXPORT void JNICALL Java_vjni_Vjni_wPower
(JNIEnv *env, jobject, jstring inputAutFile, jint n, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);
  int degree(n);

  if (degree < 2) {
    cout << "attemping to raise to power less than 2 \n";
    exit(0);
  }
  vcsn::z_automaton::automaton_t aut1 = getZAut(autFileIn);
  vcsn::z_automaton::automaton_t aut2 = getZAut(autFileIn);
  while (degree >= 2) {
    aut2 = product(aut1, aut2);
    degree--;
  }
  saveZAut(autFileOut, aut2);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wProduct
(JNIEnv *env, jobject, jstring inputAutFile1, jstring inputAutFile2, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile1, false);
  const char* str2 = env->GetStringUTFChars(inputAutFile2, false);
  const char* str3 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn1(str1);
  std::string autFileIn2(str2);
  std::string autFileOut(str3);

  vcsn::z_automaton::automaton_t aut1 = getZAut(autFileIn1);
  vcsn::z_automaton::automaton_t aut2 = getZAut(autFileIn2);
  aut1 = product(aut1, aut2);
  saveZAut(autFileOut, aut1);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wQuotient
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  aut = quotient(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wRealtime
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  realtime_here(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wStandardize
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  standardize(aut);
  saveZAut(autFileOut, aut);
}

/*JNIEXPORT void JNICALL Java_vjni_Vjni_wUnionOfStandard
(JNIEnv *env, jobject, jstring inputAutFile1, jstring inputAutFile2, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile1, false);
  const char* str2 = env->GetStringUTFChars(inputAutFile2, false);
  const char* str3 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn1(str1);
  std::string autFileIn2(str2);
  std::string autFileOut(str3);

  vcsn::z_automaton::automaton_t aut1 = getZAut(autFileIn1);
  vcsn::z_automaton::automaton_t aut2 = getZAut(autFileIn2);
  aut1 = union_of_standard(aut1, aut2);
  saveZAut(autFileOut, aut1);
}*/

JNIEXPORT void JNICALL Java_vjni_Vjni_wConcatOfStandard
(JNIEnv *env, jobject, jstring inputAutFile1, jstring inputAutFile2, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile1, false);
  const char* str2 = env->GetStringUTFChars(inputAutFile2, false);
  const char* str3 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn1(str1);
  std::string autFileIn2(str2);
  std::string autFileOut(str3);

  vcsn::z_automaton::automaton_t aut1 = getZAut(autFileIn1);
  vcsn::z_automaton::automaton_t aut2 = getZAut(autFileIn2);
  aut1 = concat_of_standard(aut1, aut2);
  saveZAut(autFileOut, aut1);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wStarOfStandard
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  star_of_standard_here(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wUnion
(JNIEnv *env, jobject, jstring inputAutFile1, jstring inputAutFile2, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile1, false);
  const char* str2 = env->GetStringUTFChars(inputAutFile2, false);
  const char* str3 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn1(str1);
  std::string autFileIn2(str2);
  std::string autFileOut(str3);

  vcsn::z_automaton::automaton_t aut1 = getZAut(autFileIn1);
  vcsn::z_automaton::automaton_t aut2 = getZAut(autFileIn2);
  union_here(aut1, aut2);
  saveZAut(autFileOut, aut1);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wTranspose
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  aut = transpose(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wTrim
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::z_automaton::automaton_t aut = getZAut(autFileIn);
  trim_here(aut);
  saveZAut(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_wAutToExp
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);
  vcsn::z_automaton::automaton_t aut = getZAut(fileIn);
  ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << aut_to_exp(generalized(aut));
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}

/*
 * Class:     vjni_Vjni
 * Method:    wDerivedTerm
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_wDerivedTerm
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile)
{
    const char* str3 = env->GetStringUTFChars(alpha, false);
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::z_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::z_automaton::automaton_t aut = vcsn::z_automaton::make_automaton(A);
    vcsn::z_automaton::rat_exp_t exp = getZExp(s_exp, A);
    derived_term_automaton(aut, exp);
    saveZAut(fileOut, aut);
}

/*
 * Class:     vjni_Vjni
 * Method:    wExpToAut
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_wExpToAut
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile)
{
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::z_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::z_automaton::automaton_t aut = vcsn::z_automaton::make_automaton(A);
    vcsn::z_automaton::rat_exp_t exp = getZExp(s_exp, A);
    standard_of(aut, exp.value());
    saveZAut(fileOut, aut);
}

/*
 * Class:     vjni_Vjni
 * Method:    wExpand
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_wExpand
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile)
{
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::z_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::z_automaton::rat_exp_t exp = getZExp(s_exp, A);
    ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << expand(exp);
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}
/*
 * Class:     vjni_Vjni
 * Method:    wIdentityExp
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_wIdentityExp
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile)
{
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::z_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::z_automaton::rat_exp_t exp = getZExp(s_exp, A);
    ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << exp;
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}

/*
 * Class:     vjni_Vjni
 * Method:    wStandard
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_wStandard
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile)
{
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::z_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::z_automaton::automaton_t aut = vcsn::z_automaton::make_automaton(A);
    vcsn::z_automaton::rat_exp_t exp = getZExp(s_exp, A);
    standard_of(aut, exp.value());
    saveZAut(fileOut, aut);
}

/*
 * Class:     vjni_Vjni
 * Method:    wThompson
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vjni_Vjni_wThompson
(JNIEnv *env, jobject, jstring alpha, jstring jexp, jstring outputFile)
{
    const char* str1 = env->GetStringUTFChars(jexp, false);
    const char* str2 = env->GetStringUTFChars(outputFile, false);
    const char* str3 = env->GetStringUTFChars(alpha, false);
    std::string s_exp(str1);
    std::string fileOut(str2);
    std::string alphabet(str3);
    vcsn::z_automaton::alphabet_t A;
    for(std::string::iterator it = alphabet.begin(); it != alphabet.end(); it++) {
        A.insert(*it);
    }
    vcsn::z_automaton::automaton_t aut = vcsn::z_automaton::make_automaton(A);
    vcsn::z_automaton::rat_exp_t exp = getZExp(s_exp, A);
    thompson_of(aut, exp.value());
    saveZAut(fileOut, aut);
}


//*********************************************//
//*************  FMP Transducer  **************//
//*********************************************//

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_tIsEmpty
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string filename(str);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(filename);

  bool result = is_empty(aut);

  return result;
}

// VGI FIX: Rename this call from HasSuccComp to
//                                  IsUseless
JNIEXPORT jboolean JNICALL Java_vjni_Vjni_tHasSuccComp
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string filename(str);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(filename);

  bool result = is_useless(aut);

  return result;
}

JNIEXPORT jboolean JNICALL Java_vjni_Vjni_tIsSubNormalized
(JNIEnv *env, jobject, jstring inputAutFile) {
  const char* str = env->GetStringUTFChars(inputAutFile, false);
  std::string filename(str);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(filename);

  bool result = is_sub_normalized(aut);

  return result;
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tEpsRemoval
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(fileIn);
  eps_removal_here(aut);
  saveFMPTransducer(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tEpsRemovalSp
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(fileIn);
  eps_removal_here_sp(aut);
  saveFMPTransducer(fileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tDomain
(JNIEnv *env, jobject, jstring inputFile, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string fileOut(str2);

  //TODO find what is the return type of domain
  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(fileIn);
  vcsn::boolean_automaton::automaton_t boolAut = vcsn::boolean_automaton::make_automaton(vcsn::boolean_automaton::alphabet_t());
  domain(aut, boolAut);
//  saveFMPTransducer(fileOut, aut);
  saveBooleanAut(fileOut, boolAut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tEval
(JNIEnv *env, jobject, jstring inputFile, jstring jexp, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile, false);
  const char* str2 = env->GetStringUTFChars(jexp, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn(str1);
  std::string s_exp(str2);
  std::string fileOut(str3);
  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(fileIn);
  vcsn::boolean_automaton::rat_exp_t exp = getBooleanExp(s_exp, aut.structure().series().monoid().first_monoid().alphabet());
  vcsn::boolean_automaton::automaton_t tmpAut = vcsn::boolean_automaton::make_automaton(aut.structure().series().monoid().first_monoid().alphabet());
  vcsn::boolean_automaton::automaton_t outputAut = vcsn::boolean_automaton::make_automaton(aut.structure().series().monoid().second_monoid().alphabet());
  standard_of(tmpAut, exp.value());
  tmpAut = quotient(tmpAut);
  evaluation_fmp(aut, tmpAut, outputAut);
  ofstream file(fileOut.c_str());
    if (file.is_open()) {
        file << aut_to_exp(outputAut);
        file.close();
    } else
        cout << "Unable to save temp text file!!";
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tEvalAut
(JNIEnv *env, jobject, jstring inputTransducer, jstring inputBoolAut, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputTransducer, false);
  const char* str2 = env->GetStringUTFChars(inputBoolAut, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string transIn(str1);
  std::string boolIn(str2);
  std::string boolOut(str3);
  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(transIn);
  vcsn::boolean_automaton::automaton_t tmpAut = getBooleanAut(boolIn);
  vcsn::boolean_automaton::automaton_t outputAut = vcsn::boolean_automaton::make_automaton(aut.structure().series().monoid().second_monoid().alphabet());
  evaluation_fmp(aut, tmpAut, outputAut);
  saveBooleanAut(boolOut, outputAut);
}

template <typename S, typename T>
void innerLtl(Element<S, T> aut, std::string autFileOut) {
  typename MUTE_TRAITS::ret outputAut = ltl_to_pair(aut);
  ofstream file(autFileOut.c_str());
  if (file.is_open()) {
    file << automaton_saver(outputAut, vcsn::tools::string_out(), vcsn::xml::XML());
    file.close();
  } else
    cout << "Unable to open file to save to";
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tLtlToPair
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);
  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  innerLtl(aut, autFileOut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tImage
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);
  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  vcsn::boolean_automaton::automaton_t outputAut = vcsn::boolean_automaton::make_automaton(aut.structure().series().monoid().second_monoid().alphabet());
  image(aut, outputAut);
  saveBooleanAut(autFileOut, outputAut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tTranspose
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  aut = transpose(aut);
  saveFMPTransducer(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tTrim
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  trim_here(aut);
  saveFMPTransducer(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tSubNormalize
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  sub_normalize_here(aut);
  saveFMPTransducer(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tCompositionCover
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  aut = composition_cover(aut);
  saveFMPTransducer(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tCompositionCoCover
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  aut = composition_co_cover(aut);
  saveFMPTransducer(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tCompose
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::fmp_transducer::automaton_t aut1 = getFMPTransducer(fileIn1);
  vcsn::fmp_transducer::automaton_t aut2 = getFMPTransducer(fileIn2);
  vcsn::fmp_transducer::automaton_t aut3 = compose(aut1, aut2);
  saveFMPTransducer(fileOut, aut3);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tUCompose
(JNIEnv *env, jobject, jstring inputFile1, jstring inputFile2, jstring outputFile) {
  const char* str1 = env->GetStringUTFChars(inputFile1, false);
  const char* str2 = env->GetStringUTFChars(inputFile2, false);
  const char* str3 = env->GetStringUTFChars(outputFile, false);
  std::string fileIn1(str1);
  std::string fileIn2(str2);
  std::string fileOut(str3);

  vcsn::fmp_transducer::automaton_t aut1 = getFMPTransducer(fileIn1);
  vcsn::fmp_transducer::automaton_t aut2 = getFMPTransducer(fileIn2);
  vcsn::fmp_transducer::automaton_t aut3 = u_compose(aut1, aut2);
  saveFMPTransducer(fileOut, aut3);
}

//TODO
//JNIEXPORT void JNICALL Java_vjni_Vjni_tToRw
//(JNIEnv *, jobject, jstring, jstring);

JNIEXPORT void JNICALL Java_vjni_Vjni_tInvert
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile) {
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);

  vcsn::fmp_transducer::automaton_t aut = getFMPTransducer(autFileIn);
  aut = invert(aut);
  saveFMPTransducer(autFileOut, aut);
}

JNIEXPORT void JNICALL Java_vjni_Vjni_tIntersection
(JNIEnv *env, jobject, jstring inputAutFile, jstring outputAutFile)
{
  const char* str1 = env->GetStringUTFChars(inputAutFile, false);
  const char* str2 = env->GetStringUTFChars(outputAutFile, false);
  std::string autFileIn(str1);
  std::string autFileOut(str2);
  vcsn::boolean_automaton::automaton_t aut = getBooleanAut(autFileIn);
  vcsn::boolean_automaton::alphabet_t A = aut.structure().series().monoid().alphabet();
  vcsn::fmp_transducer::automaton_t outputAut = vcsn::fmp_transducer::make_automaton(A, A);
  identity(aut, outputAut);
  saveFMPTransducer(autFileOut, outputAut);
}

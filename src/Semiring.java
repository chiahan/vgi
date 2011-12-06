/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 *
 * @author wkren
 */
public class Semiring {
    enum SemiringType{NUMERICAL, SERIES};
    enum SemiringSet{B, N, Z, Q, R, C};
    enum SemiringOperation{CLASSICAL, MINPLUS, MAXPLUS};
    
    private String identitySymbol;
    private String zeroSymbol;
    private SemiringType type;
    private SemiringSet set;
    private SemiringOperation operation;
    private String writingData;
    private ArrayList<Semiring> subSemiring;
    private ArrayList<Monoid> subMonoid;
    
    public Semiring() {
        identitySymbol = new String();
        zeroSymbol = new String();
        type = SemiringType.NUMERICAL;
        set = SemiringSet.B;
        operation = SemiringOperation.CLASSICAL;
        writingData = new String();
        subSemiring = new ArrayList<Semiring>();
        subMonoid = new ArrayList<Monoid>();
    }
    
    public void setSet(SemiringSet set) {
        if (type == SemiringType.NUMERICAL)
            this.set = set;
    }
    
    public void setOperation(SemiringOperation operation) {
        if (type == SemiringType.NUMERICAL)
            this.operation = operation;
    }
}

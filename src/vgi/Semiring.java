package vgi;

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
    private Semiring nextSemiring;
    private Monoid nextMonoid;
    
    public Semiring() {
        identitySymbol = new String();
        zeroSymbol = new String();
        type = SemiringType.NUMERICAL;
        set = SemiringSet.B;
        operation = SemiringOperation.CLASSICAL;
        writingData = new String();
        nextSemiring = null;
        nextMonoid = null;
    }
    
    public Semiring(String identitySymbol, String zeroSymbol, SemiringType type,
                   SemiringSet set, SemiringOperation operation, String writingData,
                   Semiring nextSemiring, Monoid nextMonoid) {
        this.identitySymbol = identitySymbol;
        this.zeroSymbol = zeroSymbol;
        this.type = type;
        this.set = set;
        this.operation = operation;
        this.writingData = writingData;
        this.nextSemiring = nextSemiring;
        this.nextMonoid = nextMonoid;
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

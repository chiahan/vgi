/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author wkren
 */
public class Type {
    private Semiring semiring;
    private Monoid monoid;
    
    public Type() {
        semiring = new Semiring();
        monoid = new Monoid();
    }
    
    public Type(Semiring semiring, Monoid monoid) {
        this.semiring = semiring;
        this.monoid = monoid;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 *
 * @author wkren
 */
public class Monoid {
    private String identitySymbol;
    
    public enum MonoidType {
        unit, free, product
    }
    MonoidType type;
    
    public enum GeneratorKind {
        simple, tuple
    }
    public enum GeneratorDescript{
        enumeration, range, set
    }
    public enum GeneratorSort {
        letter, digit, integer, alphanum
    }
    
    public class Generator{
        GeneratorKind kind;
        GeneratorDescript descript;
        GeneratorSort sort;
        int dim;
        int prodDim;
    }
    private Generator generator;
    private String writingData;
    private ArrayList<Monoid> subMonoid;
    
    public Monoid() {
        identitySymbol = new String();
        generator = new Generator();
        writingData = new String();
        subMonoid = new ArrayList<Monoid>();
    }
    
    public void setType(MonoidType type) {
        this.type = type;
    }
    
    public void setGeneratorKind(GeneratorKind kind) {
        if (this.type == MonoidType.free)
            generator.kind = kind;
    }
    
    public void setGeneratorDescript(GeneratorDescript descript){
        if (this.type == MonoidType.free)
            generator.descript = descript;
    }
    
    public void setGeneratorSort(GeneratorSort sort) {
        if ((this.type == MonoidType.free ) && 
            (this.generator.kind == GeneratorKind.simple))
            generator.sort = sort;
    }
    
    public void setGeneratorDim(int dim) {
        if ((this.type == MonoidType.free ) && 
            (this.generator.kind == GeneratorKind.tuple))
            generator.dim = dim;
    }
    
    public void setGeneratorProdDim(int dim) {
        if (this.generator.kind == GeneratorKind.tuple)
            generator.prodDim = dim;
    }
}

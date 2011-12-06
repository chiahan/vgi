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
    private int dimension;
    private String writingData;
    private int prodcutDim;
    private ArrayList<Monoid> subMonoid;

    /**
     * @return the identitySymbol
     */
    public String getIdentitySymbol() {
        return identitySymbol;
    }

    /**
     * @param identitySymbol the identitySymbol to set
     */
    public void setIdentitySymbol(String identitySymbol) {
        this.identitySymbol = identitySymbol;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * @return the writingData
     */
    public String getWritingData() {
        return writingData;
    }

    /**
     * @return the prodcutDim
     */
    public int getProdcutDim() {
        return prodcutDim;
    }

    /**
     * @param prodcutDim the prodcutDim to set
     */
    public void setProdcutDim(int prodcutDim) {
        this.prodcutDim = prodcutDim;
    }

    /**
     * @return the subMonoid
     */
    public ArrayList<Monoid> getSubMonoid() {
        return subMonoid;
    }
    
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
    }
    private Generator generator;

    public Monoid() {
        identitySymbol = new String();
        dimension = 0;
        writingData = new String();
        prodcutDim = 0;
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
}

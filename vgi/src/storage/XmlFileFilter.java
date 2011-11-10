package storage;
/**
 * @author junli
 */
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
/**
 * to filter out the files witout the assigned file name.
 * @author Junli Lu
 */
public class XmlFileFilter extends FileFilter {
    /**
     * the file type we want to keep
     */
    String fileTypeName_ = "xml";
    /**
    * @param f the file
    * @return to decide if the file is accepted or not
    * @author Junli Lu
    */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f);
        if (extension != null) 
        {
            if (extension.equals(fileTypeName_))
            {
                return true;
            } 
            else 
            {
                return false;
            }
        }
        return false;
    }
    /**
    * @return the description of this filter
    * @author Junli Lu
    */
    public String getDescription() {
        return fileTypeName_;
    }
    /**
    * @param f the file
    * @return the extension string of f
    * @author Junli Lu
    */
    protected String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
        
}


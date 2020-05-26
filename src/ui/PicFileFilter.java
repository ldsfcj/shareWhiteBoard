package ui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PicFileFilter extends FileFilter {

    String description;
    String extension;

    public PicFileFilter(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String fileName = f.getName();
        if (fileName.toUpperCase().endsWith(this.extension.toUpperCase())) return true;
        return false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getExtension() {
        return this.extension;
    }
}

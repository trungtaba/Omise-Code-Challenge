package codechallenge.MatchingEngine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    private final Config config = Config.getInstance();

    public void closeFile(File file, FileReader fileReader) {
        if (fileReader != null && file != null) {
            try {
                file.renameTo(new File(config.getArchivedDir() + file.getName()));
                file.delete();
                fileReader.close();
            } catch (IOException ex) {
                errorFile(file);
                System.out.println("Error:" + ex.toString());
            }
        }
    }

    public void closeFile(InputStream input) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                System.out.println("Error:" + ex.toString());
            }
        }
    }

    public void errorFile(File file) {
        if (file != null) {
            file.renameTo(new File(config.getErrorDir() + file.getName()));
            file.delete();
        }
    }
}

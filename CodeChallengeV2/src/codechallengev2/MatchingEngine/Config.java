package codechallengev2.MatchingEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
create a singleton config
*/
public class Config {

    private static Config CONFIG_INSTANCE = null;
    private String inputDir;
    private String outputDir;
    private String errorDir;
    private String archivedDir;
    

    public static Config getInstance() {
        if (CONFIG_INSTANCE == null) {
            synchronized (Config.class) {
                if (CONFIG_INSTANCE == null) {
                    CONFIG_INSTANCE = new Config();
                }
            }
        }
        return CONFIG_INSTANCE;
    }

    private Config() {
        
        LoadProperties();
    }

    private void LoadProperties() {
        InputStream input = null;
        Properties properties=new Properties();
        try {
            input = new FileInputStream("src/codechallengev2/config.properties");
            properties.load(input);
            inputDir = properties.getProperty("input");
            outputDir = properties.getProperty("output");
            errorDir = properties.getProperty("error");
            archivedDir = properties.getProperty("archived");
            System.out.println(inputDir);
        } catch (IOException ex) {
            System.err.println("Error:..." + ex.getMessage());
        } finally {
            if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                System.err.println("Error:" + ex.toString());
            }
        }
        }
    }

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getErrorDir() {
        return errorDir;
    }

    public void setErrorDir(String errorDir) {
        this.errorDir = errorDir;
    }

    public String getArchivedDir() {
        return archivedDir;
    }

    public void setArchivedDir(String archivedDir) {
        this.archivedDir = archivedDir;
    }

}

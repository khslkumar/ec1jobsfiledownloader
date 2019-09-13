package com.genie.job.portal;

import java.io.IOException;
import java.util.Properties;

public class GenieProperties {
	
    private static GenieProperties instance = null;
    private Properties properties;


    protected GenieProperties() throws IOException{

        properties = new Properties();
        properties.load(getClass().getResourceAsStream("/genie.properties"));

    }

    public static GenieProperties getInstance() {
        if(instance == null) {
            try {
                instance = new GenieProperties();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return instance;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

}
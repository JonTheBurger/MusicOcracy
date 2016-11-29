package com.musicocracy.fpgk.domain.dal;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;

/*
 * Because of how some versions of android handle annotations, we want to offload information about
 *  the database schema into a config file rather than reload it from annotations each time the app
 *  is run. Whenever the database schema changes, this code must be run ON THE DEVELOPMENT MACHINES
 *  (NOT the user's device) to update the config file shipped with the app. For more information,
 *  see:
 *  http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) throws Exception {
        System.out.println("test");
        String currDirectory = "user.dir";
        String configPath = "/app/src/main/res/raw/ormlite_config.txt";
        String projectRoot = System.getProperty(currDirectory);

        String fullConfigPath = projectRoot + configPath;
        System.out.println("Writing OrmLiteConfig to " + fullConfigPath);

        File configFile = new File(fullConfigPath);

        if(configFile.exists()) {
            configFile.delete();
            configFile = new File(fullConfigPath);
        }

        writeConfigFile(configFile);
    }
}

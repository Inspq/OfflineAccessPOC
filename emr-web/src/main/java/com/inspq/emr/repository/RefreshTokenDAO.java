package com.inspq.emr.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.keycloak.common.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.inspq.emr.controller.PublicationController;

/**
 * Very simple DAO, which stores/loads just one token per whole application into file in tmp directory. Useful just for example purposes.
 * In real environment, token should be stored in database.
 * 
 */
@Component
public class RefreshTokenDAO {
	
	Logger logger = LoggerFactory.getLogger(PublicationController.class);
	
	public static final String FILE = System.getProperty("java.io.tmpdir") + "/offline-access-poc";

    public void saveToken(final String token) throws IOException {
    	PrintWriter writer = null;
        try {
        	logger.debug("Storing offline token in the file: " + FILE);
        	writer = new PrintWriter(new BufferedWriter(new FileWriter(FILE)));
            writer.print(token);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public String loadToken() throws IOException {
        FileInputStream fis = null;
        try {
        	logger.debug("Loading offline token from the file: " + FILE);
            fis = new FileInputStream(FILE);
            return StreamUtil.readString(fis, StandardCharsets.UTF_8);
        } catch (FileNotFoundException fnfe) {
        	logger.error("Offline token not found in the file system " + fnfe);
            return null;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    public void deleteToken() throws IOException {
    	File file = new File(FILE);
        try {
        	logger.debug("Deleting offline token in the file: " + FILE);
        	if(file.delete())
        		logger.info(FILE + " has been successfully deleted. Offline token is unavailable now");
        } catch (Exception e) {
        	logger.error("Exception occured! " + e);
        }
    }
}

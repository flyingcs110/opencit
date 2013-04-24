/*
 * Copyright (C) 2013 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.setup;

import com.intel.dcg.console.Command;
import com.intel.dcg.console.input.Input;
import com.intel.dcg.validation.ObjectModel;
import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jbuhacoff
 */
public abstract class SetupCommand extends ObjectModel implements Command {
    private Configuration options = null;
    private SetupContext config = null;

    @Override
    public void setOptions(Configuration options) {
        this.options = options;
    }
    
    public Configuration getOptions() { 
        return options; 
    }
    
    public SetupContext getConfig() {
        if( config == null ) {
            config = new SetupContext(); // (options);    // XXX maybe don't want to do this... use Java Preferences API where possible, and set global paths etc. via JVM properties on the command line, which can also be wrapped in a COnfiguration object
        }
        return config;
    }

    @Override
    public abstract void execute(String[] args) throws Exception;
    
    // convenience methods
    
    /**
     * Use this method when you need the user to set a password for a new key. 
     * If an environment variable is provided as an option, its value is used.
     * Otherwise, the user is prompted for the password twice (to confirm).
     * 
     * If an environment variable is provided but is empty, the user is prompted.
     * 
     * @param label human-readable text to incorporate into the prompt, for example "the Data Encryption Key"
     * @param optName the name of the command-line option that can be used to name an environment variable containing the password (option value never used as the password itself)
     * @throws IOException 
     */
    public String getNewPassword(String label, String optName) throws IOException {
        String password = null;
        if( options != null && options.containsKey(optName) ) {
            String passwordVar = options.getString(optName);
            password = System.getenv(passwordVar);
            if( password == null ) {
                System.err.println(String.format("Cannot get password from environment variable '%s' specified by option '%s'", passwordVar, optName));
            }
        }
        if( password == null ) {
            password = Input.getConfirmedPasswordWithPrompt(String.format("You must protect %s with a password.", label)); // throws IOException, or always returns value or expression
        }
        return password;
    }
    
    /**
     * Use this method when you need the user to provide a password for an existing key. 
     * If an environment variable is provided as an option, its value is used.
     * Otherwise, the user is prompted for the password just once.
     * 
     * If an environment variable is provided but is empty, the user is prompted.
     * 
     * @param label human-readable text to incorporate into the prompt, for example "the Data Encryption Key"
     * @param optName the name of the command-line option that can be used to name an environment variable containing the password (option value never used as the password itself)
     * @return
     * @throws IOException 
     */
    public String getExistingPassword(String label, String optName) throws IOException {
        String password = null;
        if( options != null && options.containsKey(optName) ) {
            String passwordVar = options.getString(optName);
            password = System.getenv(passwordVar);
            if( password == null ) {
                System.err.println(String.format("Cannot get password from environment variable '%s' specified by option '%s'", passwordVar, optName));
            }
        }
        if( password == null ) {
            password = Input.getRequiredPasswordWithPrompt(String.format("A password is required to unlock %s.", label)); // throws IOException, or always returns value or expression
        }
        return password;        
    }
	
    /**
     * 
     * @param args list of option names that are required
     */
    protected void requireOptions(String... args) {
        for(String arg : args) {
            String value = options.getString(arg);
            if( value == null ) {
                fault("required option %s is missing", arg);
                continue;
            }
            if( value.isEmpty() ) {
                fault("requierd option %s is empty", arg);
                continue;
            }
        }
    }
	
}

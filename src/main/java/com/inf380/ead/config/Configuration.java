package com.inf380.ead.config;

/**
 * Class that contains application configuration variable
 * @author Abb�s
 *
 */
public class Configuration {

	/**
	 * Absolute path where the project will be store <br />
	 * User projects are stored in projectbaseUrl/username/projectName directory
	 */
public static String projectsBaseUrl = "/Users/fatoumatananakasse/Downloads/INF380";	
	/**
	 * The url of the LDAP server
	 */
	public static String LDAP_SERVER_URL = "ldap://ldap.enst.fr";
	
	/**
	 * The Distinguish Name (DN) of the LDAP
	 */
	public static String LDAP_BASE_DN = "dc=enst.fr";
}

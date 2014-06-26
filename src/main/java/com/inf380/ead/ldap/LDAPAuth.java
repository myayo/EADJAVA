package com.inf380.ead.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * Example code for retrieving a Users Primary Group
 * from Microsoft Active Directory via. its LDAP API
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class LDAPAuth {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) throws NamingException {
//    	
//    	//ldap://ldap.enst.fr/ou=People,dc=enst,dc=fr?uid=
//        
//    	String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
//    	String LDAP_SERVER_URL = "ldap://ldap.forumsys.com"; // connexion openLDAP
//    	String LDAP_BASE_DN = "ou=mathematicians,dc=example,dc=com";
//    	String LDAP_AUTHENTICATION_MODE = "simple";
//    	String LDAP_REFERRAL_MODE = "follow";
////    	String LDAP_USER = "uid=riemann"+LDAP_BASE_DN;
////    	String LDAP_PASSWORD = "password";
//     
//    	Hashtable<String, String> env = new Hashtable<>();
//    	env.put( Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY );
//    	env.put( Context.PROVIDER_URL, LDAP_SERVER_URL+"/"+LDAP_BASE_DN );
//    	env.put( Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE );
////    	env.put( Context.SECURITY_PRINCIPAL, LDAP_USER );
////    	env.put( Context.SECURITY_CREDENTIALS, LDAP_PASSWORD );
//    	env.put( Context.REFERRAL, LDAP_REFERRAL_MODE );
//     
//    	DirContext ctxtDir = null;
//    	// connexion au LDAP
//    	ctxtDir = new InitialDirContext( env );
//    	System.out.println( "Connexion: ok\n" );
//    }
	
	
	 public static void main(String[] args) {

			try {
			    // Create initial context
			    DirContext ctx = new InitialDirContext();

			    // Perform search using URL
			    Object answer = ctx.lookup(
				"ldap://ldap.forumsys.com/ou=mathematicians,dc=example,dc=com");

			    // Print the answer
			    System.out.println(answer);
			    // Close the context when we're done
			    ctx.close();
			} catch (NamingException e) {
			    e.printStackTrace();
			}
		    }
}
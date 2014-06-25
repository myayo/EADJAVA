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
    public static void main(String[] args) throws NamingException {
    	
    	//ldap://ldap.enst.fr/ou=People,dc=enst,dc=fr?uid=
        
    	String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    	String LDAP_CNX_POOL = "com.sun.jndi.ldap.connect.pool";
    	String LDAP_SERVER_URL = "ldap://ldap.enst.fr"; // connexion openLDAP
    	String LDAP_BASE_DN = "dc=enst.fr";
    	String LDAP_AUTHENTICATION_MODE = "simple";
    	String LDAP_REFERRAL_MODE = "follow";
    	String LDAP_USER = "uid=djongon"/*,ou=People,"+LDAP_BASE_DN*/;
    	String LDAP_PASSWORD = "u4kY?K92EF";
     
    	Hashtable env = new Hashtable();
    	env.put( Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY );
//    	env.put( Context.PROVIDER_URL, "ldap://ldap.enst.fr/ou=People,dc=enst,dc=fr" /* LDAP_SERVER_URL+"/"+LDAP_BASE_DN*/ );
//    	env.put( Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE );
//    	env.put( Context.SECURITY_PRINCIPAL, LDAP_USER );
//    	env.put( Context.SECURITY_CREDENTIALS, LDAP_PASSWORD );
//    	env.put( Context.REFERRAL, LDAP_REFERRAL_MODE );
    	env.put("com.sun.jndi.ldap.read.timeout", "1000");
    	env.put(Context.PROVIDER_URL, "ldap://ldap.enst.fr");
     
    	DirContext ctxtDir = null;
    	// connexion au LDAP
    	ctxtDir = new InitialDirContext( env );
    	System.out.println( "InitialDirContext: ok\n" );
    }
}
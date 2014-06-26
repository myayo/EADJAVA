package com.inf380.ead.service;

public class LdapService {

	private final String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private final String LDAP_AUTHENTICATION_MODE = "simple";
	private final String LDAP_REFERRAL_MODE = "follow";
	
	public boolean authenticate(String username, String password){
		
		return false;
	}
}

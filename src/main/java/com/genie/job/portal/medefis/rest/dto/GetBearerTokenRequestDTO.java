package com.genie.job.portal.medefis.rest.dto;

public class GetBearerTokenRequestDTO {
	private String grant_type;
	private String client_id; //httpAgencyRest
	private String client_secret; //agency
	private String scope; //apiExternalAgency
	private String username; //MyUserName
	private String password; //MyPassword
	/**
	 * @return the grant_type
	 */
	public String getGrant_type() {
		return grant_type;
	}
	/**
	 * @param grant_type the grant_type to set
	 */
	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}
	/**
	 * @return the client_id
	 */
	public String getClient_id() {
		return client_id;
	}
	/**
	 * @param client_id the client_id to set
	 */
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	/**
	 * @return the client_secret
	 */
	public String getClient_secret() {
		return client_secret;
	}
	/**
	 * @param client_secret the client_secret to set
	 */
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString()
	{
		return "grant_type ='" + getGrant_type() + "'," +
			"client_id ='" + getClient_id() + "'," +
			"client_secret ='" + getClient_secret() + "'," +
			"username ='" + getUsername() + "'," +
			"scope ='" + getScope() + "'," +
			"password ='" + getPassword()  + "';";
	}
}

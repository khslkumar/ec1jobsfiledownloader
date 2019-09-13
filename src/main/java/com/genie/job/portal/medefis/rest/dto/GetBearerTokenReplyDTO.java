package com.genie.job.portal.medefis.rest.dto;

public class GetBearerTokenReplyDTO {
	
	private String access_token; //"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1daU50bUdse…swqVY"
	private String expires_in; // 3600,
	private String token_type; // "Bearer"
	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}
	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	/**
	 * @return the token_type
	 */
	public String getToken_type() {
		return token_type;
	}
	/**
	 * @param token_type the token_type to set
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	/**
	 * @return the expires_in
	 */
	public String getExpires_in() {
		return expires_in;
	}
	/**
	 * @param expires_in the expires_in to set
	 */
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	
	public String toString()
	{
		return "access_token = '" + getAccess_token() + "'," +
		"token_type = '" + getToken_type() + "'," +
		"expires_in = '" + getExpires_in() + "';";
	}
			 
}

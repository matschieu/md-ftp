package com.github.matschieu.ftp.server;

/**
 *
 * @author Matschieu
 *
 */
public class User {

	private String login;

	private String pwd;

	/**
	 *
	 */
	public User() { }

	/**
	 * @param login
	 * @param pwd
	 */
	public User(final String login, final String pwd) {
		this.login = login;
		this.pwd = pwd;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return this.pwd;
	}

	/**
	 * @param pwd the pwd to set
	 */
	public void setPwd(final String pwd) {
		this.pwd = pwd;
	}

}

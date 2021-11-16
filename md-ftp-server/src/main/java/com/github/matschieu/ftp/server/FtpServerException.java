package com.github.matschieu.ftp.server;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerException extends Exception {

	private static final long serialVersionUID = -3597017802185006086L;

	/**
	 *
	 * @param message
	 */
	public FtpServerException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public FtpServerException(String message, Throwable cause) {
		super(message, cause);
	}
}

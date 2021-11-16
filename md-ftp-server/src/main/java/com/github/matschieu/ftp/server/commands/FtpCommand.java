package com.github.matschieu.ftp.server.commands;

import com.github.matschieu.ftp.server.FtpServerConfiguration;
import com.github.matschieu.ftp.server.FtpSession;

/**
 *
 * @author Matschieu
 *
 */
public interface FtpCommand {

	/**
	 *
	 * @return String
	 */
	String getName();

	/**
	 *
	 * @return String
	 */
	String getHelpMessage();

	/**
	 *
	 * @param cmd
	 * @return boolean
	 */
	boolean isValid(String cmd);

	/**
	 *
	 * @param cmd
	 * @param session
	 * @param config TODO
	 * @return boolean
	 */
	boolean execute(String cmd, FtpSession session, FtpServerConfiguration config);

}

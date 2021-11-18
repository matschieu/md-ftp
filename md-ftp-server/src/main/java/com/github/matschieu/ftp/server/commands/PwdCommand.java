package com.github.matschieu.ftp.server.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.matschieu.ftp.server.FtpServerConfiguration;
import com.github.matschieu.ftp.server.FtpServerResponse;
import com.github.matschieu.ftp.server.FtpSession;

/**
 *
 * @author Matschieu
 *
 */
public class PwdCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(PwdCommand.class);

	@Override
	public String getName() {
		return "PWD";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " : print working directory on remote machine";
	}

	@Override
	public boolean isValid(final String cmd) {
		return cmd != null && cmd.matches("^" + this.getName() + "$");
	}

	@Override
	public boolean execute(final String cmd, final FtpSession session, final FtpServerConfiguration config) {
		LOGGER.debug("Executing command '{}'", cmd);

		if (!this.isValid(cmd)) {
			LOGGER.debug("Invalid command '{}'", cmd);

			session.getControlOut().println(FtpServerResponse.SYNTAX_ERROR.format(cmd));
			return false;
		}

		if (!session.isUserConnected()) {
			LOGGER.debug("User {} is not connected", session.getCurrentUser());

			session.getControlOut().println(FtpServerResponse.NOT_CONNECTED.format());
			return false;
		}

		LOGGER.debug("Current working directory is {}", session.getCurrentDirPath());

		session.getControlOut().println(FtpServerResponse.CURRENT_DIRECTORY_INFO.format(session.getCurrentDirPath()));
		return true;
	}

}

package com.github.matschieu.ftp.server.commands;

import java.io.IOException;

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
public class QuitCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuitCommand.class);

	@Override
	public String getName() {
		return "QUIT";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " : terminate ftp session and exit";
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

		LOGGER.debug("Closing socket[{}]... ", session.getControlSocket().getInetAddress());

		session.getControlOut().println(FtpServerResponse.GOODBYE.format());

		try {
			session.getControlSocket().close();

			LOGGER.debug("Socket closed");
		}
		catch(final IOException e) {
			LOGGER.error("Error while quitting: " + e.getMessage(), e);

			return false;
		}

		return true;
	}

}

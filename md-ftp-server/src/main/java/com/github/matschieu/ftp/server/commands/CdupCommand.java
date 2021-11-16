package com.github.matschieu.ftp.server.commands;

import java.io.File;
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
public class CdupCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(CdupCommand.class);

	@Override
	public String getName() {
		return "CDUP";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " : change remote working directory to parent directory";
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

		try {
			final String path = session.getCurrentDir().getCanonicalPath();

			if (!path.equals(session.getRootDir().getCanonicalPath())) {
				session.setCurrentDir(new File(path + File.separator + ".."));
			}
		} catch(final IOException e) { }

		LOGGER.debug("New current working directory is {}", session.getCurrentDirPath());

		session.getControlOut().println(FtpServerResponse.COMMAND_SUCCESSFUL.format(this.getName()));

		return true;
	}

}

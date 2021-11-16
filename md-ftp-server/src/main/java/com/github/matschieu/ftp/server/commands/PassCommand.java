package com.github.matschieu.ftp.server.commands;

import java.util.StringTokenizer;

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
public class PassCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassCommand.class);

	@Override
	public String getName() {
		return "PASS";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " <user_password>";
	}

	@Override
	public boolean isValid(final String cmd) {
		return cmd != null && cmd.matches("^" + this.getName() + "[ ]+[A-Za-z0-9]+$");
	}

	@Override
	public boolean execute(final String cmd, final FtpSession session, final FtpServerConfiguration config) {
		LOGGER.debug("Executing command '{}'", cmd);

		if (!this.isValid(cmd)) {
			LOGGER.debug("Invalid command '{}'", cmd);

			session.getControlOut().println(FtpServerResponse.SYNTAX_ERROR.format(cmd));
			return false;
		}

		if (session.getCurrentUser() == null) {
			LOGGER.debug("No user defined");

			session.getControlOut().println(FtpServerResponse.CANNOT_CONNECT.format());
			return false;
		}

		if (session.isUserConnected()) {
			LOGGER.debug("User {} is already connected", session.getCurrentUser());

			session.getControlOut().println(FtpServerResponse.ALREADY_CONNECTED.format());
			return false;
		}

		final StringTokenizer st = new StringTokenizer(cmd, " ", false);
		st.nextToken();
		final String cmdPwd = st.nextToken();

		final String userPwd = config.getUserPassword(session.getCurrentUser());

		if (!cmdPwd.equals(userPwd)) {
			LOGGER.debug("Invalid password set for user {}", session.getCurrentUser());

			session.getControlOut().println(FtpServerResponse.CANNOT_CONNECT.format());
			return false;
		}

		session.setUserConnected(true);
		session.getControlOut().println(FtpServerResponse.USER_LOGGED_IN.format(session.getCurrentUser()));

		LOGGER.debug("User {} successfully connected", session.getCurrentUser());

		return true;
	}

}

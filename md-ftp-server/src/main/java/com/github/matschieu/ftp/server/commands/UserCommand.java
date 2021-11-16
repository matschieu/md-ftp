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
public class UserCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCommand.class);

	@Override
	public String getName() {
		return "USER";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " <user_name> : send new user information";
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

		if (session.isUserConnected()) {
			LOGGER.debug("Disconnect current user {}", session.getCurrentUser());

			session.setCurrentUser(null);
			session.setUserConnected(false);
		}

		final StringTokenizer st = new StringTokenizer(cmd, " ", false);
		st.nextToken();
		final String cmdUser = st.nextToken();
		String userPwd = null;

		for(final String configUser : config.getUsersAndPasswords().keySet()) {
			if (cmdUser.equals(configUser)) {
				LOGGER.debug("User {} found in configuration", cmdUser);

				session.setCurrentUser(configUser);
				userPwd = config.getUsersAndPasswords().get(configUser);
				break;
			}
		}

		if (session.getCurrentUser() == null) {
			LOGGER.debug("User {} not found in configuration", cmdUser);

			session.getControlOut().println(FtpServerResponse.CANNOT_CONNECT.format());
			return false;
		}

		if (userPwd == null || userPwd.isEmpty()) {
			LOGGER.debug("No password defined for user {}, user logged in", session.getCurrentUser());

			session.setUserConnected(true);
			session.getControlOut().println(FtpServerResponse.USER_LOGGED_IN.format(session.getCurrentUser()));
		} else {
			session.getControlOut().println(FtpServerResponse.PASSWORD_REQUIRED.format(session.getCurrentUser()));
		}

		return true;
	}

}

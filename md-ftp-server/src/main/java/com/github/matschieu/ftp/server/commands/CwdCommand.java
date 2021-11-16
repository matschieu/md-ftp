package com.github.matschieu.ftp.server.commands;

import java.io.File;
import java.io.IOException;
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
public class CwdCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(CwdCommand.class);

	@Override
	public String getName() {
		return "CWD";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " <directory> : change working directory on remote machine";
	}

	@Override
	public boolean isValid(final String cmd) {
		return cmd != null && cmd.matches("^" + this.getName() + "[ ]+[\\.\\-\\_\\/A-Za-z0-9]+$");
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
			File f;
			StringTokenizer st = new StringTokenizer(cmd, " ", false);
			st.nextToken();
			final String param = st.nextToken();

			if (param.charAt(0) != '/') {
				st = new StringTokenizer(param, File.separator, false);
				String tmpPath = session.getCurrentDir().getCanonicalPath();

				while(st.hasMoreTokens()) {
					final String tmp = st.nextToken();

					if (tmpPath.equals(session.getRootDir().getCanonicalPath()) && tmp.equals("..")) {
						LOGGER.debug("Could not going above root directory");
						break;
					} else {
						f = new File(tmpPath += File.separator + tmp);
						tmpPath = f.getCanonicalPath();

						LOGGER.debug("Exploring {}", tmpPath);
					}
				}

				f = new File(tmpPath);
			} else {
				f = new File(session.getRootDir().getCanonicalPath() + param);
			}

			if (!f.exists() || !f.isDirectory()) {
				LOGGER.debug("{} doesn't exists or is not a directory", f.getCanonicalPath());

				session.getControlOut().println(FtpServerResponse.NO_SUCH_FILE.format(param));
				return false;
			}

			session.setCurrentDir(new File(f.getCanonicalPath()));
		} catch(final IOException e) {
		} finally {
			session.getControlOut().println(FtpServerResponse.CHANGE_CURRENT_DIRECTORY.format(session.getCurrentDirPath()));
		}

		LOGGER.debug("New current working directory is {}", session.getCurrentDirPath());

		return true;
	}

}

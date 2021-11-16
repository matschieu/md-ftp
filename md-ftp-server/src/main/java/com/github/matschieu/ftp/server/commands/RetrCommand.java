package com.github.matschieu.ftp.server.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
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
public class RetrCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetrCommand.class);

	@Override
	public String getName() {
		return "RETR";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " <filename> : receive file";
	}

	@Override
	public boolean isValid(final String cmd) {
		return cmd != null && cmd.matches("^" + this.getName() + "[ ]+[A-Za-z0-9/./_///-]+$");
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

		StringTokenizer st = new StringTokenizer(cmd, " ", false);
		st.nextToken();

		final String filepath = st.nextToken();

		st = new StringTokenizer(filepath, File.separator, false);
		String filename = "";

		try {
			String tmpPath = session.getCurrentDir().getCanonicalPath();

			while(st.hasMoreTokens()) {
				filename = st.nextToken();

				if (tmpPath.equals(session.getRootDir().getCanonicalPath()) && filename.equals("..")) {
					LOGGER.debug("Could not going above root directory");

					session.getControlOut().println(FtpServerResponse.NO_SUCH_FILE.format(filepath));
					return false;
				} else {
					final File f = new File(tmpPath += File.separator + filename);
					tmpPath = f.getCanonicalPath();

					LOGGER.debug("Exploring {}", tmpPath);
				}
			}
		} catch(final IOException e) {
			st = new StringTokenizer(filepath, File.separator, false);

			while(st.hasMoreTokens()) {
				filename = st.nextToken();
			}
		}

		LOGGER.debug("Requesting to retrieve file {}", filename);

		final File f = new File(session.getCurrentDir() + File.separator + filepath);

		if (session.getDataServerSocket() != null && session.getDataServerSocket().isClosed()) {
			session.getControlOut().println(FtpServerResponse.CANNOT_OPEN_DATA_CONNECTION);
			return false;
		}

		if (!session.getCurrentDir().exists() || !session.getCurrentDir().isDirectory()) {
			LOGGER.debug("Invalid current directory {}", session.getCurrentDir());

			session.getControlOut().println(FtpServerResponse.FILE_UNAVAILABLE.format(session.getCurrentDirPath()));
			return false;
		}

		if (!f.exists() || f.isDirectory()) {
			LOGGER.debug("File {} doesn't exist or is a directory", filepath);

			session.getControlOut().println(FtpServerResponse.NO_SUCH_FILE.format(filepath));
			return false;
		}

		session.getControlOut().println(FtpServerResponse.OPEN_DATA_CONNECTION);

		Socket s = null;
		PrintStream ps = null;
		FileInputStream fin = null;

		try {
			final byte[] buffer = new byte[4096];
			s = session.getDataServerSocket().accept();
			ps = new PrintStream(s.getOutputStream());
			fin = new FileInputStream(f);

			ps.println("<RETR>");
			ps.println(filename);

			LOGGER.debug("Sending file {}", f.getCanonicalFile());

			int r = 0;
			s.getInputStream().read();

			while(r != -1 && !s.isClosed()) {
				r = fin.read(buffer, 0, buffer.length);

				LOGGER.debug("Sending {} bytes", r);

				if (r != -1) {
					LOGGER.debug("Buffer sent = {}", buffer);

					ps.write(buffer, 0, r);
					ps.flush();
				}
			}

			session.getControlOut().println(FtpServerResponse.TRANSFER_COMPLETED.format());
		} catch(final IOException e) {
			LOGGER.error("Error during file transfer: " + e.getMessage());

			session.getControlOut().println(FtpServerResponse.TRANSFER_ABORTED.format());
			return false;
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (final IOException e) { }
			}
			if (ps != null) {
				ps.close();
			}
			if (s != null) {
				try {
					s.close();
				} catch (final IOException e) { }
			}
		}

		return true;
	}

}

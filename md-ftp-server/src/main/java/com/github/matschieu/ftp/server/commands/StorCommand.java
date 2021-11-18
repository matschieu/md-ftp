package com.github.matschieu.ftp.server.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class StorCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorCommand.class);

	@Override
	public String getName() {
		return "STOR";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " <filename> : send one file";
	}

	@Override
	public boolean isValid(final String cmd) {
		return cmd != null && cmd.matches("^" + this.getName() + "[ ]+[A-Za-z0-9/./_/-//]+$");
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

		if (!session.getCurrentDir().exists() || !session.getCurrentDir().isDirectory()) {
			LOGGER.debug("Invalid current directory {}", session.getCurrentDir());

			session.getControlOut().println(FtpServerResponse.FILE_UNAVAILABLE.format(session.getCurrentDirPath()));
			return false;
		}

		final StringTokenizer st = new StringTokenizer(cmd, " ", false);
		st.nextToken();

		final String filename = st.nextToken();

		if (session.getDataServerSocket() != null && session.getDataServerSocket().isClosed()) {
			session.getControlOut().println(FtpServerResponse.CANNOT_OPEN_DATA_CONNECTION.format());
			return false;
		}

		LOGGER.debug("Requesting to store file {}", filename);

		session.getControlOut().println(FtpServerResponse.OPEN_DATA_CONNECTION.format());

		Socket s = null;
		PrintStream ps = null;
		InputStream is = null;
		FileOutputStream fout = null;

		try {
			s = session.getDataServerSocket().accept();
			ps = new PrintStream(s.getOutputStream());
			is = s.getInputStream();

			final byte[] buffer = new byte[4096];
			final File f = new File(session.getCurrentDir() + File.separator + filename);
			int nread = 0;

			LOGGER.debug("Receiving file {}", f.getCanonicalFile());

			fout = new FileOutputStream(f);

			ps.println("<STOR>");
			ps.println(filename);

			while(nread != -1 && !s.isClosed()) {
				nread = is.read(buffer);

				LOGGER.debug("Receiving {} bytes", nread);

				if (nread != -1) {
					LOGGER.debug("Buffer received = {}", buffer);

					fout.write(buffer, 0, nread);
					fout.flush();
				}
			}

			session.getControlOut().println(FtpServerResponse.TRANSFER_COMPLETED.format());
		} catch(final IOException e) {
			LOGGER.error("Error during file transfer: " + e.getMessage(), e);

			session.getControlOut().println(FtpServerResponse.TRANSFER_ABORTED.format());
			return false;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (final IOException e) { }
			}
			if (is != null) {
				try {
					is.close();
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

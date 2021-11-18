package com.github.matschieu.ftp.server.commands;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

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
public class ListCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(ListCommand.class);

	@Override
	public String getName() {
		return "LIST";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " : list contents of remote directory";
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

		if (!session.getCurrentDir().exists() || !session.getCurrentDir().isDirectory()) {
			session.getControlOut().println(FtpServerResponse.FILE_UNAVAILABLE.format(session.getCurrentDirPath()));
			return false;
		}

		if (session.getDataServerSocket() != null && session.getDataServerSocket().isClosed()) {
			session.getControlOut().println(FtpServerResponse.CANNOT_OPEN_DATA_CONNECTION.format());
			return false;
		}

		final File[] directories = session.getCurrentDir().listFiles(new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				return pathname.isDirectory();
			}
		});

		final File[] files = session.getCurrentDir().listFiles(new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				return !pathname.isDirectory();
			}
		});

		try {
			Arrays.sort(directories);
			Arrays.sort(files);
		} catch(final NullPointerException e) {
			return this.execute(cmd, session, config);
		}

		final File[] content = Stream.concat(Arrays.stream(directories), Arrays.stream(files)).toArray(File[]::new);

		session.getControlOut().println(FtpServerResponse.OPEN_DATA_CONNECTION.format());

		Socket s = null;
		PrintStream ps = null;

		try {
			s = session.getDataServerSocket().accept();
			ps = new PrintStream(s.getOutputStream());
			ps.println("<LIST>");
			s.getInputStream().read();

			for(final File f : content) {
				final Date d = new Date();
				d.setTime(f.lastModified());

				final StringBuffer str = new StringBuffer("");
				str.append(f.isDirectory() ? "d" : "-");
				str.append(f.canRead() ? "r" : "-");
				str.append(f.canWrite() ? "w" : "-");
				str.append(f.canExecute() ? "x" : "-");
				str.append("\t");
				str.append(f.length());
				str.append("\t");
				str.append(d);
				str.append("\t");
				str.append(f.getName() + "\t");

				ps.println(str.toString());
			}

			session.getControlOut().println(FtpServerResponse.TRANSFER_COMPLETED.format());
		} catch(final IOException e) {
			session.getControlOut().println(FtpServerResponse.TRANSFER_ABORTED.format());
			return false;
		} finally {
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

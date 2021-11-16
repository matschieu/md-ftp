package com.github.matschieu.ftp.server.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

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
public class HelpCommand implements FtpCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

	private final Map<String, String> helpMessagesByCommands;

	/**
	 *
	 */
	public HelpCommand() {
		this.helpMessagesByCommands = new HashMap<>();

		for(final FtpCommand command : ServiceLoader.load(FtpCommand.class).stream().filter(p -> p.type() != this.getClass()).map(p -> p.get()).collect(Collectors.toList())) {
			this.helpMessagesByCommands.put(command.getName(), command.getHelpMessage());
		}

		LOGGER.debug("Available FTP commands are: {}", Arrays.toString(this.helpMessagesByCommands.keySet().toArray()));
	}

	@Override
	public String getName() {
		return "HELP";
	}

	@Override
	public String getHelpMessage() {
		return this.getName() + " " + String.join(" | ", this.helpMessagesByCommands.keySet());
	}

	@Override
	public boolean isValid(final String cmd) {
		return cmd != null && cmd.matches("^" + this.getName() + "([ ]+(" + String.join("|", this.helpMessagesByCommands.keySet()) + "))?$");
	}

	@Override
	public boolean execute(final String cmd, final FtpSession session, final FtpServerConfiguration config) {
		LOGGER.debug("Executing command '{}'", cmd);

		if (!this.isValid(cmd)) {
			LOGGER.debug("Invalid command '{}'", cmd);

			session.getControlOut().println(FtpServerResponse.SYNTAX_ERROR.format(cmd));
			return false;
		}

		final StringTokenizer st = new StringTokenizer(cmd, " ", false);
		st.nextToken();

		String helpMessage;

		if (!st.hasMoreTokens()) {
			helpMessage = this.getHelpMessage();
		} else {
			final String param = st.nextToken();
			helpMessage = this.helpMessagesByCommands.get(param);
		}

		session.getControlOut().println(FtpServerResponse.HELP.format(helpMessage));

		return true;
	}

}

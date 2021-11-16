package com.github.matschieu.ftp.server.commands;

import java.util.Optional;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matschieu
 *
 */
public final class FtpCommandFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpCommandFactory.class);

	private static final ServiceLoader<FtpCommand> LOADER = ServiceLoader.load(FtpCommand.class);

	/**
	 *
	 */
	private FtpCommandFactory() { }

	/**
	 *
	 * @param cmd
	 * @return FtpCommand
	 */
	public static final FtpCommand createCommand(final String cmd) {
		final Optional<FtpCommand> opt;

		if (cmd != null && !cmd.isBlank()) {
			opt = LOADER.stream().filter(p -> p.type().getSimpleName().toLowerCase().startsWith(cmd.toLowerCase().split(" ")[0])).map(p -> p.get()).findFirst();
		} else {
			opt = Optional.empty();
		}


		LOGGER.info("Implementation for FTP command {} is {}", cmd, opt.orElse(null));

		return opt.orElse(null);
	}

}

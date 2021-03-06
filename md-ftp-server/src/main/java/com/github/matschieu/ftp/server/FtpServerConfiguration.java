package com.github.matschieu.ftp.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpServerConfiguration.class);

	private static final String SEPARATOR = ",";

	private static final int DEFAULT_SERVER_PORT = 5287;
	private static final int DEFAULT_SERVER_TIMEOUT = 3000;
	private static final String DEFAULT_ROOT_DIR_PATH = "/";
	private static final String DEFAULT_USER_LOGIN = "root";
	private static final String DEFAULT_USER_PWD = "1234";

	private final List<User> users = new ArrayList<>();
	private String rootDirPath;
	private int port;
	private int timeout;

	/**
	 *
	 * @return FtpServerConfiguration
	 */
	public static FtpServerConfiguration defaultConfiguration() {
		final FtpServerConfiguration config = new FtpServerConfiguration();
		config.setPort(DEFAULT_SERVER_PORT);
		config.setTimeout(DEFAULT_SERVER_TIMEOUT);
		config.setRootDirPath(DEFAULT_ROOT_DIR_PATH);
		config.registerUser(DEFAULT_USER_LOGIN, DEFAULT_USER_PWD);
		return config;
	}

	/**
	 *
	 * @param filename
	 * @return FtpServerConfiguration
	 */
	public static FtpServerConfiguration fromFile(final String filename) {
		final FtpServerConfiguration config = new FtpServerConfiguration();

		final Properties properties = new Properties();

		LOGGER.info("Loading configuration file {}", filename);

		final InputStream inputStream = FtpServerConfiguration.class.getClassLoader().getResourceAsStream(filename);

		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (final IOException e) {
				LOGGER.warn("Unable to load {} ({})", filename, e.getMessage());
			}

			try {
				inputStream.close();
			} catch (final IOException e) {
				LOGGER.info("Error when closing file {} ({})", filename, e.getMessage());
			}
		} else {
			LOGGER.warn("File {} not found", filename);
		}

		config.setPort(Integer.parseInt(properties.getProperty("server.port", "" + DEFAULT_SERVER_PORT)));
		config.setTimeout(Integer.parseInt(properties.getProperty("server.timeout", "" + DEFAULT_SERVER_TIMEOUT)));
		config.setRootDirPath(properties.getProperty("root.dir.path", DEFAULT_ROOT_DIR_PATH));

		boolean userDefined = false;

		if (properties.containsKey("users.login") && properties.containsKey("users.pwd")) {
			final String[] logins = properties.getProperty("users.login").split(SEPARATOR);
			final String[] pwds = properties.getProperty("users.pwd").split(SEPARATOR);

			if (logins.length == pwds.length) {
				for(int i = 0; i < logins.length; i++) {
					config.registerUser(logins[i], pwds[i]);
					userDefined = true;
				}
			}
		}

		if (!userDefined) {
			config.registerUser(DEFAULT_USER_LOGIN, DEFAULT_USER_PWD);
		}

		return config;
	}

	/**
	 *
	 * @param login
	 * @return boolean
	 */
	public boolean containsUser(final String login) {
		return this.users.stream().filter(u -> u.getLogin().equals(login)).count() == 1;
	}

	/**
	 *
	 * @param login
	 * @return String
	 */
	public String getUserPassword(final String login) {
		return this.users.stream().filter(u -> u.getLogin().equals(login)).findFirst().orElse(new User()).getPwd();
	}

	/**
	 *
	 * @param login
	 * @param pwd
	 * @return boolean
	 */
	public boolean registerUser(final String login, final String pwd) {
		return login != null && !login.isBlank() ? this.users.add(new User(login, pwd)) : false;
	}

	/**
	 * @return the rootDirPath
	 */
	public String getRootDirPath() {
		return this.rootDirPath;
	}

	/**
	 * @param rootDirPath the rootDirPath to set
	 */
	public void setRootDirPath(final String rootDirPath) {
		this.rootDirPath = rootDirPath;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(final int port) {
		this.port = port;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

}

package com.github.matschieu.ftp.client.cli;

/**
 *
 * @author Matschieu
 *
 */
public class FtpClientApp {

	public static final int DEFAULT_PORT = 5287;
	public static final String DEFAULT_ADDR = "127.0.0.1";

	/**
	 * Main method to start the program
	 */
	public static void main(final String[] args) {
		if (args.length == 2) {
			new FtpClient(args[0], Integer.parseInt(args[1]), true);
		} else {
			new FtpClient(DEFAULT_ADDR, DEFAULT_PORT, true);
		}
	}

}

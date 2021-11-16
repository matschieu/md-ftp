package com.github.matschieu.ftp.server;

/**
 * @author Matschieu
 */
public enum FtpServerResponse {

	OPEN_DATA_CONNECTION(150, "File status okay; about to open data connection"),

	COMMAND_SUCCESSFUL(200, "{} command successful"),
	HELP(214, "{}"),
	WELCOME(220, "Welcome, service ready"),
	GOODBYE(221, "Goodbye"),
	TRANSFER_COMPLETED(226, "Closing data connection, transfer completed"),
	USER_LOGGED_IN(230, "User {} logged in"),
	CHANGE_CURRENT_DIRECTORY(250, "directory changed to {}"),
	CURRENT_DIRECTORY_INFO(257, "{} is current directory"),

	UNKNOWN_CMD(500, "Syntax error, command unrecognized"),
	SYNTAX_ERROR(501, "Syntax error in parameters or arguments"),
	COMMAND_NOT_IMPLEMENTED(502, "Command not implemented"),
	ALREADY_CONNECTED(503, "Bad sequence of commands"),
	NOT_CONNECTED(530, "Not logged in"),
	NO_SUCH_FILE(550, "{}: No such file or directory"),

	PASSWORD_REQUIRED(331, "Password required for {}"),
	CANNOT_CONNECT(332, "Need account for login"),

	CLOSE_CONTROL_CONNECTION(421, "Closing control connection"),
	CANNOT_OPEN_DATA_CONNECTION(425, "Can't open data connection"),
	TRANSFER_ABORTED(426, "Connection closed; transfer aborted"),
	FILE_UNAVAILABLE(450, "Requested file action not taken, file unavailable"),
	;

	private final int statusCode;

	private final String message;

	/**
	 *
	 * @param statusCode
	 * @param message
	 */
	private FtpServerResponse(final int statusCode, final String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	/**
	 *
	 * @param args
	 * @return String
	 */
	public String format(final Object... args) {
		String finalMessage = this.statusCode + " " + this.message;

		if (this.message.contains("{}") && args.length > 0) {
			finalMessage = String.format(finalMessage.replace("{}", "%s"), args);
		}

		return finalMessage;
	}

}

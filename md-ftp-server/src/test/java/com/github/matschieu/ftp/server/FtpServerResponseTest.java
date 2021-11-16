package com.github.matschieu.ftp.server;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerResponseTest {

	@Test
	public void testMessageFormat() {
		Assert.assertEquals("220 Welcome, service ready", FtpServerResponse.WELCOME.format());
		Assert.assertEquals("500 Syntax error, command unrecognized", FtpServerResponse.UNKNOWN_CMD.format());
		Assert.assertEquals("501 Syntax error in parameters or arguments", FtpServerResponse.SYNTAX_ERROR.format());
		Assert.assertEquals("530 Not logged in", FtpServerResponse.NOT_CONNECTED.format());
		Assert.assertEquals("332 Need account for login", FtpServerResponse.CANNOT_CONNECT.format());
		Assert.assertEquals("503 Bad sequence of commands", FtpServerResponse.ALREADY_CONNECTED.format());
		Assert.assertEquals("230 User root logged in", FtpServerResponse.USER_LOGGED_IN.format("root"));
		Assert.assertEquals("331 Password required for root", FtpServerResponse.PASSWORD_REQUIRED.format("root"));
		Assert.assertEquals("450 Requested file action not taken, file unavailable", FtpServerResponse.FILE_UNAVAILABLE.format());
		Assert.assertEquals("150 File status okay; about to open data connection", FtpServerResponse.OPEN_DATA_CONNECTION.format());
		Assert.assertEquals("425 Can't open data connection", FtpServerResponse.CANNOT_OPEN_DATA_CONNECTION.format());
		Assert.assertEquals("226 Closing data connection, transfer completed", FtpServerResponse.TRANSFER_COMPLETED.format());
		Assert.assertEquals("426 Connection closed; transfer aborted", FtpServerResponse.TRANSFER_ABORTED.format());
		Assert.assertEquals("221 Goodbye", FtpServerResponse.GOODBYE.format());
		Assert.assertEquals("257 /folder is current directory", FtpServerResponse.CURRENT_DIRECTORY_INFO.format("/folder"));
		Assert.assertEquals("550 folder: No such file or directory", FtpServerResponse.NO_SUCH_FILE.format("folder"));
		Assert.assertEquals("250 directory changed to folder", FtpServerResponse.CHANGE_CURRENT_DIRECTORY.format("folder"));
		Assert.assertEquals("200 PWD command successful", FtpServerResponse.COMMAND_SUCCESSFUL.format("PWD"));
		Assert.assertEquals("421 Closing control connection", FtpServerResponse.CLOSE_CONTROL_CONNECTION.format());

		Assert.assertEquals("220 Welcome, service ready", FtpServerResponse.WELCOME.format("a", "b"));
		Assert.assertEquals("200 PWD command successful", FtpServerResponse.COMMAND_SUCCESSFUL.format("PWD", "CWD", ""));
		Assert.assertEquals("200 {} command successful", FtpServerResponse.COMMAND_SUCCESSFUL.format());

	}
}

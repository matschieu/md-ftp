package com.github.matschieu.ftp.server.commands;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.matschieu.ftp.server.FtpServerConfiguration;
import com.github.matschieu.ftp.server.FtpSession;
import com.github.matschieu.ftp.server.MockServerSocket;
import com.github.matschieu.ftp.server.MockSocket;

/**
 *
 * @author Matschieu
 *
 */
public abstract class FtpCommandTest {

	protected final String cmdName;
	protected final String helpMessage;
	protected final FtpCommand cmd;

	protected FtpSession session;
	protected FtpServerConfiguration config;

	/**
	 *
	 * @param cmd
	 * @param cmdName
	 * @param helpMessage
	 */
	public FtpCommandTest(final FtpCommand cmd, final String cmdName, final String helpMessage) {
		this.cmd = cmd;
		this.cmdName = cmdName;
		this.helpMessage = helpMessage;
	}

	/**
	 *
	 * @param data
	 */
	protected void readInput(final String data) {
		System.setIn(new ByteArrayInputStream(data.getBytes(), 0, data.getBytes().length));
	}

	@Before
	public void init() throws IOException {
		this.session = new FtpSession(new MockSocket(), new MockServerSocket(), new File("."));
		this.config = FtpServerConfiguration.defaultConfiguration();
	}

	@Test
	public void testName() {
		Assert.assertEquals(this.cmdName, this.cmd.getName());
	}

	@Test
	public void testHelpMessage() {
		Assert.assertEquals(this.helpMessage, this.cmd.getHelpMessage());
	}

}

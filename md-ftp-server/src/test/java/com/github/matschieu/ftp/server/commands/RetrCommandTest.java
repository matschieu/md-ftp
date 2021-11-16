package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class RetrCommandTest extends FtpCommandTest {

	public RetrCommandTest() {
		super(new RetrCommand(), "RETR", "RETR <filename> : receive file");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("RETR file"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("RETR"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("RETR", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("RETR file", this.session, this.config));

		this.session.setUserConnected(true);

		Assert.assertFalse(this.cmd.execute("RETR file", this.session, this.config));

		this.readInput("\r\n");

		Assert.assertTrue(this.cmd.execute("RETR pom.xml", this.session, this.config));
	}

}

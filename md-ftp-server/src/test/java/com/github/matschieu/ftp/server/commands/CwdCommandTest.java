package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class CwdCommandTest extends FtpCommandTest {

	public CwdCommandTest() {
		super(new CwdCommand(), "CWD", "CWD <directory> : change working directory on remote machine");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("CWD arg"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("CWD"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("CWD", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("CWD arg", this.session, this.config));

		this.session.setUserConnected(true);

		Assert.assertTrue(this.cmd.execute("CWD /target", this.session, this.config));
	}

}

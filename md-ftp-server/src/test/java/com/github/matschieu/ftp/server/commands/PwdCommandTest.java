package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class PwdCommandTest extends FtpCommandTest {

	public PwdCommandTest() {
		super(new PwdCommand(), "PWD", "PWD : print working directory on remote machine");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("PWD"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("PWD arg"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("PWD args", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("PWD", this.session, this.config));

		this.session.setUserConnected(true);

		Assert.assertTrue(this.cmd.execute("PWD", this.session, this.config));
	}

}

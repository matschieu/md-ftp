package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class UserCommandTest extends FtpCommandTest {

	public UserCommandTest() {
		super(new UserCommand(), "USER", "USER <user_name> : send new user information");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("USER root"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("USER"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("USER", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("USER bob", this.session, this.config));
		Assert.assertTrue(this.cmd.execute("USER root", this.session, this.config));
	}

}

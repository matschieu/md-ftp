package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class PassCommandTest extends FtpCommandTest {

	public PassCommandTest() {
		super(new PassCommand(), "PASS", "PASS <user_password>");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("PASS pwd"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("PASS"));
		Assert.assertFalse(this.cmd.isValid("PASS pwd1 pwd2"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("PASS", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("PASS pwd", this.session, this.config));

		this.session.setCurrentUser("root");

		Assert.assertFalse(this.cmd.execute("PASS pwd", this.session, this.config));

		this.session.setUserConnected(true);

		Assert.assertFalse(this.cmd.execute("PASS 1234", this.session, this.config));

		this.session.setUserConnected(false);

		Assert.assertTrue(this.cmd.execute("PASS 1234", this.session, this.config));

	}

}

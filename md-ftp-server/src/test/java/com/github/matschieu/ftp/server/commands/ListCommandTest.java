package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class ListCommandTest extends FtpCommandTest {

	public ListCommandTest() {
		super(new ListCommand(), "LIST", "LIST : list contents of remote directory");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("LIST"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("LIST arg"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("LIST arg", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("LIST", this.session, this.config));

		this.session.setUserConnected(true);

		this.readInput("\n");

		Assert.assertTrue(this.cmd.execute("LIST", this.session, this.config));

		this.session.setUserConnected(false);

		Assert.assertFalse(this.cmd.execute("LIST", this.session, this.config));
	}

}

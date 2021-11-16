package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class QuitCommandTest extends FtpCommandTest {

	public QuitCommandTest() {
		super(new QuitCommand(), "QUIT", "QUIT : terminate ftp session and exit");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("QUIT"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("QUIT arg"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("QUIT args", this.session, this.config));
		Assert.assertTrue(this.cmd.execute("QUIT", this.session, this.config));
	}

}

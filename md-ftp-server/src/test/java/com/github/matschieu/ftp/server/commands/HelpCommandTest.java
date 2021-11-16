package com.github.matschieu.ftp.server.commands;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class HelpCommandTest extends FtpCommandTest {

	public HelpCommandTest() {
		super(new HelpCommand(), "HELP", "HELP CWD | STOR | CDUP | PASS | RETR | QUIT | PWD | USER | LIST");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("HELP"));
		Assert.assertTrue(this.cmd.isValid("HELP LIST"));
		Assert.assertFalse(this.cmd.isValid("HELP arg"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("HELP arg", this.session, this.config));
		Assert.assertTrue(this.cmd.execute("HELP", this.session, this.config));
		Assert.assertTrue(this.cmd.execute("HELP LIST", this.session, this.config));
	}

}

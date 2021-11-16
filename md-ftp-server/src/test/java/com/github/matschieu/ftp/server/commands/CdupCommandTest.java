package com.github.matschieu.ftp.server.commands;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class CdupCommandTest extends FtpCommandTest {

	public CdupCommandTest() {
		super(new CdupCommand(), "CDUP", "CDUP : change remote working directory to parent directory");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("CDUP"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("CDUP arg"));
	}

	@Test
	public void testExecute() {
		Assert.assertFalse(this.cmd.execute("CDUP args", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("CDUP", this.session, this.config));

		this.session.setUserConnected(true);
		this.session.setCurrentDir(new File("/target/classes"));

		Assert.assertTrue(this.cmd.execute("CDUP", this.session, this.config));
	}

}

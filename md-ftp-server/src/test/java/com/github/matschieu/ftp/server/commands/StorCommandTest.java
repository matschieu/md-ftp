package com.github.matschieu.ftp.server.commands;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class StorCommandTest extends FtpCommandTest {

	public StorCommandTest() {
		super(new StorCommand(), "STOR", "STOR <filename> : send one file");
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(this.cmd.isValid("STOR file"));
		Assert.assertFalse(this.cmd.isValid("FOO"));
		Assert.assertFalse(this.cmd.isValid("STOR"));
	}

	@Test
	public void testExecute() throws IOException {
		Assert.assertFalse(this.cmd.execute("STOR", this.session, this.config));
		Assert.assertFalse(this.cmd.execute("STOR file", this.session, this.config));

		this.session.setUserConnected(true);

		new File("target/file").delete();
		Assert.assertFalse(new File("target/file").exists());

		this.readInput("Test\n");

		Assert.assertTrue(this.cmd.execute("STOR target/file", this.session, this.config));

		Assert.assertTrue(new File("target/file").exists());
	}

}

package com.github.matschieu.ftp.server;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerConfigurationTest {

	@Test
	public void testFromfileWithValidFile() {
		final FtpServerConfiguration config = FtpServerConfiguration.fromFile("ftp-server.properties");

		Assert.assertNotNull(config);

		Assert.assertNotNull(config.getRootDirPath());
		Assert.assertEquals("/home", config.getRootDirPath());

		Assert.assertNotNull(config.getPort());
		Assert.assertEquals(1234, config.getPort());

		Assert.assertNotNull(config.getTimeout());
		Assert.assertEquals(2000, config.getTimeout());

		Assert.assertNotNull(config.getUsersAndPasswords());
		Assert.assertEquals(2, config.getUsersAndPasswords().size());

		Assert.assertEquals("java2", config.getUsersAndPasswords().keySet().toArray()[0]);
		Assert.assertEquals("java1", config.getUsersAndPasswords().keySet().toArray()[1]);
		Assert.assertEquals("P4$$w0rd", config.getUsersAndPasswords().values().toArray()[0]);
		Assert.assertEquals("p4ssw0rd", config.getUsersAndPasswords().values().toArray()[1]);
	}

	@Test
	public void testFromfileWithInvalidFile() {
		final FtpServerConfiguration config = FtpServerConfiguration.fromFile("dummy.properties");

		Assert.assertNotNull(config);

		Assert.assertNotNull(config.getRootDirPath());
		Assert.assertEquals("/", config.getRootDirPath());

		Assert.assertNotNull(config.getPort());
		Assert.assertEquals(5287, config.getPort());

		Assert.assertNotNull(config.getTimeout());
		Assert.assertEquals(3000, config.getTimeout());

		Assert.assertNotNull(config.getUsersAndPasswords());
		Assert.assertEquals(1, config.getUsersAndPasswords().size());

		Assert.assertEquals("root", config.getUsersAndPasswords().keySet().iterator().next());
		Assert.assertEquals("1234", config.getUsersAndPasswords().values().iterator().next());
	}

	@Test
	public void testDefaultConfiguration() {
		final FtpServerConfiguration config = FtpServerConfiguration.defaultConfiguration();

		Assert.assertNotNull(config);

		Assert.assertNotNull(config.getRootDirPath());
		Assert.assertEquals("/", config.getRootDirPath());

		Assert.assertNotNull(config.getPort());
		Assert.assertEquals(5287, config.getPort());

		Assert.assertNotNull(config.getTimeout());
		Assert.assertEquals(3000, config.getTimeout());

		Assert.assertNotNull(config.getUsersAndPasswords());
		Assert.assertEquals(1, config.getUsersAndPasswords().size());

		Assert.assertEquals("root", config.getUsersAndPasswords().keySet().iterator().next());
		Assert.assertEquals("1234", config.getUsersAndPasswords().values().iterator().next());
	}

}

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

		Assert.assertTrue(config.containsUser("java1"));
		Assert.assertTrue(config.containsUser("java2"));

		Assert.assertEquals("p4ssw0rd", config.getUserPassword("java1"));
		Assert.assertEquals("P4$$w0rd", config.getUserPassword("java2"));
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

		Assert.assertFalse(config.containsUser("java1"));
		Assert.assertTrue(config.containsUser("root"));

		Assert.assertNull(config.getUserPassword("java1"));
		Assert.assertEquals("1234", config.getUserPassword("root"));
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

		Assert.assertFalse(config.containsUser("java1"));
		Assert.assertTrue(config.containsUser("root"));

		Assert.assertNull(config.getUserPassword("java1"));
		Assert.assertEquals("1234", config.getUserPassword("root"));
	}

}

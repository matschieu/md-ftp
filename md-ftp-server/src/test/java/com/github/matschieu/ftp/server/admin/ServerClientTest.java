package com.github.matschieu.ftp.server.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class ServerClientTest {


	@Test
	public void testGetterAndSetter() throws UnknownHostException {
		final Client client = new Client(1, InetAddress.getByName("127.0.0.1"));

		Assert.assertEquals(1, client.getCid());
		Assert.assertEquals("/127.0.0.1", client.getInetAddress().toString());

		client.setCid(2);
		client.setInetAddress(InetAddress.getByName("127.0.0.2"));

		Assert.assertEquals(2, client.getCid());
		Assert.assertEquals("/127.0.0.2", client.getInetAddress().toString());
	}

	@Test
	public void testToString() throws UnknownHostException {
		Client client = new Client(1, InetAddress.getByName("127.0.0.1"));
		Assert.assertEquals("1 -> /127.0.0.1", client.toString());

		client = new Client();
		Assert.assertEquals("0 -> null", client.toString());
	}

}

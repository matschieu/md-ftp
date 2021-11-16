package com.github.matschieu.ftp.server;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FtpSessionTest {

	private Socket socket;
	private ServerSocket serverSocket;
	private File root;

	@Before
	public void init() throws IOException {
		this.socket = new MockSocket();
		this.serverSocket = new MockServerSocket();
		this.root = new File("/target");
	}

	@Test
	public void testFtpSessionInstanciation() {
		final FtpSession session = new FtpSession(this.socket, this.serverSocket, this.root);

		Assert.assertEquals(this.socket, session.getControlSocket());
		Assert.assertEquals(this.serverSocket, session.getDataServerSocket());
		Assert.assertEquals(this.root, session.getRootDir());
		Assert.assertEquals(this.root, session.getCurrentDir());
		Assert.assertEquals("/", session.getCurrentDirPath());
		Assert.assertNotNull(session.getControlOut());

		Assert.assertNull(session.getCurrentUser());
		Assert.assertFalse(session.isUserConnected());
	}

	@Test
	public void testFtpSessionChangeCurrentDir() {
		final FtpSession session = new FtpSession(this.socket, this.serverSocket, this.root);

		File newLocation = new File("/target/classes");

		session.setCurrentDir(newLocation);

		Assert.assertEquals(this.root, session.getRootDir());
		Assert.assertEquals(newLocation, session.getCurrentDir());
		Assert.assertEquals("/classes", session.getCurrentDirPath());

		newLocation = new File("/target/classes/..");

		session.setCurrentDir(newLocation);

		Assert.assertEquals(this.root, session.getRootDir());
		Assert.assertEquals(newLocation, session.getCurrentDir());
		Assert.assertEquals("/", session.getCurrentDirPath());
		try {
			Assert.assertEquals(this.root.getCanonicalPath(), session.getCurrentDir().getCanonicalPath());
		} catch (final IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFtpSessionConnectUser() {
		final FtpSession session = new FtpSession(this.socket, this.serverSocket, this.root);

		session.setCurrentUser("Bob");

		Assert.assertEquals("Bob", session.getCurrentUser());
		Assert.assertFalse(session.isUserConnected());

		session.setUserConnected(true);

		Assert.assertEquals("Bob", session.getCurrentUser());
		Assert.assertTrue(session.isUserConnected());
	}


}

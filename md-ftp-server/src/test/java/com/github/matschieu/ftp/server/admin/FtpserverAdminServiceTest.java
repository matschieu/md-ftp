package com.github.matschieu.ftp.server.admin;

import static org.junit.Assert.fail;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.github.matschieu.ftp.server.FtpServer;
import com.github.matschieu.ftp.server.FtpServerConfiguration;
import com.github.matschieu.ftp.server.FtpServerException;

/**
 *
 * @author Matschieu
 *
 */
public class FtpserverAdminServiceTest {

	@After
	public void pause() throws InterruptedException {
		Thread.sleep(50);
	}

	private List<Socket> initSocketList() {
		final List<Socket> socketList = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			try {
				final Socket socket = new Socket("localhost", FtpServerConfiguration.defaultConfiguration().getPort());
				socketList.add(socket); // Keeping the Socket alive out of this block

				Thread.sleep(50);

				if (i == 0) {
					socket.close();
				}
			} catch (final Exception e) { }
		}

		return socketList;
	}

	@Test
	public void testStartAndStop() throws FtpServerException, InterruptedException {
		final FtpServer server = new FtpServer(FtpServerConfiguration.defaultConfiguration());
		final FtpserverAdminService admin = server.getAdminService();

		admin.start();

		Assert.assertTrue(admin.isRunning());

		Thread.sleep(50);
		admin.stop();

		Assert.assertFalse(admin.isRunning());

		Thread.sleep(50);
		admin.start();

		Assert.assertTrue(admin.isRunning());

		Thread.sleep(50);
		admin.stop();

		Assert.assertFalse(admin.isRunning());
	}
	@Test
	public void testSendMessage() throws FtpServerException {
		final FtpServer server = new FtpServer(FtpServerConfiguration.defaultConfiguration());
		final FtpserverAdminService admin = server.getAdminService();

		admin.start();

		@SuppressWarnings("unused")
		final List<Socket> socketList = this.initSocketList();

		try {
			admin.sendMessage(10, "message");
			fail();
		} catch (final FtpServerException e) { }

		admin.sendMessage(0, "message");

		admin.stop();
	}

	@Test
	public void testFireClient() throws FtpServerException {
		final FtpServer server = new FtpServer(FtpServerConfiguration.defaultConfiguration());
		final FtpserverAdminService admin = server.getAdminService();

		admin.start();

		@SuppressWarnings("unused")
		final List<Socket> socketList = this.initSocketList();

		try {
			admin.fireClient(10);
			fail();
		} catch (final FtpServerException e) { }

		Assert.assertEquals(3, admin.getConnectedClients().size());

		admin.fireClient(0);

		Assert.assertEquals(2, admin.getConnectedClients().size());

		admin.fireClient(0);

		Assert.assertEquals(1, admin.getConnectedClients().size());

		admin.fireClient(0);

		Assert.assertTrue(admin.getConnectedClients().isEmpty());

		admin.stop();
	}

}

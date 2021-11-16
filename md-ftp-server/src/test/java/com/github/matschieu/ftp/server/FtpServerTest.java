package com.github.matschieu.ftp.server;

import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerTest {

	@Test
	public void testRestart() throws InterruptedException, FtpServerException {
		final FtpServer server1 = new FtpServer(FtpServerConfiguration.defaultConfiguration());

		for(int i = 0; i < 2 ; i++) {
			server1.start();
			Thread.sleep(100);
			server1.stop();
			Thread.sleep(100);
		}
	}

	@Test
	public void testServerCreationKO() throws FtpServerException {
		final FtpServer server1 = new FtpServer(null);
		server1.start();
		try {
			new FtpServer(FtpServerConfiguration.defaultConfiguration()).start();
			fail();
		} catch(final FtpServerException e) {

		} finally {
			server1.stop();
		}
	}

	@Test
	public void testServerLife() throws FtpServerException {
		final FtpServer server = new FtpServer(FtpServerConfiguration.defaultConfiguration());

		server.start();

		Assert.assertTrue(server.isRunning());

		final List<Socket> socketList = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			try {
				final Socket socket = new Socket("localhost", FtpServerConfiguration.defaultConfiguration().getPort());
				socketList.add(socket); // Keeping the Socket alive out of this block

				final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println("LIST");
				out.flush();
				out.println("PWD");
				out.flush();

				Thread.sleep(100);

				if (i == 3) {
					out.close();
					socket.close();
				}
			} catch (final Exception e) { }
		}

		server.stop();

		Assert.assertFalse(server.isRunning());
	}

}

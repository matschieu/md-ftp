package com.github.matschieu.ftp.server.admin;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.matschieu.ftp.server.FtpServer;
import com.github.matschieu.ftp.server.FtpServerException;
import com.github.matschieu.ftp.server.FtpServerResponse;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerAdminServiceImpl implements FtpserverAdminService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpServerAdminServiceImpl.class);

	private final FtpServer ftpServer;

	private final List<Socket> clientList;

	/**
	 *
	 * @param ftpServer
	 * @param clientList
	 */
	public FtpServerAdminServiceImpl(final FtpServer ftpServer, final List<Socket> clientList) {
		this.ftpServer = Optional.of(ftpServer).get();
		this.clientList = Optional.of(clientList).get();
	}

	/**
	 *
	 * @throws FtpServerException
	 */
	@Override
	public void start() throws FtpServerException {
		this.ftpServer.start();
	}

	@Override
	public void stop() {
		this.ftpServer.stop();
	}

	@Override
	public boolean isRunning() {
		return this.ftpServer.isRunning();
	}

	@Override
	public synchronized List<Client> getConnectedClients() {
		final List<Client> clients = new ArrayList<>(this.clientList.size());

		int idx = 0;
		for(final Socket s : this.clientList) {
			clients.add(new Client(idx++, s.getInetAddress()));
		}

		LOGGER.debug("Clients connected: {}", Arrays.toString(clients.toArray()));

		return clients;
	}

	@Override
	public synchronized void sendMessage(final int cid, final String message) throws FtpServerException {
		if (cid < 0 || cid >= this.clientList.size()) {
			throw new FtpServerException("Invalid CID");
		}

		try {
			final Socket socket = this.clientList.get(cid);

			LOGGER.info("Sending message '{}' to {}...", message, socket.getInetAddress());

			socket.getOutputStream().write(message.getBytes());
			socket.getOutputStream().write("\n".getBytes());
			socket.getOutputStream().flush();

			LOGGER.info("Message sent");
		} catch(final IOException e) {
			LOGGER.error("Error while sending message: " + e.getMessage(), e);
		}
	}

	@Override
	public synchronized void fireClient(final int cid) throws FtpServerException {
		if (cid < 0 || cid >= this.clientList.size()) {
			throw new FtpServerException("Invalid CID");
		}

		try {
			final Socket socket = this.clientList.get(cid);

			LOGGER.info("Closing connection to {}...", socket.getInetAddress());

			socket.getOutputStream().write(FtpServerResponse.CLOSE_CONTROL_CONNECTION.format().getBytes());
			socket.getOutputStream().write("\n".getBytes());
			socket.close();

			this.clientList.remove(socket);

			LOGGER.info("Connection closed");
		}
		catch(final IOException e) {
			LOGGER.error("Error while closing connection: " + e.getMessage(), e);
		}
	}

}

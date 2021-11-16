package com.github.matschieu.ftp.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.matschieu.ftp.server.admin.FtpServerAdminServiceImpl;
import com.github.matschieu.ftp.server.admin.FtpserverAdminService;
import com.github.matschieu.ftp.server.commands.FtpCommand;
import com.github.matschieu.ftp.server.commands.FtpCommandFactory;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpServer.class);

	private final List<Socket> clientList;
	private final File rootDir;
	private ServerSocket controlServerSocket;
	private ServerSocket dataServerSocket;

	private final FtpServerConfiguration configuration;

	private Thread mainThread;

	private final FtpserverAdminService adminService;

	/**
	 *
	 * @param config
	 */
	public FtpServer(final FtpServerConfiguration config) {
		this.configuration = Objects.requireNonNullElse(config, FtpServerConfiguration.defaultConfiguration());

		LOGGER.info("Initializing FTP server...");

		this.controlServerSocket = null;
		this.dataServerSocket = null;
		this.clientList = new LinkedList<>();
		this.rootDir = new File(this.configuration.getRootDirPath());

		this.adminService = new FtpServerAdminServiceImpl(this, this.clientList);

		LOGGER.info("FTP server initialized (root directory is {})", this.configuration.getRootDirPath());
	}

	/**
	 * Start the server
	 * It will listen to the network to accept new client (new client = new thread)
	 * @throws FtpServerException
	 */
	public void start() throws FtpServerException {
		if (!this.isRunning()) {
			LOGGER.info("Starting FTP server on port {}...", this.configuration.getPort());

			try {
				this.controlServerSocket = new ServerSocket(this.configuration.getPort());
				this.dataServerSocket = new ServerSocket(this.configuration.getPort() -1);
				this.dataServerSocket.setSoTimeout(this.configuration.getTimeout());
			}
			catch(final IOException e) {
				LOGGER.error("Could not start FTP server: " + e.getMessage());
				this.stop();
				throw new FtpServerException(e.getMessage(), e);
			}

			this.mainThread = new Thread() {
				@Override
				public void run() {
					LOGGER.debug("Starting main thread");
					FtpServer.this.run();
				}
			};

			this.mainThread.start();

			LOGGER.info("FTP server started", this.configuration.getRootDirPath());
		}
	}

	/**
	 *
	 */
	private void run() {
		while(true) {
			Socket socket = null;

			try {
				socket = this.controlServerSocket.accept();
				final Socket tmpSocket = socket;

				synchronized (this.clientList) {
					this.clientList.add(socket);
				}

				LOGGER.info("{} is now connected", socket.getInetAddress());

				new Thread() {
					@Override
					public void run() {
						FtpServer.this.process(tmpSocket);
					}
				}.start();
			}
			catch(final IOException e) { }
			catch(final Exception e) {
				synchronized (this.clientList) {
					this.clientList.remove(socket);
				}
			}
		}
	}

	/**
	 *
	 * @param clientSocket the socket opened by the server with the client
	 */
	private void process(final Socket clientSocket) {
		final FtpSession session = new FtpSession(clientSocket, this.dataServerSocket, this.rootDir);

		try(Scanner socketInputStream = new Scanner(clientSocket.getInputStream())) {
			while(!clientSocket.isClosed()) {
				final String cmd = socketInputStream.nextLine();

				LOGGER.info("Command '{}' received from {}", cmd, clientSocket.getInetAddress());

				final FtpCommand command = FtpCommandFactory.createCommand(cmd);

				if (command != null && command.isValid(cmd)) {
					command.execute(cmd, session, this.configuration);
				}
			}
		}
		catch(final Exception e) { }
		finally {
			LOGGER.info("Ending session for {}", clientSocket.getInetAddress());

			synchronized (this.clientList) {
				this.clientList.remove(clientSocket);
			}
		}
	}

	/**
	 * To know if the server is running
	 * @return true if the server is still running
	 */
	public synchronized boolean isRunning() {
		return this.controlServerSocket != null && !this.controlServerSocket.isClosed();
	}

	/**
	 * Stop the server and notify all clients
	 */
	public synchronized void stop() {
		LOGGER.info("Stopping ftp server... ");

		if (!this.isRunning()) {
			LOGGER.info("Stopped");
			return;
		}

		LOGGER.info("Notifying all clients... ");

		synchronized (this.clientList) {
			for(final Socket socket : this.clientList) {
				if (socket.isClosed()) {
					continue;
				}

				LOGGER.debug("Closing session for {}", socket.getInetAddress());

				try {
					socket.getOutputStream().write(FtpServerResponse.CLOSE_CONTROL_CONNECTION.format().getBytes());
					socket.getOutputStream().write("\n".getBytes());
					socket.getOutputStream().flush();
					socket.close();
				} catch(final Exception e) {
					LOGGER.error("Error: " + e.getMessage(), e);
				}
			}
		}

		LOGGER.info("All sessions are now closed");

		try {
			if (!this.controlServerSocket.isClosed()) {
				this.controlServerSocket.close();
			}
		} catch(final IOException e) {
			LOGGER.debug("Error: {}", e.getMessage());
		}

		try {
			if (!this.dataServerSocket.isClosed()) {
				this.dataServerSocket.close();
			}
		} catch(final IOException e) {
			LOGGER.debug("Error: {}", e.getMessage());
		}

		if (this.mainThread != null) {
			LOGGER.debug("Stoping main thread");

			this.mainThread.interrupt();
		}

		LOGGER.info("Stopped");
	}

	/**
	 *
	 * @return FtpserverAdminService
	 */
	public FtpserverAdminService getAdminService() {
		return this.adminService;
	}

}

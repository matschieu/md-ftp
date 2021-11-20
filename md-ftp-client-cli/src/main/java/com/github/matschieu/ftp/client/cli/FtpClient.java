package com.github.matschieu.ftp.client.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Matschieu
 *
 */
public class FtpClient {

	public static final String LIST_TAG = "<LIST>";
	public static final String RETR_TAG = "<RETR>";
	public static final String STOR_TAG = "<STOR>";

	public static final String EXIT = "exit";

	private final InputStream consoleIn = System.in;
	private final PrintStream consoleOut = System.out;
	private final PrintStream consoleErr = System.err;

	private boolean prompt = false;

	private Thread controlThread;
	private Thread dataThread;

	private Socket controlSocket;
	private Scanner controlIn;
	private PrintStream controlOut;

	/**
	 * Construct a new FTP client
	 * If run has a true value, the start method is called
	 * @param address the address of the server
	 * @param port the port where the server is listening to
	 * @param run run start method ?
	 */
	public FtpClient(final String address, final int port, final boolean run) {
		try {
			this.consoleOut.print("> Connection to " + address + " on port " + port + "... ");

			this.controlSocket = new Socket(address, port);
			this.controlIn = new Scanner(this.controlSocket.getInputStream());
			this.controlOut = new PrintStream(this.controlSocket.getOutputStream(), true);

			this.consoleOut.println("ok");
			this.consoleOut.println(this.controlIn.nextLine());

			this.controlThread = new Thread() {
				@Override
				public void run() {
					FtpClient.this.sendCommand();
				}
			};

			this.dataThread = new Thread() {
				@Override
				public void run() {
					FtpClient.this.listenToResponse();
				}
			};

			if (run) {
				this.start();
			}
		} catch(final IOException e) {
			this.consoleErr.println("error: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Launch the client CLI
	 */
	public void start() {
		this.controlThread.start();
		this.dataThread.start();
	}

	/**
	 * Close the connection with the server
	 */
	public void stop() {
		if (this.controlSocket.isClosed()) {
			return;
		}

		this.controlThread.interrupt();
		this.dataThread.interrupt();

		try {
			this.controlSocket.close();
		} catch (final IOException e) {
			this.consoleErr.println(e.getMessage());
		}

		this.consoleOut.println("> Connection closed");
	}

	/**
	 *
	 */
	private void displayPrompt() {
		this.consoleOut.print("# ");
		this.consoleOut.flush();
		this.prompt = true;
	}

	/**
	 *
	 * @param response
	 */
	private void displayResponse(final String response) {
		if (this.prompt) {
			// Erase the prompt displayed to print the response and then display the prompt again
			this.consoleOut.print("\b\b");
			this.consoleOut.flush();
		}

		this.consoleOut.println(response);
		this.displayPrompt();
	}

	/**
	 *
	 * @param socket
	 * @param value
	 * @throws IOException
	 */
	private void ackReady(final Socket socket, final int value) throws IOException {
		// Notify the server we are ready to receive the data
		socket.getOutputStream().write(value);
	}

	/**
	 *
	 * @param inStream
	 * @param outStream
	 * @throws IOException
	 */
	private void fromInputIntoOutput(final InputStream inStream, final OutputStream outStream) throws IOException {
		final byte[] buffer = new byte[4096];
		int nread = 0;

		while(nread != -1) {
			nread = inStream.read(buffer, 0, buffer.length);

			if (nread != -1) {
				outStream.write(buffer, 0, nread);
				outStream.flush();
			}
		}
	}

	/**
	 *
	 * @param dataSocket
	 * @throws IOException
	 */
	private void displayResponse(final Socket dataSocket) throws IOException {
		this.ackReady(dataSocket, -1);
		this.fromInputIntoOutput(dataSocket.getInputStream(), this.consoleOut);
	}

	/**
	 *
	 * @param dataSocket
	 * @param socketScanner
	 * @throws IOException
	 */
	private void sendFile(final Socket dataSocket, final Scanner socketScanner) throws IOException {
		final File file = new File(socketScanner.nextLine());

		if (file.exists()) {
			this.ackReady(dataSocket, -1);

			final FileInputStream fileIn = new FileInputStream(file);

			this.fromInputIntoOutput(fileIn, dataSocket.getOutputStream());

			fileIn.close();
		} else {
			this.ackReady(dataSocket, 0);
			file.delete();
		}
	}

	/**
	 *
	 * @param dataSocket
	 * @param socketScanner
	 * @throws IOException
	 */
	private void receiveFile(final Socket dataSocket, final Scanner socketScanner) throws IOException {
		final FileOutputStream fileOut = new FileOutputStream(new File("." + File.separator + socketScanner.nextLine()));
		this.ackReady(dataSocket, -1);
		this.fromInputIntoOutput(dataSocket.getInputStream(), fileOut);
		fileOut.close();
	}

	/**
	 * Listen to the network waiting for an response from the server
	 */
	private void listenToResponse() {
		try {
			String response = "";
			while(!response.startsWith("221") || !response.startsWith("421") && !this.controlSocket.isClosed()) {
				response = this.controlIn.nextLine();
				this.displayResponse(response);

				if (response.startsWith("125") || response.startsWith("150")) {
					Scanner socketScanner = null;

					try(final Socket dataSocket = new Socket(this.controlSocket.getInetAddress(), (this.controlSocket.getPort() - 1))) {
						socketScanner = new Scanner(dataSocket.getInputStream());

						switch (socketScanner.nextLine()) {
						case LIST_TAG:
							this.displayResponse(dataSocket);
							break;
						case RETR_TAG:
							this.receiveFile(dataSocket, socketScanner);
							break;
						case STOR_TAG:
							this.sendFile(dataSocket, socketScanner);
							break;
						default:
							break;
						}
					} catch(final IOException e) {
					} finally {
						if (socketScanner != null) {
							socketScanner.close();
						}
					}
				}
			}

			if (!this.controlSocket.isClosed()) {
				this.stop();
			}
		} catch(final Exception e) {
			this.stop();
		}
	}

	/**
	 * Waiting for a command written by user and send it to the network
	 */
	private void sendCommand() {
		try {
			final Scanner stdin = new Scanner(this.consoleIn);

			while(!this.controlSocket.isClosed()) {
				this.displayPrompt();

				final String cmd = stdin.nextLine();

				if (EXIT.equals(cmd)) {
					break;
				}

				if (!cmd.isBlank()) {
					this.prompt = false;
					this.controlOut.println(cmd);
				}
			}

			stdin.close();

			if (!this.controlSocket.isClosed()) {
				this.stop();
			}
		} catch(final Exception e) {
			this.stop();
		}
	}

}

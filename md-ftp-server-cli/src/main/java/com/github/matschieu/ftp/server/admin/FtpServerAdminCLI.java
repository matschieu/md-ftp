package com.github.matschieu.ftp.server.admin;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import com.github.matschieu.ftp.server.FtpServerException;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerAdminCLI {

	public static final String INTERNAL_CMD_HELP = "help";
	public static final String INTERNAL_CMD_WHO  = "who";
	public static final String INTERNAL_CMD_FIRE = "fire";
	public static final String INTERNAL_CMD_SEND = "send";
	public static final String INTERNAL_CMD_STOP = "stop";
	public static final String INTERNAL_CMD_START = "start";
	public static final String INTERNAL_CMD_QUIT = "quit";

	public static final String PREFIX = "> ";
	public static final String PROMPT = "# ";

	private final FtpserverAdminService adminService;

	/**
	 *
	 * @param adminService
	 */
	public FtpServerAdminCLI(final FtpserverAdminService adminService) {
		this.adminService = Optional.of(adminService).get();
	}

	/**
	 * Display IP address of the client connected to this server
	 */
	private void displayClientConnected() {
		final List<Client> clients = this.adminService.getConnectedClients();

		System.out.println(PREFIX + "Clients connected: " + clients.size());

		for(final Client client : clients) {
			System.out.println("\t%" + client.getCid() + "\t" + client.getInetAddress());
		}
	}

	/**
	 * To close the connection for a specific client
	 * @param cid the client id (index in the list of clients)
	 * @param message the message to send t othe client
	 */
	private void sendMessage(final int cid, final String message) {
		final List<Client> clients = this.adminService.getConnectedClients().stream().filter(client -> client.getCid() == cid).collect(Collectors.toList());

		if (clients.isEmpty()) {
			System.err.println(PREFIX + "Invalid cid");
			return;
		}

		System.out.print(PREFIX + "Sending message to " + clients.iterator().next().getInetAddress() + "... ");

		try {
			this.adminService.sendMessage(cid, message);

			System.out.println("ok");
		} catch(final FtpServerException e) {
			System.err.println(PREFIX + "Error: " + e.getMessage());
		}
	}

	/**
	 * To close the connection for a specific client
	 * @param cid the client id (index in the list of clients)
	 */
	private void fireClientFromServer(final int cid) {
		final List<Client> clients = this.adminService.getConnectedClients().stream().filter(client -> client.getCid() == cid).collect(Collectors.toList());

		if (clients.isEmpty()) {
			System.err.println(PREFIX + "Invalid cid");
			return;
		}

		System.out.print(PREFIX + "closing connection for " + clients.iterator().next().getInetAddress() + "... ");

		try {
			this.adminService.fireClient(cid);
			System.out.println("ok");
		}
		catch(final FtpServerException e) {
			System.err.println(PREFIX + "Error: " + e.getMessage());
		}
	}

	/**
	 *
	 */
	public void displayHelp() {
		System.out.println(PREFIX + "Server admin commands:");
		System.out.println("\t" + INTERNAL_CMD_WHO + ": display who is connected on this server");
		System.out.println("\t" + INTERNAL_CMD_FIRE + " <cid>: fire a client");
		System.out.println("\t" + INTERNAL_CMD_SEND + " <cid> <message>: send a message to a client");
		System.out.println("\t" + INTERNAL_CMD_START + ": start this server");
		System.out.println("\t" + INTERNAL_CMD_STOP + ": stop this server and notify all clients");
		System.out.println("\t" + INTERNAL_CMD_HELP + ": display this help message");
		System.out.println("\t" + INTERNAL_CMD_QUIT + ": quit the admin console");
	}

	/**
	 *
	 */
	public void start() {
		System.out.print("Starting FTP server... ");
		try {
			this.adminService.start();
			System.out.println("OK");
		} catch (final FtpServerException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 *
	 */
	public void stop() {
		System.out.print("Stopping FTP server... ");
		this.adminService.stop();
		System.out.println("OK");
	}

	/**
	 * Run the control prompt of this server
	 */
	public void promptAdmin() {
		boolean prompt = true;
		final Scanner stdin = new Scanner(System.in);

		System.out.println("Welcome on the FTP server admin console");
		System.out.println();

		while(prompt) {
			System.out.print(PROMPT);
			System.out.flush();

			final String cmd = stdin.nextLine();

			if (cmd.equals(INTERNAL_CMD_QUIT)) {
				prompt = false;
				System.out.println("Bye!");
			} else if (cmd.equals(INTERNAL_CMD_START)) {
				this.start();
			} else if (cmd.equals(INTERNAL_CMD_STOP)) {
				this.stop();
			} else if (cmd.equals(INTERNAL_CMD_WHO)) {
				this.displayClientConnected();
			} else if (cmd.matches("^" + INTERNAL_CMD_FIRE + "[ ]+[0-9]+$")) {
				final StringTokenizer st = new StringTokenizer(cmd, " ");
				st.nextToken();
				this.fireClientFromServer(Integer.parseInt(st.nextToken()));
			} else if (cmd.matches("^" + INTERNAL_CMD_SEND + "[ ]+[0-9]+[ ]+[A-Za-z0-1/./_/-/?/!/=///\"/ ]+$")) {
				final StringTokenizer st = new StringTokenizer(cmd, " ");
				st.nextToken();
				this.sendMessage(Integer.parseInt(st.nextToken()), st.nextToken());
			} else if (cmd.equals(INTERNAL_CMD_HELP)) {
				this.displayHelp();
			} else {
				System.err.println(PREFIX + "Invalid command");
			}
		}

		stdin.close();
	}

}

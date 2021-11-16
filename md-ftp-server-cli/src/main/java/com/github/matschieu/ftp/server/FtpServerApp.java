package com.github.matschieu.ftp.server;

import com.github.matschieu.ftp.server.admin.FtpServerAdminCLI;
import com.github.matschieu.ftp.server.admin.FtpserverAdminService;

/**
 *
 * @author Matschieu
 *
 */
public class FtpServerApp {

	/**
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		final FtpServer server = new FtpServer(FtpServerConfiguration.fromFile("ftp-server.properties"));
		final FtpserverAdminService admin = server.getAdminService();

		try {
			System.out.println("Start FTP server");
			admin.start();
		} catch (final FtpServerException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		new FtpServerAdminCLI(admin).promptAdmin();

		System.out.println("Finish FTP server");

		System.exit(0);
	}

}

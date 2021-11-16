package com.github.matschieu.ftp.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matschieu
 *
 */
public class FtpSession {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpSession.class);

	public static final String ROOT_DIR_ALIAS = "/";

	private ServerSocket dataServerSocket;
	private Socket controlSocket;
	private PrintStream controlOut;
	private File rootDir;
	private File currentDir;
	private String currentUser;
	private boolean userConnected;

	/**
	 * @param controlSocket the socket with the client
	 * @param rootDir the rootDir of the server to store files
	 */
	public FtpSession(final Socket controlSocket, final ServerSocket dataServerSocket, final File rootDir) {
		try {
			this.controlSocket = controlSocket;
			this.dataServerSocket = dataServerSocket;
			this.controlOut = new PrintStream(controlSocket.getOutputStream(), true);
			this.rootDir = rootDir;
			this.currentDir = rootDir;
			this.currentUser = null;
			this.userConnected = false;

			this.controlOut.println(FtpServerResponse.WELCOME.format());
		}
		catch(final IOException e) {
			LOGGER.error("Error : " + e.getMessage(), e);
		}
	}

	/**
	 *
	 * @return String
	 */
	public String getCurrentDirPath() {
		try {
			final String currentDirPath = this.currentDir.getCanonicalPath();
			final String rootDirPath = this.rootDir.getCanonicalPath();

			if (currentDirPath.matches("^" + rootDirPath + "$")) {
				return currentDirPath.replace(rootDirPath, ROOT_DIR_ALIAS);
			} else {
				return currentDirPath.replace(rootDirPath, "");
			}
		} catch(final IOException e) {
			return this.currentDir.getPath();
		}
	}

	/**
	 * @return the dataServerSocket
	 */
	public ServerSocket getDataServerSocket() {
		return this.dataServerSocket;
	}

	/**
	 * @return the controlSocket
	 */
	public Socket getControlSocket() {
		return this.controlSocket;
	}

	/**
	 * @return the controlOut
	 */
	public PrintStream getControlOut() {
		return this.controlOut;
	}

	/**
	 * @return the rootDir
	 */
	public File getRootDir() {
		return this.rootDir;
	}

	/**
	 * @return the currentDir
	 */
	public File getCurrentDir() {
		return this.currentDir;
	}

	/**
	 * @param currentDir the currentDir to set
	 */
	public void setCurrentDir(final File currentDir) {
		this.currentDir = currentDir;
	}

	/**
	 * @return the currentUser
	 */
	public String getCurrentUser() {
		return this.currentUser;
	}

	/**
	 * @param currentUser the currentUser to set
	 */
	public void setCurrentUser(final String currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * @return the userConnected
	 */
	public boolean isUserConnected() {
		return this.userConnected;
	}

	/**
	 * @param userConnected the userConnected to set
	 */
	public void setUserConnected(final boolean userConnected) {
		this.userConnected = userConnected;
	}

}

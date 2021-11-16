package com.github.matschieu.ftp.server.admin;

import java.util.List;

import com.github.matschieu.ftp.server.FtpServerException;

public interface FtpserverAdminService {

	/**
	 *
	 * @throws FtpServerException
	 */
	void start() throws FtpServerException;

	/**
	 *
	 */
	void stop();

	/**
	 *
	 * @return boolean
	 */
	boolean isRunning();

	/**
	 *
	 * @return List<ServerClient>
	 */

	List<Client> getConnectedClients();

	/**
	 *
	 * @param cid
	 * @param message
	 * @throws FtpServerException
	 */
	void sendMessage(int cid, String message) throws FtpServerException;

	/**
	 * To close the connection for a specific client
	 * @param cid the client id (index in the list of clients)
	 * @throws FtpServerException
	 */
	void fireClient(int cid) throws FtpServerException;

}
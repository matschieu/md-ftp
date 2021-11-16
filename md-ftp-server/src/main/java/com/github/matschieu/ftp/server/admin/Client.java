package com.github.matschieu.ftp.server.admin;

import java.net.InetAddress;

/**
 *
 * @author Matschieu
 *
 */
public class Client {

	private int cid;

	private InetAddress inetAddress;

	/**
	 *
	 */
	public Client() { }

	/**
	 *
	 * @param cid
	 * @param inetAddress
	 */
	public Client(final int cid, final InetAddress inetAddress) {
		super();
		this.cid = cid;
		this.inetAddress = inetAddress;
	}

	/**
	 * @return the cid
	 */
	public int getCid() {
		return this.cid;
	}

	/**
	 * @param cid the cid to set
	 */
	public void setCid(final int cid) {
		this.cid = cid;
	}

	/**
	 * @return the inetAddress
	 */
	public InetAddress getInetAddress() {
		return this.inetAddress;
	}

	/**
	 * @param inetAddress the inetAddress to set
	 */
	public void setInetAddress(final InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	@Override
	public String toString() {
		return this.cid + " -> " + this.inetAddress;
	}
}

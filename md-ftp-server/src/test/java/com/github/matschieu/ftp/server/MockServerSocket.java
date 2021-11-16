package com.github.matschieu.ftp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Matschieu
 *
 */
public class MockServerSocket extends ServerSocket {

	private boolean closed;

	public MockServerSocket() throws IOException {
		this.closed = false;
	}

	public void setClosed(final boolean closed) {
		this.closed = closed;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
	}

	@Override
	public InetAddress getInetAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (final UnknownHostException e) {
			return null;
		}
	}

	@Override
	public Socket accept() throws IOException {
		return new MockSocket();
	}

}

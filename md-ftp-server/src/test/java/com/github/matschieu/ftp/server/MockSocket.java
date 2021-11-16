package com.github.matschieu.ftp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MockSocket extends Socket {

	private boolean closed;

	public MockSocket() {
		this.closed = false;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return System.out;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return System.in;
	}

	public void setClosed(final boolean closed) {
		this.closed = closed;
	}

	@Override
	public synchronized void close() throws IOException {
		this.closed = true;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}
}

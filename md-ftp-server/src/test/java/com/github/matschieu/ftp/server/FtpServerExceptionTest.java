package com.github.matschieu.ftp.server;

import org.junit.Assert;
import org.junit.Test;

public class FtpServerExceptionTest {

	@Test
	public void testFtpServerException() {
		final String message = "FTP Server Exception";
		final Exception exception1 = new FtpServerException(message);
		Assert.assertEquals(message, exception1.getMessage());

		final Exception exception2 = new FtpServerException(message, exception1);
		Assert.assertEquals(message, exception2.getMessage());
		Assert.assertEquals(exception1, exception2.getCause());
	}
}

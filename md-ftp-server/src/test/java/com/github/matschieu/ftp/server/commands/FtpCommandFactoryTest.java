package com.github.matschieu.ftp.server.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matschieu
 *
 */
public class FtpCommandFactoryTest {

	private final Map<String, Class<? extends FtpCommand>> commands;

	public FtpCommandFactoryTest() {
		this.commands = new HashMap<>();
		this.commands.put("CWD", CwdCommand.class);
		this.commands.put("PWD", PwdCommand.class);
		this.commands.put("USER", UserCommand.class);
		this.commands.put("PASS", PassCommand.class);
		this.commands.put("LIST", ListCommand.class);
		this.commands.put("RETR", RetrCommand.class);
		this.commands.put("STOR", StorCommand.class);
		this.commands.put("QUIT", QuitCommand.class);
		this.commands.put("CDUP", CdupCommand.class);
		this.commands.put("HELP", HelpCommand.class);
	}

	@Test
	public void testCreateCommandOK() {
		for(final Entry<String, Class<? extends FtpCommand>> entry : this.commands.entrySet()) {
			final FtpCommand command = FtpCommandFactory.createCommand(entry.getKey());
			Assert.assertNotNull(command);
			Assert.assertEquals(entry.getValue().getName(), command.getClass().getName());
		}
	}

	@Test
	public void testCreateCommandKO() {
		Assert.assertNull(FtpCommandFactory.createCommand("DUMMY"));
		Assert.assertNull(FtpCommandFactory.createCommand(""));
		Assert.assertNull(FtpCommandFactory.createCommand(null));
	}

}

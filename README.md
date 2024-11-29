
# FTP project

## Description

This project provide a very simple FTP server with a CLI admin and CLI client. It is based on a student project done at the university.

## FTP server

### Description

The server is using 2 threads at startup:
* one is waiting for connections
* the other allows to enter command to interact with the server.
When a client is connecting to the server, a new thread is created to receive and execute the commands received from the client.

The server uses a channel to control and receive the commandes and some responses.
It also uses a data channel to transfer the files according to the RFC959 (FTP).
But the way to transfer files (data flow) is not conform to the RFC: a tag is sent to the client, then the client answers when it is ready to receive the file and the server sends the file. This avoids the server to close the channel before the client reads the data.

To avoid the client to be  blocked, a timeout has been added to the data channel. So if the client doesn't open the data channel in the delay, then the server stop waiting for the client to open the file. So it is possible to use telnet as a client although it doesn't have a data channel. In this way it would be impossible to receive/send files or to obtain a response to the LIST command.

The server repsects the RFC 959 (FTP) for the return codes.

Each command is received and analysed using a regular expression to check its validity and determine the response to send to the client.

Pour obtenir la liste des commandes possibles pour intéragir avec le serveur, 
il faut entrer la commande "help".

### Add a new command

1. In the package `com.github.matschieu.ftp.server.commands`, add a new class extending the interface `com.github.matschieu.ftp.server.commands.FtpCommand`
2. Add the qualified name of this new class at the end of the file `md-ftp-server/src/main/resources/META-INF/services/com.github.matschieu.ftp.server.commands.FtpCommand`
Then the command should be available and it help message displayed when using the command `help`.

### Server configuration

The file `md-ftp-server-cli/src/main/resources/ftp-server.properties` allows to configure the root dir of the FTP server, the port, the timeout and the users and passwords

```.properties
root.dir.path=/home/mathieu/www
server.port=5287
server.timeout=3000
users.login=root,mathieu
users.pwd=1234,password
```

### Logging

Logging is performed using Logback.
It is configured in `md-ftp-server-cli/src/main/resources/logback.xml`.

### Running the server

The class `com.github.matschieu.ftp.server.FtpServerApp` must be run as JSE application.

### Server administration

When server is running, it can be managed using some commands.

Available commands are:
* help: display the help message
* who: display who is connected on this server
* fire <cid>: fire a client
* send <cid> <message>: send a message to a client
* start: start this server
* stop: stop this server and notify all clients
* quit: quit the admin console

Example of a session in the admin CLI of the server:

```
Start FTP server
Welcome on the FTP server admin console

# start
Starting FTP server... OK
# help
> Server admin commands:
	who: display who is connected on this server
	fire <cid>: fire a client
	send <cid> <message>: send a message to a client
	start: start this server
	stop: stop this server and notify all clients
	help: display this help message
	quit: quit the admin console
# who
> Clients connected: 2
	%0	/127.0.0.1
	%1	/127.0.0.1
# send 0 hello
> Sending message to /127.0.0.1... ok
# fire 1
> closing connection for /127.0.0.1... ok
# stop
Stopping FTP server... OK
# quit
Bye!
Finish FTP server
```

## FTP client

### Description

The client is using 2 threads:
* one to listen to the responses received from the server
* the other to wait for the commands from the user and then send them to the server 
When a response from the server is a tag <LIST>, <RETR> ou <STOR>, then the data channel is opned to transfer a file or a response.

### Running the client

The class `com.github.matschieu.ftp.client.cli.FtpClientApp` must be run as JSE application.

### Sending command to the FTP server

When running, the FTP client allos the user to send commands to the FTP server.

Available FTP commands are:
* CWD <directory> : change working directory on remote machine
* STOR <filename> : send one file
* CDUP : change remote working directory to parent directory
* PASS <user_password>
* RETR <filename> : receive file
* QUIT : terminate ftp session and exit
* PWD : print working directory on remote machine
* USER <user_name> : send new user information
* LIST : list contents of remote directory

Example of a session in the FTP client CLI:

```
> Connection to 127.0.0.1 on port 5287... ok
220 Welcome, service ready
# USER root
# 331 Password required for root
# PASS 1234
# 230 User root logged in
# HELP
# 214 HELP CWD | STOR | CDUP | PASS | RETR | QUIT | PWD | USER | LIST
# HELP CWD
# 214 CWD <directory> : change working directory on remote machine
# HELP STOR
# 214 STOR <filename> : send one file
# HELP CDUP
# 214 CDUP : change remote working directory to parent directory
# HELP PASS
# 214 PASS <user_password>
# HELP RETR
# 214 RETR <filename> : receive file
# HELP QUIT
# 214 QUIT : terminate ftp session and exit
# HELP PWD
# 214 PWD : print working directory on remote machine
# HELP USER
# 214 USER <user_name> : send new user information
# HELP LIST
# 214 LIST : list contents of remote directory
```

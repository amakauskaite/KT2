package pop3client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
	
	private Socket socket;
	private static final int DEFAULT_PORT = 110;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private boolean debug = false;
	
	public boolean isDebug() 
	{
		return debug;
	}

	public void setDebug(boolean debug) 
	{
		this.debug = debug;
	}

	
	public void connect (String host, int port) throws IOException
	{
		socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));
		
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		if (debug)
		System.out.println("Connected to the host");
		
		readResponseLine();
		
	}
	
	public void connect(String host) throws IOException {
		connect(host, DEFAULT_PORT);
		}
	
	public boolean isConnected() 
	{
		return socket != null && socket.isConnected();
	}
	
	public void disconnect() throws IOException 
	{
		if (!isConnected())
		throw new IllegalStateException("Not connected to a host");
		socket.close();
		reader = null;
		writer = null;
		
		if (debug)
		System.out.println("Disconnected from the host");
	}
	
	protected String readResponseLine() throws IOException
	{
		String response = reader.readLine();

		if (debug) 
		{
			System.out.println("DEBUG [in] : " + response);
		}
		
		if (response.startsWith("-ERR"))
		{
			throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
		}

		
		return response;
		
	}
	
	protected String sendCommand(String command) throws IOException 
	{
		if (debug) 
		{
			System.out.println("DEBUG [out]: " + command);
		}
		
		writer.write(command + "\n");
		writer.flush();
		return readResponseLine();
	}
	
	//COMMANDS
	
	//USER and PASS commands
	public boolean login(String username, String password) throws IOException 
	{
		try
		{
		sendCommand("USER " + username);
		sendCommand("PASS " + password);
		System.out.println("Login successful");
		return false;
		}
		catch (RuntimeException e)
		{
			System.out.println("Failed authentication");
			return true;
		}

	}

	//QUIT command
	public void logout() throws IOException 
	{
		sendCommand("QUIT");
	}
	
	//STAT command
	public int getNumberOfNewMessages() throws IOException 
	{
		String response = sendCommand("STAT");
		String[] values = response.split(" ");
		//format: +OK nn mm and we need nn
		return Integer.parseInt(values[1]);
	}
	
	//LIST without parameters
	protected void getList() throws IOException
	{
		String response = sendCommand("LIST");
		String[] values = response.split(" ");
		int i;
		for (i = 0; i < Integer.parseInt(values[1]); i++)
		{
			System.out.println(readResponseLine());
		}
	}
	
	//LIST with an argument
	protected void getList(int messageNumber) throws IOException
	{
		try
		{
			String response = sendCommand("LIST " + messageNumber);
			if(!response.contains("."))
			System.out.println(response);
			else readResponseLine();
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	 protected String[] getMultilineResponse() throws IOException
	 {
		 ArrayList<String> lines = new ArrayList<String>();
		 while (true) 
		 {
			String line = readResponseLine();
 
			 if (line == null) // Server closed connection
			 {	                
			     throw new IOException("Server unawares closed the connection.");
             }

			 if (line.equals(".")) // No more lines in the server response
			 {
				 break;
			 }

			if ((line.length() > 0) && (line.charAt(0) == '.')) // The line starts with a "." - strip it off.
			{
			    line = line.substring(1);
			}
			 // Add read line to the list of lines
			  lines.add(line);
		}

			 
       String response[] = new String[lines.size()];

       lines.toArray(response);

       return response;

	   }
	
	protected String getEmail (int number) throws IOException
	{
		try 
		{
			sendCommand("RETR " + number);
			
			return message();
	
		} 
		catch (RuntimeException e) 
		{
			System.out.println(e.getMessage());
			return null;
		}
		
		
	}
	
	protected String message () throws IOException
	{
		try 
		{
			String[] messageLines = getMultilineResponse();
			StringBuffer message = new StringBuffer();
			for (int i = 0; i < messageLines.length; i++) 
			{
				message.append(messageLines[i]);
				message.append("\n");

			}
			return new String(message);
		} 
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	//DELEtion command
	protected void deleteMessage (int messageNumber) throws IOException
	{
		
		try
		{
			sendCommand("DELE " + messageNumber);
			System.out.println("Message will be deleted.");
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	//NOOPeration command
	protected void doNothing() throws IOException
	{
		sendCommand("NOOP");
	}
	
	//RSET all messages marked for deletion command
	protected void unmarkMessages() throws IOException
	{
		sendCommand("RSET");
		System.out.println("All messages for deletion have been unmarked");
	}
	
	//TOP of the message command
	protected String topOfMessage(int messageNumber, int lineQuantity) throws IOException
	{
		try
		{
			sendCommand("TOP "+messageNumber+" "+lineQuantity);
			return message();
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	//UIDL (unique ID listing) command
	protected void uniqueID (int messageNumber) throws IOException
	{
		try
		{
			String response = sendCommand("UIDL " + messageNumber);
			System.out.println(response.substring(4));
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	
	protected String uniqueID() throws IOException
	{
		sendCommand("UIDL");
		return message();
		
	}
	
	//TODO: should I implement APOP???
}

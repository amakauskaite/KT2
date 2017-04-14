package pop3client;

import java.io.IOException;
import java.util.Scanner;
import pop3client.Client;

public class SwitchUI {
	
	
	Scanner sc = new Scanner(System.in);
	String username, password;
	boolean loop = true;

	public void startUI (Client client)
	{
		System.out.println("Welcome to the POP3 UI.");
		    
		autoLogin(client);
		    		    
		loop = true;
		
		while (loop)
		{
		    printPossibleCommands();
			int command = sc.nextInt();
			commenceSwitch(command, client);
		}

	}
	
	protected void loginByHand (Client client)
	{
		try 
		{
			while (loop) {
				System.out.println("Please write your username:");
				username = sc.next();
				System.out.println("Password?:");
				password = sc.next();
				loop = client.login(username, password);
			} 
		} 
		catch (IOException e) 
		{
			System.out.println("Failed " + e.getMessage());
		}
	}
	
	protected void autoLogin (Client client)
	{
		try
		{
			client.login("login", "password");
		}
		catch (IOException e) 
		{
			System.out.println("Failed " + e.getMessage());
		}
		
	}
	
	protected void printPossibleCommands ()
	{
		System.out.println("**********************************************************");
		System.out.println("Write the number of the command that you want to do next:");
		System.out.println("1 - number of new emails");
		System.out.println("2 - list with information about every new email");
		System.out.println("3 - information about selected email");
		System.out.println("4 - get all the emails");
		System.out.println("5 - get selected email");
		System.out.println("6 - mark message for deletion");
		System.out.println("7 - unmark all messaged from deletion");
		System.out.println("8 - do nothing (lol)");
		System.out.println("9 - get a portion of the message");
		System.out.println("10 - list of all emails with unique IDs");
		System.out.println("11 - unique ID of selected email");
		System.out.println("12 - logout");
		System.out.println("**********************************************************");
		System.out.println();
	}
	
	protected void commenceSwitch (int command, Client client)
	{
		int number;
		try
		{
		    switch (command)
		    {
		    case 1:  System.out.println("Number of new messages: " + client.getNumberOfNewMessages());
		    		 System.out.println();
		             break;
		    case 2:  client.getList();
		             break;
		    case 3:  System.out.println("Write the number of wanted email:");
		    		 number = sc.nextInt();
		    		 client.getList(number);
		    		 System.out.println();
		    		 break;
		    case 4:  for (int i=1; i<=client.getNumberOfNewMessages(); i++)
		    		{
		    			 System.out.println("-------Message number " + i + ":-------");
		    	         String message = client.getEmail(i);
		                 System.out.print(message);
		                 System.out.println("-----------------------------");
		                 System.out.println();
		    		}
		             System.out.println();
		    		 break;
		    case 5:  System.out.println("Write the number of wanted email:");
    		         number = sc.nextInt();
    		         String message = client.getEmail(number);
    		         System.out.print(message);
    		         System.out.println();
    		         break;
		    case 6:  System.out.println("Write the number of wanted email:");
    				 number = sc.nextInt();
    				 client.deleteMessage(number);
    				 System.out.println();
    				 break;
		    case 7:  client.unmarkMessages();
		             System.out.println();
		    		 break;
		    case 8:  client.doNothing();
		             System.out.println();
		    		 break;
		    case 9:  System.out.println("Write the number of wanted email:");
		    		 number = sc.nextInt();
		    	     System.out.println("Write the quantity of wanted lines (from top):");
		    		 int quantity = sc.nextInt();
		    		 System.out.print(client.topOfMessage(number, quantity));
		    		 System.out.println();
		    		 break;
		    case 10: System.out.print(client.uniqueID());
		             System.out.println();
		    		 break;
		    case 11: System.out.println("Write the number of wanted email:");
			         number = sc.nextInt();
			         client.uniqueID(number);
			         System.out.println();
			         break;
		    case 12: System.out.println("Bye bye");
		    		 client.logout();
		    		 loop = false;
		    		 break;
		    default: System.out.println("Check you command m8 and try again");
		             System.out.println();
		    		 break;
		    }
		}
		catch (IOException e)
		{
			System.out.println("Failed " + e.getMessage());
		}
	}
}

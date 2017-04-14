package pop3client;

import java.io.IOException;
//import java.util.List;
import pop3client.SwitchUI;

public class Main {

	public static void main(String[] args) 
	{
				
		Client client = new Client();
		SwitchUI ui = new SwitchUI();
		
		try 
		{
			client.setDebug(true);
			client.connect("mail.stud.vu.lt");
			ui.startUI(client);
		} 
		catch (IOException e) 
		{
			System.out.println("Failed " + e.getMessage());
		}

	}

}

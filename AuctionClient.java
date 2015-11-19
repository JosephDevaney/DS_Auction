import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class AuctionClient implements Runnable
{
	private static final long BID_TIME_SECS = 60;

	private static final String EXIT_STR = "LEAVE";

	private static final Object TIME_STR = "TIME";

	private static final String HELP_STR = "HELP";

	private Socket socket = null;
	
	private BufferedReader reader = null;
	private DataOutputStream output = null;
	
	private AuctionClientThread client;
	private AuctionItem curItem;
	private Thread thread;
	
	
	/*
	 * Sets curItem to the reference item
	 * Displays Item and time remaining to stdout
	 */
	public void setCurItem(AuctionItem item)
	{
		curItem = item;
		displayItemInfo();
		displayTime();
	}
	
	
	/*
	 * Clears the console
	 */
	public void cls()
	{
		String clear = "";
		for (int i = 0; i < 50; i++)
		{
			clear += "\n";
		}
		System.out.println(clear);
	}
	
	
	/*
	 * Displays details about the Item
	 */
	public void displayItemInfo()
	{
		System.out.println("Item '" + curItem.getName() + "' for auction");
		System.out.println("Current bid: " + curItem.getCurrentBid());
		System.out.println("Reserve Price: " + curItem.getReservePrice());
	}
	
	
	/*
	 * Displays remaining Time for item
	 */
	public void displayTime()
	{
		if (curItem != null)
		{
			System.out.println("\nThere is " + (BID_TIME_SECS - ((System.currentTimeMillis() - curItem.getStartTime()) / 1000)) + " seconds left to bid\n");
		}
	}
	
	public void displayMsg(String msg)
	{
		System.out.println(msg);
	}
	
	public void displayHelp()
	{
		System.out.println("Enter a number to make a bid");
		System.out.println("Enter '" + EXIT_STR + "' to leave the auction");
		System.out.println("Enter '" + TIME_STR + "' to get the remainingtTime");
		System.out.println("Enter '" + HELP_STR + "' to get these instructions");
		System.out.println("\n\n");
	}
	
	/*
	 * Create Socket and call joinAuction()
	 */
	public AuctionClient(String host, int port)
	{
		try
		{
			socket = new Socket(host, port);
			joinAuction();
		} 
		catch (UnknownHostException uhEx)
		{
			// TODO: handle exception
			System.out.println("Host ID not found");
			System.exit(1);
		}
		catch (IOException ioEx)
		{
			ioEx.printStackTrace();
		}
	}
	
	
	/*
	 * Read input from clients
	 * Only specific commands or numbers are allowed
	 */
	public void run()
	{
		cls();
		displayHelp();
		while(client != null)
		{
			try
			{
				String message = reader.readLine();

				if (message.equals(EXIT_STR) || message.matches("-?\\d+(\\.\\d+)?"))
				{
					output.writeUTF(message);
					output.flush();
					if (message.equals(EXIT_STR))
					{
						leaveAuction();
						break;
					}
				}
				else if (message.equals(TIME_STR))
				{
					displayTime();
				}
				else if (message.equals(HELP_STR))
				{
					displayHelp();
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				leaveAuction();
			}
		}
	}
	
	
	/*
	 * Create Input stream for User and Create Output stream.
	 * Create new AuctionClientThread and start that thread and this one
	 */
	private void joinAuction()
	{
		try
		{
			reader = new BufferedReader(new InputStreamReader(System.in)); 
			output = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (thread == null)
		{
			client = new AuctionClientThread(this, socket);
			client.start();
			thread = new Thread(this);
			thread.start();		
		}
	}
	
	
	/*
	 * Close streams and socket
	 */
	public void leaveAuction()
	{
		try
		{
			if (socket != null)
			{
				socket.close();
			}
			if (reader != null)
			{
				reader.close();
			}
			if (output != null)
			{
				output.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			client.close();
			thread = null;
		}
		
		client.close();
		thread = null;
	}

	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: java AuctionClient host port");
		}
		else
		{
			new AuctionClient(args[0], Integer.parseInt(args[1]));
		}
	}
}

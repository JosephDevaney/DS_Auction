import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class AuctionClient implements Runnable
{
	private static InetAddress host;
	private static final int port = 1234;
	private Socket socket = null;
	private BufferedReader reader = null;
	private DataOutputStream output = null;
	private AuctionClientThread client;
	private Thread thread;
	
	public void displayItemInfo(AuctionItem item)
	{
		System.out.println("Item " + item.getName() + "for auction");
		System.out.println("Current bid:" + item.getCurrentBid());
		System.out.println("Reserve Price: " + item.getReservePrice());
	}
	
	public AuctionClient()
	{
		try
		{
			host = InetAddress.getLocalHost();
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
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		AuctionClient client = new AuctionClient();
		
		
	}
	
	public void run()
	{
		while(client != null)
		{
			try
			{
				String message = reader.readLine();
				if (message == "leave" || message.matches("-?\\d+(\\.\\d+)?"));
				{				
					output.writeUTF(message);
					output.flush();
					if (message == "leave")
					{
						leaveAuction();
					}
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
			e.printStackTrace();
		}
		
		client.close();
		thread = null;
	}

}

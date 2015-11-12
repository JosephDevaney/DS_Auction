import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class AuctionClient
{
	private static InetAddress host;
	private static final int port = 1234;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		try
		{
			host = InetAddress.getLocalHost();
		} 
		catch (UnknownHostException uhEx)
		{
			// TODO: handle exception
			System.out.println("Host ID not found");
			System.exit(1);
		}
		
		joinAuction();
	}
	
	private static void joinAuction()
	{
		Socket socket = null;
		AuctionItem curItem = null;
		
		try
		{
			socket = new Socket(host, port);
			
			//Scanner auctionInput = new Scanner(socket.getInputStream());
			//PrintWriter AuctionOutput = new PrintWriter(socket.getOutputStream(), true);
			
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			
			Scanner userEntry = new Scanner(System.in);
			
			//TODO Parse info between server and client
			do
			{
				try
				{
					do
					{
						curItem = (AuctionItem) inStream.readObject();
						System.out.println("Item " + curItem.getName() + "for auction");
						System.out.println("Current bid:" + curItem.getCurrentBid());
						System.out.println("Reserve Price: " + curItem.getReservePrice());
					} while (true);
//					System.out.println("Read object from file");
//					curItem = (AuctionItem) inStream.readObject();
//					System.out.println("Item " + curItem.getName() + "for auction");
//					System.out.println("Current bid:" + curItem.getCurrentBid());
//					System.out.println("Reserve Price: " + curItem.getReservePrice());
				}
				catch (EOFException eof)
				{
					// TODO: handle exception
					System.out.println("File read");
				}
				catch (ClassNotFoundException cnfEx)
				{
					// TODO Auto-generated catch block
					cnfEx.printStackTrace();
				}
				
//				System.out.println("The current item is: " + curItem.getName());
//				System.out.println("The current bid is: " + curItem.getCurrentBid());
//				System.out.println("The reserve price is: " + curItem.getReservePrice());
				
			} while(curItem != null);
			
		} 
		catch (IOException ioEx)
		{
			// TODO: handle exception
			ioEx.printStackTrace();
		}
		finally
		{
			try
			{
				System.out.println("Closing Connection...");
				socket.close();
			} 
			catch (IOException ioEx)
			{
				System.out.println("Unable to disconnect");
				System.exit(1);
			}
		}
	}

}

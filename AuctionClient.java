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
				output.writeUTF(message);
				output.flush();
//				System.out.println(message);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		AuctionItem curItem = null;
//		
//		try
//		{
//			
//			
//			//Scanner auctionInput = new Scanner(socket.getInputStream());
//			PrintWriter AuctionOutput = new PrintWriter(socket.getOutputStream(), true);
//			
//			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
//			
//			Scanner userEntry = new Scanner(System.in);
//			
//			//TODO Parse info between server and client
//			do
//			{
//				try
//				{
////					do
////					{
////						curItem = (AuctionItem) inStream.readObject();
////						System.out.println("Item " + curItem.getName() + "for auction");
////						System.out.println("Current bid:" + curItem.getCurrentBid());
////						System.out.println("Reserve Price: " + curItem.getReservePrice());
////					} while (true);
//					System.out.println("Read object from file");
//					
//					
//				}
//				catch (EOFException eof)
//				{
//					// TODO: handle exception
//					System.out.println("File read");
//				}
//				catch (ClassNotFoundException cnfEx)
//				{
//					// TODO Auto-generated catch block
//					cnfEx.printStackTrace();
//				}
//				
////				System.out.println("The current item is: " + curItem.getName());
////				System.out.println("The current bid is: " + curItem.getCurrentBid());
////				System.out.println("The reserve price is: " + curItem.getReservePrice());
//				
//			} while(curItem != null);
//			
//		} 
//		catch (IOException ioEx)
//		{
//			// TODO: handle exception
//			ioEx.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				System.out.println("Closing Connection...");
//				socket.close();
//			} 
//			catch (IOException ioEx)
//			{
//				System.out.println("Unable to disconnect");
//				System.exit(1);
//			}
//		}

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

}

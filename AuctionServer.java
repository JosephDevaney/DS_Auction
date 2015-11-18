import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class AuctionServer implements Runnable
{
	private static ServerSocket serverSock;
	private static final int port = 1234;
	private static ArrayList<ClientHandler> clientList;
	private Thread thread = null;
	private boolean startAuction = true;
	private AuctionHandler aHandler;
	private AuctionItem curItem;
	private ClientHandler curBidHolder = null;

	public AuctionServer()
	{
		clientList = new ArrayList<ClientHandler>();
		try
		{
			serverSock = new ServerSocket(port);
			start();
		}
		catch (IOException ioEx)
		{
			// TODO: handle exception
			System.out.println("Unable to set up port " + port + "!");
			System.exit(1);
		}
	}
	
	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop()
	{
		thread = null;
	}
	
	public void run()
	{
		while (thread != null)
		{
			try
			{
				addThread(serverSock.accept());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Server accept error" + e);
				stop();
			}
//			catch (InterruptedException iEx)
//			{
//				// TODO Auto-generated catch block
//				iEx.printStackTrace();
//			}
			
			if (clientList.size() == 2 && startAuction)
			{
				curItem = AuctionItem.getCurrentItem();
				curItem.setStartTime(System.currentTimeMillis());
				broadcastItem(curItem);
				
				aHandler = new AuctionHandler(this);
				aHandler.start();
				startAuction = false;
			}
			else if (!startAuction)
			{
				clientList.get(clientList.size() - 1).sendItem(curItem);
			}
		}
	}
	
	private void addThread(Socket socket)
	{
		clientList.add(new ClientHandler(socket, this));
		
		clientList.get(clientList.size() - 1).start();
	}
	
	public void newAuctionItem()
	{
		if (curItem.getCurrentBid() >= curItem.getReservePrice())
		{
			curItem.setSold(true);
		}
		curItem = AuctionItem.nextItem();
		curItem.setStartTime(System.currentTimeMillis());
		if (curItem != null)
		{
			broadcastItem(curItem);
		}
		else
		{
			for (ClientHandler ch : clientList)
			{
				ch.sendMsg("The Auction has concluded, thank you for participating");
			}
		}
	}
	
	public synchronized void leaveAuction(ClientHandler ch)
	{
		System.out.println("Person has left auction");
		clientList.remove(ch);
		try
		{
			ch.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			System.out.println("Error closing thread");
			e.printStackTrace();
		}
		
		if (clientList.size() < 2)
		{
			aHandler.stopThread();
			aHandler.interrupt();
			startAuction = true;
		}
		
		notifyAll();
	}
	
	public synchronized void broadcastItem(AuctionItem item)
	{
		for (ClientHandler ch : clientList)
		{
			ch.sendItem(item);
		}
		notifyAll();
	}
	
	public synchronized void broadcastBidInfo()
	{
		for (ClientHandler ch : clientList)
		{
			if (ch == curBidHolder)
			{
				ch.sendMsg("Your bid is the current bid");
			}
			else
			{
				ch.sendMsg("There is a new bid");
			}
		}
		notifyAll();
	}
	
	public void newBid(String bidStr, ClientHandler ch)
	{
		double bid = Double.parseDouble(bidStr);
		if (bid > curItem.getCurrentBid())
		{
			curItem.setCurrentBid(bid);
			curItem.setStartTime(System.currentTimeMillis());
			curBidHolder = ch;
			broadcastBidInfo();
			broadcastItem(curItem);
			aHandler.interrupt();
		}
		
	}
	
	private static void loadData()
	{
		Scanner reader = null;
		try
		{
			reader = new Scanner(new File("items.dat"));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Unable to load Items");
		}
		String line = "";
		String[] args = new String[2];
		
		while (reader.hasNextLine())
		{
			line = reader.nextLine();
			args = line.split("\t");
			new AuctionItem(args[0], Double.parseDouble(args[1]));
		}
		reader.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		loadData();
		//AuctionItem[] items = { new AuctionItem("Shoe", 100.00), new AuctionItem("Tie", 85.00) };
		AuctionServer server = new AuctionServer();

	}

}

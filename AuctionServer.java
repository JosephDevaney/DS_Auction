import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionServer implements Runnable
{
	private static final long TIMER_LEN = 60000;
	private static ServerSocket serverSock;
	
	private static ArrayList<ClientHandler> clientList;
	
	private Thread thread = null;

	private boolean startAuction = true;
	private AuctionItem curItem;
	private ClientHandler curBidHolder = null;
	private Timer timer;

	
	/**
	 * Server class Constructor
	 * Initialises List of clienthandler objects
	 * Establishes Socket and calls start()
	 */
	public AuctionServer(int port)
	{
		clientList = new ArrayList<ClientHandler>();
		
		try
		{
			serverSock = new ServerSocket(port);
			start();
		}
		catch (IOException ioEx)
		{
			System.out.println("Unable to set up port " + port + "!");
			System.exit(1);
		}
	}
	
	
	/**
	 * Starts a Thread to run Server functionality. Ensures only one thread can run
	 */
	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	
	
	/**
	 * Removes reference to the current Thread. 
	 * This will break the condition in run() and allow process to exit
	 */
	public void stop()
	{
		thread = null;
	}
	
	
	/**
	 * Main loop for this Server thread.
	 * Accepts new connections from clients and passes this socket to addThread()
	 * Once at two connections have been made, it starts the auction by getting an Item from the static list in AuctionItem
	 * This gets sent to all clients
	 * AuctionHandler gets created and the Thread is started for it.
	 * If a client joins while the auction is underway send the current item to it
	 */
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
			
			if (clientList.size() == 2 && startAuction)
			{
				curItem = AuctionItem.getCurrentItem();
				curItem.setStartTime(System.currentTimeMillis());
				broadcastItem(curItem);
				
				runTimer();
				startAuction = false;
			}
			else if (!startAuction)
			{
				clientList.get(clientList.size() - 1).sendItem(curItem);
			}
		}
	}
	
	
	/*
	 * Instantiate a new clientHandler, add it to the clientList and start the Thread
	 */
	private void addThread(Socket socket)
	{
		clientList.add(new ClientHandler(socket, this));
		
		clientList.get(clientList.size() - 1).start();
	}
	
	
	/*
	 * The timer is up so select the next Item to be sent to clients
	 * If Item has reached specs to be sold, mark as sold.
	 * If there are no more items to be sold, inform participants and close
	 */
	public void newAuctionItem()
	{
		if (curItem.getCurrentBid() >= curItem.getReservePrice())
		{
			curItem.setSold(true);
		}
		curItem = AuctionItem.nextItem();
		if (curItem == null)
		{
			broadcastMsg("The Auction has concluded, thank you for participating");
		}
		else if (clientList.size() < 2)
		{
			broadcastMsg("The Auction will resume when there are at least two people");
		}
		else
		{
			broadcastItem(curItem);
			curItem.setStartTime(System.currentTimeMillis());
			runTimer();
		}
	}
	
	public synchronized void broadcastMsg(String msg)
	{
		for (ClientHandler ch : clientList)
		{
			ch.sendMsg(msg);
		}
	}
	
	
	/*
	 * Remove a client from the auction. Remove them from client list
	 * If there is only one person left, stop the auction
	 * This is synchronized so that somebody cannot leave the auction while an item is being broadcast
	 */
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
			startAuction = true;
		}
		
		notifyAll();
	}
	
	
	/*
	 * Send an item to all active clients
	 */
	public synchronized void broadcastItem(AuctionItem item)
	{
		for (ClientHandler ch : clientList)
		{
			ch.sendItem(item);
		}
		notifyAll();
	}
	
	
	/*
	 * Inform clients that there has been a new bid
	 * Inform successful bidders
	 */
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
	
	/*
	 * Handles successful new bids
	 * Resets the timer, notes the client and broadcasts the information to all clients
	 */
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
			timer.cancel();
			runTimer();
		}
		
	}
	
	private void runTimer()
	{
		timer = new Timer("Countdown");
		timer.schedule(new TimerTask() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				newAuctionItem();
			}
		}, TIMER_LEN);
	}
	
	
	/*
	 * Loads the Items data from items.dat and creates instances of them into the static list in AuctionItem
	 */
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
	
	
	/*
	 * Load the data and create instance of this class
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: java AuctionServer port");
		}
		else
		{
			loadData();
	
			new AuctionServer(Integer.parseInt(args[0]));
		}
	}

}

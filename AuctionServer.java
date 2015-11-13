import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AuctionServer implements Runnable
{
	private static ServerSocket serverSock;
	private static final int port = 1234;
	private static ArrayList<ClientHandler> clientList;
	private Thread thread = null;
	private boolean startAuction = true;
	private AuctionHandler aHandler;
	private AuctionItem curItem;

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
				int sleepTime = (int)(Math.random() * 3000);
				Thread.sleep(sleepTime);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Server accept error" + e);
				stop();
			}
			catch (InterruptedException iEx)
			{
				// TODO Auto-generated catch block
				iEx.printStackTrace();
			}
			
			if (clientList.size() == 2 && startAuction)
			{
				newAuctionItem();
				
				aHandler = new AuctionHandler(this);
				aHandler.start();
				startAuction = false;
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
		curItem = AuctionItem.getCurrentItem();
		for (ClientHandler ch : clientList)
		{
			ch.sendItem(curItem);
		}
	}
	
	public void newBid(String bidStr)
	{
		int bid = Integer.parseInt(bidStr);
		if (bid > curItem.getCurrentBid())
		{
			newAuctionItem();
			aHandler.interrupt();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		
		AuctionItem[] items = { new AuctionItem("Shoe", 100.00), new AuctionItem("Tie", 85.00) };

		AuctionServer server = new AuctionServer();

	}

}

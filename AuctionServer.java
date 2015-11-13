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
			
			if (clientList.size() == 2 && startAuction)
			{
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
		
	}
	
	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		
		AuctionItem[] items = { new AuctionItem("Shoe", 100.00), new AuctionItem("Tie", 85.00) };

		AuctionServer server = new AuctionServer();

	}

}

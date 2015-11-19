import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


/*
 * This class will create a new thread to handle connections for all clients
 */
public class ClientHandler extends Thread
{
	private static final String OBJECT_STR = "___Object___";
	private AuctionServer server;
	private Socket client;
	
	private DataInputStream input;
	private ObjectOutputStream outstream;
	
	private Thread thread = null;
	
	
	/*
	 * Constructor, creates Data and Object streams with the client
	 */
	public ClientHandler(Socket socket, AuctionServer server)
	{
		client = socket;
		this.server = server;

		try
		{
			input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		} 
		catch (IOException ioEx)
		{
			// TODO: handle exception
			ioEx.printStackTrace();
		}
		
		try
		{
			outstream = new ObjectOutputStream(client.getOutputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Waits for data from the client
	 */
	public void run()
	{
		thread = new Thread(this);
		String msg;
		
		while (thread != null)
		{
			try
			{
				msg = input.readUTF();

				if (msg.equals("LEAVE"))
				{
					server.leaveAuction(this);
				}
				else
				{
					server.newBid(msg, this);
				}
			}
			catch (IOException ioEx)
			{
				// TODO Auto-generated catch block
//				ioEx.printStackTrace();
				server.leaveAuction(this);
				thread = null;
			}	
		}
	}
	
	/*
	 * Closes connections to streams and sockets
	 */
	public void close() throws IOException
	{
		if (input != null)
		{
			input.close();
		}
		if (outstream != null)
		{
			outstream.close();
		}
		if (client != null)
		{
			client.close();
		}
	}

	
	/*
	 * Uses ObjectOutputStream to send an AuctionItem to the client. 
	 * First sends the String "___Object___" to allow client to process
	 */
	public void sendItem(AuctionItem item)
	{
		sendMsg(OBJECT_STR);
		try
		{
			outstream.writeObject(item);
			outstream.flush();
			outstream.reset();
		}
		catch (IOException ioEx)
		{
			ioEx.printStackTrace();
			server.leaveAuction(this);
			thread = null;
		}
	}
	
	
	/*
	 * Uses ObjectOutputStream to send a String to the client. 
	 */
	public void sendMsg(String msg)
	{
		try
		{			
			outstream.writeObject(msg);
			outstream.flush();
			outstream.reset();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

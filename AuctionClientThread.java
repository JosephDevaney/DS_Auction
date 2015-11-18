import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/*
 * Handles the reading of data from AuctionServer
 */
public class AuctionClientThread extends Thread
{
	private Socket socket;
	
	private ObjectInputStream inStream;
	private DataInputStream input;
	
	private AuctionClient client;
	
	
	/*
	 * Constructor - assigns local references
	 */
	public AuctionClientThread(AuctionClient client, Socket socket)
	{
		this.client = client;
		this.socket = socket;
		open();
	}
	
	
	/*
	 * Opens Streams between Client and Server
	 */
	public void open()
	{
		try
		{
			inStream = new ObjectInputStream(socket.getInputStream());
			input = new DataInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Closes Streams and socket
	 */
	public void close()
	{
		try
		{
			if (inStream != null)
			{
				inStream.close();
			}
			if (input != null)
			{
				input.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client = null;
	}
	
	
	/*
	 * Read String Data from Server
	 * Calls getItem() when the message is "___Object___" to receive an AuctionItem object
	 */
	public void run()
	{
		String message = "";
		while (client != null)
		{
			try
			{
				message = (String) inStream.readObject();
				
				if (message.equals("___Object___"))
				{
					getItem();
				}
				else
				{
					client.cls();
					client.displayMsg(message);
				}
			}
			catch (IOException ioEx)
			{
				//ioEx.printStackTrace();
				System.out.println("Error on listening socket");
				close();
			}
			catch (ClassNotFoundException cnfEx)
			{
				// TODO Auto-generated catch block
				cnfEx.printStackTrace();
			}
		}
	}
	
	
	/*
	 * Reads data from Server and casts it to an AuctionItem
	 */
	public void getItem()
	{
		AuctionItem item = null;
		
		try
		{
			item = (AuctionItem) inStream.readObject();
			client.setCurItem(item);
		}
		catch (ClassNotFoundException cnfEx)
		{
			// TODO Auto-generated catch block
			cnfEx.printStackTrace();
		}
		catch (IOException ioEx)
		{
			ioEx.printStackTrace();
			close();
		}
	
	}
}

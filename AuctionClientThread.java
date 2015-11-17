import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class AuctionClientThread extends Thread
{
	private AuctionClient client;
	private Socket socket;
	private ObjectInputStream inStream;
	
	public AuctionClientThread(AuctionClient client, Socket socket)
	{
		this.client = client;
		this.socket = socket;
		open();
	}
	
	public void open()
	{
		try
		{
			inStream = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try
		{
			if (inStream != null)
			{
				inStream.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client = null;
	}
	
	public void run()
	{
		AuctionItem item = null;
		while (client != null)
		{
			
			try
			{
				item = (AuctionItem) inStream.readObject();
				client.displayItemInfo(item);
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
}

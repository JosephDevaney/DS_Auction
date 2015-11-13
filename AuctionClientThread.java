import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
	
	public void run()
	{
		while (client != null)
		{
			
			try
			{
				client.displayItemInfo((AuctionItem) inStream.readObject());
			}
			catch (ClassNotFoundException cnfEx)
			{
				// TODO Auto-generated catch block
				cnfEx.printStackTrace();
			}
			catch (IOException ioEx)
			{
				ioEx.printStackTrace();
			}
		}
	}
}

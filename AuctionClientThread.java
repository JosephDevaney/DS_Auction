import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class AuctionClientThread extends Thread
{
	private AuctionClient client;
	private Socket socket;
	private ObjectInputStream inStream;
	private DataInputStream input;
	
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
			input = new DataInputStream(socket.getInputStream());
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
	
	public void run()
	{
		String message = "";
		while (client != null)
		{
			try
			{
				message = input.readUTF();
				System.out.println("Try again");
				if (message.equals("___Object___"))
				{
					System.out.println("Incoming object");
					getItem();
				}
				else
				{
					System.out.println("just a message");
					client.displayMsg(message);
				}
			}
			catch (IOException ioEx)
			{
				ioEx.printStackTrace();
				close();
			}
		}
	}
	
	public void getItem()
	{
		AuctionItem item = null;
		
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

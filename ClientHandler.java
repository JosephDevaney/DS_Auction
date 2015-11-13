import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread
{
	private AuctionServer server;
	private Socket client;
	private DataInputStream input;
	private PrintWriter output;
	private ObjectOutputStream outstream;
	private Thread thread = null;
	
	public ClientHandler(Socket socket, AuctionServer server)
	{
		client = socket;
		this.server = server;

		try
		{
			input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			//output = new PrintWriter(client.getOutputStream(), true);
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
	
	public void run()
	{
		thread = new Thread(this);
		
		while (thread != null)
		{
			try
			{
				server.newBid(input.readUTF());
			}
			catch (IOException ioEx)
			{
				// TODO Auto-generated catch block
				ioEx.printStackTrace();
				server.leaveAuction(this);
				thread = null;
			}
			
			
		}
		
	}
	
	public void close() throws IOException
	{
		if (client != null)
		{
			client.close();
		}
		if (input != null)
		{
			input.close();
		}
		if (output != null)
		{
			output.close();
		}
	}

	public void sendItem(AuctionItem item)
	{
		// TODO Auto-generated method stub
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
	
}

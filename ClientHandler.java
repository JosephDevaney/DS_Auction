import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread
{
	private AuctionServer server;
	private Socket client;
	private Scanner input;
	private PrintWriter output;
	private ObjectInputStream inStream;
	private ObjectOutputStream outstream;
	private Thread thread = null;
	
	public ClientHandler(Socket socket, AuctionServer server)
	{
		client = socket;
		this.server = server;

		try
		{
			input = new Scanner(client.getInputStream());
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
			//inStream = new ObjectInputStream(client.getInputStream());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		//thread = new Thread(this);
		AuctionItem item = null;

		item = AuctionItem.getCurrentItem();
		try
		{
			outstream.writeObject(item);
			outstream.flush();
			System.out.println("outstream object");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			outstream.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

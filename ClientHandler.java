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
	
	public ClientHandler(Socket socket, AuctionServer server)
	{
		client = socket;
		this.server = server;

//		try
//		{
//			input = new Scanner(client.getInputStream());
//			output = new PrintWriter(client.getOutputStream(), true);
//		} 
//		catch (IOException ioEx)
//		{
//			// TODO: handle exception
//			ioEx.printStackTrace();
//		}
		
		try
		{
			System.out.println("Trying to create streams!");
			//inStream = new ObjectInputStream(client.getInputStream());
			outstream = new ObjectOutputStream(client.getOutputStream());
			
			System.out.println("Created streams!");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		AuctionItem item = null;
		System.out.println("ClientHandler - run()");
		item = AuctionItem.getCurrentItem();
		try
		{
			outstream.writeObject(item);
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

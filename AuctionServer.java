import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AuctionServer
{
	private static ServerSocket serverSock;
	private static final int port = 1234;
	private static ArrayList<ClientHandler> clientList;

	public static void main(String[] args) throws IOException
	{
		// TODO Auto-generated method stub
		try
		{
			serverSock = new ServerSocket(port);
		}
		catch (IOException ioEx)
		{
			// TODO: handle exception
			System.out.println("Unable to set up port " + port + "!");
			System.exit(1);
		}

		clientList = new ArrayList<ClientHandler>();

		AuctionItem[] items = { new AuctionItem("Shoe", 100.00), new AuctionItem("Tie", 85.00) };

		do
		{
			Socket client = serverSock.accept();

			System.out.println("New client.");

			ClientHandler handler = new ClientHandler(client);

			clientList.add(handler);
			System.out.println("start handler");
			handler.start();
			System.out.println("handler started");

		} while (true);

	}

}

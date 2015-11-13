
public class AuctionHandler extends Thread
{
	private AuctionServer server = null;
	
	public AuctionHandler(AuctionServer server)
	{
		this.server = server;
	}
	
	public void run()
	{
		boolean isComplete;
		while (true)
		{
			isComplete = manageAuctionTime(6000);
			if (isComplete)
			{
				server.newAuctionItem();
			}
		}
	}

	public boolean manageAuctionTime(long time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			return false;
		}
		return true;
		
	}
}

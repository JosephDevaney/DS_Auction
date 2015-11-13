
public class AuctionHandler extends Thread
{
	private AuctionServer server = null;
	private boolean runThread = true;
	
	public AuctionHandler(AuctionServer server)
	{
		this.server = server;
	}
	public void stopThread()
	{
		runThread = false;
	}
	
	public void run()
	{
		boolean isComplete;
		while (runThread)
		{
			isComplete = manageAuctionTime(60000);
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

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Stores data about the Items for sale
 */
public class AuctionItem implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private double reservePrice;
	private double currentBid;
	private static int currentItem = 0;
	private boolean sold;
	private long startTime;
	
	private static ArrayList<AuctionItem> items = new ArrayList<>();
	private static ArrayList<AuctionItem> soldItems = new ArrayList<>();
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double getReservePrice()
	{
		return reservePrice;
	}

	public void setReservePrice(double reservePrice)
	{
		this.reservePrice = reservePrice;
	}

	public double getCurrentBid()
	{
		return currentBid;
	}

	public void setCurrentBid(double currentBid)
	{
		this.currentBid = currentBid;
	}

	public boolean isSold()
	{
		return sold;
	}

	public void setSold(boolean sold)
	{
		this.sold = sold;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	public AuctionItem (String name, double reservePrice)
	{
		this.name = name;
		this.reservePrice = reservePrice;
		currentBid = 0;
		sold = false;
		startTime = 0;
		
		items.add(this);
	}
	
	public static AuctionItem getCurrentItem ()
	{		
		return items.get(currentItem);
	}
	
	/*
	 * Returns the next Item in the list
	 */
	public static AuctionItem nextItem()
	{
		AuctionItem item = items.get(currentItem);
		if (item.isSold())
		{
			soldItems.add(items.remove(currentItem));
		}
		else
		{
			currentItem = (currentItem + 1) % items.size();
		}
		
		return items.size() > 0 ? items.get(currentItem) : null;
	}
	
}

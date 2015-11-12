import java.io.Serializable;
import java.util.ArrayList;

public class AuctionItem implements Serializable
{
	private String name;
	private double reservePrice;
	private double currentBid;
	private static int currentItem = 0;
	
	private static ArrayList<AuctionItem> items = new ArrayList<>();
	
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

	public AuctionItem (String name, double reservePrice)
	{
		this.name = name;
		this.reservePrice = reservePrice;
		currentBid = 0;
		
		items.add(this);
	}
	
	public static AuctionItem getCurrentItem ()
	{
		AuctionItem item = items.get(currentItem);
		currentItem = (currentItem + 1) % items.size();
		return item;
	}
	
}

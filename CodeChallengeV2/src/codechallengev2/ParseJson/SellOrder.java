package codechallengev2.ParseJson;

public class SellOrder implements Comparable {
    private double sellPrice;
    private double sellVolume;

    public SellOrder(double buyPrice, double buyVolume) {
        this.sellPrice = buyPrice;
        this.sellVolume = buyVolume;
    }

    public SellOrder() {
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getSellVolume() {
        return sellVolume;
    }

    public void setSellVolume(double sellVolume) {
        this.sellVolume = sellVolume;
    }

    @Override
    public int compareTo(Object o) {
        SellOrder sellCompare=(SellOrder)o;
        return this.getSellPrice()-sellCompare.getSellPrice()>0.0?1:-1;
    }
    
    
}

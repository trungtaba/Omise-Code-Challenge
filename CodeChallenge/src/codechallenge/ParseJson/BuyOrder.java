package codechallenge.ParseJson;

public class BuyOrder {
    private double buyPrice;
    private double buyVolume;

    public BuyOrder(double buyPrice, double buyVolume) {
        this.buyPrice = buyPrice;
        this.buyVolume = buyVolume;
    }

    public BuyOrder() {
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getBuyVolume() {
        return buyVolume;
    }

    public void setBuyVolume(double buyVolume) {
        this.buyVolume = buyVolume;
    }
    
    
}

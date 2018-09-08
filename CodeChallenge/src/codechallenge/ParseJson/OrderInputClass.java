package codechallenge.ParseJson;
//enum OrderType{buy,sell};

public class OrderInputClass {
    private OrderType orderType;
    private double price;
    private double amount;

    public OrderInputClass(OrderType orderType, double price, double volume) {
        this.orderType = orderType;
        this.price = price;
        this.amount = volume;
    }

    public OrderInputClass() {
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public OrderInputClass createCommandClass(OrderType command, double price, double volume){
        OrderInputClass commandInputClass=new OrderInputClass(command, price, volume);
        return  commandInputClass;
    }

    @Override
    public String toString() {
        return this.orderType+ "\t"+this.price+"\t"+this.amount;
        
    }
    
    public BuyOrder CreateBuyOrder(){
        if(this.orderType!=OrderType.buy)return null;
        return new BuyOrder(price, amount);
    }
    
    public SellOrder CreateSellOrder(){
        if(this.orderType!=OrderType.sell)return null;
        return new SellOrder(price, amount);
    }
}

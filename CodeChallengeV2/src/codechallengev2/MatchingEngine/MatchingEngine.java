package codechallengev2.MatchingEngine;



import codechallengev2.ParseJson.BuyOrder;
import codechallengev2.ParseJson.OrderInputClass;
import codechallengev2.ParseJson.OrderType;
import codechallengev2.ParseJson.ParseJson;
import codechallengev2.ParseJson.SellOrder;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MatchingEngine {

    private ArrayList<BuyOrder> buyOrderList;
    private ArrayList<SellOrder> sellOrderList;
    private ArrayList<Integer> sellOrderRemoveList;
    private ArrayList<Integer> buyOrderRemoveList;
    private ParseJson parseJson;
    private final Config config=Config.getInstance();
    //private CompareUtil compareUtil;

    public MatchingEngine() {
        buyOrderList = new ArrayList<>();
        sellOrderRemoveList = new ArrayList<>();
        buyOrderRemoveList = new ArrayList<>();
        sellOrderList = new ArrayList<>();
        parseJson = new ParseJson();
        //compareUtil=new CompareUtil();
    }

    /*
    Core function: marching
    +receive _pathFileOutput file and parse to ArrayList of orders
    +looping through every orders, checking order type
        +if type=sell, maping to buy orders list
        +if trype=buy, mapping to sell orders list
     */
    public void Matching(MatchingType matchingType) {
        if (matchingType == MatchingType.File) {
            File directory = new File(config.getInputDir());
            String[] list=directory.list();
            int fileCount = list.length;
            
            for (int i = 0; i < fileCount; i++) {
                File file=new File(config.getInputDir()+list[i]);
                MatchingFromFile(file);
            }
        }
    }

    public void MatchingFromFile(File file) {
        ArrayList<OrderInputClass> inputOrders = parseJson.ParseInputOrderFromFile(file);
        int orderSize = inputOrders.size();
        OrderInputClass orderInputClass;
        for (int i = 0; i < orderSize; i++) {
            orderInputClass = inputOrders.get(i);
            if (orderInputClass.getOrderType() == OrderType.sell) {
                MatchingBuyOrder(orderInputClass);
            } else {
                MatchingSellOrder(orderInputClass);
            }
        }
        //return parseJson.ParseOutputOrder(buyOrderList, sellOrderList);
        parseJson.ParseOutputOrderToFile(buyOrderList, sellOrderList, file);
        FormatOrderList();
    }

    /*
    +talking sell order as input
    +looping though buy order list, checking condition
        + if sell.price > buy.price : break
        + if sell.price <= buy.price
            + if sell.amount < buy.amount: remove sell, update buy.amount-=sell.amount; complete loop
            + if sell.amount >= buy.amount: remove buy, update sell.amount-=buy.amount; comtinue loop
     */
    private void MatchingBuyOrder(OrderInputClass sellOrder) {
        int sizeBuyOrder = buyOrderList.size();
        ArrayList<Integer> removeList = new ArrayList<>();
        BuyOrder buyOrder;
        for (int i = 0; i < sizeBuyOrder; i++) {
            buyOrder = buyOrderList.get(i);
            if (sellOrder.getPrice() <= buyOrder.getBuyPrice()) {
                if (sellOrder.getAmount() <= buyOrder.getBuyVolume()) {
                    //if(compareUtil.CompareDouble(sellOrder.getAmount(), buyOrder.getBuyVolume()) !=1){    
                    buyOrderList.get(i).setBuyVolume(round(buyOrder.getBuyVolume() - sellOrder.getAmount(), 3));
                    sellOrder.setAmount(0.0);
                    break;
                } else {
                    sellOrder.setAmount(round(sellOrder.getAmount() - buyOrder.getBuyVolume(), 3));
                    removeList.add(i);
                    //AddToBuyOrderRemove(i);
                    //buyOrderList.get(i).setBuyVolume(0.0);
                    //buyOrderList.remove(i);
                }
            } else {
                break;
            }
        }
        RemoveBuyOrder(removeList);
        UpdateSellOrder(sellOrder);
    }

    private void MatchingSellOrder(OrderInputClass buyOrder) {
        int sizeSellOrder = sellOrderList.size();
        ArrayList<Integer> removeList = new ArrayList<>();
        SellOrder sellOrder;

        for (int i = 0; i < sizeSellOrder; i++) {
            sellOrder = sellOrderList.get(i);
            if (buyOrder.getPrice() >= sellOrder.getSellPrice()) {
                //if(compareUtil.CompareDouble(buyOrder.getPrice(), sellOrder.getSellPrice()) !=-1){          
                if (buyOrder.getAmount() <= sellOrder.getSellVolume()) {
                    //if(compareUtil.CompareDouble(buyOrder.getAmount(), sellOrder.getSellVolume()) !=1){
                    sellOrderList.get(i).setSellVolume(round(sellOrder.getSellVolume() - buyOrder.getAmount(), 3));

                    buyOrder.setAmount(0.0);
                    break;
                } else {
                    buyOrder.setAmount(round(buyOrder.getAmount() - sellOrder.getSellVolume(), 3));
                    removeList.add(i);
                    //AddToSellOrderRemove(i);
                    //sellOrderList.get(i).setSellVolume(0.0);
                    //sellOrderList.remove(i);
                }
            } else {
                break;
            }
        }
        RemoveSellOrder(removeList);
        UpdateBuyOrder(buyOrder);
    }

    /*
    update sell order will be called after completing match function.
    + if sell list if empty, add sell order to sell order list
    + loop though sell orderlist, find the first sell order higher the current order.
        + if position =-1 mean the no one sell order higher the current order
          we compare the current sell with the last item in list. if
          
     */
    private void UpdateSellOrder(OrderInputClass sellOrder) {
        if (sellOrder.getAmount() == 0.0) {
            return; // amount is equal zero
        }
        int sellListsize = sellOrderList.size();
        //if sell list if empty, add sell order to sell order list
        if (sellListsize == 0) {
            sellOrderList.add(sellOrder.CreateSellOrder());
            return;
        }
        int position = -1;
        for (int i = 0; i < sellListsize; i++) {
            if (sellOrder.getPrice() < sellOrderList.get(i).getSellPrice()) {
                //if(compareUtil.CompareDouble(sellOrder.getPrice(), sellOrderList.get(i).getSellPrice())==-1){
                position = i;
                break;
            }
        }

        //position =-1 mean the no one sell order higher the current order
        if (position == -1) {
            //comparing the current sell order with the last sell order item in the list
            //if the price is equal, update the last sell order item
            if (sellOrder.getPrice() - sellOrderList.get(sellListsize - 1).getSellPrice() < 1e-8) {
                //if(compareUtil.CompareDouble(sellOrder.getPrice(), sellOrderList.get(sellListsize-1).getSellPrice())==0){
                sellOrderList.get(sellListsize - 1).setSellVolume(round(sellOrder.getAmount() + sellOrderList.get(sellListsize - 1).getSellVolume(), 3));
            } else {
                //the current sell order have the highest price, add to the last item in sell order list
                sellOrderList.add(sellListsize, sellOrder.CreateSellOrder());
            }
            //position = 0 mean the no one sell order lower the current order
        } else if (position == 0) {
            //comparing the current sell order with the fist sell order item in the list
            //if the price is equal, update the first sell order item
            if (sellOrder.getPrice() - sellOrderList.get(0).getSellPrice() < 1e-8) {
                //if(compareUtil.CompareDouble(sellOrder.getPrice(), sellOrderList.get(0).getSellPrice())==0){              
                sellOrderList.get(0).setSellVolume(round(sellOrder.getAmount() + sellOrderList.get(0).getSellVolume(), 3));
            } else {
                //the current sell order have the lowest price, add to the first item in sell order list
                sellOrderList.add(0, sellOrder.CreateSellOrder());
            }
            //sellOrderList.add(position, sellOrder.CreateSellOrder());
            //Comparing the current item with the first previous item of list(position) (FPI)
            //if the two item are equal, update FPI, if the current sell order is higher, add the current item after FPI
            //else add the current item before FPI
        } else if (sellOrder.getPrice() - sellOrderList.get(position - 1).getSellPrice() == 0.0) {
            //}else if(compareUtil.CompareDouble(sellOrder.getPrice(), sellOrderList.get(position-1).getSellPrice())==0){
            sellOrderList.get(position - 1).setSellVolume(sellOrderList.get(position - 1).getSellVolume() + sellOrder.getAmount());
        } else if (sellOrder.getPrice() - sellOrderList.get(position - 1).getSellPrice() > 0.0) {
            //}else if(compareUtil.CompareDouble(sellOrder.getPrice(), sellOrderList.get(position-1).getSellPrice())==1){
            sellOrderList.add(position, sellOrder.CreateSellOrder());
        } else {
            sellOrderList.add(position - 1, sellOrder.CreateSellOrder());
        }
    }

    private void UpdateBuyOrder(OrderInputClass buyOrder) {
        if (buyOrder.getAmount() < 1e-8) {
            return; // amount is equal zero
        }
        int buyListsize = buyOrderList.size();
        if (buyListsize == 0) {
            buyOrderList.add(buyOrder.CreateBuyOrder());
            return;
        }
        int position = -1;
        for (int i = 0; i < buyListsize; i++) {
            if (buyOrder.getPrice() > buyOrderList.get(i).getBuyPrice()) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            if (buyOrder.getPrice() - buyOrderList.get(buyListsize - 1).getBuyPrice() == 0.0) {
                buyOrderList.get(buyListsize - 1).setBuyVolume(buyOrder.getAmount() + buyOrderList.get(buyListsize - 1).getBuyVolume());
            } else {
                buyOrderList.add(buyListsize, buyOrder.CreateBuyOrder());
            }
        } else if (position == 0) {
            if (buyOrder.getPrice() - buyOrderList.get(0).getBuyPrice() < 1e-8) {
                buyOrderList.get(0).setBuyVolume(round(buyOrder.getAmount() + buyOrderList.get(0).getBuyVolume(), 3));
            } else {
                buyOrderList.add(0, buyOrder.CreateBuyOrder());
            }
            //buyOrderList.add(0, buyOrder.CreateBuyOrder());
        } else if (buyOrder.getPrice() - buyOrderList.get(position - 1).getBuyPrice() == 0.0) {//consider equal
            buyOrderList.get(position - 1).setBuyVolume(buyOrderList.get(position - 1).getBuyVolume() + buyOrder.getAmount());
        } else if (buyOrder.getPrice() - buyOrderList.get(position - 1).getBuyPrice() < 0.0) {
            buyOrderList.add(position, buyOrder.CreateBuyOrder());
        } else {
            buyOrderList.add(position - 1, buyOrder.CreateBuyOrder());
        }
    }

    /*
    version 1: instead of removing order, we set volume to 0 
     */
    private void RemoveSellOrder(ArrayList<Integer> removeList) {
        removeList.forEach((removeitem) -> {
            sellOrderList.get(removeitem).setSellVolume(0.0);
        });
    }

    /*
    version 1: instead of removing order, we set volume to 0 
     */
    private void RemoveBuyOrder(ArrayList<Integer> removeList) {
        removeList.forEach((removeitem) -> {
            buyOrderList.get(removeitem).setBuyVolume(0.0);
        });
    }

    private static double round(double number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public void FormatOrderList() {
        buyOrderList.clear();
        sellOrderList.clear();
    }
}

package codechallengev2.ParseJson;

import codechallengev2.MatchingEngine.Config;
import codechallengev2.MatchingEngine.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParseJson {

    private OrderInputClass commandInputClass;
    private final Config config=Config.getInstance();
    private final FileUtil fileUtil=new FileUtil();

    public ParseJson() {
        commandInputClass = new OrderInputClass();
    }

    public ParseJson(OrderInputClass commandInputClass, Properties properties) {
        this.commandInputClass = commandInputClass;
    }



    public ArrayList<OrderInputClass> ParseInputOrderFromFile(File file) {
        OrderType commandType;
        double price;
        double amount;
        ArrayList<OrderInputClass> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);

            Object obj = parser.parse(fileReader);
            JSONObject orders = (JSONObject) obj;
            JSONArray commandsArray = (JSONArray) orders.get("orders");
            int commandsSize = commandsArray.size();
            for (int i = 0; i < commandsSize; i++) {
                JSONObject commands = (JSONObject) commandsArray.get(i);
                if (commands.get("command").toString().equalsIgnoreCase("buy")) {
                    commandType = OrderType.buy;
                } else {
                    commandType = OrderType.sell;
                }
                price = Double.parseDouble(commands.get("price").toString());
                amount = Double.parseDouble(commands.get("amount").toString());
                OrderInputClass newCommandInputClass = commandInputClass.createCommandClass(commandType, price, amount);

                //System.out.println(newCommandInputClass.toString());
                list.add(newCommandInputClass);
            }
        } catch (IOException | NumberFormatException | ParseException ex) {
            fileUtil.errorFile(file);
            System.out.println("Parse error" + ex);
        } finally {
            fileUtil.closeFile(file,fileReader);
        }
        return list;
    }

    public ArrayList<OrderInputClass> ParseInputOrderFromString(String _inputCommands) {
        OrderType commandType;
        double price;
        double amount;
        ArrayList<OrderInputClass> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(_inputCommands);
            JSONObject orders = (JSONObject) obj;
            JSONArray commandsArray = (JSONArray) orders.get("orders");
            int commandsSize = commandsArray.size();
            for (int i = 0; i < commandsSize; i++) {
                JSONObject commands = (JSONObject) commandsArray.get(i);
                if (commands.get("command").toString().equalsIgnoreCase("buy")) {
                    commandType = OrderType.buy;
                } else {
                    commandType = OrderType.sell;
                }
                price = Double.parseDouble(commands.get("price").toString());
                amount = Double.parseDouble(commands.get("amount").toString());
                OrderInputClass newCommandInputClass = commandInputClass.createCommandClass(commandType, price, amount);

                //System.out.println(newCommandInputClass.toString());
                list.add(newCommandInputClass);
            }
        } catch (ParseException ex) {
            System.out.println("Parse error" + ex);
        }
        return list;
    }

    public String ParseOutputOrder(ArrayList<BuyOrder> buyOrders, ArrayList<SellOrder> sellOrders) {
        String result = null;
        //Put buy order
        int buyOrdersize = buyOrders.size();
        JSONArray buyOrdeArray = new JSONArray();
        for (int i = 0; i < buyOrdersize; i++) {
            if (buyOrders.get(i).getBuyVolume() != 0.0) {
                JSONObject buyOrder = new JSONObject();
                buyOrder.put("price", buyOrders.get(i).getBuyPrice());
                buyOrder.put("volume", buyOrders.get(i).getBuyVolume());
                buyOrdeArray.add(buyOrder);
            }
        }
        JSONObject buyOutput = new JSONObject();
        buyOutput.put("buy", buyOrdeArray);
        result += buyOutput;
        //System.out.println(buyOutput);

        //Put sell order
        int sellOrdersize = sellOrders.size();
        JSONArray sellOrdeArray = new JSONArray();
        for (int i = 0; i < sellOrdersize; i++) {
            JSONObject sellOrder = new JSONObject();
            sellOrder.put("volume", sellOrders.get(i).getSellVolume());
            sellOrder.put("price", sellOrders.get(i).getSellPrice());
            sellOrdeArray.add(sellOrder);
        }
        JSONObject sellOutput = new JSONObject();
        sellOutput.put("sell", sellOrdeArray);
        result += sellOutput;
        //System.out.println(sellOutput);
        return result;
    }

    public void ParseOutputOrderToFile(ArrayList<BuyOrder> buyOrders, ArrayList<SellOrder> sellOrders, File file) {
        //String result=null;
        //Put buy order
        JSONObject result = new JSONObject();
        int buyOrdersize = buyOrders.size();
        JSONArray buyOrdeArray = new JSONArray();
        for (int i = 0; i < buyOrdersize; i++) {
            if (buyOrders.get(i).getBuyVolume() != 0.0) {
                JSONObject buyOrder = new JSONObject();
                buyOrder.put("price", buyOrders.get(i).getBuyPrice());
                buyOrder.put("volume", buyOrders.get(i).getBuyVolume());
                             
                buyOrdeArray.add(buyOrder);
            }
        }
        //JSONObject buyOutput=new JSONObject();
        //buyOutput.put("buy", buyOrdeArray);
        result.put("buy", buyOrdeArray);
        //System.out.println(buyOutput);

        //Put sell order
        int sellOrdersize = sellOrders.size();
        JSONArray sellOrdeArray = new JSONArray();
        for (int i = 0; i < sellOrdersize; i++) {
            if (sellOrders.get(i).getSellVolume() != 0.0) {
                JSONObject sellOrder = new JSONObject();
                sellOrder.put("price", sellOrders.get(i).getSellPrice());
                sellOrder.put("volume", sellOrders.get(i).getSellVolume());
                             
                sellOrdeArray.add(sellOrder);
            }
        }
        //JSONObject sellOutput=new JSONObject();
        //sellOutput.put("sell", sellOrdeArray);
        result.put("sell", sellOrdeArray);
        FileWriter fileReader = null;
        try {
            String fileOutput=config.getOutputDir()+file.getName().replace("input", "output");
            fileReader = new FileWriter(fileOutput);
            //System.out.println(result.toJSONString());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gson.toJson(result);
            fileReader.write(prettyJson);
            //fileReader.write(result.toString());
            fileReader.flush();
            fileReader.close();
        } catch (IOException ex) {
            fileUtil.errorFile(file);
            Logger.getLogger(ParseJson.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

}

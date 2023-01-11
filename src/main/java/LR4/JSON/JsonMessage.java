package LR4.JSON;

import LR4.cfgClasses.MessageCFGClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonMessage {

    public String toJson(double powerAmount, double price){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MessageCFGClass obj_cfg = new MessageCFGClass(powerAmount, price);
        String message = gson.toJson(obj_cfg, MessageCFGClass.class); // Преобразование в строку
        return message;
    }

    public MessageCFGClass fromJson(String message){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MessageCFGClass obj_cfg = gson.fromJson(message, MessageCFGClass.class); // Преобразование в объект
        return obj_cfg;
    }

}

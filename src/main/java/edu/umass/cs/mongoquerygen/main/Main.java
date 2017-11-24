package edu.umass.cs.mongoquerygen.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.umass.cs.mongoquerygen.parser.ParseExpression;

/**
 * Created by kanantharamu on 11/23/17.
 */
public class Main
{
    public static void main(String[] args) {
        JsonElement jsonElement = new JsonParser().parse("{'a':'b', 'c':1}");
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add("A");
        jsonArray.add("B");
        jsonArray.add(1);

        jsonObject.add("arr", jsonArray);
        System.out.println(jsonObject.toString());


        String expr = "and(\n" +
                "\tgt(~user.wind.max, ${event.data.windspeed}),\n" +
                "\tor(\n" +
                "\t\teq(a, b),\n" +
                "\t\tlocation_within_geo(~user.location, ${event.data.windpolygon})\n" +
                "\t)\n" +
                ")";

        System.out.println(ParseExpression.toQuery(expr));
    }
}

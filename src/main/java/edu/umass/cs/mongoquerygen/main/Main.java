package edu.umass.cs.mongoquerygen.main;

import edu.umass.cs.mongoquerygen.parser.ParseExpression;

/**
 * Created by kanantharamu on 11/23/17.
 */
public class Main
{
    public static void main(String[] args) {


        String expr = "gt(~user.temperature, ${temperature.value})";

        System.out.println(ParseExpression.toQuery(expr));
    }
}

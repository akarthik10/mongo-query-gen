package edu.umass.cs.mongoquerygen.parser;

import edu.umass.cs.mongoquerygen.token.Token;
import edu.umass.cs.mongoquerygen.token.Tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by kanantharamu on 11/24/17.
 */
public class ParseExpression
{
    private static Map<String, Function> functions = new HashMap<String, Function>();
    private static String PARAM_START = "PARAM_START";

    static {
        addFunctions();
    }

    public static void addFunction(Function f)
    {
        functions.put(f.getName(), f);
    }

    public static void addFunctions()
    {
        addFunction(new Function("eq", 2) {
            @Override
            public String eval(List<String> params) {
                String field = params.get(0);
                String value = params.get(1);
                String jsonStr = "{ " + field + ": { $eq : " + value + " } }";
                return jsonStr;
            }
        });

        addFunction(new Function("not", 2) {
            @Override
            public String eval(List<String> params) {
                String field = params.get(0);
                String condition = params.get(1);
                String jsonStr = "{ " + field + " : { $not: " + condition + " }}";
                return jsonStr;
            }
        });

        addFunction(new Function("lt", Function.VARIABLE_NUM_PARAMS) {
            @Override
            public String eval(List<String> params) {
                if (params.size() == 2)
                {
                    String field = params.get(0);
                    String condition = params.get(1);
                    String jsonStr = "{ " + field + " : { $lt: " + condition + " }}";
                    return jsonStr;
                } else {
                    String field = params.get(0);
                    return "{ $lt : " + field + "}";
                }

            }
        });


        addFunction(new Function("gt", Function.VARIABLE_NUM_PARAMS) {
            @Override
            public String eval(List<String> params) {
                if (params.size() == 2)
                {
                    String field = params.get(0);
                    String condition = params.get(1);
                    String jsonStr = "{ " + field + " : { $gt: " + condition + " }}";
                    return jsonStr;
                } else {
                    String field = params.get(0);
                    return "{ $gt : " + field + "}";
                }

            }
        });



        addFunction(new Function("or", Function.VARIABLE_NUM_PARAMS) {
            @Override
            public String eval(List<String> params) {
                StringBuilder jsonStr = new StringBuilder("{ $or : [");
                for (int i=0; i< params.size(); i++)
                {
                    jsonStr.append(params.get(i));
                    if (i != params.size() - 1)
                    {
                        jsonStr.append(',');
                    }
                }
                jsonStr.append("]}");
                return jsonStr.toString();
            }
        });

        addFunction(new Function("and", Function.VARIABLE_NUM_PARAMS) {
            @Override
            public String eval(List<String> params) {
                StringBuilder jsonStr = new StringBuilder("{ $and : [");
                for (int i=0; i< params.size(); i++)
                {
                    jsonStr.append(params.get(i));
                    if (i != params.size() - 1)
                    {
                        jsonStr.append(',');
                    }
                }
                jsonStr.append("]}");
                return jsonStr.toString();
            }
        });

        addFunction(new Function("location_within_geo", 2) {
            @Override
            public String eval(List<String> params) {
                String point = params.get(0);
                String geo = params.get(1);
                return "{" + point + ":" +
                            "$geoWithin : {" +
                                " $geometry : " + geo +
                            "}" +
                        "}";

            }
        });

        addFunction(new Function("distance_between_points", 4) {
            @Override
            public String eval(List<String> params) {
                String field = params.get(0);
                String geo = params.get(1);
                String minDistance = params.get(2);
                String maxDistance = params.get(3);
                return "{" + field + ": { " +
                            "$near : { " +
                                "$geometry: "+geo+", " +
                                "$maxDistance: "+maxDistance+", " +
                                "$minDistance: "+minDistance+
                                "} " +
                            "}" +
                        "}";
            }
        });
    }


    public static List<Token> convert(String expression)
    {
        Token lastFunction = null;
        Token prev = null;

        List<Token> output = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        Tokenizer tokenizer = new Tokenizer(expression);

        while (tokenizer.hasNext())
        {
            Token token = tokenizer.next();
            switch(token.tokenType)
            {
                case FUNCTION:
                    stack.push(token);
                    lastFunction = token;
                    break;
                case COMMA:
                    while (!stack.isEmpty() && stack.peek().tokenType != Tokenizer.TokenType.OPEN_PARANTHESES)
                    {
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty())
                    {
                        throw new RuntimeException("Parse error while parsing function '"+ lastFunction + "'");
                    }
                    break;
                case OPEN_PARANTHESES:
                    if (prev.tokenType == Tokenizer.TokenType.FUNCTION)
                    {
                        output.add(token);
                    }
                    stack.push(token);
                    break;
                case CLOSE_PARANTHESES:
                    while (!stack.isEmpty() && stack.peek().tokenType != Tokenizer.TokenType.OPEN_PARANTHESES)
                    {
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty())
                    {
                        throw new RuntimeException("Mismatched parentheses");
                    }
                    stack.pop();

                    if (!stack.isEmpty() && stack.peek().tokenType == Tokenizer.TokenType.FUNCTION)
                    {
                        output.add(stack.pop());
                    }
                    break;
                case PARAM_LITERAL:
                case VARIABLE:
                    output.add(token);

            }

            prev = token;
        }

        while (!stack.isEmpty())
        {
            Token t = stack.pop();
            if (t.tokenType == Tokenizer.TokenType.OPEN_PARANTHESES || t.tokenType == Tokenizer.TokenType.CLOSE_PARANTHESES)
            {
                throw new RuntimeException("Mismatched parentheses");
            }
            output.add(t);
        }

        return output;
    }


    public static String eval(List<Token> tokens)
    {
        Stack<String> stack = new Stack<String>();
        for (final Token token: tokens)
        {
            switch (token.tokenType)
            {
                case VARIABLE:
                case PARAM_LITERAL:
                    stack.push(token.content);
                    break;
                case FUNCTION:
                    Function f = functions.get(token.content);

                    List<String> p = new ArrayList<String>(
                                            f.getNumParams() == Function.VARIABLE_NUM_PARAMS ? 0 : f.getNumParams()
                                            );
                    while (!stack.isEmpty() && stack.peek() != PARAM_START)
                    {
                        p.add(0, stack.pop());
                    }

                    if (stack.peek() == PARAM_START)
                    {
                        stack.pop();
                    }

                    if (f.getNumParams() != Function.VARIABLE_NUM_PARAMS && f.getNumParams()!=p.size())
                    {
                        throw new RuntimeException("Invalid number of parameters for '"+ f.getName() +"'");
                    }

                    String res = f.eval(p);
                    stack.push(res);
                    break;
                case OPEN_PARANTHESES:
                    stack.push(PARAM_START);
                    break;
            }
        }

        return stack.pop();
    }

    public static String toQuery(String expression)
    {
        List<Token> RPN = convert(expression);
        return eval(RPN);
    }

}

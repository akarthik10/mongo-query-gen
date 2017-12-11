package edu.umass.cs.mongoquerygen.token;

import java.util.Iterator;

import static edu.umass.cs.mongoquerygen.token.Tokenizer.TokenType.CLOSE_PARANTHESES;
import static edu.umass.cs.mongoquerygen.token.Tokenizer.TokenType.COMMA;
import static edu.umass.cs.mongoquerygen.token.Tokenizer.TokenType.OPEN_PARANTHESES;
import static edu.umass.cs.mongoquerygen.token.Tokenizer.TokenType.PARAM_LITERAL;
import static edu.umass.cs.mongoquerygen.token.Tokenizer.TokenType.VARIABLE;

/**
 * Created by kanantharamu on 11/23/17.
 */
public class Tokenizer implements Iterator<Token>
{
    private String expression;
    private int pos = 0;
    private Token prevToken;

    public enum TokenType { OPEN_PARANTHESES, CLOSE_PARANTHESES, COMMA, FUNCTION, PARAM_LITERAL, UNKNOWN, VARIABLE }
    String variableAllowed="~.";
    String functionAllowed = "0123456789_";


    public Tokenizer(String expression)
    {
        this.expression = expression;
    }

    public boolean hasNext() {
        return pos < expression.length();
    }

    private char peekNext()
    {
        if (pos < (expression.length() - 1))
        {
            return expression.charAt(pos+1);
        }
        return 0;
    }

    public Token next() {
        Token token = new Token();

        if (pos >= expression.length())
        {
            return prevToken = null;
        }

        char ch = expression.charAt(pos);
        while (Character.isWhitespace(ch) && pos < (expression.length()-1))
        {
            ch = expression.charAt(++pos);
        }
        token.pos = pos;

        if (ch == '$' && peekNext() == '{')
        {
            while (ch != '}' && pos < expression.length())
            {
                token.append(expression.charAt(pos++));
                ch = pos == expression.length() ? 0 : expression.charAt(pos);
            }

            if (ch != '}')
            {
                throw new RuntimeException("Unmatched braces");
            }

            token.append(ch);
            pos++;
            token.tokenType = VARIABLE;
        } else if (ch == '"' )
        {
            token.append(expression.charAt(pos++));
            ch = expression.charAt('"');

            while (ch != '"' && pos < expression.length())
            {
                token.append(expression.charAt(pos++));
                ch = pos == expression.length() ? 0 : expression.charAt(pos);
            }

            if (ch != '"')
            {
                throw new RuntimeException("Unmatched quotes");
            }

            token.append(ch);
            pos++;
            token.tokenType = PARAM_LITERAL;
        }
        // Starting character of a field name
        else if (Character.isLetter(ch) || ch == '~' || Character.isDigit(ch)) {
            while (Character.isLetter(ch) || variableAllowed.indexOf(ch) >= 0 || functionAllowed.indexOf(ch)>= 0 || Character.isDigit(ch))
            {
                token.append(expression.charAt(pos++));
                ch = pos == expression.length() ? 0 : expression.charAt(pos);
            }
            if (Character.isWhitespace(ch))
            {
                while (Character.isWhitespace(ch) && pos < expression.length())
                {
                    ch = expression.charAt(pos++);
                }
                pos --;
            }
            token.tokenType = ch == '(' ? TokenType.FUNCTION : PARAM_LITERAL;
        } else if (ch == '(' || ch == ')' || ch == ',')
        {
            if (ch == '(') token.tokenType = OPEN_PARANTHESES;
            else if (ch == ')') token.tokenType = CLOSE_PARANTHESES;
            else token.tokenType = COMMA;
            token.append(ch);
            pos++;
        } else {
            throw new RuntimeException("Unexpected character: " + ch);
        }

        return token;
    }

    public void remove() {
        throw new RuntimeException("remove() is unsupported");
    }

}

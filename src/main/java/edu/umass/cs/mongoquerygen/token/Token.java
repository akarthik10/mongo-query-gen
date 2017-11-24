package edu.umass.cs.mongoquerygen.token;

/**
 * TODO: Use a String builder for efficiency
 * Created by kanantharamu on 11/23/17.
 */
public class Token
{
    public Tokenizer.TokenType tokenType;
    public String content = "";
    int pos = 0;

    public void append(char c)
    {
        content += c;
    }

    public void append(String s)
    {
        content += s;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}

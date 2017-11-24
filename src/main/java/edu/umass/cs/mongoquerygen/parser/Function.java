package edu.umass.cs.mongoquerygen.parser;

import java.util.List;

/**
 * Created by kanantharamu on 11/24/17.
 */
public abstract  class Function
{

    private String name;
    private int numParams;

    static int VARIABLE_NUM_PARAMS = -1;

    public Function(String name, int numParams)
    {
        this.name = name;
        this.numParams = numParams;
    }

    public String getName() {
        return name;
    }

    public int getNumParams() {
        return numParams;
    }

    public abstract String eval(List<String> params);
}

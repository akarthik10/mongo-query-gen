package edu.umass.cs.mongoquerygen.main;

import edu.umass.cs.mongoquerygen.parser.ParseExpression;

/**
 * Give a flat query, convert it to mongo query.
 * Created by kanantharamu on 11/23/17.
 */
public class MongoQueryGen
{
    private String flatQuery;

    // Empty private constructor
    private MongoQueryGen() {

    }


    public String getFlatQuery() {
        return flatQuery;
    }

    private void setFlatQuery(String flatQuery) {
        this.flatQuery = flatQuery;
    }

    public String generate()
    {
        String generated = ParseExpression.toQuery(flatQuery);
        if (generated.startsWith("{") && generated.endsWith("}"))
        {
            generated = generated.substring(1, generated.length()-1);
        }
        return generated;
    }


    public static final class MongoQueryGenBuilder {
        private String flatQuery;

        public MongoQueryGenBuilder() {
        }

        public static MongoQueryGenBuilder aMongoQueryGen() {
            return new MongoQueryGenBuilder();
        }

        public MongoQueryGenBuilder withFlatQuery(String flatQuery) {
            this.flatQuery = flatQuery;
            return this;
        }

        public MongoQueryGen build() {
            MongoQueryGen mongoQueryGen = new MongoQueryGen();
            mongoQueryGen.setFlatQuery(flatQuery);
            return mongoQueryGen;
        }
    }
}

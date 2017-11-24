package edu.umass.cs.mongoquerygen.main;

import edu.umass.cs.mongoquerygen.variableresolver.LookupKey;

/**
 * Give a flat query, convert it to mongo query.
 * Created by kanantharamu on 11/23/17.
 */
public class MongoQueryGen
{
    private String flatQuery;
    private LookupKey variableResolver;

    // Empty private constructor
    private MongoQueryGen() {

    }

    public LookupKey getVariableResolver() {
        return variableResolver;
    }

    private void setVariableResolver(LookupKey variableResolver) {
        this.variableResolver = variableResolver;
    }

    public String getFlatQuery() {
        return flatQuery;
    }

    private void setFlatQuery(String flatQuery) {
        this.flatQuery = flatQuery;
    }


    public static final class MongoQueryGenBuilder {
        private String flatQuery;
        private LookupKey variableResolver;

        public MongoQueryGenBuilder() {
        }

        public static MongoQueryGenBuilder aMongoQueryGen() {
            return new MongoQueryGenBuilder();
        }

        public MongoQueryGenBuilder withFlatQuery(String flatQuery) {
            this.flatQuery = flatQuery;
            return this;
        }

        public MongoQueryGenBuilder withVariableResolver(LookupKey variableResolver) {
            this.variableResolver = variableResolver;
            return this;
        }

        public MongoQueryGen build() {
            MongoQueryGen mongoQueryGen = new MongoQueryGen();
            mongoQueryGen.setFlatQuery(flatQuery);
            mongoQueryGen.setVariableResolver(variableResolver);
            return mongoQueryGen;
        }
    }
}

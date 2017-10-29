package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class QueryExplanations extends Model {

    @Id
    String id;

    String queryExplanationsJSON;

    public QueryExplanations( String queryExplanationsJSON) {
        this.queryExplanationsJSON = queryExplanationsJSON;
    }
}

package models;

public class Query {



    String subject;
    String predicate;
    String object;

    public Query(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;

    }

    public Query() {
    }

    @Override
    public String toString() {
        return "Query{" +
                "subject='" + subject + '\'' +
                ", predicate='" + predicate + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}








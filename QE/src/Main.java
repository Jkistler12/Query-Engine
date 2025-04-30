public class Main {

    public static void main(String[] args) {

    }

    /**
     * Runs the given query on the database
     *
     * Implements the following algorithm
     *
     * Determine the type of query (from select, insert or delete)
     * If select query
     *   Select data
     *   Print results
     * Else if insert query
     *   Insert data
     * Else if delete is given
     *   Delete data
     *
     * @param query
     * @param db
     */
    public static void runQuery(String query, Database db) {
        if (query.contains("SELECT")){
            db.selectData(query);
        }
        else if (query.contains("INSERT")) {
            db.insertData(query);
        }
        else if (query.contains("DELETE")) {
            db.deleteData(query);
        }
        else {
            throw new IllegalArgumentException("Not a valid Query type");
        }
    }
}

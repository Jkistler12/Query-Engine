import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main database class
 * Database as a list of tables, list of schemas and a folder name where the database is stored
 * Database is stored (on the disk) in the form of three csv files and schema text file
 */
class Database {
    private List<ITable> tables;
    private List<ISchema> schemas;
    private String folderName;

    /**
     * Constructor
     * Creates the empty tables and schema lists
     * Reads the schema file to add schemas to the database
     * Populates the database table (with the data read from the csv files)
     * @param folderName
     * @param schemaFileName
     */
    public Database(String folderName, String schemaFileName) {
        this.tables=new ArrayList<>();
        this.schemas=new ArrayList<>();
        this.folderName=folderName;
        IO.readSchema(schemaFileName,folderName,this);
    }

    /**
     * Adds a table to the database
     * @param table
     */
    public void addTable(ITable table) {
        this.tables.add(table);
    }

    /**
     * Adds a table schema to the database
     * @param schema
     */
    public void addSchema(ISchema schema) {
        this.schemas.add(schema);
    }

    /**
     * Return the list of tables in the database
     * @return
     */
    public List<ITable> getTables() {
        return this.tables;
    }

    /**
     * Returns the list of schemas in the database
     * @return
     */
    public List<ISchema> getSchemas() {
        return this.schemas;
    }

    /**
     * The list of tables in the database is initialized with empty tables in the constructor
     * An empty table has a name and an empty list of tuples
     * This method sets the empty table in the list to the one provided as a parameter
     * @param table
     */
    public void updateTable(ITable table) {
        for(int i=0; i<this.tables.size();i++){
            if(this.tables.get(i).getName().equals(table.getName())){
                this.tables.set(i,table);
                break;
            }
        }
    }

    /**
     * Populates the database
     *
     * Implements the following algorithm
     *
     * For each table in the db (tables are initially empty)
     *   Get the table's data from the csv file (by calling the read table method)
     *   Update the table (by calling the udpate table method)
     */
    public void populateDB() {
        for(ITable t: this.tables){
            ITable nt=IO.readTable(t.getName(),t.getSchema(),folderName);
            updateTable(nt);
        }

    }

    /**
     * Insert data into a table based upon the insert query
     * If the query is invalid throws an InvalidQueryException
     *
     * Implements the following algorithm
     *
     * Parse the insert into clause to get the table name, attribute name(s) and value(s)
     * If the query in not valid
     *   Throw an invalid query exception
     *   Exit
     * Create a new tuple with the schema of the table
     * Set the tuple values to the values from the query
     * Open the file corresponding to the table name
     * Append the tuple values (as comma separated values) to the end of the file
     *
     * @param query
     * @throws InvalidQueryException
     */
    public void insertData(String query) throws InvalidQueryException {
        String TableName="";
        List<String>attributes= new ArrayList<>();
        List<String>values= new ArrayList<>();
        int i=0;
        String word="";
        boolean isAttributes=false;
        boolean isValues=false;
        int into=query.toLowerCase().indexOf("into");
        int par=query.indexOf('(',into);
        TableName=query.substring(into+4,par).trim();
        ITable table= findTable(TableName);
        i=par;
        while(i<query.length()){
                char c=query.charAt(i);
                if(c=='('){
                    if(!isAttributes && !isValues){
                        isAttributes= true;
                        word="";
                }else if(isAttributes){
                        isValues=true;
                        isAttributes=false;
                        word="";
                    }
            }else if(c==')'){
                    if(isAttributes){
                        if(!word.trim().isEmpty()){
                            attributes.add(word.trim());
                        }
                        isAttributes=false;
                    }else if(isValues){
                        if(!word.trim().isEmpty()){
                            values.add(word.trim());
                        }
                        isValues=false;
                    }
                    word="";
                }else if(c==','){
                    if(isAttributes){
                        attributes.add(word.trim());
                        word="";
                    }else if(isValues){
                        values.add(word.trim());
                        word="";
                    }else{
                        word=word+c;
                    }
                }else {
                    word=word+c;
                }
                i++;


        }
        ITuple tuple= new Tuple(table.getSchema());
        for(int j=0;j<attributes.size();j++){
            String attName= attributes.get(j);
            boolean found=false;
            for(int k=0;k<table.getSchema().getAttributes().size();k++){
                String schemaAtt= table.getSchema().getAttributes().get(k).split(":")[0];
                if(schemaAtt.equals(attName)){
                    tuple.setValue(k, values.get(j));
                    found=true;
                    break;
                }
            }

        }
        IO.writeTuple(TableName,tuple.getValues(),folderName);
        table.addTuple(tuple);
    }

    /**
     * Selects data from a table (and returns it in the form of a results table)
     * If the query in not valid, throws an InvalidQueryException
     *
     * A query is valid if
     *
     * 1.	It has a select clause (select keyword followed by at least one attribute name)
     * 2.	It has a from clause (from keyword followed by a table name)
     * 3.	All the attribute names in the select clause are in the schema
     * 4.	The table name in the from clause is in the schema
     * 5.	All the attribute names in the where clause (if present) are in the schema
     * 6.	The attribute name in the order by clause (if present) is in the schema
     *
     * Implements the following algorithm
     *
     * Parse the query to get the select, from, where and order by clauses and the attribute and table names and condition
     * If the query is not valid
     *   Throw an invalid query exception
     *   Exit
     * Create a new results schema based with the attributes from the select clause
     * Create a new result table
     * For each tuple in the table
     *   If the tuple matches the where clause condition(s)
     *     Create a new results tuple using the result schema
     *     Set the results tuple values to the current tuple corresponding values
     *     Add the results tuple to the result table
     * Return results table
     *
     *
     * @param query
     * @return
     * @throws InvalidQueryException
     */
    public ITable selectData(String query) throws InvalidQueryException {
        int selectStart = query.toLowerCase().indexOf("select") + 6;
        int fromStart = query.toLowerCase().indexOf("from");
        int whereStart = query.toLowerCase().indexOf("where");
        String fromClause;
        String selectClause= query.substring(selectStart,fromStart).trim();
        if (whereStart == -1) {
            fromClause = query.substring(fromStart + 4).trim();
        } else {
            fromClause = query.substring(fromStart + 4, whereStart).trim();
        }
        String whereClause;
        if(whereStart==-1){
            whereClause="";
        }else{
            whereClause=query.substring(whereStart+5).trim();
        }
        int orderStart=query.toLowerCase().indexOf("order by");
        String orderClause;
        if(orderStart==-1){
            orderClause="";
        }else{
            orderClause=query.substring(orderStart+9).trim();
        }
        ITable table=findTable(fromClause);
        String[] attributes=selectClause.split(",");
        for(int i=0;i<attributes.length;i++){
            attributes[i]=attributes[i].trim();
        }
        Map<Integer,String> schemaAtt= table.getSchema().getAttributes();
        for(String s: attributes){
            boolean found= false;
            for(String t: schemaAtt.values()){
                String SchemaAttributes=t.split(":")[0];
                if(SchemaAttributes.equals(s)){
                    found=true;
                    break;
                }
            }
        }
        Map<Integer, String>result= new HashMap<>();
        int index=0;
        for(String s: attributes){
            for(int j=0;j<schemaAtt.size();j++ ){
                String sv=schemaAtt.get(j);
                String sa= sv.split(":")[0];
                if(sa.equals(s)){
                    result.put(index++,sv);
                    break;
                }
            }
        }
        ISchema resultSchema= new Schema(result);
        ITable resultTable= new Table("Result",resultSchema);
        for(ITuple t: table.getTuples()){
            boolean isWhere=true;
            if(!whereClause.isEmpty()){
                String[]condition= whereClause.split("=");
                if(condition.length==2){
                    String col=condition[0].trim();
                    String val= condition[1].trim();
                    int colIndex=-1;
                    for(int i=0;i<schemaAtt.size();i++){
                        String schemaCol= schemaAtt.get(i).split(":")[0];
                        if(schemaCol.equals(col)){
                            colIndex=i;
                            break;
                        }
                    }
                    if(colIndex!=-1){
                        if(!t.getValue(colIndex).equals(val)){
                            isWhere=false;
                        }
                    }
                }
            }
            if(isWhere){
                ITuple resultTuple=new Tuple(resultSchema);
                int Index=0;
                for(String s: attributes){
                    for(int i=0;i<schemaAtt.size();i++){
                        String schemaCol=schemaAtt.get(i).split(":")[0];
                        if(schemaCol.equals(s)){
                            resultTuple.setValue(Index++,t.getValue(i));
                            break;
                        }
                    }
                }
                resultTable.addTuple(resultTuple);
            }
            if(!orderClause.isEmpty()){
                int OrderIndex=-1;
                for(int i=0; i<schemaAtt.size();i++){
                    String SchemaCol= schemaAtt.get(i).split(":")[0];
                    if(SchemaCol.equals(orderClause)){
                        OrderIndex=i;
                        break;
                    }
                }
                if(OrderIndex!=-1){
                    List<ITuple> tupleList=resultTable.getTuples();
                    for(int i=0;i<tupleList.size()-1;i++){
                        for(int j=i+1;j<tupleList.size();j++){
                            String val1=(String) tupleList.get(i).getValue(OrderIndex);
                            String val2=(String) tupleList.get(j).getValue(OrderIndex);
                            if(val1.compareTo(val2)>0){
                                ITuple temp= tupleList.get(i);
                                tupleList.set(i,tupleList.get(j));
                                tupleList.set(j,temp);
                            }
                        }
                    }
                }
            }
        }
        return resultTable;
    }
    /**
     * Delete data from a table
     * If the query in not valid, throws an InvalidQueryException
     *
     * Implements the following algorithm
     *
     * Parse the query to get the from and where clauses
     * Parse the from clause to get the table name
     * If the query in not valid
     *   Throw an invalid query exception
     *   Exit
     * If where clause is not empty
     *   Parse the where clause to get the the condition
     *   For each tuple in the table
     *     If the where clause condition is true
     *       Remove the tuple from the table
     * Else
     *   For each tuple in the table
     *     Remove the tuple from the table
     * Write the table to the file
     *
     * @param query
     * @throws InvalidQueryException
     */
    public void deleteData(String query) throws InvalidQueryException {
        int fromStart= query.toLowerCase().indexOf("from");
        int whereStart= query.toLowerCase().indexOf("where");
        String fromClause;
        String whereClause;
        if(whereStart==-1){
            fromClause=query.substring(fromStart+4).trim();
            whereClause="";
        }else{
            fromClause=query.substring(fromStart+4,whereStart).trim();
            whereClause=query.substring(whereStart+5).trim();
        }
        ITable table=findTable(fromClause);
        Map<Integer,String> schemaAtt=table.getSchema().getAttributes();
        List<ITuple>tuples=table.getTuples();
        List<ITuple>nt=new ArrayList<>();
        if(!whereClause.isEmpty()){
            String[]condition=whereClause.split("=");
            String col=condition[0].trim();
            String val=condition[1].trim();
            int colIndex=-1;
            for(int i=0;i<schemaAtt.size();i++){
                String schemaCol=schemaAtt.get(i).split(":")[0];
                if(schemaCol.equals(col)){
                    colIndex=i;
                    break;
                }
            }
            for(ITuple t: tuples){
                if(!t.getValue(colIndex).equals(val)){
                    nt.add(t);
                }
            }

        }
        tuples.clear();
        tuples.addAll(nt);
        IO.writeTable(table, folderName);
    }
    private ITable findTable(String s){
        for(ITable t: tables){
            if(t.getName().equals(s)){
                return t;
            }
        }
        return null;
    }

}

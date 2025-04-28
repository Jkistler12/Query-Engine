import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * this is the IO utility class
 */
public class IO {

    /**
     * Reads the table's data from a csv file
     *
     * Implement the following algorithm
     *
     * Open the csv file from the folder (corresponding to the tablename)
     *   For each line in the csv file
     *     Parse the line to get attribute values
     *     Create a new tuple with the schema of the table
     *     Set the tuple values to the attribute values
     *     Add the tuple to the table
     * Close file
     *
     * Return table
     * @param tablename
     * @param schema
     * @param folder
     * @return
     */
    public static ITable readTable(String tablename, ISchema schema, String folder) {
        ITable table=new Table(tablename,schema);
        try(Scanner FileInput=new Scanner(new File(folder+"/"+tablename+".csv"))){
            while(FileInput.hasNextLine()){
                String line=FileInput.nextLine().trim();
                ITuple tuple= new Tuple(schema);
                int index=0;
                String s="";
                for(int i=0; i< line.length();i++){
                    char current=line.charAt(i);
                    if(current==','){
                        index++;
                        tuple.setValue(index,s.trim());
                        s="";

                    }else {
                        s=s+current;
                    }
                }
                if(!s.isEmpty()){
                    tuple.setValue(index,s.trim());
                }
                table.addTuple(tuple);
            }

        }catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }
        return table;

    }

    /**
     * Writes the tables' data to a csv file
     *
     * Implement the following algorithm
     *
     * Open the csv file from the folder (corresponding to the tablename)
     * Clear all file content
     * For each tuple in table
     *   Write the tuple values to the file in csv format
     *
     * @param table
     * @param folder
     */
    public static void writeTable(ITable table, String folder) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(folder + "/" + table.getName() + ".csv")))) {
           ISchema schema=table.getSchema();
           int schemaSize=schema.getAttributes().size();

            for (ITuple t : table.getTuples()) {
                StringBuilder line= new StringBuilder();
                for (int i = 0; i < schemaSize; i++) {
                    if (t.getValue(i) != null) {
                        line.append(t.getValue(i).toString());
                    }
                    if (i <schemaSize-1) {
                        line.append(",");
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Prints the table to console (mainly used to print the output of the select query)
     *
     * Implements the following algorithm
     *
     * Print the attribute names from the schema as tab separated values
     * For each tuple in the table
     *   Print the values in tab separated format
     *
     *
     * @param table
     * @param schema
     */
    public static void printTable(ITable table, ISchema schema) {
        for(int i=0; i<schema.getAttributes().size();i++){
           System.out.print(schema.getName(i)+" ");
        }
        System.out.println();
        for(ITuple t: table.getTuples()){
            for(int i=0;i<t.getValues().length;i++){
                System.out.print(t.getValue(i)+" ");
            }
            System.out.println();
        }
    }


    /**
     * Writes a tuple to a csv file
     *
     * Implements the following algorithm
     *
     * Open the csv file from the folder (corresponding to the tablename)
     * Append the tuple (as array of strings) in the csv format to the file
     *
     * @param tableName
     * @param values
     * @param folder
     */
    public static void writeTuple(String tableName, Object[] values, String folder) {
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(new File(folder+"/"+tableName+".csv")))){
            for(int i=0;i< values.length;i++){
                if(values[i]!=null){
                    writer.write(values[i].toString());
                }
                if(i< values.length-1){
                    writer.write(",");
                }
            }
            writer.newLine();
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads and parses the schema, creates schema objects and (empty) tables and adds them to the provided database
     * The schema is stored in a text file:
     *
     * Implements the following algorithm
     *
     * Open the schema file
     * For each line
     *   Parse the line to get the table name, attribute names and attribute types
     *   Create an attribute map of (index, att_name:att_type) pairs
     *   For each attribute
     *     Store the index and name:type pair in the map (index represents the position of attribute in the schema)
     *   Create a new schema object with this attribute map
     *   Add the schema object to the database
     *   Create a new table object with the table name and the schema object
     *   Add the table to the database
     *
     * @param schemaFileName
     * @param folderName
     * @param db
     */
    public static void readSchema(String schemaFileName, String folderName, Database db) {
        try(Scanner scanner= new Scanner(new File(folderName+"/"+schemaFileName))){
            while(scanner.hasNextLine()){
                String line=scanner.nextLine().trim();
                if(line.length()>0){
                    String[]seperate=line.split(" ");
                    String TName= seperate[0];
                    Map<Integer,String> attributes= new HashMap<>();
                    for(int i=1;i<seperate.length;i++){
                        String[]attPart=seperate[i].split(":");
                        String AName= attPart[0];
                        String AType= attPart[1];
                        attributes.put(i-1,AName+":"+AType);
                    }
                    ISchema schema=new Schema(attributes);
                    ITable table= new Table(TName,schema);
                    db.addTable(table);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: "+schemaFileName);
        }

    }


}

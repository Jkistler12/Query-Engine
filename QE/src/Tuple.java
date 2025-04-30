import java.util.HashMap;
import java.util.Map;

/**
 * A tuple is an ordered collection of Objects and their associated types (Integer, Double or String)
 * Objects are stored in an array while types are stored in a map of (index, type)
 *
 */
public class Tuple implements ITuple {
    private Object[] values;
    private Map<Integer, Class<?>> typeMap;

    /**
     * The constructor receives a schema and creates the object array and typemap (representing the tuple)
     * The schema has the types of attributes stored as strings ("Integer", "Double", "String")
     * Based upon these types the constructor stores the actual class (Integer.class, Double.class,
     * String.class) to the typemap
     * @param schema
     */
    public Tuple(ISchema schema) {
        this.typeMap = new HashMap<>();
        for (int i = 0; i < schema.getAttributes().size();i++){
            Class<?> c;
            if (schema.getType(i) == "String"){
                c = String.class;
            }
            else if (schema.getType(i) == "Integer") {
                c = Integer.class;
            }
            else {
                c = Double.class;
            }
            this.typeMap.put(i,c);
        }
        this.values = new Object[this.typeMap.size()];
    }

    /**
     * Stores the value at the given index in the (tuple) object
     * The value is converted from the object to its actual class from the typemap
     * @param index
     * @param value
     */
    @Override

    public void setValue(int index, Object value) {
        Class<?> c = typeMap.get(index);
        if (c == Integer.class){
            value = Integer.parseInt(c.toString());
        }
        else if (c == Double.class) {
            value = Double.parseDouble(c.toString());
        }
        else if (!c.isInstance(value)){
            throw new IllegalArgumentException("Not a valid CLass");
        }
        values[index] = value;
    }

    /**
     * Returns the value at a given index from the tuple object
     * @param index
     * @return
     * @param <T>
     */
    @Override
    public <T> T getValue(int index) {
        return (T) this.values[index];
    }

    /**
     * Returns the tuple as an array of Objects
     * @return
     */
    @Override
    public Object[] getValues() {
        return this.values;
    }

    /**
     * Sets the tuple values to the provided ones
     * The values are converted from objects to their actual classes from the typemap
     * @param values
     */
    @Override
    public void setValues(Object[] values) {
        this.values = values;
    }
}

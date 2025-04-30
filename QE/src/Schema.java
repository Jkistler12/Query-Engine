import java.util.HashMap;
import java.util.Map;

/**
 * the schema is stored as a map of (index, name:type) pairs
 */
public class Schema implements ISchema {

    private Map<Integer, String> attributes;

    /**
     * constructor
     * @param attributes the given Map of attributes
     */
    public Schema(Map<Integer, String> attributes) {
        attributes = new HashMap<>();
        this.attributes = attributes;
    }

    /**
     * getter
     * @return the map of attributes for This object
     */
    @Override
    public Map<Integer, String> getAttributes() {
        return this.attributes;
    }

    /**
     * splits the name:type to return the attribute name
     * @param index the index of the name:type pair wanted
     * @return a String representing the name attributed to the index
     */
    @Override
    public String getName(int index) {
        return this.attributes.get(index).substring(0,this.attributes.get(index).indexOf(':'));
    }

    /**
     * splits the name:type to return the attribute type
     * @param index the index of the name:type pair wanted
     * @return a String representing the name attributed to the index
     */

    @Override
    public String getType(int index) {
        return this.attributes.get(index).substring(this.attributes.get(index).indexOf(':') + 1);
    }
}

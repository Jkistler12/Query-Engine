import java.util.Map;

/**
 * the schema is stored as a map of (index, name:type) pairs
 */
public class Schema implements ISchema {

    private Map<Integer, String> attributes;

    /**
     * constructor
     * @param attributes
     */
    public Schema(Map<Integer, String> attributes) {

    }

    /**
     * getter
     * @return
     */
    @Override
    public Map<Integer, String> getAttributes() {

    }

    /**
     * splits the name:type to return the attribute name
     * @param index
     * @return
     */
    @Override
    public String getName(int index) {

    }

    /**
     * splits the name:type to return the attribute type
     * @param index
     * @return
     */

    @Override
    public String getType(int index) {

    }
}
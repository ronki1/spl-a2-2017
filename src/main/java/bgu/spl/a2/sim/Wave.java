package bgu.spl.a2.sim;

import java.util.List;

/**
 * Created by ron on 27/12/16.
 */
public class Wave {

    private final List<Integer> startIds;
    private final List<Integer> qtys;
    private final List<String> names;

    /**
     * Constructor
     * @param startIds
     * @param qtys
     * @param names
     */
    public Wave(List<Integer> startIds, List<Integer> qtys, List<String> names) {

        this.startIds = startIds;
        this.qtys = qtys;
        this.names = names;
    }

    /**
     *
     * @return start id's of objects
     */
    public List<Integer> getStartIds() {
        return startIds;
    }

    /**
     *
     * @return quantities of objects
     */
    public List<Integer> getQtys() {
        return qtys;
    }

    /**
     *
     * @return names of objects
     */
    public List<String> getNames() {
        return names;
    }
}

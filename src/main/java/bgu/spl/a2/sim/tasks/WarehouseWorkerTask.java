package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

/**
 * Created by ron on 01/01/17.
 */
public class WarehouseWorkerTask extends Task<Integer> {

    private boolean canComplete = false;
    public WarehouseWorkerTask() {

    }

    @Override
    protected void start() {
        System.out.println(this.waitingCounter);
    }

    public void finish() {
        this.complete(1);
    }

}

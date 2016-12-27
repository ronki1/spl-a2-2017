package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ron on 27/12/16.
 */
public class ProductTask extends Task<Product> {

    private String name;
    private int startId;
    private Warehouse warehouse;
    private ManufactoringPlan mp;
    private int waitingRemaining;
    private long finalId=0;

    public ProductTask(String name, int startId, Warehouse warehouse) {
        super();
        this.name = name;
        this.startId = startId;
        this.warehouse = warehouse;
        mp = warehouse.getPlan(name);
        waitingRemaining = mp.getTools().length;

    }

    @Override
    protected void start() {
        Task[] spawns = new Task[mp.getParts().length];
        ArrayList<ProductTask> spawns2 = new ArrayList<>();
        for (int j=0; j<mp.getParts().length; j++) {
            ProductTask task = new ProductTask(mp.getParts()[j],startId+1,warehouse);
            spawns[j] = task;
            spawns2.add(task);
        }
        this.whenResolved(spawns2,()->{
            ArrayList<String> sortedTools = new ArrayList<String>();
            for (int i = 0; i < mp.getTools().length; i++) {
                sortedTools.add(mp.getTools()[i]);
            }
            Collections.sort(sortedTools);
            for (String tool : sortedTools) {
                Deferred<Tool> toolDeferred = warehouse.acquireTool(tool);
                toolDeferred.whenResolved(()->{
                    toolAcquired(toolDeferred.get(),spawns2);
                });
            }
        });
        this.spawn(spawns);

    }

    protected synchronized void toolAcquired(Tool t, ArrayList<ProductTask> pArr) {
        waitingRemaining--;
        for (ProductTask p : pArr) {
            finalId+= Math.abs(t.useOn(p.getResult().get()));
        }
        if (waitingRemaining == 0){
            Product p1 = new Product(startId,name);
            p1.setFinalId(startId+finalId);
            for (ProductTask pt : pArr) {
                p1.addPart(pt.getResult().get());
            }
            complete(p1);
        }
    }
}

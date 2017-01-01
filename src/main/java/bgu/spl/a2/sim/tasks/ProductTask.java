package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

import java.util.*;

/**
 * Created by ron on 27/12/16.
 */
public class ProductTask extends Task<Product> {

    private String name;
    private int startId;
    private Warehouse warehouse;
    private ManufactoringPlan mp;
    //private int waitingRemaining;
    private long finalId=0;
    private ArrayList<WarehouseWorkerTask> wwtArr = new ArrayList<>();
    private WarehouseWorkerTask[] wwt2;

    /**
     * Initializes the ProductTask
     * @param name
     * @param startId
     * @param warehouse
     */
    public ProductTask(String name, int startId, Warehouse warehouse) {
        super();
        this.name = name;
        this.startId = startId;
        this.warehouse = warehouse;
        mp = warehouse.getPlan(name);
        //waitingRemaining = mp.getTools().length;
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
        if(spawns.length != 0) {
            this.whenResolved(spawns2, () -> {
                if(mp.getTools().length != 0) {
                    ArrayList<String> sortedTools = new ArrayList<String>();
                    for (int i = 0; i < mp.getTools().length; i++) {
                        sortedTools.add(mp.getTools()[i]);
                    }
                    Collections.sort(sortedTools, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    for (String tool : sortedTools) {
//                        Deferred<Tool> toolDeferred = warehouse.acquireTool(tool);
                        WarehouseWorkerTask wwt = new WarehouseWorkerTask();
                        wwtArr.add(wwt);
//                        toolDeferred.whenResolved(() -> {
//                            toolAcquired(toolDeferred.get(), spawns2, wwt);
//                        });
                    }
                    ProductTask.this.whenResolved(wwtArr, () -> {
                        Product p1 = new Product(startId,name);
                        p1.setFinalId(startId+finalId);
                        for (ProductTask pt : spawns2) {
                            p1.addPart(pt.getResult().get());
                        }
                        ProductTask.this.complete(p1);
                    });
                    wwt2 = new WarehouseWorkerTask[wwtArr.size()];
                    for (int i = 0; i < wwtArr.size(); i++) {
                        wwt2[i] = wwtArr.get(i);
                    }
                    this.spawn(wwt2);
                    for (int i = 0; i < sortedTools.size(); i++) {
                        Deferred<Tool> toolDeferred = warehouse.acquireTool(sortedTools.get(i));
                        WarehouseWorkerTask wwt = wwt2[i];
                        toolDeferred.whenResolved(() -> {
                            toolAcquired(toolDeferred.get(), spawns2, wwt);
                        });
                    }
                }
                else {
                    Product p1 = new Product(startId,name);
                    complete(p1);
                }
            });
            this.spawn(spawns);
        }
        else {
            if(mp.getTools().length != 0) {
                ArrayList<String> sortedTools = new ArrayList<String>();
                for (int i = 0; i < mp.getTools().length; i++) {
                    sortedTools.add(mp.getTools()[i]);
                }
                Collections.sort(sortedTools, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                for (String tool : sortedTools) {
//                    Deferred<Tool> toolDeferred = warehouse.acquireTool(tool);
                    WarehouseWorkerTask wwt = new WarehouseWorkerTask();
                    wwtArr.add(wwt);
//                    toolDeferred.whenResolved(() -> {
//                        toolAcquired(toolDeferred.get(), spawns2, wwt);
//                    });
                }
                ProductTask.this.whenResolved(wwtArr, () -> {
                    Product p1 = new Product(startId,name);
                    p1.setFinalId(startId+finalId);
                    for (ProductTask pt : spawns2) {
                        p1.addPart(pt.getResult().get());
                    }
                    ProductTask.this.complete(p1);
                });
                wwt2 = new WarehouseWorkerTask[wwtArr.size()];
                for (int i = 0; i < wwtArr.size(); i++) {
                    wwt2[i] = wwtArr.get(i);
                }
                this.spawn(wwt2);
                for (int i = 0; i < sortedTools.size(); i++) {
                    Deferred<Tool> toolDeferred = warehouse.acquireTool(sortedTools.get(i));
                    WarehouseWorkerTask wwt = wwt2[i];
                    toolDeferred.whenResolved(() -> {
                        toolAcquired(toolDeferred.get(), spawns2, wwt);
                    });
                }
            }
            else {
                Product p1 = new Product(startId,name);
                complete(p1);
            }
        }

    }

    /**
     * acquires a tool. Synchronization is needed in order to avoid conflict in returning tools (the queue may not remain ecmpty in end of runtime)
     * @param t
     * @param pArr
     */
    protected synchronized void toolAcquired(Tool t, ArrayList<ProductTask> pArr, WarehouseWorkerTask wwt) {
        //waitingRemaining--;
        for (ProductTask p : pArr) {
            finalId+= Math.abs(t.useOn(p.getResult().get()));
        }
        warehouse.releaseTool(t);
        wwt.finish();
//        if (waitingRemaining == 0){
//            Product p1 = new Product(startId,name);
//            p1.setFinalId(startId+finalId);
//            for (ProductTask pt : pArr) {
//                p1.addPart(pt.getResult().get());
//            }
//            complete(p1);
//        }
    }
}

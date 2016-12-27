package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ron on 27/12/16.
 */
public class WaveTask extends Task<List<Product>> {

    private int index;
    private List<Integer> startIds;
    private List<Integer> qtys;
    private List<String> names;
    private Warehouse warehouse;

    public WaveTask(int index, List<Integer> startIds, List<Integer> qtys, List<String> names, Warehouse warehouse) {
        this.index = index;
        this.startIds = startIds;
        this.qtys = qtys;
        this.names = names;
        this.warehouse = warehouse;
    }

    @Override
    protected void start() {
            ProductTask[] spawns = new ProductTask[qtys.get(index)];
            List<ProductTask> spawns2 = new ArrayList<>();
            for (int j = 0; j < qtys.get(index); j++) {
                spawns[j] = new ProductTask(names.get(index), startIds.get(index) + j, warehouse);
                spawns2.add(spawns[j]);
            }
            this.whenResolved(spawns2, ()->{
                if(index+1< qtys.size()) {
                    WaveTask nextWaveTask = new WaveTask(index + 1, startIds, qtys,names,warehouse);
                    this.spawn(nextWaveTask);
                } else {
                    
                }
            });
            this.spawn(spawns);

    }
}

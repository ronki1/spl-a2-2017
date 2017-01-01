package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.Wave;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ron on 27/12/16.
 */
public class WaveTask extends Task<List<Product>> {

    private int index;
    private List<Wave> waves;
    private Warehouse warehouse;

    /**
     * Constructor
     * @param index
     * @param waves
     * @param warehouse
     */
    public WaveTask(int index, List<Wave> waves, Warehouse warehouse) {
        this.index = index;
        this.waves = waves;
        this.warehouse = warehouse;
    }

    @Override
    protected void start() {
        Wave currentWave = waves.get(index);
        List<ProductTask> allSpawns = new ArrayList<>();
        for (int i = 0; i < currentWave.getNames().size(); i++) {
            for (int j = 0; j < currentWave.getQtys().get(i); j++) {
                allSpawns.add(new ProductTask(currentWave.getNames().get(i), currentWave.getStartIds().get(i) + j, warehouse));
            }

        }
        this.whenResolved(allSpawns, () -> {
            if (index + 1 < waves.size()) {
                WaveTask nextWaveTask = new WaveTask(index + 1, waves, warehouse);
                ArrayList<WaveTask> nextWaveList = new ArrayList<WaveTask>();
                nextWaveList.add(nextWaveTask);
                this.whenResolved(nextWaveList, () -> {
                    ArrayList<Product> retProducts = new ArrayList<Product>();
                    for (ProductTask t : allSpawns) {
                        retProducts.add(t.getResult().get());
                    }
                    retProducts.addAll(nextWaveList.get(0).getResult().get());
                    complete(retProducts);
                });
                this.spawn(nextWaveTask);
            } else {
                List<Product> retProducts = new ArrayList<>();
                for (ProductTask task : allSpawns) {
                    retProducts.add(task.getResult().get());
                }
                complete(retProducts);
            }
        });
        Task[] spawns = new Task[allSpawns.size()];
        for (int i = 0; i < allSpawns.size(); i++) {
            spawns[i] = allSpawns.get(i);
        }
        this.spawn(spawns);
    }
}

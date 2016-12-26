/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

    private static WorkStealingThreadPool pool;
    private static Warehouse warehouse = new Warehouse();

    /**
     * Begin the simulation
     * Should not be called before attachWorkStealingThreadPool()
     */
    public static ConcurrentLinkedQueue<Product> start() {

    }

    /**
     * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
     *
     * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
     */
    public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
        pool = myWorkStealingThreadPool;
    }

    public static int main(String[] args) {
        try {
            String file = new String(Files.readAllBytes(Paths.get(args[0])));

            JSONObject mainObj = new JSONObject(file);
            int numOfThreads = mainObj.getInt("threads"); //TODO ??

            JSONArray toolsArray = mainObj.getJSONArray("tools");
            for (int i = 0; i < toolsArray.length(); i++) {
                JSONObject toolObj = toolsArray.getJSONObject(i);
                if (toolObj.getString("tool").equals("gs-driver")) {
                    Tool tool = new GcdScrewDriver();
                    warehouse.addTool(tool, toolObj.getInt("qty"));
                } else if (toolObj.getString("tool").equals("np-hammer")) {
                    Tool tool = new NextPrimeHammer();
                    warehouse.addTool(tool, toolObj.getInt("qty"));
                } else if (toolObj.getString("tool").equals("rs-pliers")) {
                    Tool tool = new RandomSumPliers();
                    warehouse.addTool(tool, toolObj.getInt("qty"));
                }
            }

            JSONArray plansArray = mainObj.getJSONArray("plans");
            for (int i = 0; i < plansArray.length(); i++) {
                JSONObject planObject = plansArray.getJSONObject(i);
                JSONArray planToolsArray = planObject.getJSONArray("tools");
                String[] planTools = new String[planToolsArray.length()];
                for (int j = 0; j < planToolsArray.length(); j++) {
                    planTools[j] = planToolsArray.getString(j);
                }
                JSONArray planPartsArray = planObject.getJSONArray("parts");
                String[] planParts = new String[planPartsArray.length()];
                for (int j = 0; j < planPartsArray.length(); j++) {
                    planParts[j] = planPartsArray.getString(j);
                }
                warehouse.addPlan(new ManufactoringPlan(planObject.getString("product"), planParts, planTools));
            }

            WorkStealingThreadPool newPool = new WorkStealingThreadPool(numOfThreads);

            JSONArray wavesArray = mainObj.getJSONArray("waves");
            for (int i = 0; i < wavesArray.length(); i++) {
                JSONArray waveArray = wavesArray.getJSONArray(i);
                for (int j = 0; j < waveArray.length(); j++) {
                    JSONObject productObj = waveArray.getJSONObject(j);
                    for (int k = 0; k < productObj.getInt("qty"); k++) {
                        newPool.submit(new ProductTask(productObj.getString("product"), productObj.getInt("startId")));
                    }
                }
            }

            attachWorkStealingThreadPool(newPool);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO waves

        return 0; //TODO ??
    }
}

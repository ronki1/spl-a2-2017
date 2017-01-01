package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.Deferred;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
//	private AtomicIntegerArray tools = new AtomicIntegerArray(3); // 0- random sum, 1-gcd, 2-next prime
	private int[] tools = new int[3];// 0- random sum, 1-gcd, 2-next prime
	private List<ManufactoringPlan> plans = new ArrayList<>();
	private BlockingDeque<Deferred> rsDeferreds = new LinkedBlockingDeque<>();
	private BlockingDeque<Deferred> gcdDeferreds = new LinkedBlockingDeque<>();
	private BlockingDeque<Deferred> npDeferreds = new LinkedBlockingDeque<>();

	/**
	* Constructor
	*/
    public Warehouse() {

	}

	/**
	* Tool acquisition procedure
	* Note that this procedure is non-blocking and should return immediatly
	* @param type - string describing the required tool
	* @return a deferred promise for the  requested tool
	*/
    public synchronized Deferred<Tool> acquireTool(String type) {
    	Deferred<Tool> retVal = new Deferred<Tool>();
		if (type.equals("gs-driver")){
			tools[1]--;
			if(tools[1]<0) {
				tools[1]++;
				gcdDeferreds.addLast(retVal);
			}
			else {
				retVal.resolve(new GcdScrewDriver());
			}
		}
		else if (type.equals("rs-pliers")){
			tools[0]--;
			if(tools[0]<0) {
				tools[0]++;
				rsDeferreds.addLast(retVal);
			}
			else {
				retVal.resolve(new RandomSumPliers());
			}
		} else if (type.equals("np-hammer")) {
			tools[2]--;
			if(tools[2]<0) {
				tools[2]++;
				npDeferreds.addLast(retVal);
			}
			else {
				retVal.resolve(new NextPrimeHammer());
			}
		}

		return retVal;
	}

	/**
	* Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	* @param tool - The tool to be returned
	*/
    public synchronized void releaseTool(Tool tool){
		if (tool.getType().equals("gs-driver")){
			if (tools[1] == -1)
				System.out.println("");
			tools[1]++;
			if (gcdDeferreds.size()>0) {
				gcdDeferreds.pop().resolve(new GcdScrewDriver());
				tools[1]--;
			}
		}
		else if (tool.getType().equals("rs-pliers")){
			if (tools[0] == -1)
				System.out.println("");
			tools[0]++;
			if (rsDeferreds.size()>0) {
				rsDeferreds.pop().resolve(new RandomSumPliers());
				tools[0]--;
			}
		} else if (tool.getType().equals("np-hammer")) {
			if (tools[2] == -1)
				System.out.println("");
			tools[2]++;
			if (npDeferreds.size()>0) {
				npDeferreds.pop().resolve(new NextPrimeHammer());
				tools[2]--;
			}
		}
	}

	
	/**
	* Getter for ManufactoringPlans
	* @param product - a string with the product name for which a ManufactoringPlan is desired
	* @return A ManufactoringPlan for product
	*/
    public ManufactoringPlan getPlan(String product) {
    	for (ManufactoringPlan plan : plans) {
    		if (plan.getProductName().equals(product))
    			return plan;
		}
		return null;
	}
	
	/**
	* Store a ManufactoringPlan in the warehouse for later retrieval
	* @param plan - a ManufactoringPlan to be stored
	*/
    public void addPlan(ManufactoringPlan plan) {
		plans.add(plan);
	}
    
	/**
	* Store a qty Amount of tools of type tool in the warehouse for later retrieval
	* @param tool - type of tool to be stored
	* @param qty - amount of tools of type tool to be stored
	*/
    public void addTool(Tool tool, int qty){
    	if (tool.getType().equals("gs-driver"))
    		tools[1]+=qty;
    	else if (tool.getType().equals("rs-pliers"))
			tools[0]+=qty;
		else if (tool.getType().equals("np-hammer"))
			tools[2]+=qty;
	}

}

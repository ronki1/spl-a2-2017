package bgu.spl.a2;

import bgu.spl.a2.sim.tasks.WarehouseWorkerTask;
import bgu.spl.a2.test.MergeSort;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;
    private BlockingDeque<Task> tasks;
    private int stealingCounter = 0;
    private int nextVictimNumber;
    private boolean stealing = false;
    private boolean running = false;
    private boolean executing = false;

    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
        tasks = new LinkedBlockingDeque<>();
    }

    @Override
    public void run() {
        try {
            pool.vm.await(pool.vm.getVersion() + 1);
        } catch (InterruptedException e) {
            return;
        }
        running = true;
            while (running) {
                try {
                    Task k = tasks.peekFirst();
                    System.out.println("K is " + k);
                    System.out.println("K waits for" +k.waitingCounter);
                    if (k != null) { //TODO what if stolen during check? cannot be stolen since stealing is done only if there is more than one task
                        if(k.getResult().isResolved()) {
                            this.removeFromQueue(k);
                        }
                        else if(k.waitingCounter > 0) {
                            rescheduleTask(k);
                        }
                        else { //can run
                            if(k.started) {
                                if(k.runCallback == true) {
                                    k.runCallback = false;
                                    if(k.whenResolvedCallback!= null) k.whenResolvedCallback.run();
                                }
                            }
                            else {
                                k.running = (true);
                                k.started = (true);
                                k.start();
                            }
                        }
                    }
                    if (tasks.size() == 0 || (tasks.size()==1 && tasks.getFirst().waitingCounter>0)) {
                        startSteal();
                        if(tasks.size() == 0)
                            pool.vm.await(pool.vm.getVersion() + 1);
                    }
                }catch (InterruptedException e) {
                    System.out.println("Thread " + this.id + " interrupted");
                    running = false;
                }
            }

    }

    /**
     * adds tasks to Processor
     * @param t - task to be added
     */
    protected void addTask(Task t) {
        t.handle(this);
        tasks.addLast(t);
        stealing=false;
    }

    /**
     * notifies Processor that a task has ended
     * @param t
     */
    protected void taskEnded(Task t) {
        removeFromQueue(t);
        pool.taskEnded(t);
    }

    protected void rescheduleTask(Task t) {
        t.running = (false);
        if (tasks.remove(t)) {
            tasks.addLast(t);
            pool.taskRescheduled(t);
            //TODO pause execution of task
        }

        pool.taskRescheduled(t);
    }

    /**
     * returns if a processor has waiting tasks
     * @return
     */
    protected boolean hasTasksLeft() {
        return tasks.size() > 0;
    }

    /**
            * returns if a processor has waiting tasks
     * @return
             */
    protected int numOfTasksLeft() {
        return tasks.size();
    }

    /**
     *
     */
    protected boolean removeFromQueue(Task t) {
            return tasks.remove(t);
    }

    /**
     * get last task to be executed
     * @return Task if exists, null if no task exists
     */
    protected Task getLastTask() {
        return tasks.peekLast();
    }

    /**
     * start stealing operation
     */
    protected void startSteal() throws InterruptedException {
        if(pool.getProcessors().length==1) return;
        this.stealing = true;
        Processor target = pool.getProcessors()[nextVictimNumber];
        int stealGoal = target.numOfTasksLeft()/2, alreadyStolen = 0;
        ArrayList<Task> targets = new ArrayList<>();
        outer: while (target.numOfTasksLeft() >1 && alreadyStolen<=stealGoal) {
            boolean stealSuccess = steal(target);
                    if (stealSuccess) {
                        alreadyStolen += 1;
                    } else {
                        break outer;
                    }
        }
            stealing = false;
        nextVictimNumber = (nextVictimNumber+1)%pool.getProcessors().length;
        if(pool.getProcessors()[nextVictimNumber] == this) nextVictimNumber = (nextVictimNumber+1)%pool.getProcessors().length;
    }

    /**
     * returns true is was able to steal task, else false.
     * @param targetP processor from which we want to steal
     * @param t target task to be stolen
     */
    private boolean steal(Processor targetP, Task t) {
        boolean success = targetP.removeFromQueue(t);
        if(success) this.addTask(t);
        return success;
    }

    /**
     * Steals from a target processor
     * @param targetP processor to steal from
     * @return if steal was successful
     */
    private boolean steal(Processor targetP) {
        Task t = targetP.getLastTask();
        if(t==null) return false;
        if(t.running || t.getResult().isResolved()) return false;
        if(targetP.numOfTasksLeft() <= 1) return false;
        boolean success = targetP.removeFromQueue(t);
        if(success) {
            this.addTask(t);
            System.out.println(this +" Stole from "+ targetP+", task with array "+ t.toString());
        }
        return success;
    }

    /**
     * setter
     * @param num
     */
    protected void setNextVictimNumber(int num) {
        this.nextVictimNumber = num;
    }

    /**
     * getter
     * @return thread number of next victim
     */
    protected int getNextVictimNumber() {
        return nextVictimNumber;
    }

    /**
     *
     * @return if processor is executing
     */
    protected boolean isexecuting() {
        return executing;
    }

    protected void subTaskFinished(Task t) {
        this.pool.subTaskFinished(t);
    }
}

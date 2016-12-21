package bgu.spl.a2;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
        //TODO: replace method body with real implementation
        throw new UnsupportedOperationException("Not Implemented Yet.");
    }

    /**
     * adds tasks to Processor
     * @param t - task to be added
     */
    protected void addTask(Task t) {
        tasks.addLast(t);
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
        if (tasks.remove(t)) {
            tasks.addLast(t);
            pool.taskRescheduled(t);
            //TODO pause execution of task
        }
    }

    /**
     * returns if a processor has waiting tasks
     * @return
     */
    protected boolean hasTasksLeft() {
        return tasks.size() == 0 ? true:false;
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
     * start stealing operation
     */
    protected void startSteal() {
        this.stealing = true;
        Processor target = pool.getProcessors()[nextVictimNumber];
        int stealGoal = target.numOfTasksLeft()/2, alreadyStolen = 0;
        while (target.numOfTasksLeft() <=1 && this.numOfTasksLeft()==0) {
            try {
                wait();
                //if(steal(target,))
            } catch (InterruptedException e) {
                e.printStackTrace();
                //TODO what if interrupted because of new task while waiting for stealing
            }
        }
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
     * transfer task to stealing thread. Synchronization is needed in order to avoid stealing of task that starts being executed.
     * @param t task to be transferred
     * @return if transfer succeeded
     */
    /*protected synchronized boolean tranferTask(Task t) {

    }*/
    /**
     * setter
     * @param num
     */
    protected void setNextVictimNumber(int num) {
        this.nextVictimNumber = num;
    }

    public int getNextVictimNumber() {
        return nextVictimNumber;
    }
}

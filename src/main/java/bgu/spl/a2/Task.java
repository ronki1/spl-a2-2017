package bgu.spl.a2;

import bgu.spl.a2.sim.tasks.WarehouseWorkerTask;
import bgu.spl.a2.test.MergeSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;

/**
 * an abstract class that represents a task that may be executed using the
 * {@link WorkStealingThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the task result type
 */
public abstract class Task<R> {

    protected Processor handler;
    protected boolean started=false,running=false;
    protected Runnable whenResolvedCallback;
    protected Deferred defferedObj = new Deferred();
    private int numOfSubtasksWaiting = 0;
    private CountDownLatch latch;
    protected int waitingCounter;
    protected ArrayList<Task> tasksDependsOn = new ArrayList<>();
    protected boolean runCallback = false;
    /**
     * start handling the task - note that this method is protected, a handler
     * cannot call it directly but instead must use the
     * {@link #handle(bgu.spl.a2.Processor)} method
     */
    protected abstract void start();

    /**
     *
     * start/continue handling the task
     *
     * this method should be called by a processor in order to start this task
     * or continue its execution in the case where it has been already started,
     * any sub-tasks / child-tasks of this task should be submitted to the queue
     * of the handler that handles it currently
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * @param handler the handler that wants to handle the task
     */
    /*package*/ final void handle(Processor handler) {
        this.handler = handler;
    }

    /**
     * This method schedules a new task (a child of the current task) to the
     * same processor which currently handles this task.
     *
     * @param task the task to execute
     */
    protected final void spawn(Task<?>... task) {
        for (int i=0; i< task.length; i++) {
            handler.addTask(task[i]);
        }
//        if(this.getWaitingCounter()>0) handler.rescheduleTask(this);
        System.out.println("task order spawned");
        System.out.println("Spawned Task is " + this);
//        for (Task t2 : handler.tasks) {
            //System.out.println("Task Rescheduled, Processor "+ handler +" tasks: "+t2.toString());
//        }
    }

    /**
     * add a callback to be executed once *all* the given tasks results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given tasks completed.
     *
     * @param tasks
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void whenResolved(Collection<? extends Task<?>> tasks, Runnable callback) {
        whenResolvedCallback = callback;
        Iterator itr = tasks.iterator();
        latch = new CountDownLatch(tasks.size());
        waitingCounter = tasks.size();
        numOfSubtasksWaiting+=tasks.size();
        for (Task<?> t : tasks) {
            tasksDependsOn.add(t);
            //if (t.getResult().isResolved()) waitingCounter--;
            //TODO if using latches, decrease num
            t.getResult().whenResolved(()->{
                Task.this.subTaskFinished(this);
            });
        }
        running = false;
        handler.rescheduleTask(this);
    }

    /**
     * resolve the internal result - should be called by the task derivative
     * once it is done.
     *
     * @param result - the task calculated result
     */
    protected final void complete(R result) {
        System.out.println(this.handler + " Resolved Task with: "+ this.toString());
        defferedObj.resolve(result);
        handler.taskEnded(this);
    }

    /**
     * @return this task deferred result
     */
    public final Deferred<R> getResult() {
        return this.defferedObj;
    }

    /**
     * callback location when task has finished, Synchronized to prevent countdown errors.
     * @param t
     */
    private synchronized void subTaskFinished(Task t) {
        waitingCounter--;
        if(waitingCounter == 0) {
            runCallback = true;
        }
        this.handler.subTaskFinished(this);
    }

    /**
     * getter
     * @return
     */
    private int getWaitingCounter() {
        return waitingCounter;
    }

    /**
     * setter
     * @param running
     */
    private void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * setter
     * @param started
     */
    private void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * getter
     * @return
     */
    private boolean isStarted() {
        return started;
    }

    /**
     * getter
     * @return
     */
    private boolean isRunning() {
        return running;
    }


}

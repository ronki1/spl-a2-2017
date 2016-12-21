package bgu.spl.a2;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {

    protected int nthreads;
    private Processor[] processors;
    protected Thread[] threads;
    protected VersionMonitor vm;

    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
        this.nthreads = nthreads;
        processors = new Processor[nthreads];
        threads = new Thread[nthreads];
        for (int i=0; i<nthreads; i++) {
            processors[i] = new Processor(i,this);
            processors[i].setNextVictimNumber((i+1)%nthreads);
        }

        this.vm = new VersionMonitor();
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
        int taskToBeSubmitted = (int) Math.random()*this.nthreads;
        processors[taskToBeSubmitted].addTask(task);
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
        //TODO: replace method body with real implementation
        throw new UnsupportedOperationException("Not Implemented Yet.");
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (int i=0; i<nthreads;i++) {
            threads[i] = new Thread(processors[i]);
            threads[i].start();
        }
    }
    /**
     * notifies pool that a task has ended
     * @param t
     */
    protected void taskEnded(Task t) {
        if(!t.handler.hasTasksLeft()) { //if no tasks were left->steal
            t.handler.startSteal();
        }
        vm.inc();
    }

    /**
     * notifies pool that a task has started
     * @param t
     */
    protected void taskStarted(Task t) {

    }

    /**
     * notifies pool that a task was rescheduled
     * @param t
     */
    protected void taskRescheduled(Task t) {

    }

    public Processor[] getProcessors() {
        return processors;
    }
}

package bgu.spl.a2;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {
    private int version = 0;

    public int getVersion() {
        return version;
    }

    /**
     * increments the version number. Should be synchronized since we want to avoid increment problems as seen at practical session
     */
    public synchronized void inc() {
        this.version++;
        notifyAll();
    }

    public void await(int version) throws InterruptedException {
        while (this.version<version) {
            synchronized (this) {
                wait();
            }
        }
    }
}

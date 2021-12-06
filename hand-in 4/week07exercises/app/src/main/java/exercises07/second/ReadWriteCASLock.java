// For week 7
// raup@itu.dk * 10/10/2021
package exercises07.second;

import java.util.concurrent.atomic.AtomicReference;

import exercises07.second.exception.InvalidUnlockException;
import exercises07.second.exception.NotFoundException;

class ReadWriteCASLock implements SimpleRWTryLockInterface {
    private static void tesSingleThreaded() {

        ReadWriteCASLock rwc = new ReadWriteCASLock();
//        test the writer lock/unlock
//        rwc.writerTryLock();
//        rwc.writerUnlock();
        // test reader while a writer has the lock
//        rwc.writerTryLock();
        rwc.readerTryLock();
//        rwc.writerUnlock();
        // test reader lock/unlock
//        rwc.readerTryLock();
        System.out.println("-------------------------");
//        rwc.writerTryLock();
        rwc.readerUnlock();

    }


    private static void testMultiThreaded()  {

        ReadWriteCASLock rwc = new ReadWriteCASLock();
        new Thread(() -> {
            try {
                rwc.writerTryLock();
                rwc.writerUnlock();
                rwc.readerTryLock();
                rwc.readerTryLock();
                rwc.readerUnlock();
                rwc.readerUnlock();
                System.out.println("---------------------");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }).start();
        new Thread(() -> {
            try {
                rwc.writerTryLock();
                rwc.writerUnlock();
                rwc.readerTryLock();
                rwc.readerTryLock();
                rwc.readerUnlock();
                rwc.readerUnlock();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        //TODO execute tests (7.2.5 & 7.2.6)
        tesSingleThreaded();
        testMultiThreaded();
    }

    public static AtomicReference<Holders> holder = new AtomicReference<>();


    public boolean readerTryLock() {
        Holders oldValue;
        while ((oldValue = holder.get()) == null || oldValue instanceof ReaderList)
            if (holder.compareAndSet(oldValue, new ReaderList(Thread.currentThread(), (ReaderList) oldValue)))
                return true;
        return false;
    }

    public void readerUnlock() {
        final Thread current = Thread.currentThread();
        Holders oldValue;
        while ((oldValue = holder.get()) != null
                && oldValue instanceof ReaderList
                && ((ReaderList) oldValue).contains(current))
            if (holder.compareAndSet(oldValue, ((ReaderList) oldValue).remove(current)))
                return;


        throw new InvalidUnlockException("the calling thread does not hold the lock");
     }

    public boolean writerTryLock() {

        Holders writer = new Writer(Thread.currentThread());
       return holder.compareAndSet(null, writer);
    }

    public void writerUnlock() {
        final Thread current = Thread.currentThread();
        Holders oldValue;
        while ((oldValue = holder.get()) != null
                && oldValue instanceof Writer
                && ((Writer) oldValue).thread.equals(current)) {
            if(holder.compareAndSet(oldValue, null))
                return;
        }
        throw new InvalidUnlockException("the calling thread does not hold a write lock");
    }

    private static abstract class Holders {
    }

    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;

        public ReaderList(Thread thread, ReaderList reader) {
            this.thread = thread;
            this.next = reader;
        }

        public boolean contains(Thread thread) {
            if (this.thread.equals(thread)) {
                return true;
            } else {
                if (next == null)
                    return false;
                return this.next.contains(thread);
            }
        }


        public ReaderList remove(Thread thread) {
            if (!contains(thread))
                throw new NotFoundException("no such a thread found in the list");
            if (this.thread.equals(thread))
                return next;
            else
                return new ReaderList(this.thread, this.next == null ? null : this.next.remove(thread));
        }

    }

    private static class Writer extends Holders {
        public final Thread thread;

        public Writer(Thread t) {
            this.thread = t;
        }

    }
}

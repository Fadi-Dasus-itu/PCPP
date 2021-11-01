// For week 7
// raup@itu.dk * 10/10/2021
package exercises07.second;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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


    private static void testMultiThreaded() throws InterruptedException {

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

    public static void main(String[] args) throws InterruptedException {
        //TODO execute tests (7.2.5 & 7.2.6)
        tesSingleThreaded();
        testMultiThreaded();
    }

    public static AtomicReference<Holders> holder = new AtomicReference<>();


    public boolean readerTryLock() {

        if (this.holder.get() instanceof Writer) {
            System.out.println("reader could not acquire the lock since a writer already has it");
            return false;
        }

        var updatedSuccessfully = false;
        var current = this.holder;
        var newHolder = new ReaderList(Thread.currentThread(), (ReaderList) current.get());
        updatedSuccessfully = holder.compareAndSet(null, newHolder);
        if (updatedSuccessfully) {
            System.out.println("there is no one acquiring the lock and the reader successfully locked ");
        }

        if (!updatedSuccessfully && current.get() instanceof ReaderList) {
            System.out.println("there is some reader(s) acquiring the lock, the reader tried to acquire the lock");
            do {
                if (current.get() instanceof ReaderList) {
                    var current2 = this.holder;
                    var newHolder2 = new ReaderList(Thread.currentThread(), (ReaderList) current.get());
                    updatedSuccessfully = current2.compareAndSet(current.get(), newHolder2);
                }
            } while (!updatedSuccessfully);
            System.out.println("reader successfully acquired the lock and added to the list of readers");
        }
        return updatedSuccessfully;
    }


    public void readerUnlock() {
        var current = this.holder;
        var isReader = (current.get() instanceof ReaderList);
        if (!isReader) {
            throw new InvalidUnlockException("the calling thread does not hold the lock");
        }
        var updatedSuccessfully = false;
        System.out.println("reader tries to unlock");
        if (this.holder.get() instanceof ReaderList) {

            do {
                var current2 = this.holder;
                var newReader = ((ReaderList) this.holder.get()).remove((ReaderList) current2.get());
                updatedSuccessfully = current2.compareAndSet(current2.get(), newReader);
            } while (!updatedSuccessfully);
            System.out.println("reader successfully unlocked");
        }

    }

    public boolean writerTryLock() {

        Holders writer = new Writer(Thread.currentThread());

        var success = holder.compareAndSet(null, writer);
        if (success) {
            System.out.println("writer locked successfully");
        } else {
            System.out.println("writer could not acquire the lock");
        }
        return success;
    }

    public void writerUnlock() {

        var current = this.holder;
        if (!current.compareAndSet(current.get(), null))
            throw new InvalidUnlockException("the calling thread does not hold a write lock");
        System.out.println("writer unlocked successfully");
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


        public ReaderList remove(ReaderList readerList) {
            if (!contains(readerList.thread))
                throw new NotFoundException("no such a thread found in the list");
            var newReaderList = readerList.next;
            if (Thread.currentThread().equals(readerList.thread))
                return newReaderList;
            throw new InvalidUnlockException("the current thread does not hold the lock");
        }

    }

    private static class Writer extends Holders {
        public final Thread thread;

        public Writer(Thread t) {
            this.thread = t;
        }

    }
}

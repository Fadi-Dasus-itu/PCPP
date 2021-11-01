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
        rwc.writerTryLock();
        rwc.writerUnlock();
        // test reader while a writer has the lock
        rwc.writerTryLock();
        rwc.readerTryLock();
        rwc.writerUnlock();
        // test reader lock/unlock
        rwc.readerTryLock();
        System.out.println("-------------------------");
        rwc.writerTryLock();
        rwc.readerUnlock();

    }


    private static void testMultiThreaded() throws InterruptedException {

        ReadWriteCASLock rwc = new ReadWriteCASLock();
        new Thread(() -> {
            try {
//                rwc.writerTryLock();
//                rwc.writerUnlock();
//                rwc.readerTryLock();

                rwc.readerTryLock();
                rwc.readerUnlock();
                System.out.println("---------------------");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }).start();
        Thread.sleep(1000);
        new Thread(() -> {
            try {
//                rwc.writerTryLock();
//                rwc.writerUnlock();

//                rwc.readerTryLock();
//
                rwc.readerUnlock();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) throws InterruptedException {
        //TODO execute tests (7.2.5 & 7.2.6)
//        tesSingleThreaded();
        testMultiThreaded();
    }

    public static AtomicReference<Holders> holder = new AtomicReference<>();


    public boolean readerTryLock() {
        System.out.println("///////");
        System.out.println(Thread.currentThread().getName());
        System.out.println("///////");
        if (this.holder.get() instanceof Writer) {
            System.out.println("reader could not acquire the lock since a writer already has it");
            return false;
        }
        var updatedSuccessfully = false;

        var current = this.holder;
        var newHolder = new ReaderList(Thread.currentThread());
        updatedSuccessfully = holder.compareAndSet(null, newHolder);
        if (updatedSuccessfully) {

            ReaderList.list.addLast(newHolder);
            System.out.println("there is no one acquiring the lock and the reader successfully lock list size is: " + ReaderList.list.size());
        }
        if (!updatedSuccessfully && current.get() instanceof ReaderList) {
            System.out.println("there is some reader(s) acquiring the lock, the reader tried to acquire the lock");

            ReaderList.list.addLast(newHolder);
            do {
                updatedSuccessfully = current.compareAndSet(current.get(), newHolder);
            } while (!updatedSuccessfully);
            System.out.println("reader successfully acquired the lock and added to the list of readers");
            System.out.println("number of readers are: " + ReaderList.list.size());
        }
        return updatedSuccessfully;
    }


    public void readerUnlock() {
        System.out.println("///////");
        System.out.println(Thread.currentThread().getName());
        System.out.println("///////");
        var current = this.holder;
        var isReader = (current.get() instanceof ReaderList);
        if (!isReader) {
            throw new InvalidUnlockException("the calling thread does not hold a write lock");
        }
        var updatedSuccessfully = false;
        System.out.println("reader tries to unlock");
        if (isReader) {
           var removed = ReaderList.remove((ReaderList) current.get());
           if(!removed){
               throw new InvalidUnlockException("the calling thread does not hold a write lock");
           }
                System.out.println("reader has been removed");
                System.out.println("the reader is removed from the list, list size now is :" + ReaderList.list.size());

            do {
                if (ReaderList.list.size() > 0) {
                    updatedSuccessfully = current.compareAndSet(current.get(), ReaderList.list.getFirst());
                }

                if (ReaderList.list.size() == 0) {
                    updatedSuccessfully = current.compareAndSet(current.get(), null);
                }

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
        private final ReaderList next = null;
        public static LinkedList<ReaderList> list = new LinkedList<>();

        public ReaderList(Thread thread) {
            this.thread = thread;
        }

        public static boolean contains(ReaderList readerList) {
            return list.stream().filter(x -> x.thread.equals(readerList.thread)).count() > 0;
        }

        public static boolean remove(ReaderList readerList) {
            AtomicBoolean removed = new AtomicBoolean(false);
            if (!contains(readerList))
                throw new NotFoundException("no such a thread found in the list");
            ReaderList.list = list
                    .stream()
                    .filter((x) -> {
                        var match = !x.thread.getName().equals(Thread.currentThread().getName());
                        if (!match) {
                            removed.set(true);
                        }
                        return match;

                    })
                    .collect(Collectors.toCollection(LinkedList::new));
            return removed.get();
        }
    }

    private static class Writer extends Holders {
        public final Thread thread;

        public Writer(Thread t) {
            this.thread = t;
        }

    }
}

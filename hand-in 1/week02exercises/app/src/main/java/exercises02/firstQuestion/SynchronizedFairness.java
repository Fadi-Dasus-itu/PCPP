package exercises02.firstQuestion;

import java.util.ArrayList;
import java.util.List;

// inspired by Jakob Jenkov
public class SynchronizedFairness {
        private boolean           isLocked       = false;
        private Thread            lockingThread  = null;
        private List<SynchronizingObject> waitingThreads =
                new ArrayList<SynchronizingObject>();

        public void lock() throws InterruptedException{
            SynchronizingObject queueObject           = new SynchronizingObject();
            boolean     isLockedForThisThread = true;
            synchronized(this){
                waitingThreads.add(queueObject);
            }

            while(isLockedForThisThread){
                synchronized(this){
                    isLockedForThisThread =
                            isLocked || waitingThreads.get(0) != queueObject;
                    if(!isLockedForThisThread){
                        isLocked = true;
                        waitingThreads.remove(queueObject);
                        lockingThread = Thread.currentThread();
                        return;
                    }
                }
                try{
                    queueObject.doWait();
                }catch(InterruptedException e){
                    synchronized(this) { waitingThreads.remove(queueObject); }
                    throw e;
                }
            }
        }

        public synchronized void unlock(){
            if(this.lockingThread != Thread.currentThread()){
                throw new IllegalMonitorStateException(
                        "Calling thread has not locked this lock");
            }
            isLocked      = false;
            lockingThread = null;
            if(waitingThreads.size() > 0){
                waitingThreads.get(0).doNotify();
            }
        }

}

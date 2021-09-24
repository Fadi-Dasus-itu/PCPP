package exercises04.second;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PersonIdGenerator {
    private static PersonIdGenerator instance = null;
    private static final List<AtomicLong> syncList = Collections.synchronizedList(new ArrayList<>());

    private PersonIdGenerator() {
    }

    public static synchronized PersonIdGenerator getInstance() {
        synchronized (PersonIdGenerator.class) {
            if (instance == null) {
                instance = new PersonIdGenerator();
            }
            return instance;
        }
    }

    public static synchronized long getNextAvailableId() {
        synchronized (PersonIdGenerator.class) {
            if (syncList.size() == 0) {
                syncList.add(new AtomicLong(0));
                return 0;
            }
            var newId = syncList.get(syncList.size()-1).incrementAndGet();
            syncList.add(new AtomicLong(newId));
            return newId;
        }
    }

    public static synchronized void getNextAvailableId(long id) {
        synchronized (PersonIdGenerator.class) {
            if (syncList.size() == 0) {
                syncList.add(new AtomicLong(id));
            }
            syncList.add(new AtomicLong(id));
        }
    }


}

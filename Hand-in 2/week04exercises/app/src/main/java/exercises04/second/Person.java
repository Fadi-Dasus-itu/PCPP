package exercises04.second;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Person {
    private volatile long id;
    private volatile String name;
    private volatile String address;
    private volatile int zip;

    private final Lock LOCK = new ReentrantLock();

    public Person() {
        synchronized (this.LOCK) {
            this.id = PersonIdGenerator.getNextAvailableId();
        }
    }

    public Person(long id) {
        synchronized (this.LOCK) {
            this.id = id;
            PersonIdGenerator.getNextAvailableId(id);
        }
    }

    public void setAddressAndSZip(String address, int zip) {
        synchronized (this.LOCK) {
            this.address = address;
            this.zip = zip;
        }
    }

    public void setName(String name) {
        synchronized (this.LOCK) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public int getZip() {
        return zip;
    }

    private void setZip(int zip) {
        this.zip = zip;
    }

    public long getId() {
        return id;
    }


}

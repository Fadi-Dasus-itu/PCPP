package exercises01;

import java.util.concurrent.locks.ReentrantLock;

public class CountIncrementer extends ReentrantLock {

    private int count = 0;

    void  incrementPrefix() {
        // 1- read the field
        // 2- add one
        // write to the field again
        //        ALOAD 0
        //        DUP   // Make duplicate reference
        //        GETFIELD exercises01/test.count : I
        //        ICONST_1
        //        IADD
        //        PUTFIELD exercises01/test.count : I
        this.lock();
        count++;
        this.unlock();
    }

    void incrementPrefix2() {
        // 1- read the field
        // 2- add one
        // write to the field againALOAD 0
        //        ALOAD 0
        //        DUP  //  duplicate reference for another variable
        //        GETFIELD exercises01/test.count : I
        //        ICONST_1
        //        IADD
        //        PUTFIELD exercises01/test.count : I
        count += 1;
    }

    void increment() {
        // in this case I am reading twice
        count = count + 1;

        //        ALOAD 0  // Push local variable 0 (this)
        //        ALOAD 0  // Push local variable 0 (this)
        //        GETFIELD exercises01/test.count : I
        //        ICONST_1   // Push int constant 1
        //        IADD       // add integer 1
        //        PUTFIELD exercises01/test.count : I
    }


    void incrementPostfix(){
        ++count;
        //         L0
        //        LINENUMBER 46 L0
        //        ALOAD 0
        //        DUP
        //        GETFIELD exercises01/CountIncrementer.count : I
        //        ICONST_1
        //        IADD
        //        PUTFIELD exercises01/CountIncrementer.count : I
        //        L1
        //        LINENUMBER 47 L1
        //        RETURN
        //        L2
        //        LOCALVARIABLE this Lexercises01/CountIncrementer; L0 L2 0
        //        MAXSTACK = 3
        //        MAXLOCALS = 1
    }
}

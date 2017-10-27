package utils;

public class SyncCounter {


    int count=0;

    public synchronized int getCount() {
        return ++count;
    }

}

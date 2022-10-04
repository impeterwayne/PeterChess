package com.peterwayne.peterchess.pattern;

public interface MyObservable {
    void addObserver(MyObserver observer);
    void removeObserver(MyObserver observer);
    void notifyObservers(Object o);
}

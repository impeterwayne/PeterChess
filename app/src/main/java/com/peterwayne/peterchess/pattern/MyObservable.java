package com.peterwayne.peterchess.pattern;

public interface MyObservable {
    void addObserver(MyObserver observer);
    void removeObserver(MyObserver observer);
    void removeAllObservers();
    void notifyObservers(Object o);
}

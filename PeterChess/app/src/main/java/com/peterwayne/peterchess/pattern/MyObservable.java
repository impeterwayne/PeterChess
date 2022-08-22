package com.peterwayne.peterchess.pattern;

public interface MyObservable {
    public void addObserver(MyObserver observer);
    public void removeObserver(MyObserver observer);
    public void notifyObservers();
}

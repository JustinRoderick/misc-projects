/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: Main
Description: a description of what the class provides would normally be expected.
*/

package org.railroad;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SwitchManager {
    private Map<Integer, ReentrantLock> switches;

    public SwitchManager(int maxSwitches) {
        switches = new HashMap<>();
        for (int i = 1; i <= maxSwitches; i++) {
            switches.put(i, new ReentrantLock());
        }
    }

    public boolean lockSwitch(int switchNumber) {
        ReentrantLock lock = switches.get(switchNumber);
        if (lock == null) return false;
        return lock.tryLock();
    }

    public void unlockSwitch(int switchNumber) {
        ReentrantLock lock = switches.get(switchNumber);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
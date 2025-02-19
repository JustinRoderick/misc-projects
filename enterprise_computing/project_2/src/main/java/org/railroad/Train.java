/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: Train
Description: Represents a Train object and has the logic to run a train through the simulation
*/

package org.railroad;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Train implements Runnable {

    private static final AtomicInteger dispatchCounter = new AtomicInteger(1);
    private int number;
    private int inbound;
    private int outbound;
    private List<Integer> requiredSwitches;
    private SwitchManager switchManager;
    private boolean onPermanentHold = false;
    private boolean dispatched;
    private int dispatchSequence;
    private boolean isOnHold;

    public Train(int number, int inbound, int outbound, List<Integer> requiredSwitches, SwitchManager switchManager) {
        this.number = number;
        this.inbound = inbound;
        this.outbound = outbound;
        this.switchManager = switchManager;
        this.requiredSwitches = requiredSwitches;
        this.onPermanentHold = (requiredSwitches == null);
        this.dispatched = false;
        this.isOnHold = false;
    }

    @Override
    public void run() {
        if (onPermanentHold) {
            System.out.println("*************\nTrain " + number + " is on permanent hold and cannot be dispatched.\n*************");
            return;
        }

        while (!dispatched) {

            if (!switchManager.lockSwitch(requiredSwitches.get(0))) {
                System.out.println("Train " + number + ": UNABLE TO LOCK first required switch: Switch " + requiredSwitches.get(0) + ". Train will wait...");
                waitSomeTime();
                continue;
            }
            System.out.println("Train " + number + ": HOLDS LOCK on Switch " + requiredSwitches.get(0) + ".");

            if (!switchManager.lockSwitch(requiredSwitches.get(1))) {
                System.out.println("Train " + number + ": UNABLE TO LOCK second required switch: Switch " + requiredSwitches.get(1) + ".");
                switchManager.unlockSwitch(requiredSwitches.get(0));
                System.out.println("Train " + number + ": Releasing lock on first required switch: Switch " + requiredSwitches.get(0) + ". Train will wait...");
                waitSomeTime();
                continue;
            }
            System.out.println("Train " + number + ": HOLDS LOCK on Switch " + requiredSwitches.get(1) + ".");

            if (!switchManager.lockSwitch(requiredSwitches.get(2))) {
                System.out.println("Train " + number + ": UNABLE TO LOCK third required switch: Switch " + requiredSwitches.get(2) + ".");
                switchManager.unlockSwitch(requiredSwitches.get(0));
                switchManager.unlockSwitch(requiredSwitches.get(1));
                System.out.println("Train " + number + ": Releasing locks on first and second required switches: Switch " + requiredSwitches.get(0) + " and Switch " + requiredSwitches.get(1) + ". Train will wait...");
                waitSomeTime();
                continue;
            }
            System.out.println("Train " + number + ": HOLDS LOCK on Switch " + requiredSwitches.get(2) + ".");

            System.out.println("Train " + number + ": HOLDS ALL NEEDED SWITCH LOCKS – Train movement begins.");
            simulateMovement();

            System.out.println("Train " + number + ": Clear of yard control.");
            System.out.println("Train " + number + ": Releasing all switch locks.");
            switchManager.unlockSwitch(requiredSwitches.get(0));
            System.out.println("Train " + number + ": Unlocks/releases lock on Switch " + requiredSwitches.get(0) + ".");
            switchManager.unlockSwitch(requiredSwitches.get(1));
            System.out.println("Train " + number + ": Unlocks/releases lock on Switch " + requiredSwitches.get(1) + ".");
            switchManager.unlockSwitch(requiredSwitches.get(2));
            System.out.println("Train " + number + ": Unlocks/releases lock on Switch " + requiredSwitches.get(2) + ".");
            System.out.println("Train " + number + ": Has been dispatched and moves on down the line out of yard control into CTC.");
            System.out.println("@ @ @ TRAIN " + number + ": DISPATCHED @ @ @");
            dispatched = true;
            dispatchSequence = dispatchCounter.getAndIncrement();
            isOnHold = false;
            break;
        }
    }

    public int getNumber() { return number; }
    public int getInbound() { return inbound; }
    public int getOutbound() { return outbound; }
    public boolean isDispatched() { return dispatched; }
    public boolean isOnHold() { return isOnHold || onPermanentHold; }
    public int getDispatchSequence() { return dispatchSequence; }
    public List<Integer> getSwitches() { return requiredSwitches; }

    private void waitSomeTime() {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void simulateMovement() {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
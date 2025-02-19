/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: Train
Description: a description of what the class provides would normally be expected.
*/

package org.railroad;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Train implements Runnable {
    private int number;
    private int inbound;
    private int outbound;
    private List<Integer> requiredSwitches;
    private SwitchManager switchManager;
    private boolean onPermanentHold = false;

    public Train(int number, int inbound, int outbound, List<Integer> requiredSwitches, SwitchManager switchManager) {
        this.number = number;
        this.inbound = inbound;
        this.outbound = outbound;
        this.requiredSwitches = requiredSwitches;
        this.switchManager = switchManager;
        if (requiredSwitches == null) {
            this.onPermanentHold = true;
        }
    }

    @Override
    public void run() {
        if (onPermanentHold) {
            System.out.println("*************\nTrain " + number + " is on permanent hold and cannot be dispatched.\n*************");
            return;
        }

        while (true) {

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
            break;
        }
    }

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
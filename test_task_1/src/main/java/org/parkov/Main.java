package org.parkov;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        CommonResource commonResource = new CommonResource();

        for (int i = 0; i < 5; i++) {
            commonResource.cashRegisters.add(new CashRegister(i));
        }

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(new CashRegisterProcessor(commonResource));
            t.setName("Thread " + i);
            t.start();
        }
    }
}

class CommonResource {
    ArrayList<CashRegister> cashRegisters = new ArrayList<>();
}

class CashRegister {
    private final int id;
    private final ReentrantLock locker = new ReentrantLock();

    public CashRegister(int id) {
        this.id = id;
    }

    public boolean tryProcess() throws InterruptedException {
        if (locker.tryLock()) {
            try {
                Thread.sleep(1000);
                System.out.printf("CashRegister %d processed by %s\n", id, Thread.currentThread().getName());
                return true;
            } finally {
                locker.unlock();
            }
        }
        return false;
    }
}

class CashRegisterProcessor implements Runnable {
    private final CommonResource commonResource;

    public CashRegisterProcessor(CommonResource commonResource) {
        this.commonResource = commonResource;
    }

    @Override
    public void run() {
        for (int i = 0; i < commonResource.cashRegisters.size(); i++) {
            CashRegister register = commonResource.cashRegisters.get(i);
            try {
                if (register.tryProcess()) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}

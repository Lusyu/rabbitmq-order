package com.exam.lpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ABCDsort {
    private static int sum=1;
    private static Lock lock=new ReentrantLock();
    private static final Condition conditionA=lock.newCondition();
    private static final Condition conditionB=lock.newCondition();
    private static final Condition conditionC=lock.newCondition();
    private static final CountDownLatch countDownLatch=new CountDownLatch(2);

    public static void main(String[] args) {
        System.out.println(binarySearch(new Integer[]{2,4,55,22,11},1));
        long count = countDownLatch.getCount();
        new Thread(()->{
            for (int i=0;i<count;i++){
                try {
                    printA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();
        new Thread(()->{
            for (int i=0;i<count;i++){
                try {
                    printB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"B").start();
        new Thread(()->{
            for (int i=0;i<count;i++){
                try {
                    printC();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"C").start();
    }

    public static void printA() throws InterruptedException {
        lock.lock();
        System.out.println("唤醒A");
        if (sum!=1){
            conditionA.await();
        }
        System.out.print("["+Thread.currentThread().getName());
        sum=2;
        conditionB.signal();
        lock.unlock();
    }

    public static void printB() throws InterruptedException {
        lock.lock();
        System.out.println("唤醒B");
        if (sum!=2){
            conditionB.await();
        }
        System.out.print(Thread.currentThread().getName());
        sum=3;
        conditionC.signal();
        lock.unlock();

    }

    public static void printC() throws InterruptedException {
        lock.lock();
        System.out.println("唤醒C");
        if (sum!=3){
            conditionC.await();
        }
        System.out.print(Thread.currentThread().getName()+"]");
        sum=1;
        conditionA.signal();
        countDownLatch.countDown();
        lock.unlock();
    }

    public static Integer binarySearch(Integer[] arr,Integer current){
        int start=0;
        int end=arr.length-1;
        int index=-1;
        while (end>=start){
            int mid=(start+end)/2;
            if (arr[mid]>current){
                end=mid-1;
            }else if (arr[mid]<current){
                start=mid+1;
            }else if (arr[mid]==current){
                index=mid;
                break;
            }
        }
        return index;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;
import sun.reflect.generics.tree.ArrayTypeSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

public class MergeSort extends Task<int[]> {

    public final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {
        System.out.println("started running on "+this.handler +": " + Arrays.toString(array));
        int[] tmp = new int[array.length];
            mergeSort(array, tmp,  0,  array.length - 1);
    }

    private static void merge(int[ ] a, int[ ] tmp, int left, int right, int rightEnd )
    {
        if(a.length == 1) {
            tmp = a;
            return;
        }
        int leftEnd = right - 1;
        int k = left;
        int num = rightEnd - left + 1;

        while(left <= leftEnd && right <= rightEnd)
            if(a[left]<=a[right])
                tmp[k++] = a[left++];
            else
                tmp[k++] = a[right++];

        while(left <= leftEnd)    // Copy rest of first half
            tmp[k++] = a[left++];

        while(right <= rightEnd)  // Copy rest of right half
            tmp[k++] = a[right++];

        // Copy tmp back
        for(int i = 0; i < num; i++, rightEnd--)
            a[rightEnd] = tmp[rightEnd];
    }


    private void mergeSort(int[] a, int[] tmp, int left, int right)
    {
        if(a.length == 1) {
            this.complete(a);
            return;
        }
        if( left < right )
        {
            int center = (left + right) / 2;
            Task lms = new MergeSort(Arrays.copyOfRange(a,left, center+1));
            Task rms = new MergeSort(Arrays.copyOfRange(a,center+1, right+1));
            Task[] tarr = new Task[]{lms,rms};
            ArrayList<Task<int[]>> anotherList = new ArrayList<>();
            anotherList.add(lms);
            anotherList.add(rms);
            this.whenResolved((Collection<? extends Task<?>>) anotherList,()->{
                int[] l = anotherList.get(0).getResult().get();
                int[] r = anotherList.get(1).getResult().get();
                int[] j;
                if(l==null) j = r;
                else if (r==null) j=l;
                else {
                    j = new int[l.length + r.length];
                    System.arraycopy(l, 0, j, 0, l.length);
                    System.arraycopy(r, 0, j, l.length, r.length);
                }

                merge(j, tmp, left, center + 1, right);
                this.complete(j);
            });
            this.spawn(tarr);
//            mergeSort(a, tmp, left, center);
//            mergeSort(a, tmp, center + 1, right);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(1);
        int n = 40000; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();
        //array = new int[]{12,11,10,9,8,7,6,5,4,3,2,1,0};

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
            int[] arr = task.getResult().get();
            System.out.println("Merge Sort Finished! Result: "+Arrays.toString(task.getResult().get()));
            System.out.println("Checking");
            boolean failed = false;
            for(int j=0; j<arr.length-1;j++) {
                if(arr[j]> arr[j+1])  {
                    System.out.println("Check Failed");
                    failed = true;
                    break;
                }
            }
            if(!failed) System.out.println("Success");
            l.countDown();
        });

        l.await();
        pool.shutdown();
        }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    private boolean checkSorted(int[] array) {
        for(int j=0; j<array.length-1;j++) {
            if(array[j]> array[j+1]) return false;
        }
        return true;
    }
}

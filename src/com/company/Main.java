package com.company;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime();

        ExternalSort();

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
    }

    private static void ExternalSort() throws IOException {
        var result = ExternalSort.sortInBatch(new File("ip_addresses"));
        System.out.println("RESULT:" + result);
    }
}

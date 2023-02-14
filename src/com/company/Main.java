package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime();

        ExternalSort();

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
    }

    public static void getLineCountByReader() throws IOException {
        try (var lnr = new LineNumberReader(new BufferedReader(new FileReader("result1.txt")))) {
            while (lnr.readLine() != null) ;
            System.out.println("RESULT:" + lnr.getLineNumber());
        }
    }

    private static void ExternalSort() throws IOException {
        var result = ExternalSort.sortInBatch(new File("result1.txt"));
        System.out.println("RESULT:" + result);
    }

    private static void generate() {
        for(int i = 0; i < 5000; i++) {
            int r1 = (int)Math.floor(Math.random() * 255 + 1);
            int r2 = (int)Math.floor(Math.random() * 255 + 1);
            int r3 = (int)Math.floor(Math.random() * 255 + 1);
            int r4 = (int)Math.floor(Math.random() * 255 + 1);
            String separator = ".";
            StringBuilder temp  =  new StringBuilder();
            var tempString = temp.append(r1).append(separator)
                .append(r2).append(separator)
                .append(r3).append(separator)
                .append(r4);

            try(FileWriter fw = new FileWriter("result.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                for(int j = 0; j < 10000; j++) {
                    out.println(tempString);
                }
                for(int j = 0; j < 10000; j++) {
                    r1 = (int)Math.floor(Math.random() * 255 + 1);
                    r2 = (int)Math.floor(Math.random() * 255 + 1);
                    r3 = (int)Math.floor(Math.random() * 255 + 1);
                    r4 = (int)Math.floor(Math.random() * 255 + 1);
                    temp  =  new StringBuilder();
                    tempString = temp.append(r1).append(separator)
                        .append(r2).append(separator)
                        .append(r3).append(separator)
                        .append(r4);

                    out.println(tempString);
                }
                System.out.println("done by:" + i);
            } catch (IOException e) {
            }
        }
    }
}

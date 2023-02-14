package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static com.company.Utils.*;

/**
 * Goal: offer a generic external-memory for counting unique IP addresses program in Java.
 *
 * It must be : - hackable (easy to adapt) - scalable to large files - sensibly
 * efficient.
 *
 * This software is in the public domain.
 *
 * For very large files, you might want to use an appropriate flag to allocate
 * more memory to the Java VM: java -Xms2G
 *
 * https://en.wikipedia.org/wiki/External_sorting
 */

public class ExternalSort {

    public static long sortInBatch(File file) throws IOException {
        return sortInBatch(file, Charset.defaultCharset());
    }

    public static long sortInBatch(File file, Charset cs)
        throws IOException {
        BufferedReader fbr = new BufferedReader(new InputStreamReader(
            new FileInputStream(file), cs));
        return sortInBatch(fbr, file.length(), defaultcomparator, DEFAULTMAXTEMPFILES,
            estimateAvailableMemory(), cs, null, 0);
    }

    public static long sortInBatch(final BufferedReader fbr,
                                         final long datalength, final Comparator<String> cmp,
                                         final int maxtmpfiles, long maxMemory, final Charset cs,
                                         final File tmpdirectory, final int numHeader)
        throws IOException {
        List<File> files = new ArrayList<>();
        long blocksize = estimateBestSizeOfBlocks(datalength,
            maxtmpfiles, maxMemory);// in
        // bytes

        try {
            List<String> tmplist = new ArrayList<>();
            String line = "";
            try {
                int counter = 0;
                while (line != null) {
                    long currentblocksize = 0;// in bytes
                    while ((currentblocksize < blocksize)
                        && ((line = fbr.readLine()) != null)) {
                        // as long as you have enough
                        // memory
                        if (counter < numHeader) {
                            counter++;
                            continue;
                        }
                        tmplist.add(line);
                        currentblocksize += StringSizeEstimator
                            .estimatedSizeOf(line);
                    }
                    files.add(sortAndSave(tmplist, cmp, cs,
                        tmpdirectory));
                    tmplist.clear();
                }
            } catch (EOFException oef) {
                if (tmplist.size() > 0) {
                    files.add(sortAndSave(tmplist, cmp, cs,
                        tmpdirectory));
                    tmplist.clear();
                }
            }
        } finally {
            fbr.close();
        }
        return mergeSortedFiles(files);
    }

    public static File sortAndSave(List<String> tmplist,
                                   Comparator<String> cmp, Charset cs, File tmpdirectory) throws IOException {
        tmplist = tmplist.parallelStream().sorted(cmp).collect(Collectors.toCollection(ArrayList<String>::new));

        File newtmpfile = File.createTempFile("sortInBatch",
            "flatfile", tmpdirectory);
        newtmpfile.deleteOnExit();
        OutputStream out = new FileOutputStream(newtmpfile);
        try (BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
            out, cs))) {
            String lastLine = null;
            Iterator<String> i = tmplist.iterator();
            if(i.hasNext()) {
                lastLine = i.next();
                fbw.write(lastLine);
                fbw.newLine();
            }
            while (i.hasNext()) {
                String r = i.next();
                // Skip duplicate lines
                if (cmp.compare(r, lastLine) != 0) {
                    fbw.write(r);
                    fbw.newLine();
                    lastLine = r;
                }
            }
        }
        return newtmpfile;
    }

    private static long mergeSortedFiles(List<File> files)
        throws IOException {
        return mergeSortedFiles(files, defaultcomparator, Charset.defaultCharset());
    }

    private static long mergeSortedFiles(List<File> files,
                                         final Comparator<String> cmp, Charset cs) throws IOException {
        ArrayList<IOStringStack> bfbs = new ArrayList<>();
        for (File f : files) {
            InputStream in = new FileInputStream(f);
            BufferedReader br;

            br = new BufferedReader(new InputStreamReader(
                in, cs));

            BinaryFileBuffer bfb = new BinaryFileBuffer(br);
            bfbs.add(bfb);
        }
        long rowcounter = mergeSortedFiles(cmp, bfbs);
        for (File f : files) {
            f.delete();
        }
        return rowcounter;
    }

    private static long mergeSortedFiles(final Comparator<String> cmp, List<IOStringStack> buffers) throws IOException {
        PriorityQueue<IOStringStack> pq = new PriorityQueue<>(11,
            (i, j) -> cmp.compare(i.peek(), j.peek()));
        for (IOStringStack bfb : buffers) {
            if (!bfb.empty()) {
                pq.add(bfb);
            }
        }
        long rowcounter = 0;
        try {
            String lastLine = null;
            if(pq.size() > 0) {
                IOStringStack bfb = pq.poll();
                lastLine = bfb.pop();
                ++rowcounter;
                if (bfb.empty()) {
                    bfb.close();
                } else {
                    pq.add(bfb); // add it back
                }
            }
            while (pq.size() > 0) {
                IOStringStack bfb = pq.poll();
                String r = bfb.pop();
                // Skip duplicate lines
                if  (cmp.compare(r, lastLine) != 0) {
                    lastLine = r;
                    ++rowcounter;
                }
                if (bfb.empty()) {
                    bfb.close();
                } else {
                    pq.add(bfb); // add it back
                }
            }
        } finally {
            for (IOStringStack bfb : pq) {
                bfb.close();
            }
        }
        return rowcounter;
    }

    /**
     * default comparator between strings.
     */
    public static Comparator<String> defaultcomparator = String::compareTo;

    /**
     * Default maximal number of temporary files allowed.
     */
    public static final int DEFAULTMAXTEMPFILES = 1024;

}
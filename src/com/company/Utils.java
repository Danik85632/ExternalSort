package com.company;

public class Utils {
    /**
     * This method calls the garbage collector and then returns the free
     * memory. This avoids problems with applications where the GC hasn't
     * reclaimed memory and reports no available memory.
     *
     * @return available memory
     */
    public static long estimateAvailableMemory() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        long presFreeMemory = r.maxMemory() - allocatedMemory;
        return presFreeMemory;
    }

    /**
     * we divide the file into small blocks. If the blocks are too small, we
     * shall create too many temporary files. If they are too big, we shall
     * be using too much memory.
     *
     * @param sizeoffile how much data (in bytes) can we expect
     * @param maxtmpfiles how many temporary files can we create (e.g., 1024)
     * @param maxMemory Maximum memory to use (in bytes)
     * @return the estimate
     */
    public static long estimateBestSizeOfBlocks(final long sizeoffile,
                                                final int maxtmpfiles, final long maxMemory) {
        long blocksize = sizeoffile / maxtmpfiles
            + (sizeoffile % maxtmpfiles == 0 ? 0 : 1);
        if (blocksize < maxMemory / 2) {
            blocksize = maxMemory / 2;
        }
        return blocksize;
    }
}

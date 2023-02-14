package com.company;

public final class StringSizeEstimator {

    private static int OBJ_OVERHEAD;

    private StringSizeEstimator() {
    }
    /**
     * If exists and is 32 bit then we assume a 32bit JVM.
     * The sizes below are a bit rough as we don't take into account
     * advanced JVM options such as compressed oops
     * however if our calculation is not accurate it'll be a bit over
     * so there is no danger of an out of memory error because of this.
     * */
    static {

        boolean IS_64_BIT_JVM = true;
        String arch = System.getProperty("sun.arch.data.model");
        if (arch != null) {
            if (arch.contains("32")) {
                IS_64_BIT_JVM = false;
            }
        }
        int OBJ_HEADER = IS_64_BIT_JVM ? 16 : 8;
        int ARR_HEADER = IS_64_BIT_JVM ? 24 : 12;
        int OBJ_REF = IS_64_BIT_JVM ? 8 : 4;
        int INT_FIELDS = 12;
        OBJ_OVERHEAD = OBJ_HEADER + INT_FIELDS + OBJ_REF + ARR_HEADER;

    }

    public static long estimatedSizeOf(String s) {
        return (s.length() * 2) + OBJ_OVERHEAD;
    }
}
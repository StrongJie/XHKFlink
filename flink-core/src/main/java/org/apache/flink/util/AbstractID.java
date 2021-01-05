package org.apache.flink.util;

import org.apache.flink.annotation.PublicEvolving;

import java.io.Serializable;
import java.util.Random;

@PublicEvolving
public class AbstractID implements Comparable<AbstractID>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Random RND = new Random();

    /** The size of a long in bytes. */
    private static final int SIZE_OF_LONG = 8;

    /** The size of the ID in byte. */
    public static final int SIZE = 2 * SIZE_OF_LONG;

    // ----------------------------------------------

    /** The upper part of the actual ID. */
    protected final long upperPart;

    /** The lower part of the actual ID. */
    protected final long lowerPart;

    /** The memoized value returned by toString(). */
    private transient String hexString;

    // ----------------------------------------------

    /**
     * Constructs a new ID with a specific bytes value.
     */
    public AbstractID(byte[] bytes) {
        if (bytes == null || bytes.length != SIZE) {
            throw new IllegalArgumentException("Argument bytes must by an array of" + SIZE + " bytes");
        }

        this.lowerPart = byteArrayToLong(bytes, 0);
        this.upperPart = byteArrayToLong(bytes, SIZE_OF_LONG);
    }

    /**
     * Constructs a new abstract ID.
     *
     * @param lowerPart the lower bytes of the ID
     * @param upperPart the higher bytes of the ID
     */
    public AbstractID(long lowerPart, long upperPart) {
        this.lowerPart = lowerPart;
        this.upperPart = upperPart;
    }

    /**
     * Copy constructor: Creates a new abstract ID from the given one.
     *
     * @param id the abstract ID to copy
     */
    public AbstractID(AbstractID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        this.lowerPart = id.lowerPart;
        this.upperPart = id.upperPart;
     }

    /**
     * Constructs a new random ID from a uniform distribution.
     */
    public AbstractID() {
        this.lowerPart = RND.nextLong();
        this.upperPart = RND.nextLong();
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Gets the lower 64 bits of the ID.
     *
     * @return The lower 64 bits of the ID.
     */
    public long getLowerPart() {
        return lowerPart;
    }

    /**
     * Gets the upper 64 bits of the ID.
     *
     * @return The upper 64 bits of the ID.
     */
    public long getUpperPart() {
        return upperPart;
    }






    // --------------------------------------------------------------------------------------------
    //  Conversion Utilities
    // --------------------------------------------------------------------------------------------

    /**
     * Converts the given byte array to a long.
     *
     * @param ba the byte array to be converted
     * @param offset the offset indicating at which byte inside the array the conversion shall begin
     * @return the long variable
     */
    private static long byteArrayToLong(byte[] ba, int offset) {
        long l = 0;

        for (int i = 0; i < SIZE_OF_LONG; ++i) {
            l |= (ba[offset + SIZE_OF_LONG - 1 - i] & 0xffL) << (i << 3);
        }

        return l;
    }

    /**
     * Converts a long to a byte array.
     *
     * @param l the long variable to be converted
     * @param ba the byte array to store the result the of the conversion
     * @param offset offset indicating at what position inside the byte array the result of the conversion shall be stored
     */
    private static void longToByteArray(long l, byte[] ba, int offset) {
        for (int i = 0; i < SIZE_OF_LONG; ++i) {
            final int shift = i << 3; // i * 8
            ba[offset + SIZE_OF_LONG - 1 - i] = (byte) ((l & (0xffL << shift)) >>> shift);
        }
    }



}
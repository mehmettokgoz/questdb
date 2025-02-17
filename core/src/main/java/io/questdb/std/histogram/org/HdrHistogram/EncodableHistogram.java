/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package io.questdb.std.histogram.org.HdrHistogram;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

/**
 * A base class for all encodable (and decodable) histogram classes. Log readers and writers
 * will generally use this base class to provide common log processing across the integer value
 * based AbstractHistogram subclasses and the double value based DoubleHistogram class.
 *
 */
public abstract class EncodableHistogram {

    public abstract int getNeededByteBufferCapacity();

    public abstract int encodeIntoCompressedByteBuffer(final ByteBuffer targetBuffer, int compressionLevel);

    public abstract long getStartTimeStamp();

    public abstract void setStartTimeStamp(long startTimeStamp);

    public abstract long getEndTimeStamp();

    public abstract void setEndTimeStamp(long endTimestamp);

    public abstract String getTag();

    public abstract void setTag(String tag);

    public abstract double getMaxValueAsDouble();

    /**
     * Decode a {@link EncodableHistogram} from a compressed byte buffer. Will return either a
     * { org.HdrHistogram.Histogram} or { org.HdrHistogram.DoubleHistogram} depending
     * on the format found in the supplied buffer.
     *
     * @param buffer The input buffer to decode from.
     * @param minBarForHighestTrackableValue A lower bound either on the highestTrackableValue of
     *                                       the created Histogram, or on the HighestToLowestValueRatio
     *                                       of the created DoubleHistogram.
     * @return The decoded { org.HdrHistogram.Histogram} or { org.HdrHistogram.DoubleHistogram}
     * @throws DataFormatException on errors in decoding the buffer compression.
     */
    static EncodableHistogram decodeFromCompressedByteBuffer(
            ByteBuffer buffer,
            final long minBarForHighestTrackableValue) throws DataFormatException {
        // Peek iun buffer to see the cookie:
        int cookie = buffer.getInt(buffer.position());
        if (DoubleHistogram.isDoubleHistogramCookie(cookie)) {
            return DoubleHistogram.decodeFromCompressedByteBuffer(buffer, minBarForHighestTrackableValue);
        } else {
            return Histogram.decodeFromCompressedByteBuffer(buffer, minBarForHighestTrackableValue);
        }
    }
}

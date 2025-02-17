/**
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package io.questdb.std.histogram.org.HdrHistogram;

import java.util.Iterator;

/**
 * Used for iterating through histogram values in linear steps. The iteration is
 * performed in steps of <i>valueUnitsPerBucket</i> in size, terminating when all recorded histogram
 * values are exhausted. Note that each iteration "bucket" includes values up to and including
 * the next bucket boundary value.
 */
public class LinearIterator extends AbstractHistogramIterator implements Iterator<HistogramIterationValue> {
    private long valueUnitsPerBucket;
    private long currentStepHighestValueReportingLevel;
    private long currentStepLowestValueReportingLevel;

    /**
     * Reset iterator for re-use in a fresh iteration over the same histogram data set.
     * @param valueUnitsPerBucket The size (in value units) of each bucket iteration.
     */
    public void reset(final long valueUnitsPerBucket) {
        reset(histogram, valueUnitsPerBucket);
    }

    private void reset(final AbstractHistogram histogram, final long valueUnitsPerBucket) {
        super.resetIterator(histogram);
        this.valueUnitsPerBucket = valueUnitsPerBucket;
        this.currentStepHighestValueReportingLevel = valueUnitsPerBucket - 1;
        this.currentStepLowestValueReportingLevel = histogram.lowestEquivalentValue(currentStepHighestValueReportingLevel);
    }

    /**
     * @param histogram The histogram this iterator will operate on
     * @param valueUnitsPerBucket The size (in value units) of each bucket iteration.
     */
    public LinearIterator(final AbstractHistogram histogram, final long valueUnitsPerBucket) {
        reset(histogram, valueUnitsPerBucket);
    }

    @Override
    public boolean hasNext() {
        if (super.hasNext()) {
            return true;
        }
        // If the next iteration will not move to the next sub bucket index (which is empty if
        // if we reached this point), then we are not yet done iterating (we want to iterate
        // until we are no longer on a value that has a count, rather than util we first reach
        // the last value that has a count. The difference is subtle but important)...
        // When this is called, we're about to begin the "next" iteration, so
        // currentStepHighestValueReportingLevel has already been incremented, and we use it
        // without incrementing its value.
        return (currentStepHighestValueReportingLevel < nextValueAtIndex);
    }

    @Override
    void incrementIterationLevel() {
        currentStepHighestValueReportingLevel += valueUnitsPerBucket;
        currentStepLowestValueReportingLevel = histogram.lowestEquivalentValue(currentStepHighestValueReportingLevel);
    }

    @Override
    long getValueIteratedTo() {
        return currentStepHighestValueReportingLevel;
    }

    @Override
    boolean reachedIterationLevel() {
        return ((currentValueAtIndex >= currentStepLowestValueReportingLevel) ||
                (currentIndex >= histogram.countsArrayLength - 1)) ;
    }
}

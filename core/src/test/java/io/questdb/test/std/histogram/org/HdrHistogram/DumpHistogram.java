package io.questdb.test.std.histogram.org.HdrHistogram;

import io.questdb.std.histogram.org.HdrHistogram.*;

import java.nio.ByteBuffer;

public class DumpHistogram {
    static String hist1 = "DHISTwAAAAMAAAAAAAAABByEkxQAAABBeNqTaZkszMDAoMwAAcxQmhFC2f+3OwBhHZdgecrONJWDpZuF5zozSzMTUz8bVzcL03VmjkZGlnqWRkY2AGoTC78=";
    static String hist2 = "DHISTwAAAAMAAAAAAAAAAhyEkxQAAAAieNqTaZkszMDAwMIAAcxQmhFCyf+32wBhMa0VYAIAUp8EHA==";

    static void dumpHistogram(String histString) throws Exception {
        final ByteBuffer buffer = ByteBuffer.wrap(Base64Helper.parseBase64Binary(histString));
        DoubleHistogram histogram = DoubleHistogram.decodeFromCompressedByteBuffer(buffer, 0);
        dumpHistogram(histogram);
    }

    static void dumpHistogram(DoubleHistogram histogram) {
        AbstractHistogram iHist = histogram.integerValuesHistogram;
        System.out.format("digits = %d, min = %12.12g, max = %12.12g\n", histogram.getNumberOfSignificantValueDigits(), histogram.getMinNonZeroValue(), histogram.getMaxValue());
        System.out.format("lowest = %12.12g, highest = %12.12g\n", histogram.getCurrentLowestTrackableNonZeroValue(), histogram.getCurrentHighestTrackableValue());
        System.out.format("lowest(i) = %d, highest(i) = %d\n",
                iHist.countsArrayIndex((long)(histogram.getCurrentLowestTrackableNonZeroValue() * histogram.getDoubleToIntegerValueConversionRatio())),
                iHist.countsArrayIndex((long)(histogram.getCurrentHighestTrackableValue() * histogram.getDoubleToIntegerValueConversionRatio())));
        System.out.format("length = %d, imin = %d, imax = %d\n", histogram.integerValuesHistogram.countsArrayLength,
                histogram.integerValuesHistogram.getMinNonZeroValue(),
                histogram.integerValuesHistogram.getMaxValue());
        System.out.format("index 4644 here translates to %12.12g\n", iHist.valueFromIndex(4644) * histogram.getIntegerToDoubleValueConversionRatio());
        for (DoubleHistogramIterationValue val : histogram.recordedValues()) {
            HistogramIterationValue iVal = val.getIntegerHistogramIterationValue();
            int index = iHist.countsArrayIndex(iVal.getValueIteratedTo());
            System.out.format("[%d] %12.12g, %12.12g - %12.12g : %d\n",index,
                    val.getValueIteratedTo(),
                    histogram.lowestEquivalentValue(val.getValueIteratedTo()),
                    histogram.highestEquivalentValue(val.getValueIteratedTo()),
                    val.getCountAtValueIteratedTo());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("hist1:");
        dumpHistogram(hist1);
        System.out.println("hist2:");
        dumpHistogram(hist2);

        DoubleHistogram h = new DoubleHistogram(3);
        h.recordValue(0.000999451);
        System.out.println("h:");
        dumpHistogram(h);

        h.recordValue(h.integerValuesHistogram.valueFromIndex(4644) * h.getIntegerToDoubleValueConversionRatio());
        System.out.println("h':");
        dumpHistogram(h);

        h.recordValue(0.0119934);
        System.out.println("h':");
        dumpHistogram(h);
    }
}

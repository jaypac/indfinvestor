package com.indfinvestor.app.indexdata.sink;

import com.indfinvestor.app.indexdata.IndexDataFetchParams;

import java.io.InputStream;
import java.util.Collection;

public interface IndexDataSink {

    SinkType getType();

    void write(IndexDataFetchParams params, InputStream is);

    default void writeAll(IndexDataFetchParams params, Collection<InputStream> inputStreams) {
        for (InputStream is : inputStreams) {
            write(params, is);
        }
    }
}

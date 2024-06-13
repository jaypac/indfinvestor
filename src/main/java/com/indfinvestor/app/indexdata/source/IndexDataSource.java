package com.indfinvestor.app.indexdata.source;

import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.sink.IndexDataSink;

import java.time.LocalDate;

public interface IndexDataSource {

    IndexSourceType getType();

    boolean hasNext();

    void fetch(IndexDataFetchParams indexDataFetchParams, IndexDataSink indexDataSink);
}

package com.indfinvestor.app.indexdata;

import com.indfinvestor.app.indexdata.sink.SinkType;
import com.indfinvestor.app.indexdata.source.IndexSourceType;

import java.time.LocalDate;

public record IndexDataFetchParams(
        String uuid,
        String tradingName,
        String indexName,
        IndexSourceType indexSourceType,
        SinkType sinkType,
        LocalDate from,
        LocalDate to
) {
}

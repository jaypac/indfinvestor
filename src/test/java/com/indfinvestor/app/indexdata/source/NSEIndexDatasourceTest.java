package com.indfinvestor.app.indexdata.source;

import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.IndexDataRetriever;
import com.indfinvestor.app.indexdata.sink.SinkType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@SpringBootTest
public class NSEIndexDatasourceTest {

    @Autowired
    private IndexDataRetriever indexDataRetriever;


    @Test
    public void nseIndexDatasourceTest() {

        var tradingName = "NIFTY 100";
        var indexName = "NIFTY 100";
        var uuid = UUID.randomUUID().toString();
        var indexSourceType = IndexSourceType.NSE;
        var sinkType = SinkType.FILE;
        var fromDate = LocalDate.of(1990, Month.JANUARY, 1);
        var toDate = LocalDate.now();
        var params = new IndexDataFetchParams(uuid, tradingName, indexName, indexSourceType, sinkType, fromDate, toDate);
        indexDataRetriever.process(params);
    }
}

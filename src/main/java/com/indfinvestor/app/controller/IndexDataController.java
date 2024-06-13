package com.indfinvestor.app.controller;


import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.IndexDataRetriever;
import com.indfinvestor.app.indexdata.sink.SinkType;
import com.indfinvestor.app.indexdata.source.IndexSourceType;
import com.indfinvestor.app.indexprocessor.service.IndexDataProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@RestController
public class IndexDataController {

    private final IndexDataRetriever indexDataRetriever;
    private final IndexDataProcessor indexDataProcessor;

    public IndexDataController(IndexDataRetriever indexDataRetriever, IndexDataProcessor indexDataProcessor) {
        this.indexDataRetriever = indexDataRetriever;
        this.indexDataProcessor = indexDataProcessor;
    }

    @GetMapping("/get")
    public String getData() {

        String uuid = UUID.randomUUID().toString();
        var fromDate = LocalDate.of(1990, Month.JANUARY, 1);
        var toDate = LocalDate.of(2024, Month.MAY, 31);
        var indexFetchParams = new IndexDataFetchParams(uuid, "NIFTY 50", IndexSourceType.YAHOO, SinkType.FILE, fromDate, toDate);

        indexDataRetriever.process(indexFetchParams);

        return "Done...";

    }

    @GetMapping("/process")
    public String process() {

        indexDataProcessor.doExecute();

        return "Done...";

    }
}

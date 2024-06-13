package com.indfinvestor.app.controller;


import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.IndexDataRetriever;
import com.indfinvestor.app.indexdata.sink.SinkType;
import com.indfinvestor.app.indexdata.source.IndexSourceType;
import com.indfinvestor.app.indexprocessor.service.IndexDataProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
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

//        var list = List.of("NIFTY 100", "NIFTY ALPHA 50", "NIFTY ALPHA LOW-VOLATILITY 30",
//                "NIFTY ALPHA QUALITY LOW-VOLATILITY 30", "NIFTY ALPHA QUALITY VALUE LOW-VOLATILITY 30");

//        var list = List.of("NIFTY LARGEMIDCAP 250","NIFTY LOW VOLATILITY 50","NIFTY MIDCAP 50","NIFTY MIDCAP 100",
//                "NIFTY MIDCAP 150","NIFTY NEXT 50","NIFTY50 VALUE 20","NIFTY100 LOW VOLATILITY 30",
//                "NIFTY100 QUALITY 30","NIFTY200 ALPHA 30","NIFTY200 MOMENTUM 30","NIFTY200 QUALITY 30",
//                "NIFTY200 VALUE 30");

        var list = List.of("BSE Low Volatility");

        //String pattern = "dd MMM yyyy";
        String pattern = "dd-MMM-yy";
        for (String filename : list) {
            String fileName = "C:\\Jubin\\temp\\historical_data\\" + filename + ".csv";
            File csvFile = new File(fileName);
            indexDataProcessor.doExecute(csvFile, pattern);

        }


        return "Done...";

    }
}

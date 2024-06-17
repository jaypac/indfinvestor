package com.indfinvestor.app.controller;


import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.IndexDataRetriever;
import com.indfinvestor.app.indexdata.sink.SinkType;
import com.indfinvestor.app.indexdata.source.IndexSourceType;
import com.indfinvestor.app.indexprocessor.service.IndexDataProcessor;
import com.indfinvestor.app.navprocessor.service.NavDataProcessor;
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
    private final NavDataProcessor navDataProcessor;

    public IndexDataController(IndexDataRetriever indexDataRetriever, IndexDataProcessor indexDataProcessor, NavDataProcessor navDataProcessor) {
        this.indexDataRetriever = indexDataRetriever;
        this.indexDataProcessor = indexDataProcessor;
        this.navDataProcessor = navDataProcessor;
    }

    @GetMapping("/get")
    public String getData() {

        String uuid = UUID.randomUUID().toString();
        var fromDate = LocalDate.of(1990, Month.JANUARY, 1);
        var toDate = LocalDate.of(2024, Month.JUNE, 14);
        var indexFetchParams = new IndexDataFetchParams(uuid, "NIFTY 50", IndexSourceType.NSE, SinkType.FILE, fromDate, toDate);

        indexDataRetriever.process(indexFetchParams);

        return "Done...";

    }

    @GetMapping("/process-index")
    public String processIndex() {

        var list = List.of("NIFTY 100", "NIFTY 200", "NIFTY 50", "NIFTY 500", "NIFTY ALPHA 50",
                "NIFTY ALPHA LOW-VOLATILITY 30", "NIFTY ALPHA QUALITY LOW-VOLATILITY 30",
                "NIFTY ALPHA QUALITY VALUE LOW-VOLATILITY 30", "NIFTY BANK", "NIFTY CONSUMER DURABLES",
                "NIFTY DIVIDEND OPPORTUNITIES 50", "NIFTY HEALTHCARE", "NIFTY IT", "NIFTY LARGEMIDCAP 250",
                "NIFTY LOW VOLATILITY 50", "NIFTY MICROCAP 250", "NIFTY MIDCAP 100", "NIFTY MIDCAP 150",
                "NIFTY MIDCAP 50", "NIFTY MIDCAP150 MOMENTUM 50", "NIFTY NEXT 50", "NIFTY OIL & GAS",
                "NIFTY PRIVATE BANK", "NIFTY PSU BANK", "NIFTY SMALLCAP 250", "NIFTY SMALLCAP 50",
                "NIFTY SMALLCAP250 MOMENTUM QUALITY 100", "NIFTY SMALLCAP250 QUALITY 50", "NIFTY TOTAL MARKET",
                "NIFTY100 EQUAL WEIGHT", "NIFTY100 LOW VOLATILITY 30", "NIFTY100 QUALITY 30", "NIFTY200 ALPHA 30",
                "NIFTY200 MOMENTUM 30", "NIFTY200 QUALITY 30", "NIFTY200 VALUE 30", "NIFTY50 EQUAL WEIGHT",
                "NIFTY50 VALUE 20", "NIFTY500 VALUE 50");
        String pattern = "dd MMM yyyy";

/*        var list = List.of("BSE Low Volatility");
        String pattern = "dd-MMM-yy";*/

        for (String filename : list) {
            String fileName = "C:\\Jubin\\temp\\historical_data\\" + filename + ".csv";
            File csvFile = new File(fileName);
            indexDataProcessor.doExecute(csvFile, pattern);
        }

        return "Done...";
    }

    @GetMapping("/process-nav")
    public String processNav() {

        var list = List.of("ABSL","Axis","Canara Robeco","Edelweiss","HDFC",
                "ICICI","Kotak","Mirae Asset","Motilal Oswal","Navi","Nippon","PPFAS","Quant","SBI","UTI");

//        var list = List.of("Mirae Asset");

        for (String filename : list) {
            String fileName = "C:\\Jubin\\temp\\nav_data\\" + filename + ".txt";
            File csvFile = new File(fileName);
            navDataProcessor.doExecute(csvFile);
        }

        return "Done...";
    }
}

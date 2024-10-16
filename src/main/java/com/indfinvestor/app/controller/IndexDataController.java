package com.indfinvestor.app.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.IndexDataRetriever;
import com.indfinvestor.app.indexdata.dto.NseIndexMapping;
import com.indfinvestor.app.indexdata.sink.SinkType;
import com.indfinvestor.app.indexdata.source.IndexSourceType;
import com.indfinvestor.app.indexprocessor.service.IndexDataProcessor;
import com.indfinvestor.app.navprocessor.service.NavDataProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

@RestController
public class IndexDataController {

    private final IndexDataRetriever indexDataRetriever;
    private final IndexDataProcessor indexDataProcessor;
    private final NavDataProcessor navDataProcessor;

    @Value("classpath:indexMapping.json")
    private Resource resourceFile;

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
        var indexFetchParams = new IndexDataFetchParams(uuid, "NIFTY 50", "NIFTY 50", IndexSourceType.NSE, SinkType.FILE, fromDate, toDate);

        indexDataRetriever.process(indexFetchParams);

        return "Done...";

    }

    @GetMapping("/get-nse")
    public String getNseData() {


        var list = List.of("NIFTY 100", "NIFTY 200", "NIFTY 50", "NIFTY 50 ARBITRAGE", "NIFTY 50 FUTURES PR",
                "NIFTY 50 FUTURES TR", "NIFTY 500", "NIFTY ALPHA 50", "NIFTY ALPHA LOW-VOLATILITY 30",
                "NIFTY ALPHA QUALITY LOW-VOLATILITY 30", "NIFTY ALPHA QUALITY VALUE LOW-VOLATILITY 30",
                "NIFTY AUTO", "NIFTY BANK", "NIFTY CAPITAL MARKETS", "NIFTY COMMODITIES", "NIFTY CONSUMER DURABLES",
                "NIFTY CORE HOUSING", "NIFTY CPSE", "NIFTY DIVIDEND OPPORTUNITIES 50", "NIFTY ENERGY", "NIFTY EV & NEW AGE AUTOMOTIVE",
                "NIFTY FINANCIAL SERVICES", "NIFTY FINANCIAL SERVICES 25/50", "NIFTY FINANCIAL SERVICES EX-BANK",
                "NIFTY FMCG", "NIFTY GROWTH SECTORS 15", "NIFTY HEALTHCARE", "NIFTY HIGH BETA 50", "NIFTY HOUSING",
                "NIFTY INDIA CONSUMPTION", "NIFTY INDIA CORPORATE GROUP INDEX - ADITYA BIRLA GROUP",
                "NIFTY INDIA CORPORATE GROUP INDEX - MAHINDRA GROUP", "NIFTY INDIA CORPORATE GROUP INDEX - TATA GROUP",
                "NIFTY INDIA CORPORATE GROUP INDEX - TATA GROUP 25% CAP", "NIFTY INDIA DEFENCE", "NIFTY INDIA DIGITAL",
                "NIFTY INDIA MANUFACTURING", "NIFTY INDIA TOURISM", "NIFTY INFRASTRUCTURE", "NIFTY IPO", "NIFTY IT",
                "NIFTY LARGEMIDCAP 250", "NIFTY LOW VOLATILITY 50", "NIFTY MEDIA", "NIFTY METAL", "NIFTY MICROCAP 250",
                "NIFTY MIDCAP 100", "NIFTY MIDCAP 150", "NIFTY MIDCAP 50", "NIFTY MIDCAP LIQUID 15", "NIFTY MIDCAP SELECT",
                "NIFTY MIDCAP150 MOMENTUM 50", "NIFTY MIDCAP150 QUALITY 50", "NIFTY MIDSMALL FINANCIAL SERVICES",
                "NIFTY MIDSMALL HEALTHCARE", "NIFTY MIDSMALL INDIA CONSUMPTION", "NIFTY MIDSMALL IT & TELECOM",
                "NIFTY MIDSMALLCAP 400", "NIFTY MIDSMALLCAP400 MOMENTUM QUALITY 100", "NIFTY MNC", "NIFTY MOBILITY",
                "NIFTY NEXT 50", "NIFTY NON-CYCLICAL CONSUMER", "NIFTY OIL & GAS", "NIFTY PHARMA",
                "NIFTY PRIVATE BANK", "NIFTY PSE", "NIFTY PSU BANK", "NIFTY QUALITY LOW-VOLATILITY 30",
                "NIFTY REALTY", "NIFTY REITS & INVITS", "NIFTY RURAL", "NIFTY SERVICES SECTOR", "NIFTY SHARIAH 25",
                "NIFTY SMALLCAP 100", "NIFTY SMALLCAP 250", "NIFTY SMALLCAP 50", "NIFTY SMALLCAP250 MOMENTUM QUALITY 100",
                "NIFTY SMALLCAP250 QUALITY 50", "NIFTY SME EMERGE", "NIFTY TOP 10 EQUAL WEIGHT", "NIFTY TOP 15 EQUAL WEIGHT",
                "NIFTY TOTAL MARKET", "NIFTY TRANSPORTATION & LOGISTICS", "NIFTY100 ALPHA 30", "NIFTY100 ENHANCED ESG",
                "NIFTY100 EQUAL WEIGHT", "NIFTY100 ESG", "NIFTY100 ESG SECTOR LEADERS", "NIFTY100 LIQUID 15",
                "NIFTY100 LOW VOLATILITY 30", "NIFTY100 QUALITY 30", "NIFTY200 ALPHA 30", "NIFTY200 MOMENTUM 30",
                "NIFTY200 QUALITY 30", "NIFTY200 VALUE 30", "NIFTY50 DIVIDEND POINTS", "NIFTY50 EQUAL WEIGHT",
                "NIFTY50 PR 1X INVERSE", "NIFTY50 PR 2X LEVERAGE", "NIFTY50 SHARIAH", "NIFTY50 SHARIAH",
                "NIFTY50 TR 1X INVERSE", "NIFTY50 TR 2X LEVERAGE", "NIFTY50 USD", "NIFTY50 VALUE 20", "NIFTY500 EQUAL WEIGHT",
                "NIFTY500 LARGEMIDSMALL EQUAL-CAP WEIGHTED", "NIFTY500 MOMENTUM 50", "NIFTY500 MULTICAP 50:25:25",
                "NIFTY500 MULTICAP INDIA MANUFACTURING 50:30:20", "NIFTY500 MULTICAP INFRASTRUCTURE 50:30:20",
                "NIFTY500 MULTICAP MOMENTUM QUALITY 50", "NIFTY500 SHARIAH", "NIFTY500 VALUE 50");
        String uuid = UUID.randomUUID().toString();
        try {

            var json = new String(Files.readAllBytes(Paths.get(resourceFile.getURI())));
            ObjectMapper objectMapper = new ObjectMapper();
            NseIndexMapping[] indexMappings = objectMapper.readValue(json, NseIndexMapping[].class);

            for (var indexName : list) {
                var indexFetchParams = getIndexDataFetchParams(indexMappings, indexName, uuid);
                indexDataRetriever.process(indexFetchParams);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return "Done...";

    }

    private static IndexDataFetchParams getIndexDataFetchParams(NseIndexMapping[] indexMappings, String indexName, String uuid) {
        var tradingName = "";
        for (NseIndexMapping mapping : indexMappings) {
            if (indexName.equalsIgnoreCase(mapping.getIndexLongName())) {
                tradingName = mapping.getTradingIndexName();
                break;
            }
        }

        if (tradingName.isEmpty()) {
            throw new IllegalArgumentException("No trading name found for index: " + indexName);
        }

        var fromDate = LocalDate.of(1990, Month.JANUARY, 1);
        var toDate = LocalDate.of(2024, Month.OCTOBER, 15);
        var indexFetchParams = new IndexDataFetchParams(uuid, tradingName, indexName, IndexSourceType.NSE, SinkType.FILE, fromDate, toDate);
        return indexFetchParams;
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


        var list2 = List.of("NIFTY 100", "NIFTY 200", "NIFTY 50", "NIFTY 500", "NIFTY ALPHA 50", "NIFTY ALPHA LOW-VOLATILITY 30", "NIFTY ALPHA QUALITY LOW-VOLATILITY 30", "NIFTY ALPHA QUALITY VALUE LOW-VOLATILITY 30", "NIFTY IT", "NIFTY LARGEMIDCAP 250", "NIFTY LOW VOLATILITY 50", "NIFTY MICROCAP 250", "NIFTY MIDCAP 100", "NIFTY MIDCAP 150", "NIFTY MIDCAP 50", "NIFTY MIDCAP SELECT", "NIFTY MIDCAP150 MOMENTUM 50", "NIFTY MIDCAP150 QUALITY 50", "NIFTY MIDSMALLCAP 400", "NIFTY MIDSMALLCAP400 MOMENTUM QUALITY 100", "NIFTY NEXT 50", "NIFTY PHARMA", "NIFTY PRIVATE BANK", "NIFTY QUALITY LOW-VOLATILITY 30", "NIFTY SMALLCAP 100", "NIFTY SMALLCAP 250", "NIFTY SMALLCAP 50", "NIFTY SMALLCAP250 QUALITY 50", "NIFTY100 ALPHA 30", "NIFTY100 EQUAL WEIGHT", "NIFTY100 LOW VOLATILITY 30", "NIFTY100 QUALITY 30", "NIFTY200 ALPHA 30", "NIFTY200 MOMENTUM 30", "NIFTY200 QUALITY 30", "NIFTY200 VALUE 30", "NIFTY50 EQUAL WEIGHT", "NIFTY50 VALUE 20", "NIFTY500 EQUAL WEIGHT", "NIFTY500 LARGEMIDSMALL EQUAL-CAP WEIGHTED", "NIFTY500 MOMENTUM 50", "NIFTY500 MULTICAP 50_25_25", "NIFTY500 MULTICAP INDIA MANUFACTURING 50_30_20", "NIFTY500 MULTICAP INFRASTRUCTURE 50_30_20", "NIFTY500 MULTICAP MOMENTUM QUALITY 50", "NIFTY500 SHARIAH", "NIFTY500 VALUE 50");
        String pattern = "dd MMM yyyy";

/*        var list = List.of("BSE Low Volatility");
        String pattern = "dd-MMM-yy";*/

        for (String filename : list2) {
            String fileName = "C:\\Jubin\\temp\\historical_data\\16-10-2024\\index" + filename + ".csv";
            File csvFile = new File(fileName);
            indexDataProcessor.doExecute(csvFile, pattern);
        }

        return "Done...";
    }

    @GetMapping("/process-nav")
    public String processNav() {

        var list = List.of("ABSL", "Axis", "Benchmark", "Canara Robeco", "Edelweiss", "HDFC",
                "ICICI", "Kotak", "Mirae Asset", "Motilal Oswal", "Navi", "Nippon", "PPFAS", "Quant", "SBI", "UTI");

//        var list = List.of("Mirae Asset");

        for (String filename : list) {
            String fileName = "C:\\Jubin\\temp\\nav_data\\" + filename + ".txt";
            File csvFile = new File(fileName);
            navDataProcessor.doExecute(csvFile);
        }

        return "Done...";
    }
}

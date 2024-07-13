package com.indfinvestor.app.indexdata.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.sink.ConsoleDataSink;
import com.indfinvestor.app.indexdata.sink.IndexDataSink;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class NSEIndexDatasource implements IndexDataSource {

    private static final Logger LOG = LoggerFactory.getLogger(NSEIndexDatasource.class);


    @Override
    public IndexSourceType getType() {
        return IndexSourceType.NSE;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void fetch(IndexDataFetchParams params, IndexDataSink indexDataSink) {

        var formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        var startDate = params.from().format(formatter);
        var endDate = params.to().format(formatter);

        var mapper = new ObjectMapper();
        try {
            var requestSubParams = Map.of("name", params.indexName(), "startDate", startDate, "endDate", endDate, "indexName", params.indexName());
            var requestParams = Map.of("cinfo", mapper.writeValueAsString(requestSubParams));
            var content = mapper.writeValueAsString(requestParams);
            //String content = "{\"name\":\"NIFTY 50\",\"startDate\":\"01-Jan-1990\",\"endDate\":\"31-Dec-2023\"}";

            var customClient = RestClient.builder()
                    .requestFactory(clientHttpRequestFactory())
                    .build();

            var result = customClient.post()
                    .uri("https://www.niftyindices.com/Backpage.aspx/getHistoricaldatatabletoString")
                    .contentType(APPLICATION_JSON)
                    .accept(MediaType.ALL)
                    .body(content)
                    .retrieve()
                    .body(String.class);

            indexDataSink.write(params, new ByteArrayInputStream(result.getBytes()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(HttpClientBuilder.create().setUserAgent("PostmanRuntime/7.36.0").build());
        return clientHttpRequestFactory;
    }

    public static void main(String[] args) {
        NSEIndexDatasource nseIndexDatasource = new NSEIndexDatasource();

        String uuid = UUID.randomUUID().toString();
        String indexName = "NIFTY 50";
        LocalDate fromDate = LocalDate.of(1990, Month.JANUARY, 1);
        LocalDate toDate = LocalDate.now();
        IndexDataFetchParams params = new IndexDataFetchParams(uuid, indexName, null, null, fromDate, toDate);

        nseIndexDatasource.fetch(params, new ConsoleDataSink());
    }
}


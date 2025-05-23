package com.indfinvestor.app.indexdata.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.sink.IndexDataSink;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class TrueDataIndexDatasource implements IndexDataSource {


    @Override
    public IndexSourceType getType() {
        return IndexSourceType.TRUEDATA;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void fetch(IndexDataFetchParams params, IndexDataSink indexDataSink) {

        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var startDate = params.from().format(formatter);
        var endDate = params.to().format(formatter);

        var requestParams = Map.of("name", params.indexName(), "startDate", startDate, "endDate", endDate);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(requestParams);
            //String content = "{\"name\":\"NIFTY 50\",\"startDate\":\"01-Jan-1990\",\"endDate\":\"31-Dec-2023\"}";

            RestClient customClient = RestClient.builder()
                    .requestFactory(clientHttpRequestFactory())
                    .build();

            var symbol = "NSE:NIFTY50-INDEX";
            var result = customClient.get()
                    .uri("https://api-t1.fyers.in/data/history?symbol={symbol}&resolution=1D&date_format=1&range_from=2024-01-01&range_to=2024-01-05&cont_flag=",symbol)
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
}

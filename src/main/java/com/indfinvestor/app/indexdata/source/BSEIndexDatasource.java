package com.indfinvestor.app.indexdata.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.sink.IndexDataSink;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class BSEIndexDatasource implements IndexDataSource {


    @Override
    public IndexSourceType getType() {
        return IndexSourceType.BSE;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void fetch(IndexDataFetchParams params, IndexDataSink indexDataSink) {

        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

            var result = customClient.get()
                    .uri("https://api.bseindia.com/BseIndiaAPI/api/ProduceCSVForDate/w?strIndex=SENSEX&dtFromDate=01/01/1984&dtToDate=31/12/2023")
                    .header("Origin", "https://www.bseindia.com")
                    .header("Referer", "https://www.bseindia.com/")
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

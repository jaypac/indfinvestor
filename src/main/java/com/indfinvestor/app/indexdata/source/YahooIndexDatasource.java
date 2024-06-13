package com.indfinvestor.app.indexdata.source;

import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.sink.IndexDataSink;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;

@Service
public class YahooIndexDatasource implements IndexDataSource {


    @Override
    public IndexSourceType getType() {
        return IndexSourceType.YAHOO;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void fetch(IndexDataFetchParams params, IndexDataSink indexDataSink) {

        var formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        try {

            RestClient customClient = RestClient.builder().requestFactory(clientHttpRequestFactory()).build();

            var result = customClient
                    .get()
                    .uri("https://query1.finance.yahoo.com/v7/finance/download/^NSEI?period1=1674654423&period2=1706190423&interval=1d&events=history&includeAdjustedClose=true")
                    .header("host","query1.finance.yahoo.com")
                    .accept(MediaType.ALL)
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

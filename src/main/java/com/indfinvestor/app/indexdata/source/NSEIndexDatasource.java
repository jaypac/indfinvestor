package com.indfinvestor.app.indexdata.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import com.indfinvestor.app.indexdata.sink.IndexDataSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.net.HttpCookie;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
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
            var requestSubParams = Map.of("name", params.tradingName(), "startDate", startDate, "endDate", endDate, "indexName", params.indexName());
            var requestParams = Map.of("cinfo", mapper.writeValueAsString(requestSubParams));
            var content = mapper.writeValueAsString(requestParams);
            var customClient = RestClient.builder()
                    .requestFactory(clientHttpRequestFactory())
                    .build();

            var cookieTest = customClient
                    .get()
                    .uri("https://www.niftyindices.com/reports/historical-data")
                    .retrieve()
                    .toEntity(String.class);

            var setCookieHeader = cookieTest.getHeaders().get(HttpHeaders.SET_COOKIE).getFirst();
            log.info("cookies: {}", setCookieHeader);
            // Parse using HttpCookie
            List<HttpCookie> cookies = HttpCookie.parse(setCookieHeader);

            // Typically there's just one main cookie here
            HttpCookie cookie = cookies.get(0);

            String name = cookie.getName();     // ASP.NET_SessionId
            String value = cookie.getValue();

            Thread.sleep(5000);

            var result = customClient.post()
                    .uri("https://www.niftyindices.com/Backpage.aspx/getHistoricaldatatabletoString")
                    .contentType(APPLICATION_JSON)
                    .accept(MediaType.ALL)
                    .body(content)
                    .cookie(name, value)
                    .retrieve()
                    .body(String.class);

            assert result != null;
            var formattedResult = result.replace("\"[", "[").replace("]\"", "]").replace("\\\"", "\"");
            indexDataSink.write(params, new ByteArrayInputStream(formattedResult.getBytes()));

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

    }

}


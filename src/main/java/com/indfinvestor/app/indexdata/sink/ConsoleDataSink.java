package com.indfinvestor.app.indexdata.sink;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ConsoleDataSink implements IndexDataSink {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleDataSink.class);

    @Override
    public SinkType getType() {
        return SinkType.CONSOLE;
    }

    @Override
    public void write(IndexDataFetchParams params, InputStream is) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, StandardCharsets.UTF_8);
            String theString = writer.toString();
            String theJson = theString.replace("\"[", "[")
                    .replace("]\"", "]")
                    .replace("\\\"", "\"");
            LOG.info(theJson);

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.findAndRegisterModules();
            NseResponseData result = mapper.readValue(theJson, NseResponseData.class);
            int a = 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}


@Data
@NoArgsConstructor
class NseResponseData {

    @JsonProperty("d")
    private List<NseResponse> data;

}

@Data
@NoArgsConstructor
class NseResponse {

    @JsonProperty("OPEN")
    private String open;

    @JsonProperty("HIGH")
    private String high;

    @JsonProperty("LOW")
    private String low;

    @JsonProperty("CLOSE")
    private String close;

    @JsonProperty("HistoricalDate")
    @JsonFormat(pattern = "dd MMM yyyy", locale = "en")
    private LocalDate date;

}
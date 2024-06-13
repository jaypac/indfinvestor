package com.indfinvestor.app.indexprocessor.transformer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.indfinvestor.app.indexprocessor.model.IndexData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class NSEFileDatasourceTransformer implements DatasourceTransformer {

    @Override
    public List<IndexData> transform(File csvFile, String pattern) {

        var result = new ArrayList<IndexData>();
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); // use first row as header; otherwise defaults are fine
        try (MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class)
                .with(schema)
                .readValues(csvFile)) {

            DateTimeFormatter df = new DateTimeFormatterBuilder()
                    // case insensitive to parse JAN and FEB
                    .parseCaseInsensitive()
                    // add pattern
                    .appendPattern(pattern)
                    // create formatter (use English Locale to parse month names)
                    .toFormatter(Locale.ENGLISH);

            while (it.hasNext()) {
                Map<String, String> rowAsMap = it.next();
                String name = rowAsMap.get("Index Name");
                String date = rowAsMap.get("Date");
                String close = rowAsMap.get("Close");
                var indexData = new IndexData(name, LocalDate.parse(date, df), close);
                result.add(indexData);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return result;
    }

    public static void main(String[] args) {
        var nseFileDatasourceTransformer = new NSEFileDatasourceTransformer();
        File csvFile = new File("C:\\Users\\Jubin\\Downloads\\NIFTY 50_Historical_PR_01061990to11062024.csv");
        nseFileDatasourceTransformer.transform(csvFile,"dd MMM yyyy");
    }
}

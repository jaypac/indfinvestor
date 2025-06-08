package com.indfinvestor.app.index.nse.bulkload;

import com.indfinvestor.app.index.model.dto.IndexCsvData;
import com.indfinvestor.app.index.model.dto.IndexNavDetails;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

public class NseCsvItemReader implements ItemReader<IndexNavDetails> {

    private FlatFileItemReader<IndexCsvData> delegate;
    private boolean dataRead = false;

    public NseCsvItemReader(@Value("#{stepExecutionContext['fileName']}") String fileName) {
        this.delegate = createDelegate(fileName);
    }

    public FlatFileItemReader<IndexCsvData> createDelegate(String filename) {

        FlatFileItemReader<IndexCsvData> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filename));
        reader.setLinesToSkip(1); // Skip header

        // Create line mapper
        DefaultLineMapper<IndexCsvData> lineMapper = new DefaultLineMapper<>();

        // Create tokenizer for delimited files
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("Index Name", "Date", "Open", "High", "Low", "Close");

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new IndexFieldSetMapper());

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Override
    public IndexNavDetails read() throws Exception {
        if (dataRead) {
            return null; // End of data
        }

        IndexNavDetails indexNavDetails = new IndexNavDetails();
        indexNavDetails.setHistoricalNavData(new ArrayList<>());
        IndexCsvData indexCsvData;
        delegate.open(new ExecutionContext());
        while ((indexCsvData = delegate.read()) != null) {

            indexNavDetails.setIndexName(indexCsvData.getName());
            indexNavDetails.getHistoricalNavData().add(indexCsvData);
        }

        dataRead = true;
        return indexNavDetails.getHistoricalNavData().isEmpty() ? null : indexNavDetails;
    }
}

// 3. Custom FieldSetMapper for handling different header names
class IndexFieldSetMapper implements FieldSetMapper<IndexCsvData> {

    @Override
    public IndexCsvData mapFieldSet(FieldSet fieldSet) {
        IndexCsvData data = new IndexCsvData();

        // Map CSV headers to object fields
        data.setName(fieldSet.readString("Index Name"));
        var date = getDate(fieldSet);
        data.setDate(date);
        data.setClose(fieldSet.readBigDecimal("Close"));
        return data;
    }

    private static LocalDate getDate(FieldSet fieldSet) {
        String dateStr = fieldSet.readString("Date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy").withLocale(java.util.Locale.ENGLISH);
        DateTimeFormatter formatter2 =
                DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(java.util.Locale.ENGLISH);
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            date = LocalDate.parse(dateStr, formatter2);
        }

        return date;
    }
}

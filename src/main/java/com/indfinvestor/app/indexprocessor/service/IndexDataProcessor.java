package com.indfinvestor.app.indexprocessor.service;

import com.indfinvestor.app.indexprocessor.model.IndexData;
import com.indfinvestor.app.indexprocessor.transformer.DatasourceTransformer;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class IndexDataProcessor {

    private final DatasourceTransformer transformer;
    private final IndexRollingReturnProcessor indexRollingReturnProcessor;

    public IndexDataProcessor(DatasourceTransformer transformer, IndexRollingReturnProcessor indexRollingReturnProcessor) {
        this.transformer = transformer;
        this.indexRollingReturnProcessor = indexRollingReturnProcessor;
    }

    public void doExecute(File csvFile, String pattern) {

        List<IndexData> historicalIndexData = transformer.transform(csvFile, pattern);
        indexRollingReturnProcessor.calculateRollingReturns(historicalIndexData);

    }
}

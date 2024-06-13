package com.indfinvestor.app.indexprocessor.service;

import com.indfinvestor.app.indexprocessor.model.IndexData;
import com.indfinvestor.app.indexprocessor.transformer.DatasourceTransformer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexDataProcessor {

    private final DatasourceTransformer transformer;
    private final RollingReturnProcessor rollingReturnProcessor;

    public IndexDataProcessor(DatasourceTransformer transformer, RollingReturnProcessor rollingReturnProcessor) {
        this.transformer = transformer;
        this.rollingReturnProcessor = rollingReturnProcessor;
    }

    public void doExecute(){

        List<IndexData> historicalIndexData = transformer.transform();
        rollingReturnProcessor.calculateRollingReturns(historicalIndexData);

    }
}

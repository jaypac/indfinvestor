package com.indfinvestor.app.navprocessor.service;

import com.indfinvestor.app.navprocessor.model.MfNavRecord;
import com.indfinvestor.app.navprocessor.transformer.DatasourceTransformer;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NavDataProcessor {

    private final DatasourceTransformer transformer;
    private final MfRollingReturnProcessor mfRollingReturnProcessor;

    public NavDataProcessor(DatasourceTransformer transformer, MfRollingReturnProcessor mfRollingReturnProcessor) {
        this.transformer = transformer;
        this.mfRollingReturnProcessor = mfRollingReturnProcessor;
    }

    public void doExecute(File inputFile) {
        try {
            var content = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
            Map<String,List<MfNavRecord>> historicalIndexData = transformer.transform(content);
            mfRollingReturnProcessor.calculateRollingReturns(historicalIndexData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}

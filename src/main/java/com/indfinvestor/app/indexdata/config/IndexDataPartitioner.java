package com.indfinvestor.app.indexdata.config;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.partition.support.Partitioner;
//import org.springframework.batch.item.ExecutionContext;
import org.hibernate.sql.exec.spi.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IndexDataPartitioner
        //implements Partitioner
{

    private final String COMMA_DELIMITER = ",";

    private final Resource dataFile;

    public IndexDataPartitioner(Resource mappingFile) {
        this.dataFile = mappingFile;
    }

    //@Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dataFile.getInputStream()))) {
            String line;
            int count = 1;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                String indexName = values[0];
                String tradingName = values[2];
//                ExecutionContext context =
//                        new ExecutionContext();
//                context.putString("indexName", indexName);
//                context.putString("tradingName", tradingName);
//                partitions.put("partition" + count++, context);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("Jubin Partitions {}", partitions);
        return partitions;
    }
}

package com.indfinvestor.app.index.stats;

import com.indfinvestor.app.index.model.entity.dto.IndexDetailsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
@StepScope
public class IndexDetailsPartitioner implements Partitioner {

    @Value("${index.stats.partition.query}")
    private String partitionSqlQuery;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<IndexDetailsDto> allSchemes = findAll();
        Map<String, ExecutionContext> partitions = new HashMap<>(gridSize);
        for (int i = 0; i < allSchemes.size(); i++) {
            ExecutionContext context = new ExecutionContext();
            context.put("indexDetails", allSchemes.get(i));
            context.putInt("partitionIndex", i);
            partitions.put("partition" + i + "-" + allSchemes.get(i).getName(), context);
        }

        return partitions;
    }

    private List<IndexDetailsDto> findAll() {
        return namedParameterJdbcTemplate.query(
                partitionSqlQuery,
                Collections.emptyMap(),
                (rs, rowNum) -> new IndexDetailsDto(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("indexType"),
                        rs.getString("subIndexType"),
                        rs.getString("exchange")));
    }
}

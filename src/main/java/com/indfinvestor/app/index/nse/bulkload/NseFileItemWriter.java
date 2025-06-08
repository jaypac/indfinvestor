package com.indfinvestor.app.index.nse.bulkload;

import com.indfinvestor.app.index.model.dto.IndexNavDetails;
import com.indfinvestor.app.index.model.entity.IndexDetails;
import com.indfinvestor.app.index.service.IndexDetailsService;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
@RequiredArgsConstructor
public class NseFileItemWriter implements ItemWriter<IndexNavDetails> {

    private final IndexDetailsService indexDetailsService;

    @Value("${index.details.writer.insert}")
    private String schemeMfNavDetailsInsert;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private void convertToEntity(IndexNavDetails indexNavDetails) {
        IndexDetails indexDetails = indexDetailsService.getIndexByName(indexNavDetails.getIndexName());
        if (indexDetails == null) {
            log.error("Index not found for name {}", indexNavDetails.getIndexName());
            throw new RuntimeException("Index not found");
        }

        log.info("Started writing records for index {} ...", indexDetails.getName());
        var startTime = System.currentTimeMillis();

        List<MapSqlParameterSource> entries = new ArrayList<>();
        for (var entry : indexNavDetails.getHistoricalNavData()) {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                    .addValue("navDate", entry.getDate(), Types.DATE)
                    .addValue("netAssetValue", entry.getClose(), Types.NUMERIC)
                    .addValue("indexId", indexDetails.getId(), Types.BIGINT);
            entries.add(mapSqlParameterSource);
        }

        MapSqlParameterSource[] array = entries.toArray(new MapSqlParameterSource[entries.size()]);
        namedParameterJdbcTemplate.batchUpdate(schemeMfNavDetailsInsert, array);

        log.info(
                "Finished writing records for index {} in {} ms",
                indexDetails.getName(),
                System.currentTimeMillis() - startTime);
    }

    @Override
    public void write(Chunk<? extends IndexNavDetails> chunk) {
        chunk.getItems().forEach(item -> {
            convertToEntity(item);
        });
    }
}

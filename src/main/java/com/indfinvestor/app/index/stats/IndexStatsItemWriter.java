package com.indfinvestor.app.index.stats;

import com.indfinvestor.app.index.model.entity.dto.IndexReturnStatsDto;
import com.indfinvestor.app.index.model.entity.dto.IndexRollingReturns;
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
public class IndexStatsItemWriter implements ItemWriter<IndexRollingReturns> {

    @Value("${index.stats.writer.insert}")
    private String indexStatsInsertQuery;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends IndexRollingReturns> chunk) {

        List<MapSqlParameterSource> entries = new ArrayList<>();
        chunk.getItems().forEach(item -> {
            var indexId = item.indexId();
            var returnDtos = item.indexReturnStatsDtos();

            for (IndexReturnStatsDto record : returnDtos) {
                MapSqlParameterSource entry = new MapSqlParameterSource()
                        .addValue("year", record.getYear(), Types.BIGINT)
                        .addValue("standardDeviation", record.getStandardDeviation(), Types.NUMERIC)
                        .addValue("mean", record.getMean(), Types.NUMERIC)
                        .addValue("percentile90", record.getPercentile90(), Types.NUMERIC)
                        .addValue("percentile95", record.getPercentile95(), Types.NUMERIC)
                        .addValue("negative", record.getNegative(), Types.NUMERIC)
                        .addValue("count5", record.getCount5(), Types.NUMERIC)
                        .addValue("count10", record.getCount10(), Types.NUMERIC)
                        .addValue("count15", record.getCount15(), Types.NUMERIC)
                        .addValue("count20", record.getCount20(), Types.NUMERIC)
                        .addValue("count25Plus", record.getCount25Plus(), Types.NUMERIC)
                        .addValue("totalCount", record.getTotalCount(), Types.NUMERIC)
                        .addValue("startingYear", record.getStartingYear(), Types.BIGINT)
                        .addValue("indexId", indexId, Types.BIGINT);
                entries.add(entry);
            }
        });
        MapSqlParameterSource[] array = entries.toArray(new MapSqlParameterSource[entries.size()]);
        jdbcTemplate.batchUpdate(indexStatsInsertQuery, array);
    }
}

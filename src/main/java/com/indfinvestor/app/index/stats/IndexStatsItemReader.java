package com.indfinvestor.app.index.stats;

import com.indfinvestor.app.index.model.entity.dto.IndexDetailsDto;
import com.indfinvestor.app.index.model.entity.dto.IndexNavDto;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsDto;
import com.indfinvestor.app.nav.model.dto.MfSchemeNavDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Slf4j
public class IndexStatsItemReader implements ItemReader<IndexDetailsDto> {

    @Value("${index.stats.reader.query}")
    private String indexDetailsQuery;

    private final IndexDetailsDto indexDetailsDto;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String startingYear;
    private boolean noInput = false;

    public IndexStatsItemReader(
            IndexDetailsDto indexDetailsDto,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            String startingYear) {
        this.indexDetailsDto = indexDetailsDto;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.startingYear = startingYear;
    }

    @Override
    public IndexDetailsDto read() {

        if (noInput) {
            return null;
        }

        noInput = true;
        indexDetailsDto.setIndexNavs(findAll(indexDetailsDto.getId()));
        return indexDetailsDto;
    }

    private List<IndexNavDto> findAll(Long schemeId) {

        // Create parameter map
        LocalDate firstDayOfYear = LocalDate.of(Integer.parseInt(startingYear), Month.JANUARY, 1);

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("indexId", schemeId, Types.BIGINT)
                .addValue("startingDate", firstDayOfYear, Types.DATE);

        return namedParameterJdbcTemplate.query(
                indexDetailsQuery,
                mapSqlParameterSource,
                (rs, rowNum) -> new IndexNavDto(
                        rs.getLong("id"),
                        new BigDecimal(rs.getString("netAssetValue")),
                        rs.getDate("navDate").toLocalDate()));
    }
}

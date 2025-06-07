package com.indfinvestor.app.index.model.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class IndexNavDto implements Serializable {

    private Long id;
    private BigDecimal netAssetValue;
    private LocalDate navDate;

    public IndexNavDto(Long id, BigDecimal netAssetValue, LocalDate navDate) {
        this.id = id;
        this.netAssetValue = netAssetValue;
        this.navDate = navDate;
    }
}

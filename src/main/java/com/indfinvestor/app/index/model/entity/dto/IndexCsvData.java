package com.indfinvestor.app.index.model.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexCsvData {

    @NotBlank private String name;

    @NotNull private LocalDate date;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;

    @NotNull private BigDecimal close;
}

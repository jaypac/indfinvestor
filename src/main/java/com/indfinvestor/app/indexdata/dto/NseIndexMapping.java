package com.indfinvestor.app.indexdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NseIndexMapping {

    @JsonProperty("Trading_Index_Name")
    private String tradingIndexName;

    @JsonProperty("Index_long_name")
    private String indexLongName;

}

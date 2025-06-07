package com.indfinvestor.app.index.model.entity.dto;

import com.indfinvestor.app.nav.model.dto.MfReturnStatsDto;

import java.util.List;

public record IndexRollingReturns(Long indexId, List<IndexReturnStatsDto> indexReturnStatsDtos) {}

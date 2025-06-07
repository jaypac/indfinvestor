package com.indfinvestor.app.index.model.entity.dto;

import java.util.List;

public record IndexRollingReturns(Long indexId, List<IndexReturnStatsDto> indexReturnStatsDtos) {}

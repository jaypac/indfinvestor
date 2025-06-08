package com.indfinvestor.app.index.model.dto;

import java.util.List;

public record IndexRollingReturns(Long indexId, List<IndexReturnStatsDto> indexReturnStatsDtos) {}

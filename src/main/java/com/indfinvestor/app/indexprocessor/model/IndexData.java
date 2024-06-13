package com.indfinvestor.app.indexprocessor.model;

import java.time.LocalDate;

public record IndexData(String name, LocalDate date, String close) {
}

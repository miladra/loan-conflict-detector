package com.amazing.credit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class InvertedIndex {

    private String fileName ;
    private int lineNumber ;
    private BigDecimal creditLimit;
    private boolean isConflict;
}

package com.amazing.credit.model;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Customer {

    private String name;
    private String address;
    private String postcode;
    private String phone;
    private BigDecimal creditLimit;
    private String birthday;
    private String fileName;
    private int lineNumber;

}
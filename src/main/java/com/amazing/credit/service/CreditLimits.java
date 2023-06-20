package com.amazing.credit.service;

import com.amazing.credit.model.Customer;

import java.io.File;
import java.util.List;

public interface CreditLimits {
    void loadWorkbooksFiles(File[] files);

    List<Customer> getCreditConflict();

    List<Customer> getAllWorkBook();
}

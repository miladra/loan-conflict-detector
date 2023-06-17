package com.amazing.credit.parser;

import com.amazing.credit.model.Customer;
import com.amazing.credit.repository.InvertedIndexRepository;
import com.amazing.credit.utility.FileType;
import com.amazing.credit.utility.NormalizeUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class CSVFileParser extends AbstractFileParser {
    private final Logger log = LoggerFactory.getLogger(CSVFileParser.class);

    private final InvertedIndexRepository invertedIndexRepository;

    public CSVFileParser(InvertedIndexRepository invertedIndexRepository) {
        super(FileType.CSV);
        this.invertedIndexRepository = invertedIndexRepository;
    }

    @Override
    void createFromLines(String line, int lineNumber, String fileName) {
        try {

            if (validation(line)) {
                Customer customer = convertLineToCustomer(line, lineNumber, fileName);
                if (Objects.isNull(customer))
                    return;

                String key = NormalizeUtility.removeSpecialCharacters(customer.getName() + customer.getPostcode());
                invertedIndexRepository.add(key, fileName, lineNumber, customer.getCreditLimit());
            } else {
                log.warn("Data is not valid file {} line {}", fileName, lineNumber);
            }
        } catch (Exception ex) {
            log.warn("Bad formatted line file {} line {}", fileName, lineNumber);
        }
    }

    private boolean validation(String line) {

        String[] record = line.split(",");
        String name = record[0].trim() + record[1].trim();
        String postalCode = record[3].trim();
        String creditLimit = record[5].trim();


        if (StringUtils.isEmpty(name))
            return false;

        if (StringUtils.isEmpty(postalCode))
            return false;

        if (StringUtils.isEmpty(creditLimit) || !StringUtils.isNumeric(creditLimit.replace(".", "")))
            return false;

        try {
            BigDecimal creditLimitValue = new BigDecimal(creditLimit);
            if (creditLimitValue.compareTo(BigDecimal.ZERO) <= 0)
                return false;
        } catch (Exception ex) {
            return false;
        }

        return true;

    }

    public Customer convertLineToCustomer(String line, int lineNumber, String fileName) {
        try {
            String[] p = line.split(",");


            String name = (p[0] + "," + p[1]);
            name = name.replace("\"", "").trim();
            String address = (p[2]).trim();
            String postcode = p[3].trim();
            String phone = p[4].trim();
            String creditLimit = p[5].trim();
            String birthday = p[6].trim();


            return Customer.builder()
                    .name(name)
                    .address(address)
                    .postcode(postcode)
                    .phone(phone)
                    .creditLimit(new BigDecimal(creditLimit))
                    .birthday(birthday)
                    .fileName(fileName)
                    .lineNumber(lineNumber)
                    .build();
        } catch (Exception ex) {
            log.error("Failed to parse line {} Exception {}", line, ex.getMessage());
            return null;
        }
    }
}

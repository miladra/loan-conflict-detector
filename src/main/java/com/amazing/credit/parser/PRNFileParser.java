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
public class PRNFileParser extends AbstractFileParser {

    private final Logger log = LoggerFactory.getLogger(PRNFileParser.class);

    private final BigDecimal HUNDRED = new BigDecimal("100");

    private final InvertedIndexRepository invertedIndexRepository;

    public PRNFileParser(InvertedIndexRepository invertedIndexRepository) {
        super(FileType.PRN);
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
                invertedIndexRepository.add(key, fileName, lineNumber, customer.getCreditLimit().divide(HUNDRED));
            } else {
                log.warn("Data is not valid file {} line {}", fileName, lineNumber);
            }
        } catch (Exception ex) {
            log.warn("Bad formatted line file {} line {}", fileName, lineNumber);
        }
    }

    private boolean validation(String line) {

        String name = line.substring(0, 16).replace(", ", "").trim();
        String postalCode = line.substring(38, 47).trim();
        String creditLimit = line.substring(62, 74).trim();

        if (StringUtils.isEmpty(name))
            return false;

        if (StringUtils.isEmpty(postalCode))
            return false;

        if (StringUtils.isEmpty(creditLimit) || !StringUtils.isNumeric(creditLimit))
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
            String name = line.substring(0, 16).trim();
            String address = line.substring(16, 38).trim();
            String postcode = line.substring(38, 47).trim();
            String phone = line.substring(47, 62).trim();
            String creditLimit = line.substring(62, 74).trim();
            String birthday = line.substring(74).trim();
            birthday = birthday.substring(6, 8) + "/" + birthday.substring(4, 6) + "/" + birthday.substring(0, 4);

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

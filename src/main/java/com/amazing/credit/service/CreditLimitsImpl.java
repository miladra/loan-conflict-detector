package com.amazing.credit.service;

import com.amazing.credit.model.Customer;
import com.amazing.credit.model.InvertedIndex;
import com.amazing.credit.parser.CSVFileParser;
import com.amazing.credit.parser.PRNFileParser;
import com.amazing.credit.repository.InvertedIndexRepository;
import com.amazing.credit.utility.FileType;
import com.amazing.credit.utility.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CreditLimitsImpl implements CreditLimits {

    @Value("${sources.directory}")
    String sourcesDirectory;

    private final FilesLoader filesLoader;
    private final InvertedIndexRepository invertedIndexRepository;
    private final CSVFileParser csvFileParser;
    private final PRNFileParser prnFileParser;
    
    CreditLimitsImpl(FilesLoaderImpl filesLoader, InvertedIndexRepository invertedIndexRepository, CSVFileParser csvFileParser, PRNFileParser prnFileParser) {
        this.filesLoader = filesLoader;
        this.invertedIndexRepository = invertedIndexRepository;
        this.csvFileParser = csvFileParser;
        this.prnFileParser = prnFileParser;

    }

    /**
     * Loads workbooks.
     * @param files files of workbook folder on classpath.
     */
    @Override
    public void loadWorkbooksFiles(File[] files) {
        Arrays.stream(files)
        .parallel()
        .forEach(filesLoader::loadFileFromPath);
    }

    @Override
    public List<Customer>  getCreditConflict() {

        List<Customer> customers = new ArrayList<>();
        invertedIndexRepository.getIndicesStream()
                .flatMap(List::stream)
                .filter(InvertedIndex::isConflict)
                .forEach( r-> {

                    try(BufferedReader bufferedReader = new BufferedReader(new FileReader(ResourceUtils.getFile(sourcesDirectory + "/" + r.getFileName())))) {
                       Optional<String> line = bufferedReader.lines().skip(r.getLineNumber()).findFirst();
                       if(line.isPresent()) {
                           getCustomersByFileName(customers, r, line.get());
                       } else {
                           log.error("Failed to find line {} in file {} ", r.getLineNumber() , r.getFileName());
                       }
                    } catch(Exception e){
                        log.error("Failed to find line {} in file {}, {} ", r.getLineNumber() , r.getFileName() , e.getMessage());
                    }
                });
     return customers;
    }
    private void getCustomersByFileName(List<Customer> customers, InvertedIndex r, String line) {
        FileType fileType = FileType.valueOf((r.getFileName().substring(r.getFileName().lastIndexOf(".") + 1)).toUpperCase());
        if (fileType.equals(FileType.CSV)) {
            customers.add(csvFileParser.convertLineToCustomer(line, r.getLineNumber(), r.getFileName()));
        } else if (fileType.equals(FileType.PRN)) {
            customers.add(prnFileParser.convertLineToCustomer(line, r.getLineNumber(), r.getFileName()));
        } else {
            log.error("Failed to find file type");
        }
    }

    @Override
    public List<Customer> getAllWorkBook() {

        final List<Customer> customers = new ArrayList<>();
        invertedIndexRepository.getIndicesStream()
                .flatMap(List::stream)
                .forEach( r-> {

                    try(BufferedReader bufferedReader = new BufferedReader(new FileReader(ResourceUtils.getFile(sourcesDirectory + "/" + r.getFileName())))) {
                        Optional<String> line = bufferedReader.lines().skip(r.getLineNumber()).findFirst();
                        if(line.isPresent()) {
                            getCustomersByFileName(customers, r, line.get());
                        } else {
                            log.error("Failed to find line {} in file {}", r.getLineNumber() , r.getFileName());
                        }
                    } catch(Exception e){
                        log.error("Failed to find line {} in file {}, {} ", r.getLineNumber() , r.getFileName() , e.getMessage());
                    }
                });
                return customers;
    }
}

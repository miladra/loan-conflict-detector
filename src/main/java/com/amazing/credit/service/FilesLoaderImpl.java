package com.amazing.credit.service;

import com.amazing.credit.parser.CSVFileParser;
import com.amazing.credit.parser.PRNFileParser;
import com.amazing.credit.utility.FileType;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FilesLoaderImpl implements FilesLoader {

    private final CSVFileParser csvFileParser;
    private final PRNFileParser prnFileParser;

    public FilesLoaderImpl(CSVFileParser csvFileParser, PRNFileParser prnFileParser) {
        this.csvFileParser = csvFileParser;
        this.prnFileParser = prnFileParser;
    }


    @Override
    public void loadFileFromPath(File file) {
        FileType fileType = FileType.valueOf(file.getPath().substring(file.getPath().lastIndexOf(".") + 1).toUpperCase());
        if(fileType.equals(FileType.CSV)){
           csvFileParser.parse(file);
        } else if(fileType.equals(FileType.PRN)){
           prnFileParser.parse(file);
        } else {
            throw new RuntimeException("The parser of file not found");
        }


    }
}

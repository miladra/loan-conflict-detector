package com.amazing.credit.parser;

import com.amazing.credit.utility.FileType;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public abstract class AbstractFileParser implements FileParser {

    private final FileType fileType;

    AbstractFileParser(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public void parse(File file) {
        try {
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                int index = 1;
                String fileName = file.getName();
                // Skip first line, it is header
                bufferedReader.readLine();
                while ((line = bufferedReader.readLine()) != null) {
                    createFromLines(line ,index  , fileName);
                    index++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    abstract void createFromLines(String line , int index , String fileName);
}

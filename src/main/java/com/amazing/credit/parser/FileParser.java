package com.amazing.credit.parser;

import java.io.File;

/**
 * Parser Interface.
 */

public interface FileParser {
    /**
     * parse a file
     *
     * @param file The file that will be parsed
     */
    void parse(File file);
}

package com.amazing.credit.service;

import java.io.File;

@FunctionalInterface
public interface FilesLoader {
    void loadFileFromPath(File file);
}

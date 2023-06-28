package com.amazing.credit;

import com.amazing.credit.service.CreditLimits;
import com.amazing.credit.utility.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Profile("!test")
@Component
@Slf4j
public class AppStartupRunner implements CommandLineRunner {

    @Value("${sources.directory}")
    private String sourcesDirectory;
    private final CreditLimits creditLimits;

    public AppStartupRunner(CreditLimits creditLimits) {
        this.creditLimits = creditLimits;
    }

    @Override
    public void run(String...args) {
        applicationStartup();
    }

    private void applicationStartup() {
        try {
            File[] files = ResourceUtils.getFiles(sourcesDirectory);
            creditLimits.loadWorkbooksFiles(files);

            creditLimits.getCreditConflict();
        } catch (FileNotFoundException e) {
            log.error("Sources directory not found {}" , sourcesDirectory);
        }
    }
}
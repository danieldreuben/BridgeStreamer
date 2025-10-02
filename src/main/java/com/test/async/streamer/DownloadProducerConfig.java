package com.test.async.streamer;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DownloadProducerConfig {

    private final DownloadRegistry registry;

    public DownloadProducerConfig(DownloadRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void init() {
        registry.register("downloadPDFInvoice", (command, result) -> {
            String invoiceId = command.params().getOrDefault("invoiceId", "unknown");
            String pdfContent = "PDF Invoice for #" + invoiceId;
            result.getStream().write(pdfContent.getBytes());
        });

        registry.register("downloadCSVReport", (command, result) -> {
            String csv = "col1,col2\nval1,val2\n";
            result.getStream().write(csv.getBytes());
        });
    }
}

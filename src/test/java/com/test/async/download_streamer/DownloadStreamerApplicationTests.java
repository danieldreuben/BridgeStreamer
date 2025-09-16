package com.test.async.download_streamer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.test.async.streamer.DownloadCommand;
import com.test.async.streamer.DownloadRegistry;
import com.test.async.streamer.DownloadResult;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DownloadRegistry.class)
class DownloadProducerTest {

    private DownloadRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new DownloadRegistry();

        // PDF producer: writes 100 lines
        registry.register("downloadPDFInvoice", (command, result) -> {
            String invoiceId = command.params().getOrDefault("invoiceId", "unknown");
            for (int i = 1; i <= 100; i++) {
                String pdfContent = "PDF Invoice #" + i + " for " + invoiceId + "\n";
                result.getStream().write(pdfContent.getBytes(StandardCharsets.UTF_8));
            }
        });

        // CSV producer: writes 100 rows
        registry.register("downloadCSVReport", (command, result) -> {
            String header = "col1,col2\n";
            result.getStream().write(header.getBytes(StandardCharsets.UTF_8));
            for (int i = 1; i <= 100; i++) {
                String row = "val1_" + i + ",val2_" + i + "\n";
                result.getStream().write(row.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    @Test
    void testPdfInvoiceProducer() throws Exception {
        DownloadCommand cmd = new DownloadCommand("downloadPDFInvoice", "username", Map.of("invoiceId", "12345"));
        DownloadResult result = new DownloadResult("invoice.pdf");

        registry.find("downloadPDFInvoice").ifPresentOrElse(producer -> {
            try {
                producer.produce(cmd, result);
                String content = result.getStream().toString(StandardCharsets.UTF_8);
                // basic sanity check
                assertTrue(content.contains("PDF Invoice #1 for 12345"));
                assertTrue(content.contains("PDF Invoice #100 for 12345"));
            } catch (Exception e) {
                fail("Exception in producer", e);
            }
        }, () -> fail("Producer not found"));
    }

    @Test
    void testCsvReportProducer() throws Exception {
        DownloadCommand cmd = new DownloadCommand("downloadCSVReport", "username", Map.of());
        DownloadResult result = new DownloadResult("report.csv");

        registry.find("downloadCSVReport").ifPresentOrElse(producer -> {
            try {
                producer.produce(cmd, result);
                String content = result.getStream().toString(StandardCharsets.UTF_8);
                assertTrue(content.contains("col1,col2"));           // header
                assertTrue(content.contains("val1_1,val2_1"));       // first row
                assertTrue(content.contains("val1_100,val2_100"));   // last row
            } catch (Exception e) {
                fail("Exception in producer", e);
            }
        }, () -> fail("Producer not found"));
    }

    @Test
    void testUnknownProducer() {
        assertTrue(registry.find("nonExistentProducer").isEmpty(),
                "Unknown producer should not be found in registry");
    }
}


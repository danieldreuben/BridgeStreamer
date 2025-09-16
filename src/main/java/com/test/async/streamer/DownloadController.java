package com.test.async.streamer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 5. Controller
@RestController
@RequestMapping("/downloads")
public class DownloadController {

    final DownloadRegistry registry = new DownloadRegistry();

    public DownloadController() {
        // Register sample producer
        registry.register("downloadPDFInvoice", (command, result) -> {
            // fake PDF content
            String invoiceId = command.params().getOrDefault("invoiceId", "unknown");
            String pdfContent = "PDF Invoice for #" + invoiceId;
            result.getStream().write(pdfContent.getBytes());
        });

        registry.register("downloadCSVReport", (command, result) -> {
            String csv = "col1,col2\nval1,val2\n";
            result.getStream().write(csv.getBytes());
        });
    }

    @PostMapping("/{name}")
    public ResponseEntity<byte[]> download(
            @PathVariable String name,
            @RequestBody(required = false) Map<String, String> params) {

        // Handle null body gracefully
        if (params == null) {
            params = Map.of();
        }

        DownloadCommand cmd = new DownloadCommand(name, "username", params);
        DownloadResult result = new DownloadResult(name + ".dat");

        return registry.find(name).map(producer -> {
            try {
                producer.produce(cmd, result);

                byte[] data = result.getStream().toByteArray();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(data);

            } catch (Exception e) {
                return ResponseEntity.internalServerError()
                        .body(("Error generating download: " + e.getMessage()).getBytes());
            }
        }).orElseGet(() ->
                ResponseEntity.badRequest()
                        .body(("Unknown download type: " + name).getBytes())
        );
    }
}
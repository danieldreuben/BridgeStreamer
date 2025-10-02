package com.test.async.streamer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/downloads")
public class DownloadController {

    private final DownloadRegistry registry;

    public DownloadController(DownloadRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/{name}")
    public ResponseEntity<byte[]> download(
            @PathVariable String name,
            @RequestBody(required = false) Map<String, String> params) {

        if (params == null) {
            params = Map.of();
        }
System.out.println("test");
        DownloadCommand cmd = new DownloadCommand(name, "username", params);
        DownloadResult result = new DownloadResult(name + ".dat");

        return registry.find(name).map(producer -> {
            try {
                producer.produce(cmd, result);
                byte[] data = result.getStream().toByteArray();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + result.getFilename() + "\"")
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

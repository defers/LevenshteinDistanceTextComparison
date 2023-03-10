package com.defers.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class MyFileWriterImpl implements MyFileWriter {
    private final BufferedWriter bw;

    public MyFileWriterImpl(String path) throws IOException {
        bw = Files.newBufferedWriter(Path.of(path));
    }

    @Override
    public void writeFile(Map<String, String> data) throws IOException {
        data.entrySet().stream()
                .forEach(e -> {
                    try {
                        bw.write(e.getKey() + ":" + e.getValue() + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
        bw.flush();
    }

    @Override
    public void close() throws Exception {
        bw.flush();
        bw.close();
    }
}

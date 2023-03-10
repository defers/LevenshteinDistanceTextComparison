package com.defers.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFileReaderImpl implements MyFileReader {
    private final BufferedReader br;

    public MyFileReaderImpl(String path) throws IOException {
        this.br = Files.newBufferedReader(Path.of(path));
    }

    @Override
    public List<String> readFile(int numOfRows) throws IOException {
        List<String> resultList = new ArrayList<>();

        for (int i = numOfRows; i > 0; i--) {
            String line = br.readLine();
            if (Objects.nonNull(line)) {
                resultList.add(line);
            }
        }
        return resultList;
    }

    @Override
    public void close() throws Exception {
        br.close();
    }
}

package com.defers.io;

import java.io.IOException;
import java.util.List;

public interface MyFileReader extends AutoCloseable {
    List<String> readFile(int numOfRows) throws IOException;
}

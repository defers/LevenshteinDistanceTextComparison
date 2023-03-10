package com.defers.io;

import java.io.IOException;
import java.util.Map;

public interface MyFileWriter extends AutoCloseable {
    void writeFile(Map<String, String> data) throws IOException;
}

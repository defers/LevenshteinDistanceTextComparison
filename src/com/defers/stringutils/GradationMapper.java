package com.defers.stringutils;

import java.util.List;
import java.util.Map;

public interface GradationMapper {
    Map<String, String> getGradation(List<String> string1, List<String> string2);
}

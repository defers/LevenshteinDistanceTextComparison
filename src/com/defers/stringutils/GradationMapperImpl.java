package com.defers.stringutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradationMapperImpl implements GradationMapper {
    private final StringComparator stringComparator;

    public GradationMapperImpl(StringComparator stringComparator) {
        this.stringComparator = stringComparator;
    }

    @Override
    public Map<String, String> getGradation(List<String> string1, List<String> string2) {
        Map<String, String> resMap = new HashMap<>();
        Map<String, Map<String, Integer>> resultGradation = new HashMap<>();
        List<String> notMatched = new ArrayList<>(string2);

        for (var stringToken : string1) {
            Map<String, Integer> gradationMap = new HashMap<>();
            for (var stringTokenAnother : string2) {
                int resultWeigh = stringComparator.compareStrings(stringToken, stringTokenAnother);
                gradationMap.put(stringTokenAnother, resultWeigh);
            }
            resultGradation.put(stringToken, gradationMap);
        }

        for (int i = 0; i < string1.size(); i++) {
            String curString = string1.get(i);
            resMap.put(curString, "?");
            var corEntry = resultGradation.get(curString).entrySet()
                    .stream().min((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                    .get();

            String correspondingString = corEntry.getKey();
            int percentDiff = corEntry.getValue();
            boolean findCorString = true;

            for (var key : resultGradation.keySet()) {
                if (key.equals(curString)) continue;
                int anotherPercentDiff = resultGradation.get(key).entrySet()
                        .stream()
                        .filter(
                                e -> e.getKey().equals(correspondingString)
                        ).findFirst()
                        .get()
                        .getValue();
                if (anotherPercentDiff > percentDiff) {
                    findCorString = false;
                }
            }

            if (findCorString) {
                resMap.put(curString, correspondingString);
                notMatched.remove(correspondingString);
            }
        }
        notMatched.stream().forEach(e -> resMap.put(e, "?"));
        return resMap;
    }
}

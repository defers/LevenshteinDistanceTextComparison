package com.defers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * Calculate Levinstein distance by Wagner–Fischer algorithm
 * {<a href = https://github.com/defers/LevenshteinDistanceTextComparison>git repository</a>}
 *
 * @author Alexey Vinogradov
 */
public class Main {

    public static void main(String[] args) throws IOException {

        String input = "input.txt";
        String output = "output.txt";

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of rows N");
        int n = sc.nextInt();
        System.out.println("Enter number of rows M");
        int m = sc.nextInt();

        List<String> string1 = new ArrayList<>();
        List<String> string2 = new ArrayList<>();

        try (MyFileReader fileReader = new MyFileReaderImpl(input)) {
            string1 = fileReader.readFile(n);
            string2 = fileReader.readFile(m);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringComparator stringComparator = new StringComparatorImpl();
        GradationMapper gm = new GradationMapperImpl(stringComparator);
        Map<String, String> resMap = gm.getGradation(string1, string2);

        try (MyFileWriter fileWriter = new MyFileWriterImpl(output)) {
            fileWriter.writeFile(resMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

interface GradationMapper {
    Map<String, String> getGradation(List<String> string1, List<String> string2);
}

class GradationMapperImpl implements GradationMapper {
    private final StringComparator stringComparator;

    GradationMapperImpl(StringComparator stringComparator) {
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

interface MyFileWriter extends AutoCloseable {
    void writeFile(Map<String, String> data) throws IOException;
}

class MyFileWriterImpl implements MyFileWriter {
    private final BufferedWriter bw;

    MyFileWriterImpl(String path) throws IOException {
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

interface MyFileReader extends AutoCloseable {
    List<String> readFile(int numOfRows) throws IOException;
}

class MyFileReaderImpl implements MyFileReader {
    private final BufferedReader br;

    MyFileReaderImpl(String path) throws IOException {
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

interface StringComparator {
    int compareStrings(String string1, String string2);
}

class StringComparatorImpl implements StringComparator {

    final private static String SPLITERATOR = " ";

    @Override
    public int compareStrings(String string1, String string2) {

        String[] tokens1 = string1.split(SPLITERATOR);
        String[] tokens2 = string2.split(SPLITERATOR);
        int resultWeigh = 0;

        for (int idx = 0; idx < tokens1.length; idx++) {

            String token1 = tokens1[idx];
            for (int jdx = 0; jdx < tokens2.length; jdx++) {

                String token2 = tokens2[jdx];

                int len1 = token1.length();
                int len2 = token2.length();
                int[][] matrix = new int[len1 + 1][len2 + 1];

                fillFirstRowAndColumn(matrix, len1, len2);
                int distance = calculateLevenshteinDistance(matrix, token1, token2);
                double percentDiff = distance == 0 ? 0 : Math.ceil(distance / (double) len1 * 100);
                resultWeigh += specifyPercentage(percentDiff);
            }
        }
        return resultWeigh;
    }

    private int calculateLevenshteinDistance(int[][] matrix, String token1, String token2) {
        int len1 = token1.length();
        int len2 = token2.length();
        int cost;
        int distance = 0;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                cost = token1.charAt(i - 1) == token2.charAt(j - 1) ? 0 : 1;
                int[] previousRow = matrix[i - 1];
                int[] currentRow = matrix[i];
                matrix[i][j] = Math.min(Math.min(previousRow[j] + 1, currentRow[j - 1] + 1), previousRow[j - 1] + cost);
                distance = matrix[i][j];
            }
        }
        return distance;
    }

    private int specifyPercentage(double percentDiff) {
        int resultWeigh = 0;

        if (percentDiff == 0) {
            resultWeigh = 10;
        } else if (percentDiff <= 10) {
            resultWeigh = 5;
        } else if (percentDiff <= 30) {
            resultWeigh = 3;
        } else if (percentDiff <= 50) {
            resultWeigh = 1;
        }

        return resultWeigh;
    }

    private void fillFirstRowAndColumn(int[][] matrix, int len1, int len2) {
        for (int i = 0; i <= len1; i++) {
            matrix[i][0] = i;
        }

        for (int i = 0; i <= len2; i++) {
            matrix[0][i] = i;
        }
    }

}
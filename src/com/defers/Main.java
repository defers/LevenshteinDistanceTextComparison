package com.defers;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        StringComparator stringComparator = new StringComparator();
        String[] string1 = {"гвоздь", "шуруп", "краска синяя", "ведро для воды"};
        String[] string2 = {"краска", "корыто для воды", "шуруп 3х1.5"};

        Map<String, Map<String, Integer>> resultGradation = new HashMap<>();

        for (var stringToken: string1) {
            Map<String, Integer> gradationMap = new HashMap<>();
            for (var stringTokenAnother: string2) {
                int resultWeigh = stringComparator.compareStrings(stringToken, stringTokenAnother);
                gradationMap.put(stringTokenAnother, resultWeigh);

            }
            resultGradation.put(stringToken, gradationMap);
        }

    }

}

class StringComparator {

    final private String SPLITERATOR = " ";

    public int compareStrings(String string1, String string2) {

        String[] tokens1 = string1.split(SPLITERATOR);
        String[] tokens2 = string2.split(SPLITERATOR);

        Map<String, Map<String, Double>> resultGradation = new HashMap<>();
        int resultWeigh = 0;

        for (int idx = 0; idx < tokens1.length; idx++) {
            String token1 = tokens1[idx];
            Map<String, Double> gradationMap = new HashMap<>();

            for (int jdx = 0; jdx < tokens2.length; jdx++) {
                String token2 = tokens2[jdx];

                int len1 = token1.length();
                int len2 = token2.length();

                int[][] matrix = new int[len1+1][len2+1];
                fillFirstRowAndColumn(matrix, len1, len2);

                int cost;
                int distance = 0;
                for (int i = 1; i <= len1; i++) {
                    for (int j = 1; j <= len2; j++) {
                        cost = token1.charAt(i-1) == token2.charAt(j-1) ? 0 : 1;
                        int[] previousRow = matrix[i-1];
                        int[] currentRow = matrix[i];
                        matrix[i][j] = Math.min(Math.min(previousRow[j] + 1, currentRow[j-1] + 1), previousRow[j-1] + cost);
                        distance = matrix[i][j];
                    }
                }
                double percentDiff = distance == 0 ? 0 : Math.ceil(distance / (double) len1 * 100);
                gradationMap.put(token2, percentDiff);
                if (percentDiff == 0) {
                    resultWeigh += 10;
                } else if (percentDiff <= 10) {
                    resultWeigh += 5;
                } else if (percentDiff <= 30) {
                    resultWeigh += 3;
                } else if (percentDiff <= 50) {
                    resultWeigh += 1;
                }
            }

            resultGradation.put(token1, gradationMap);
        }


        return resultWeigh;
    }

    private void fillFirstRowAndColumn(int[][] matrix, int len1, int len2) {
        matrix[0][0] = 0;

        for (int i = 1; i <= len1; i++) {
            matrix[i][0] = i;
        }

        for (int i = 1; i <= len2; i++) {
            matrix[0][i] = i;
        }
    }

}
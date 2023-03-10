package com.defers.stringutils;

public class StringComparatorImpl implements StringComparator {

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

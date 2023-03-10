package com.defers;

import com.defers.io.MyFileReader;
import com.defers.io.MyFileReaderImpl;
import com.defers.io.MyFileWriter;
import com.defers.io.MyFileWriterImpl;
import com.defers.stringutils.GradationMapper;
import com.defers.stringutils.GradationMapperImpl;
import com.defers.stringutils.StringComparator;
import com.defers.stringutils.StringComparatorImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


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


package interpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CompLexer<V> implements Lexer {
    private Scanner scan;
    private ArrayList<String[]> all_lines = new ArrayList<>();
    private String[] str_s =null;

    public CompLexer(String v) {
        try {
            scan = new Scanner(new BufferedReader(new FileReader(v)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public CompLexer(String[] s)
    {
        str_s =s;
    }

    public CompLexer(V v) {
        scan = new Scanner((Readable) v);

    }

    public ArrayList<String[]> lexicalCheck() {
        if (str_s == null) {
            while (scan.hasNextLine()) {
                all_lines.add(scan.nextLine().replaceFirst("=", " = ").replaceFirst("\t","").split("\\s+"));
            }
        } else {
            Arrays.stream(str_s).forEach(s -> all_lines.add(s.replaceFirst("=", " = ").replaceFirst("\t", "").split("\\s+")));
        }

        return all_lines;
    }
}

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ConsoleUI {

    private static final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

    public static String inputString(String statement) {

        statement(statement);
        try {
            return consoleReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void statement(String statement) {
        System.out.println(statement);
    }

    public static void list(List<String> list) {
        list.forEach(System.out::println);
    }

    public static void breakLine() {
        statement("");
    }

    public static int menu(String statement, List<String> options) {
        statement(statement);
        for (int i = 0; i < options.size(); i++) {
            statement("[" + (i + 1) + "] " + options.get(i));
        }
        Integer option = null;
        do {
            try {
                String readed = consoleReader.readLine();
                option = Integer.parseInt(readed);
            } catch (NumberFormatException exception) {
                statement("Digite um valor inteiro correspondente à opção desejada");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (option == null);

        return option;
    }
}

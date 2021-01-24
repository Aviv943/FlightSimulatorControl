package commands;

import expressions.ShuntingYard;
import interpreter.CompParser;

public class ReturnCommand implements Command {

    @Override
    public void executeCommand(String[] array) {
        StringBuilder exp = new StringBuilder();

        int i = 1;
        while (i < array.length) {
            exp.append(array[i]);
            i++;
        }

        CompParser.returnValue = ShuntingYard.calc(exp.toString());
    }
}

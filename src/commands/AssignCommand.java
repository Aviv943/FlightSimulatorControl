package commands;

import expressions.ShuntingYard;
import interpreter.CompParser;

public class AssignCommand implements Command {
    @Override
    public void executeCommand(String[] array) {
        switch (array[2]) {
            case "bind":
                if (CompParser.symbols.get(array[0]).getV() != CompParser.symbols.get(array[3]).getV())
                    CompParser.symbols.get(array[0]).setV(CompParser.symbols.get(array[3]).getV());
                CompParser.symbols.get(array[3]).addObserver(CompParser.symbols.get(array[0]));
                CompParser.symbols.get(array[0]).addObserver(CompParser.symbols.get(array[3]));

                break;
            default:
                StringBuilder exp = new StringBuilder();
                int i = 2;
                while (i < array.length) {
                    exp.append(array[i]);
                    i++;
                }

                double tmp = ShuntingYard.calc(exp.toString());

                if (CompParser.symbols.get(array[0]).getLocation() != null) {
                    ConnectCommand.out.println("set " + CompParser.symbols.get(array[0]).getLocation() + " " + tmp);
                    ConnectCommand.out.flush();
                    System.out.println("set " + CompParser.symbols.get(array[0]).getLocation() + " " + tmp);
                }
                else {
                    CompParser.symbols.get(array[0]).setV(tmp);
                }
                break;
        }
    }
}

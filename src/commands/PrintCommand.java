package commands;

import interpreter.CompParser;

public class PrintCommand implements Command {
    @Override
    public void executeCommand(String[] array) {

        int i=1;
        while (i<array.length) {
            if(CompParser.symbols.containsKey(array[i])) {
                System.out.print(array[i]+CompParser.symbols.get(array[i]).getV());
            } else {
                System.out.print(array[i]);
            }

            i++;
        }

        System.out.println("");
    }
}

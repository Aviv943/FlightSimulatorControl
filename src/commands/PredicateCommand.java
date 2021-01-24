package commands;

import expressions.CoditionBulider;

public class PredicateCommand implements Command {
    double bool;
    public double getBool() {
        return bool;
    }
    public void setBool(double bool) {
        this.bool = bool;
    }

    @Override
    public void executeCommand(String[] array) {
        StringBuilder s=new StringBuilder();

        int i=1;
        while (i<array.length-1) {
            s.append(array[i]);
            i++;
        }

        bool = CoditionBulider.calc(s.toString());
    }
}

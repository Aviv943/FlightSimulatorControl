package commands;

public class IfCommand extends ConditionCommand {
    @Override
    public void executeCommand(String[] array) {
        PredicateCommand predicateCommand = (PredicateCommand)commands.get(0).getCommand();
        commands.get(0).calculate();
        if (predicateCommand.getBool()!=0) {
            int i = 1;
            while (i < commands.size()) {
                commands.get(i).calculate();
                i++;
            }
            commands.get(0).calculate();
        }
    }
}
package commands;

import expressions.Expression;

public class CommandExpression implements Expression {
    private Command command;
    private String[] strings;

    public CommandExpression(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String[] getS() {
        return strings;
    }

    public void setS(String[] strings) {
        this.strings = strings;
    }

    @Override
    public double calculate() {
        command.executeCommand(strings);
        return 0;
    }
}

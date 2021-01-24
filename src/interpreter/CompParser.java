package interpreter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import commands.*;

public class CompParser implements Parser {
    private HashMap<String, CommandExpression> cmdTable =new HashMap<>();
    private GenericFactory cmdFactory =new GenericFactory();
    public static HashMap<String,Var> symbols;
    public ArrayList<String[]> lines;
    public ArrayList<CommandExpression> cmds;
    public static double returnValue;
    public static ArrayList<String> vars;

    public CompParser(ArrayList<String[]> lines) {
        this.cmds =new ArrayList<>();
        this.lines = lines;
        symbols =new HashMap<>();
        cmdFactory.insertProduct("openDataServer",OpenDataServer.class);
        cmdFactory.insertProduct("connect",ConnectCommand.class);
        cmdFactory.insertProduct("while",LoopCommand.class);
        cmdFactory.insertProduct("var",DefineVarCommand.class);
        cmdFactory.insertProduct("return",ReturnCommand.class);
        cmdFactory.insertProduct("=",AssignCommand.class);
        cmdFactory.insertProduct("disconnect",DisconnectCommand.class);
        cmdFactory.insertProduct("print",PrintCommand.class);
        cmdFactory.insertProduct("sleep",SleepCommand.class);
        cmdFactory.insertProduct("predicate",PredicateCommand.class);
        cmdFactory.insertProduct("autoroute",AutoRouteCommand.class);
        cmdFactory.insertProduct("if",IfCommand.class);
        cmdTable.put("openDataServer", new CommandExpression(new OpenDataServer()));
        cmdTable.put("connect",new CommandExpression(new ConnectCommand()));
        cmdTable.put("while",new CommandExpression(new LoopCommand()));
        cmdTable.put("var",new CommandExpression(new DefineVarCommand()));
        cmdTable.put("return",new CommandExpression(new ReturnCommand()));
        cmdTable.put("=",new CommandExpression(new AssignCommand()));
        cmdTable.put("disconnect",new CommandExpression(new DisconnectCommand()));
        Scanner scanner = null;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader("simulator_vars.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        vars=new ArrayList<>();

        while(scanner.hasNext())
        {
            vars.add(scanner.nextLine());
        }

        for (String str:vars)
        {
            symbols.put(str,new Var(str));
        }
    }

    private Command parseCondition(ArrayList<String[]> array) {
        ConditionCommand commands=(ConditionCommand) cmdFactory.getNewProduct(array.get(0)[0]);
        int i = 0;
        ArrayList<CommandExpression> arrayList=new ArrayList<>();
        CommandExpression commandExpression = new CommandExpression((Command) cmdFactory.getNewProduct("predicate"));
        commandExpression.setS(array.get(0));
        arrayList.add(commandExpression);
        commands.setCommands(arrayList);
        commands.getCommands().addAll(1,this.parseCommands(new ArrayList<>(array.subList(i+1,array.size()))));

        return commands;
    }

    
    private ArrayList<CommandExpression> parseCommands(ArrayList<String[]> array){
        ArrayList<CommandExpression> cmds=new ArrayList<>();

        int i = 0;
        while (i < array.size()) {
            CommandExpression cmd = new CommandExpression((Command) cmdFactory.getNewProduct(array.get(i)[0]));

            if(cmd.getCommand()!=null) {
                if (array.get(i)[0].equals("while") ||array.get(i)[0].equals("if") ) {
                    int index = i;
                    i+=findCloser(new ArrayList<>(array.subList(i+1,array.size())))+1;
                    cmd.setCommand(this.parseCondition(new ArrayList<>(array.subList(index, i))));
                }
            }

            else {
                cmd=new CommandExpression((Command) cmdFactory.getNewProduct(array.get(i)[1]));
            }

            cmd.setS(array.get(i));
            cmds.add(cmd);
            i++;
        }

        return cmds;
    }

    private int findCloser(ArrayList<String[]> array){
        Stack<String> closer_stack=new Stack<>();
        closer_stack.push("{");
        for(int i=0;i<array.size();i++) {

            if(array.get(i)[0].equals("while") ||array.get(i)[0].equals("if") ) {
                closer_stack.push("{");
            }

            if(array.get(i)[0].equals("}")) {
                closer_stack.pop();
                if(closer_stack.isEmpty()) {
                    return i;
                }
            }
        }

        return 0;
    }

    public void parse() {
        this.cmds =this.parseCommands(lines);

    }

    public double execute(){
        for (CommandExpression e: cmds) {
            e.calculate();
        }

        return returnValue;
    }
}

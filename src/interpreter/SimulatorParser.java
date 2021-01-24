package interpreter;

import java.util.ArrayList;
import java.util.HashMap;

import commands.*;


public class SimulatorParser implements Parser{
	private HashMap<String, Command> cmdTable =new HashMap<>();
	public static HashMap<String,Var> symbolTable=new HashMap<>();
	private ArrayList<String[]> lines;
	public SimulatorParser(ArrayList<String[]> lines) {
		this.lines = lines;
		cmdTable.put("openDataServer", new OpenDataServer());
		cmdTable.put("connect", new ConnectCommand());
		cmdTable.put("while", new LoopCommand());
	}
	
	public void parse() {
		for (int i = 0; i < lines.size(); i++) {
			Command c= cmdTable.get(lines.get(i)[0]);

			if (c!=null) {
				if (lines.get(i)[0].equals("while")) {
					int index= i;

					while(!lines.get(i)[0].equals("}")) {
						i++;
					}

					this.parseCondition(new ArrayList<String[]>(lines.subList(index, i)));
				}
			}

				c.executeCommand(lines.get(i));
		}
	}

	private void parseCondition(ArrayList<String[]> array) {
		ArrayList<Command> cmds=new ArrayList<>();
		int i=0;
		String[] tmp=array.get(i);
		ConditionCommand c=(ConditionCommand) cmdTable.get(tmp[0]);
		i++;

		for (; i < array.size(); i++) {
			Command cmd= cmdTable.get(array.get(i)[0]);

			if(cmd!=null) {
				if(array.get(i)[0].equals("while")) {
					int index= i;

					while(array.get(i)[0]!="}") {
						i++;
					}

					this.parseCondition(new ArrayList<>(array.subList(index, i)));
				}
			}
				cmds.add(cmd);
		}
	}
}

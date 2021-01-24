package commands;

import expressions.ShuntingYard;
import interpreter.CompParser;
import interpreter.Var;

public class DefineVarCommand implements Command {

	@Override
	public void executeCommand(String[] array) {
		Var var = new Var();
		if (array.length>2) {
			if (array[3].equals("bind")) {
				CompParser.symbols.put(array[1],CompParser.symbols.get(array[4]));
			}
			else {
				StringBuilder exp = new StringBuilder();

				for (int i = 3; i < array.length; i++) {
					exp.append(array[i]);
				}

				var.setV(ShuntingYard.calc(exp.toString()));
				CompParser.symbols.put(array[1],var);
			}
		}
		else {
			CompParser.symbols.put(array[1],new Var());
		}

	}

}

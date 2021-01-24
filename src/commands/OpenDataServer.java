package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import expressions.ShuntingYard;
import interpreter.CompParser;
import server_side.MySerialServer;
import server_side.Server;

public class OpenDataServer implements Command {
	public static volatile boolean stop = false;
	public static Object wait = new Object();
	Server server;

	@Override
	public void executeCommand(String[] array) {
		stop = false;
		server = new MySerialServer();
		server.open(Integer.parseInt(array[1]), (in, out) -> {
			BufferedReader Bin=new BufferedReader(new InputStreamReader(in));
			synchronized (OpenDataServer.wait) {
				OpenDataServer.wait.notifyAll();
			}

			while (!stop) {
				try {
					String Line;
					Line = Bin.readLine();
					String[] vars = Line.split(",");

					int i=0;
					while (i<vars.length) {
						if(Double.parseDouble(vars[i])!=CompParser.symbols.get(CompParser.vars.get(i)).getV()) {
							CompParser.symbols.get(CompParser.vars.get(i)).setV(Double.parseDouble(vars[i]));
						}

						i++;
					}
				} catch(IOException exception){
					exception.printStackTrace();
				}
				try {
					long num = (long) ShuntingYard.calc("1000/" + array[2]);
					Thread.sleep(num);
				} catch (InterruptedException e) {
				}
			}

			server.stop();
		});
	}
}
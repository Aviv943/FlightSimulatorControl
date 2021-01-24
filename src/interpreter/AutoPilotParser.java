package interpreter;

import java.util.ArrayList;

public class AutoPilotParser {
    CompParser p;
    public static volatile boolean stop_it =true;
    public static volatile boolean close_it =false;
    public static Thread calc_thread;
    public int i = 0;

    public AutoPilotParser(CompParser p) {
        this.p = p;
    }

    public void parse() {
        p.parse();
        i=0;
    }

    public void execute(){
        calc_thread = new Thread(()->{
            while (!close_it) {
                while (!stop_it && i < p.cmds.size()) {
                    p.cmds.get(i).calculate();
                    i++;
                }
            }
        });

        calc_thread.start();
    }

    public void add(ArrayList<String[]> lines){
        p.lines.clear();
        p.lines.addAll(lines);
        CompParser.symbols.put("stop",new Var(1));
        for (String[] strings:p.lines) {
            if (strings[0].equals("while")) {
                StringBuilder stringBuilder = new StringBuilder(strings[strings.length-2]);
                stringBuilder.append("&&stop!=0");
                strings[strings.length-2]=stringBuilder.toString();
            }
        }
    }

    public void stop(){
        Var stop= CompParser.symbols.get("stop");

        if(stop!=null) {
            stop.setV(0);
        }

        AutoPilotParser.stop_it =true;
    }

    public void Continue() {
        CompParser.symbols.get("stop").setV(1);
    }
}
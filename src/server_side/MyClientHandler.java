package server_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MyClientHandler implements ClientHandler {
	CacheManager cm;
	Solver solver;
	public MyClientHandler(CacheManager cm ) {
		this.cm=cm;
	}
	
	@Override
	public void handleClient(InputStream in, OutputStream out) {
		BufferedReader bin =new BufferedReader(new InputStreamReader(in));
		PrintWriter binOut=new PrintWriter(new OutputStreamWriter(out));
			try {
				String binLine;
				String Solved;
				ArrayList<String[]> lines=new ArrayList<>();
				while(!(binLine = bin.readLine()).equals("end"))
				{
					lines.add(binLine.split(","));
				}
				int j=0;
				int[][]mat=new int[lines.size()][];
				for (int i = 0; i < mat.length-1; i++) {
					String[] tmp=lines.get(i);
					mat[i]=new int[tmp.length];
					for (String s : tmp) {
						mat[i][j]=Integer.parseInt(s);
						j++;
					}
					j=0;
				}
			Matrix m=new Matrix(mat);
				Astar.Heuristic heuristic=new Astar.Heuristic() {
					@Override
					public double cost(State s, State goalState) {
						String start=(String)(s.getState_template());
						String[] tmp=start.split(",");
						double x1=Integer.parseInt(tmp[0]);
						double y1=Integer.parseInt(tmp[1]);
						String end=(String)goalState.getState_template();
						tmp=end.split(",");
						double x2=Integer.parseInt(tmp[0]);
						double y2=Integer.parseInt(tmp[1]);
						return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
					}
				};
			//Searcher searcher=new BFS();
			Searcher searcher=new Astar(heuristic);
			solver=new SolverSearcher<>(searcher);
			m.setIntialState(binLine= bin.readLine());
			m.setGoalState(binLine= bin.readLine());
			if(cm.Check(m.toString()))
			{
				Solved=(String) cm.Extract(m.toString());
			}
			else {
				Solved=(String) solver.Solve(m);
				String[] arrows=Solved.split("->");
				Solved="";
				String[] arrow1;
				String[] arrow2;
				int x,y;
				for (int i = 0; i < arrows.length-1; i++) {
					arrow1=arrows[i].split(",");
					arrow2=arrows[i+1].split(",");
					x=Integer.parseInt(arrow2[0])-Integer.parseInt(arrow1[0]);
					y=Integer.parseInt(arrow2[1])-Integer.parseInt(arrow1[1]);
					if(x>0)
						Solved+="Down"+",";
					else if(x<0)
						Solved+="Up"+",";
					else
						if(y>0)
							Solved+="Right"+",";
						else
							Solved+="Left"+",";
				
						}
				cm.Save(m.toString(), Solved);
			}
			binOut.println(Solved.substring(0, Solved.length()-1));
			binOut.flush();
		}catch (IOException e) {e.printStackTrace();}
	

	}

}

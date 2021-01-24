package server_side;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;


public class FileCacheManager<Problem,Solution> implements CacheManager<Problem,Solution>  {

	HashMap<Problem,Solution> problem_solution_table;
	Properties props;
	
	@SuppressWarnings("unchecked")
	public FileCacheManager() {
		props =new Properties();
		String name="hash2.properties";
		try {
			props.load(new FileInputStream(name));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.problem_solution_table = new HashMap<>();
		if(props !=null)
		{
			Enumeration<?> E= props.propertyNames();
			while(E.hasMoreElements())
			{
				Problem key=(Problem)E.nextElement();
				if(key!=null)
					this.problem_solution_table.put(key,(Solution) props.get(key));
			}
		}
		
	}

	@Override
	public Boolean Check(Problem in) {
		if(problem_solution_table.isEmpty())
			return false;
		return problem_solution_table.containsKey(in);
		
	}

	@Override
	public Solution Extract(Problem in) {
		return problem_solution_table.get(in);
	}

	@Override
	public void Save(Problem in,Solution out) {
		problem_solution_table.put(in, out);
		props.putAll(this.problem_solution_table);
		String name="hash2.properties";
		try {
			props.store(new FileOutputStream(name), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}

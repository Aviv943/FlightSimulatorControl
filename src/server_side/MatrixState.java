package server_side;



public class MatrixState extends State<String> {

	//private String state;
	public MatrixState(String state) {
		super(state);
		this.setCameFrom(null);
	}
	@Override
	public int hashCode() {
		final int prime_num = 31;
		int result = super.hashCode();
		result = prime_num * result + ((state_template == null) ? 0 : state_template.hashCode());
		return result;
	}
	@Override
	public boolean equals(State s) {
		if (this.state_template.intern() == ((String) s.getState_template()).intern())
			return true;
		return false;
	}

	
	
	
	
}

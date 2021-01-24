package server_side;



public class State<T>  {

	protected T state_template;
	private double cost;
	private State cameFrom;
	
	public State(T state_template) {
		this.state_template = state_template;
	}
	public State() {
		this.state_template =null;
		this.cost=0;
		this.cameFrom=null;
	}
	
	public boolean equals(State s) {
		return state_template.equals(s.getState_template());
	}

	public T getState_template() {
		return state_template;
	}

	public void setState_template(T state_template) {
		this.state_template = state_template;
		}

	@Override
	public String toString() {
		return "State [state=" + state_template + ", cost=" + cost + ", cameFrom=" + cameFrom + "]";
	}
	public double getCost() {
		if(this.getCameFrom()!=null)
			return this.getCameFrom().getCost()+cost;
	
		else
			return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public State getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(State cameFrom) {
		this.cameFrom = cameFrom;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state_template == null) ? 0 : state_template.hashCode());
		return result;
	}


	
	
	
}

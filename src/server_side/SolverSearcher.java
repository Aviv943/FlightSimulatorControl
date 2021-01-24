package server_side;

public class SolverSearcher<Problem,Solution> implements Solver<Problem, Solution> {
	private Searcher searcher;
	public SolverSearcher(Searcher searcher) {
		this.searcher = searcher;
	}
	@Override
	public Solution Solve(Problem p) {
		return (Solution) searcher.search((Searchable) p);
	}

}

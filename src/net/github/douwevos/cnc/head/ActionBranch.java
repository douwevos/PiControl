package net.github.douwevos.cnc.head;

public class ActionBranch implements CncAction {

	private CncActionQueue branchedQueue;
	private boolean hasFinished;
	
	
	public ActionBranch(CncActionQueue branchedQueue) {
		this.branchedQueue = branchedQueue;
	}


	@Override
	public void run(CncContext context) {
		branchedQueue.activate();
		hasFinished = true;
	}
	
	
	@Override
	public boolean hasFinished(CncContext context) {
		return hasFinished;
	}
	
}

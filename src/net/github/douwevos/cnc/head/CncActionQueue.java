package net.github.douwevos.cnc.head;

public interface CncActionQueue {
	
	void lineTo(MicroLocation location, CncHeadSpeed speed);

	ActionMoveTo resetTo(MicroLocation newLocation, CncHeadSpeed speed);
	
	void enqueueAction(CncAction action, boolean atFront);

	
	CncActionQueue branch(boolean atFront);

	ActionReturnFromBranch returnFromBranch(boolean waitFor);
	
	CncActionQueue getParent();

	void activate();

	MicroLocation getLocationAtActivation();

	
}
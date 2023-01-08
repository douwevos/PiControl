package net.github.douwevos.cnc.head;

import net.github.douwevos.cnc.holer.SourcePiece;

public interface CncContext {

	CncActionQueue getActiveQueue();

	MicroLocation getHeadLocation();
	
	void moveHeadLocation(MicroLocation next, CncHeadSpeed speed);

	CncHead getHead();

	void setActiveQueue(CncActionQueue queue);
	
	void setSourcePiece(SourcePiece bounds);
	
	SourcePiece getSourcePiece();
	
	void setActive(boolean active);
}

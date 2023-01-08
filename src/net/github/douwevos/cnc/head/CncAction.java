package net.github.douwevos.cnc.head;

public interface CncAction {
	void run(CncContext context);

	boolean hasFinished(CncContext context);
}
package net.github.douwevos.cnc.head;

public interface CncHead {

	public void setEnabled(boolean enabled);
	
	public void stepTo(MicroLocation next, CncHeadSpeed speed);

	public CncLocation getLocation();

	public MicroLocation toMicroLocation(CncLocation cncLocation);

	public CncLocation toCncLocation(MicroLocation microLocation);
}

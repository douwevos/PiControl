package net.github.douwevos.cnc.holer;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHeadService;

public class CncRuntimeContext {

	private final CncConfiguration configuration;
	private final CncHeadService headService;
	private final HolerModel holerModel = new HolerModel(false);
	
	private final CncProgramRunner cncProgramRunner;
	
	
	public CncRuntimeContext(CncConfiguration configuration , CncHeadService headService) {
		this.configuration = configuration;
		this.headService = headService;
		cncProgramRunner = new CncProgramRunner(configuration, headService);
	}
	
	public HolerModel getHolerModel() {
		return holerModel;
	}
	
	
	public CncProgramRunner getCncProgramRunner() {
		return cncProgramRunner;
	}
	
	public CncHeadService getHeadService() {
		return headService;
	}
	
	public CncConfiguration getConfiguration() {
		return configuration;
	}
	
}

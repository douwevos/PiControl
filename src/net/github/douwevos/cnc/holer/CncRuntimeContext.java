package net.github.douwevos.cnc.holer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.model.EditableLayer;
import net.github.douwevos.cnc.model.EditableModel;
import net.github.douwevos.cnc.model.EditablePolyLine;
import net.github.douwevos.cnc.model.EditableRectangle;
import net.github.douwevos.cnc.model.EditableText;
import net.github.douwevos.cnc.poly.PolyDot;
import net.github.douwevos.cnc.poly.PolyForm;
import net.github.douwevos.justflat.ttf.TextLayout;
import net.github.douwevos.justflat.ttf.format.Ttf;
import net.github.douwevos.justflat.ttf.reader.TrueTypeFontParser;
import net.github.douwevos.justflat.values.Bounds2D;
import net.github.douwevos.justflat.values.Point2D;

public class CncRuntimeContext {

	private final CncConfiguration configuration;
	private final CncHeadService headService;
	private final HolerModel holerModel = new HolerModel(false);
	
	private final CncProgramRunner oldCncProgramRunner;
	private final NewCncProgramRunner cncProgramRunner; 
	
	private EditableModel editableModel = new EditableModel();

	
	
	private static Ttf ttf2;
	
	
	static {
		TrueTypeFontParser ttfParser = new TrueTypeFontParser();
		try {
			ttf2 = ttfParser.parse(new File("/usr/share/fonts/truetype/freefont/FreeSans.ttf"));
//			ttf2 = ttfParser.parse(new File("./src/Purisa.ttf"));
			
		} catch (IOException e) {
		}
		
	}

	
	public CncRuntimeContext(CncConfiguration configuration , CncHeadService headService) {
		this.configuration = configuration;
		this.headService = headService;
		oldCncProgramRunner = new CncProgramRunner(configuration, headService);
		cncProgramRunner = new NewCncProgramRunner(configuration, headService);

		
		EditableLayer layer = new EditableLayer();
		layer.addItem(new EditableRectangle(new Bounds2D(10,10,10000,10000), 20));
		List<PolyDot> dotList = new ArrayList<>();
		dotList.add(new PolyDot(2000,100, false));
		dotList.add(new PolyDot(800,7900, false));
		dotList.add(new PolyDot(5800,2500, false));
		dotList.add(new PolyDot(4500,700, true));
		PolyForm polyForm = new PolyForm(dotList , true);
		layer.addItem(new EditablePolyLine(polyForm , 20));

		
		TextLayout textLayout = new TextLayout(ttf2, "Douwe");
		EditableText editableText = new EditableText(textLayout, new Point2D(100, 8000), 3000, 50);
		layer.addItem(editableText);
		
		editableModel.addLayer(layer);

		
	}
	
	public HolerModel getHolerModel() {
		return holerModel;
	}
	
	
	public CncProgramRunner getCncProgramRunner() {
		return oldCncProgramRunner;
	}

	public NewCncProgramRunner getNewCncProgramRunner() {
		return cncProgramRunner;
	}

	
	public CncHeadService getHeadService() {
		return headService;
	}
	
	public CncConfiguration getConfiguration() {
		return configuration;
	}

	public EditableModel getEditableModel() {
		return editableModel;
	}

}

package douwe;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.github.douwevos.cnc.head.CncConfiguration;
import net.github.douwevos.cnc.head.CncHead;
import net.github.douwevos.cnc.head.CncHeadService;
import net.github.douwevos.cnc.head.impl.CncConfigurationImpl;
import net.github.douwevos.cnc.head.impl.CncHeadServiceImpl;
import net.github.douwevos.cnc.head.impl.MockCncHead;
import net.github.douwevos.cnc.head.impl.PiCncHead;
import net.github.douwevos.cnc.holer.CncMainPanel;
import net.github.douwevos.cnc.holer.CncPerspectiveBoard;
import net.github.douwevos.cnc.holer.CncRuntimeContext;
import net.github.douwevos.cnc.holer.calibration.CalibrationPerspective;
import net.github.douwevos.cnc.holer.run.RunPerspective;
import net.github.douwevos.cnc.ui.editor.EditorPerspective;
import net.github.douwevos.cnc.ui.plan.PlanPerspective;
import net.github.douwevos.cnc.ui.widget.CncUIButton;
import net.github.douwevos.cnc.ui.widget.CncUIButtons;
import net.github.douwevos.cnc.ui.widget.CncUIFrame;

public class MainHoler {

	public static void main(String[] args) throws InterruptedException {

		
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame();
			frame.setBounds(0, 0, 1500, 1000);
			Container contentPane = frame.getContentPane();
			
			CncConfiguration configuration = new CncConfigurationImpl();
//			CncHead head = new PiCncHead(configuration);
			CncHead head = new MockCncHead(configuration);
			CncHeadService headService = new CncHeadServiceImpl(head);
			
			CncRuntimeContext cncRuntimeContext = new CncRuntimeContext(configuration, headService);
			
			CncMainPanel panel = new CncMainPanel(cncRuntimeContext);
			contentPane.add(panel);
			
			

			EditorPerspective designPerspective = createPerspectiveMenu(panel);
			
//			DesignPerspective designPerspective = new DesignPerspective();
			panel.selectPerspective(designPerspective);
			
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
			
		});
		
		

		for(int idx=0; idx<10000000; idx++) {
			Thread.sleep(1000L);
		}

		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and
		// scheduled tasks)
//		gpio.shutdown();
	}

	private static EditorPerspective createPerspectiveMenu(CncPerspectiveBoard perspectiveBoard) {
		CncUIFrame frameMenu = new CncUIFrame("Perspective");

		CncUIButtons buttons = new CncUIButtons();
		

		EditorPerspective editorPerspective = new EditorPerspective(frameMenu);

		buttons.addButton(new CncUIButton("Editor", () -> {
			perspectiveBoard.selectPerspective(editorPerspective);
		}));


//		DesignPerspective designPerspective = new DesignPerspective(frameMenu);
//
//		buttons.addButton(new CncUIButton("Design", () -> {
//			perspectiveBoard.selectPerspective(designPerspective);
//		}));

		CalibrationPerspective calibrationPerspective = new CalibrationPerspective(frameMenu);
		buttons.addButton(new CncUIButton("Calibration", () -> {
			perspectiveBoard.selectPerspective(calibrationPerspective);
		}));

		PlanPerspective planPerspective = new PlanPerspective(frameMenu);
		buttons.addButton(new CncUIButton("Plan", () -> {
			perspectiveBoard.selectPerspective(planPerspective);
		}));

		
		RunPerspective runPerspective = new RunPerspective(frameMenu);
		buttons.addButton(new CncUIButton("Run", () -> {
			perspectiveBoard.selectPerspective(runPerspective);
		}));
		
		frameMenu.add(buttons);
		
		return editorPerspective;
	}

}

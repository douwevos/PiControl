package net.github.douwevos.cnc.ui.widget;

import java.util.ArrayList;
import java.util.List;

public class CncUIPropertyGroup {

	private List<CncUIProperty> properties = new ArrayList<>();
	
	public void addProperty(CncUIProperty property) {
		if (properties.contains(property)) {
			return;
		}
		properties.add(property);
	}
	
	public void update() {
		int x = 0;
		for(CncUIProperty prop : properties) {
			int kw = prop.getOwnKeyWidth();
			if (x<kw) {
				x = kw;
			}
		}

		for(CncUIProperty prop : properties) {
			prop.useKeyWidth(x);
		}
	}
	
	
	public interface PropertyObserver {
		public boolean valueChanged(String newValue);
	}
	
}

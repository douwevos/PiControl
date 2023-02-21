package net.github.douwevos.cnc.plan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.github.douwevos.cnc.head.MicroLocation;

public class CncPlan implements Iterable<CncPlanItem> {

	List<CncPlanItem> items = new ArrayList<>();
	
	
	public void add(CncPlanItem item) {
		items.add(item);
	}
	
	
	public void exportItemsTo(List<CncPlanItem> output) {
		output.addAll(items);
	}

	public void exportItemsTo(CncPlan exportTo) {
		exportTo.items.addAll(items);
	}
	
	public Stream<CncPlanItem> streamItems() {
		return items.stream().filter(Objects::nonNull);
	}

	
	@Override
	public Iterator<CncPlanItem> iterator() {
		return items.iterator();
	}

	public MicroLocation getLastLocation() {
		for(int idx=items.size()-1; idx>=0; idx--) {
			CncPlanItem item = items.get(idx);
			if (item instanceof CncLineTo) {
				return ((CncLineTo) item).getLocation();
			}
		}
		return null;
	}

	public MicroLocation getFirstLocation() {
		for(int idx=0; idx<items.size(); idx++) {
			CncPlanItem item = items.get(idx);
			if (item instanceof CncLineTo) {
				return ((CncLineTo) item).getLocation();
			}
		}
		return null;
	}


	
}

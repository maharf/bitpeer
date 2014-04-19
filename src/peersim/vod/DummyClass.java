package peersim.vod;

import peersim.edsim.*;

public class DummyClass extends NextCycleEvent{
	public DummyClass(String param){
		super(param);
		System.out.println("execute next cycle by CDScheduler");
	}
	public void nextCycle(){
		System.out.println("execute next cycle by nextCycle");
	}
}

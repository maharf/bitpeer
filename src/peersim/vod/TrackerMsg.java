
package peersim.vod;

import peersim.core.*;
import peersim.bittorrent.*;


public class TrackerMsg extends SimpleMsg{
	private String filename;
	private String infohash;
	
	public TrackerMsg(int type, Node sender, String filename, String infohash){
		super.type = type;
		super.sender = sender;
		this.filename = filename;
		this.infohash = infohash;
	}
	public String getFilename() {
		return this.filename;
	}
	public String getInfohash(){
		return this.infohash;
	}

}

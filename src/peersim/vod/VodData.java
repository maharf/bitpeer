package peersim.vod;
/*
 * VodData, data type for video on demand data
 */
public class VodData{
	private String vodID=null;
	private int vodSize=0;
	private int numSegment=0;
	
	public VodData(){
		
	}
	public VodData(String vID, int vSize, int nSegment){
		this.vodID = vID;
		this.vodSize = vSize;
		this.numSegment = nSegment;
	}
	
	public String getVodID(){
		return this.vodID;
	}
	public int getVodSize(){
		return this.vodSize;
	}
	public int getNumSegment(){
		return this.numSegment;
	}

}

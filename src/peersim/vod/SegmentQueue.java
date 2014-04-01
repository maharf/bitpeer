package peersim.vod;

public class SegmentQueue {
	private int[] segmentWindow;
	
	public SegmentQueue(int[] sgmt, int windowLength) {
		// TODO Auto-generated constructor stub
		//this.segmentWindow = sgmt;
		this.segmentWindow = new int[windowLength];
		//segment window initialization 
		this.createQueue(windowLength);
		
	}
	public void createQueue(int length){
		for(int i=0; i<length; i++){
			segmentWindow[i]=0;
		}
	}
	public int getSegmentLength(){
		return this.segmentWindow.length;
	}
	public void queue() {
		
	}
	public void addSegment(){
		
	}
	public void deleteSegment(){
		
	}
	public boolean isEmpty(int[] sgmt) {
		boolean empty=false;
		//if() {
			
		//}
		return empty;
	}
	public boolean isFull(int [] sgmt) {
		
		return true;
	}
	
}

package peersim.vod;

public class SegmentQueue {
	//private int[] segmentWindow;
	private int head, tail; //segment window define by position of head and tail
	private int windowLen;
	private int maxSegmentIdx;
	private int[] segment;
	private int length;
	
	public SegmentQueue(int[] segment, int length) {
		// TODO Auto-generated constructor stub
		this.tail=99;
		this.head=0;
		this.segment = segment;
		this.length = length;
		this.windowLen = tail-head;
		
		//segment window initialization 
		this.maxSegmentIdx = length;
		this.tail=-1;
		this.head=-1;
		
	}
	public void resetQueue(int length){
		for(int i=0; i<length; i++){
			segment[i]=0;
		}
		this.tail = -1;
		this.head = -1;
	}
	public int getSegmentLen(){
		return this.length;
	}
	public int getWindowLen(){
		return this.windowLen;
	}
	public void addSegment(){
		if(this.tail!=this.maxSegmentIdx){
			tail++;
		}
	}
	public void deleteSegment(){
		if(this.head!=this.maxSegmentIdx){
			head++;
		}
	}
	public void playSegment(){
		if(this.tail!=this.maxSegmentIdx){
			if(this.tail!=this.windowLen){
				this.addSegment();
			}
			else{
				this.addSegment();
				this.deleteSegment();
			}
		}
		else {
			this.deleteSegment();
		}
	}
	public boolean isEmpty(int[] sgmt) {
		if (this.head==-1 && this.tail==-1){
			return true;
		}
		return false;
	}
	public boolean isFull(int [] sgmt) {
		if(tail == this.windowLen){
			return true;
		}
		return false;
	}
	
}

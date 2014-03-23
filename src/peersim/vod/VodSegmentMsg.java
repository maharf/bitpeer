package peersim.vod;

import peersim.bittorrent.SimpleMsg;
import peersim.core.*;
/**
 * This class is extended from {@link SimpleMsg}, and represent the VOD packet data that is sent among peer
 */

class VodSegmentMsg extends SimpleMsg{	
	final Node sender; //sender
	final int messageType; //message type
	final int payload; //segment data to be sent
	final int seqNumber; //sequence number of segment
	final int timeStamp;
	final String vodID;
	int[] array;
	/**
	 * define if message is request or response 
	 */
	boolean isRequest;
	/**
	 * define if message is ack message or nack message
	 */
	boolean ack;
	
	public VodSegmentMsg(int messageType, boolean ack, boolean isRequest, Node sender, int payload, int seqNumber, int timeStamp, String vodID){
		this.payload = payload;
		this.ack = ack;
		this.isRequest = isRequest;
		this.sender = sender;
		this.messageType = messageType;
		this.seqNumber = seqNumber;
		this.timeStamp = timeStamp;
		this.vodID = vodID;
	}
}

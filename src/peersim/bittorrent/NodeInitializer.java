/*
 * Copyright (c) 2007-2008 Fabrizio Frioli, Michele Pedrolli
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * --
 *
 * Please send your questions/suggestions to:
 * {fabrizio.frioli, michele.pedrolli} at studenti dot unitn dot it
 *
 */

package peersim.bittorrent;

import peersim.core.*;
import peersim.config.Configuration;

/**
 *	This class provides a way to initialize a single node of the network.
 *	The initialization is performed by choosing the bandwidth of the node
 *	and choosing how much the shared file has been downloaded.
 */
public class NodeInitializer{
	
	/**
	 *	The protocol to operate on.
	 *	@config
	 */
	private static final String PAR_PROT="protocol";
	
	/**
	 *	The percentage of nodes with no downloaded pieces.
	 *	@config
	 *	@see "The documentation for an example on how to properly set this parameter."
	 */
	private static final String PAR_NEWER_DISTR="newer_distr";
	
	/**
	 *	The percentage of seeders in the network.
	 *	@config
	 */
	private static final String PAR_SEEDER_DISTR="seeder_distr";

	/**
	 *	The percentage of nodes with no downloaded pieces,
	 *	as defined in {@see #PAR_NEWER_DISTR}.
	 */
	private int newerDistr;
	
	/**
	 *	The percentage of seeder nodes,
	 *	as defined in {@see #PAR_SEEDER_DISTR}.
	 */
	private int seederDistr;
	
	/**
	 *	The BitTorrent protocol ID.
	 */	
	private final int pid;
	
	/*
	 * windows length
	 */
	private int winLen=2;
	
	/**
	 *	The basic constructor of the class, which reads the parameters
	 *	from the configuration file.
	 *	@param prefix the configuration prefix for this class
	 */
	public NodeInitializer(String prefix){
		this.pid = Configuration.getPid(prefix+"."+PAR_PROT);
		this.newerDistr = Configuration.getInt(prefix+"."+PAR_NEWER_DISTR);
		this.seederDistr = Configuration.getInt(prefix+"."+PAR_SEEDER_DISTR);
		
	}
	
	/**
	 *	Initializes the node <tt>n</tt> associating it
	 *	with the BitTorrent protocol and setting the reference to the tracker,
	 *	the status of the file and the bandwidth.
	 *	@param n The node to initialize
	 */
	public void initialize(Node n){
		//node intialization
		//System.out.println("----- node initialization, node: "+n.getID()+" ------");
		Node tracker = Network.get(0);
		BitTorrent p;
		p = (BitTorrent)n.getProtocol(pid);
		p.setTracker(tracker);
		p.setThisNodeID(n.getID());
		setFileStatus(p);
		setBandwidth(p);
		//System.out.println("\n");
	}

	/**
	 *	Sets the status of the shared file according to the
	 *	probability value given by {@link #getProbability()}.
	 *	@param p The BitTorrent protocol
	 */
	private void setFileStatus(BitTorrent p){
		int percentage = getProbability();
//		System.out.println("set filestatus nodeID:"+p.getThisNodeID()+", prob:"+percentage);
		chooseVodSegment(percentage, p);
	}
	
	/**
	 *	Set the maximum bandwidth for the node, choosing
	 *	uniformly at random among 4 values.
	 *	<p>
	 *	The allowed bandwidth speed are 640 Kbps, 1 Mbps, 2 Mbps and 4 Mbps.
	 *	</p>
	 *	@param p The BitTorrent protocol
	 */
	//disesuaikan dengan kondisi di kampus, cek untuk peak traffic-nya 
	private void setBandwidth(BitTorrent p){
		int value = CommonState.r.nextInt(4);
		switch(value){
			case 0: p.setBandwidth(640);break; //640Kbps
			case 1: p.setBandwidth(1024);break;// 1Mbps
			case 2: p.setBandwidth(2048);break;// 2Mbps
			case 3: p.setBandwidth(4096);break; //4Mbps
		}
	}
	
	/**
	 *	Sets the completed pieces for the given protocol <tt>p</tt>.
	 *	@parm percentage The percentage of the downloaded pieces, according to {@link #getProbability()}
	 *	@param p the BitTorrent protocol
	 */
//	private void choosePieces(int percentage, BitTorrent p){
//		double temp = ((double)p.nPieces/100.0)*percentage; // We use a double to avoid the loss of precision
//												 // during the division operation
//		System.out.println("nPieces:"+p.nPieces);
//		int completed = (int)temp; //integer number of piece to set as completed
//							  //0 if the peer is a newer
//		p.setCompleted(completed);
//		if(percentage == 100)
//			p.setPeerStatus(1);
//		int tmp;
//		//random allocation of completed pieces, each piece
//		//of data have a value 16
//		while(completed!=0){
//			tmp = CommonState.r.nextInt(p.nPieces);
//			if(p.getStatus(tmp)!=16){
//				p.setStatus(tmp, 16);
//				completed--;
//			}
////			System.out.println("piece index:"+tmp+": "+p.getStatus(tmp)+" ");
//		}		
//	}
	/*
	 * VoD segment initialization,
	 */
	private void chooseVodSegment(int percentage, BitTorrent p){
		int frontBuf=0;
		int rearBuf=0;
		int pBufPart=0; //primary buffer percentage of initial segment
		int bsBufPart=0; //backward secondary buffer percentage of initial segment
		
		
		int playbackWin = p.getPlaybackWin();
		int[][] pBuffPos = p.getBsBuffPos();
		int[][] fsBuffPos = p.getFsBuffPos();
		int[][] bsBuffPos= p.getBsBuffPos();
		double temp = ((double)p.nPieces/100.0)*percentage; // We use a double to avoid the loss of precision
												 // during the division operation
		
		int completed = (int)temp; //integer number of piece to set as completed
							  //0 if the peer is a newer
		int partPos=0;
		
		p.setCompleted(completed);
		if(percentage == 100)
			p.setPeerStatus(1); //current peer have completed segment
		
		int numCompleted= completed;
		int firstSegmentPos = 0;
		//init default value (0) to segmentStatus, memoryBuffer, and diskBuffer
		for(int i=0; i<p.nPieces; i++){
			p.setSegmentStat(i, 0);
		}
		for(int i=0; i<p.getMemoryBufferSize(); i++){
			p.setMemoryBuff(i, 0);
		}
		for(int i=0; i<p.getDiskBufferSize(); i++){
			p.setDiskBuff(i, 0);
		}
		
		//pos-1 is the rear position of playback windows
		
		if(completed!=0) {
			
			
//			System.out.println("\nchooseVodPieces nodeID:"+p.getThisNodeID()+", completed:"+completed+", nPieces:"+p.nPieces+", partPos:"+partPos);
//			System.out.println("memory size:"+p.getMemoryBufferSize());
//			System.out.println("disk size:"+p.getDiskBufferSize());
			
			int segmentIdx=0; //segment index
			int pbOffset = p.getPlaybackOffset(); //playback offset (memory index)
			int mbSize = p.getMemoryBufferSize(); //memory buffer size
			System.out.println("playback offset position:"+p.getPlaybackOffset());
			System.out.println("1. completed: "+completed);
			//initialize segment in primary buffer and forward secondary buffer continuously
			if(percentage == 100){
				segmentIdx = p.nPieces-(mbSize - pbOffset);
				firstSegmentPos = segmentIdx;
				System.out.println("segment position:"+segmentIdx);
				for(int i=pbOffset; i<mbSize; i++){
					p.setMemoryBuff(i, segmentIdx);
					p.setSegmentStat(segmentIdx, 1);
					if(completed==0){
						break;
					}
					segmentIdx++;
					completed--;
				}
			}
			else{
				segmentIdx = CommonState.r.nextInt(completed); //choose segment index randomly as the first position of playback offset
				System.out.println("segment position:"+segmentIdx);
				firstSegmentPos = segmentIdx;
				for(int i=pbOffset; i<mbSize; i++){
					p.setMemoryBuff(i, segmentIdx);
					p.setSegmentStat(segmentIdx, 1);
					if(completed==0){
						break;
					}
					segmentIdx++;
					completed--;
				}
			}
			System.out.println("2. completed: "+completed);
			segmentIdx=firstSegmentPos;
			System.out.println("segment position:"+segmentIdx);
			//initialize segment in backward secondary buffer sequentially
			if(completed!=0){	
				//System.out.println("backward secondary buffer position:");
				for(int i=pbOffset; i>=0;i--){
					p.setMemoryBuff(i, segmentIdx);
					p.setSegmentStat(segmentIdx, 1);
					if(completed==0){
						break;
					}
					segmentIdx--;
					completed--;
				}
			}
			System.out.println("3. completed: "+completed);
			System.out.println("segment position:"+segmentIdx);
			//initialize segment in disk buffer
			if(completed!=0){
				int diskIdx=0;
				for(int i=segmentIdx; i>=0;){
					if(p.getDiskBuffer(diskIdx)==0){
						p.setDiskBuff(diskIdx, i);
						p.setSegmentStat(i, 1);
						if(completed==0){
							break;
						}
						diskIdx++;
						completed--;
						i--;
					}
					else{
						diskIdx++;
					}
				}
			}
		}
		System.out.println("segment number left:"+completed);
		int[] memoryBuffer=p.getAllMemoryBuffer();
		System.out.println("memory buffer:");
		for(int i=0; i<memoryBuffer.length; i++){
			System.out.print(memoryBuffer[i]+" ");
		}
		System.out.println();
		int[] diskBuffer=p.getAllDiskBuffer();
		System.out.println("disk buffer:");
		for(int i=0; i<diskBuffer.length; i++){
			System.out.print(diskBuffer[i]+" ");
		}
		System.out.println();
		int[] segmentStat=p.getAllSegmentStatus();
		System.out.println("segment status:");
		for(int i=0; i<segmentStat.length; i++){
			System.out.print(segmentStat[i]+" ");
		}
		System.out.println();
		
		
		//random allocation of completed pieces, each piece
		//of data have a value 16
//		while(completed!=0){
//			p.setStatus(pos, 16);
//			//p.setMemoryBuff(pos, 16);
//			p.setSegmentStat(pos,1);
//			pos++; //create sequential order of segment
//			completed--;
//			if(pos==p.nPieces){ //the last position of pieces
//				break;
//			}
//		}
//		rearBuf = pos;
		p.setWinBufferPos(frontBuf, rearBuf);
		p.initPlaybackPos(frontBuf);
		p.initReqBufferPos(rearBuf+1);
		//print out status of node
//		if(p.getThisNodeID()==4){
//			System.out.println("get status:");
//			for(int a=0; a<p.nPieces; a++){
//				System.out.print(p.getStatus(a)+" ");
//			}
//			System.out.println();
//			System.out.println("get segment status:");
//			for(int a=0; a<p.nPieces; a++){
//				System.out.print(p.getSegmentStatus(a)+" ");
//			}
//			System.out.println();
//		}
			
		
	}
	
	/**
	 *	Gets a probability according with the parameter <tt>newer_distr</tt>
	 *	defined in the configuration file.
	 *	@return the probabilty value, where 0 means that the peer is new and no pieces has been downloaded,
	 *			100 means that the peer is a seeder; other values defines a random probability.
	 *	@see #PAR_NEWER_DISTR
	 */
	private int getProbability(){
		int value = CommonState.r.nextInt(100); //Returns the next pseudorandom, uniformly distributed int value from this random number generator's sequence
		//System.out.println("value1: "+value);
		if((value+1)<=seederDistr){
			return 100;
		}
		value = CommonState.r.nextInt(100);
		//System.out.println("value2: "+value);
		if((value+1)<=newerDistr){
			return 0; // A newer peer, with probability newer_distr
		}
		else{
			value = CommonState.r.nextInt(9);
			return (value+1)*10;
		}
	}
}
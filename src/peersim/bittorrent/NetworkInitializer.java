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
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import java.util.Random;

/**
 * This {@link Control} ...
 */
public class NetworkInitializer implements Control {
	/**
	* The protocol to operate on.
	*
	* @config
	*/
	private static final String PAR_PROT="protocol";
		
	private static final String PAR_TRANSPORT="transport";
	
	private static final int TRACKER = 10;
	
	private static final int CHOKE_TIME = 12;
	
	private static final int OPTUNCHK_TIME = 13;
	
	private static final int ANTISNUB_TIME = 14;
	
	private static final int CHECKALIVE_TIME = 15;
	
	private static final int TRACKERALIVE_TIME = 16;
	
	/** Protocol identifier, obtained from config property */
	private final int pid;
	private final int tid;
	private NodeInitializer init;
	
	private Random rnd;
	
	public NetworkInitializer(String prefix) {
		pid = Configuration.getPid(prefix+"."+PAR_PROT);
		tid = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		init = new NodeInitializer(prefix);
	}
	
	public boolean execute() {
		
		System.out.println("----------- network initialization-----------");
		int completed;
		Node tracker = Network.get(0);
		
		// manca l'inizializzazione del tracker;
		//tracker initialization, identified by 0 pid
		
		((BitTorrent)Network.get(0).getProtocol(pid)).initializeTracker();
		//add neighbor to current tracker
		System.out.println("add neighbor - network size:"+Network.size());
		for(int i=1; i<Network.size(); i++){
			//System.err.println("chiamate ad addNeighbor " + i);
			((BitTorrent)Network.get(0).getProtocol(pid)).addNeighbor(Network.get(i));
//			System.out.println("node setup and initilization, nodeID:"+Network.get(i).getID());
			init.initialize(Network.get(i));
//			System.out.println("node segmen status: ");
//			int status[] = ((BitTorrent)Network.get(i).getProtocol(pid)).getSegmentStatus();
//			for(int a=0; a<status.length; a++){
//				System.out.print(status[a]+" ");
//			}
//			System.out.println();
		}
//		System.out.println("tracker cache and neighbor:");
//		Neighbor cache[] =  ((BitTorrent)Network.get(0).getProtocol(pid)).getCache();
//		System.out.println("nPieces: "+((BitTorrent)Network.get(0).getProtocol(pid)).getNPieces());
//		for(int i=0; i< cache.length; i++){
//			if(cache[i].node!=null){
//				System.out.print(cache[i].node.getID()+" ");
//			}
//			else{
//				System.out.print("null ");
//			}	
//		}
//		System.out.println();
//		System.out.println("get tracker nNodes: "+((BitTorrent)Network.get(0).getProtocol(pid)).getNNodes());
		//for each nodes available (except tracker) send it some initial message 
//		for(int i=1; i< Network.size(); i++){
//			
//			Node n = Network.get(i);
//			System.out.println("Send initial configuration and message to nodeID: "+n.getID());
//			System.out.println("get node:"+n.getID()+", nNodes: "+((BitTorrent)Network.get((int)n.getID()).getProtocol(pid)).getNNodes());
//			long latency = ((Transport)n.getProtocol(tid)).getLatency(n,tracker);
//			//System.out.println("latency of message: "+latency);
//			Object ev = new SimpleMsg(TRACKER, n);
//			EDSimulator.add(latency,ev,tracker,pid);
//			ev = new SimpleEvent(CHOKE_TIME);
//			EDSimulator.add(10000,ev,n,pid);
//			ev = new SimpleEvent(OPTUNCHK_TIME);
//			EDSimulator.add(30000,ev,n,pid);
//			ev = new SimpleEvent(ANTISNUB_TIME);
//			EDSimulator.add(60000,ev,n,pid);
//			ev = new SimpleEvent(CHECKALIVE_TIME);
//			EDSimulator.add(120000,ev,n,pid);
//			ev = new SimpleEvent(TRACKERALIVE_TIME);
//			EDSimulator.add(1800000,ev,n,pid);
//		}
			
		// testing for single node request to tracker, node=8
			Node n = Network.get(4);
			System.out.println("Send initial configuration and message from nodeID: "+n.getID());
			System.out.println("get node:"+n.getID()+", nNodes: "+((BitTorrent)Network.get((int)n.getID()).getProtocol(pid)).getNNodes());
			long latency = ((Transport)n.getProtocol(tid)).getLatency(n,tracker);
			//System.out.println("latency of message: "+latency);
			Object ev = new SimpleMsg(TRACKER, n);
			EDSimulator.add(latency,ev,tracker,pid);
			ev = new SimpleEvent(CHOKE_TIME);
			EDSimulator.add(10000,ev,n,pid);
			ev = new SimpleEvent(OPTUNCHK_TIME);
			EDSimulator.add(30000,ev,n,pid);
			ev = new SimpleEvent(ANTISNUB_TIME);
			EDSimulator.add(60000,ev,n,pid);
			ev = new SimpleEvent(CHECKALIVE_TIME);
			EDSimulator.add(120000,ev,n,pid);
			ev = new SimpleEvent(TRACKERALIVE_TIME);
			EDSimulator.add(1800000,ev,n,pid);
		
		return true;
	}
	
	}

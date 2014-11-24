package dblockcache;

import java.io.IOException;

import common.Constants.DiskOperationType;
import common.Constants;
import virtualdisk.VirtualDisk;

public class DBuffer extends AbstractDBuffer{

	private boolean valid;
	private boolean dirty;
	private boolean held;
	private boolean pinned;
	private int blockId;
	private byte[] dbuf = new byte[Constants.BLOCK_SIZE];
	private VirtualDisk myDisk;
	public DBuffer(int bId,VirtualDisk disk){
		blockId = bId;
		myDisk = disk; 
	}
	/* Start an asynchronous fetch of associated block from the volume */
	@Override
	public void startFetch() {
		// TODO Auto-generated method stub
		pinned=true;
		valid=false;
		System.out.println("Now in fetch");
		try {
			myDisk.startRequest(this,DiskOperationType.READ);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/* Start an asynchronous write of buffer contents to block on volume */
	@Override
	public void startPush() {
		// TODO Auto-generated method stub
		System.out.println("Now in push");
		pinned=true;
		try {
			myDisk.startRequest(this,DiskOperationType.WRITE);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		if(valid) return true;
		return false;
	}

	@Override
	public synchronized boolean waitValid() {
		// TODO Auto-generated method stub
		while(!valid)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return true;
	}

	@Override
	public boolean checkClean() {
		// TODO Auto-generated method stub
		if(dirty) return false;
		return true;
	}

	@Override
	public synchronized boolean waitClean() {
		// TODO Auto-generated method stub
		while(dirty)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return true;
	}

	@Override
	public boolean isBusy() {
		// TODO Auto-generated method stub
		if((held)||(pinned)) return true;
		return false;
	}
	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	@Override
	public synchronized int read(byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		//some errors
		System.out.println(valid);
		waitValid();
		for(int i=0;i<count;i++){
			buffer[startOffset+i] = dbuf[i];
		}
		return count;
	}
	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.
	 */
	@Override
	public synchronized int write(byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		//some errors
		for(int i=0;i<count;i++){
			dbuf[i] = buffer[startOffset+i];
		}
		dirty=true;
		return count;
	}

	@Override
	public synchronized void ioComplete() {
		// TODO Auto-generated method stub
		System.out.println("IO completed");
		pinned=false;
		valid=true;
		dirty=false;
		notifyAll();
	}
	/* An upcall from VirtualDisk layer to fetch the blockID associated with a startRequest operation */
	@Override
	public int getBlockID() {
		// TODO Auto-generated method stub
		return blockId;
	}
	/* An upcall from VirtualDisk layer to fetch the buffer associated with DBuffer object*/
	@Override
	public byte[] getBuffer() {
		// TODO Auto-generated method stub
		return dbuf;
	}
	
	public void setHeld(boolean value){
		held=value;
	}

}

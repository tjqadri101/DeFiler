package dblockcache;

import common.Constants.DiskOperationType;
import common.Constants;

public class DBuffer extends AbstractDBuffer{

	private boolean valid;
	private boolean dirty;
	private boolean held;
	private boolean pinned;
	private int blockId;
	private byte[] dbuf = new byte[Constants.BLOCK_SIZE];
	public DBuffer(){
		
	}
	/* Start an asynchronous fetch of associated block from the volume */
	@Override
	public void startFetch() {
		// TODO Auto-generated method stub
		pinned=true;
		valid=false;
		startRequest(this,DiskOperationType.READ);
	}
	/* Start an asynchronous write of buffer contents to block on volume */
	@Override
	public void startPush() {
		// TODO Auto-generated method stub
		pinned=true;
		startRequest(this,DiskOperationType.WRITE);
		dirty=false;
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
	public int read(byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		if(!valid) return -1;
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
	public int write(byte[] buffer, int startOffset, int count) {
		// TODO Auto-generated method stub
		for(int i=0;i<count;i++){
			dbuf[i] = buffer[startOffset+i];
		}
		dirty=true;
		return count;
	}

	@Override
	public void ioComplete() {
		// TODO Auto-generated method stub
		pinned=false;
		
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

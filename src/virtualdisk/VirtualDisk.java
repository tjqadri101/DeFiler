package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import common.Constants;
import common.Constants.DiskOperationType;
import dblockcache.AbstractDBuffer;
import dblockcache.DBuffer;

public class VirtualDisk  extends AbstractVirtualDisk{


	public VirtualDisk(String volName, boolean format)
			throws FileNotFoundException, IOException {
		super(volName, format);
		// TODO Auto-generated constructor stub
	}

	public VirtualDisk(boolean format)
			throws FileNotFoundException, IOException {
		super(format);
		// TODO Auto-generated constructor stub
	}

	public VirtualDisk()
			throws FileNotFoundException, IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startRequest(DBuffer buf, DiskOperationType operation)
			throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		synchronized(_queue){
			while(!_queue.offer(new Request(buf, operation)));
			_queue.notifyAll();
		}

	}

	@Override
	protected void completeOldestRequest() throws IllegalArgumentException,
	IOException {
		// TODO Auto-generated method stub
		synchronized(_queue){
			while(_queue.isEmpty()&&!_done){
				try {
					_queue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Request req = _queue.poll();
			if(req == null)
				return;
			try {
				if (req._operation == DiskOperationType.READ) {
					readBlock(req._buffer);
				} else {
					writeBlock(req._buffer);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				req._buffer.ioComplete();
				if(_queue.isEmpty()) _queue.notifyAll();
			}
			
		}
	}
	//Call this after DFS.sync()
	public void done(){
		synchronized(_queue){
			while(!_queue.isEmpty()){
				try {
					_queue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			_done = true;
			_queue.notifyAll();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!_done){
			try {
				completeOldestRequest();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}



}

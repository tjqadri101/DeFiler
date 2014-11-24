package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import common.Constants;
import common.Constants.DiskOperationType;
import dblockcache.AbstractDBuffer;

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
	public void startRequest(AbstractDBuffer buf, DiskOperationType operation)
			throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub

	}

}

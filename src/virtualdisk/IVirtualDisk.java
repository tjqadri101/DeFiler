package virtualdisk;
import java.io.IOException;

import common.Constants.DiskOperationType;
import dblockcache.AbstractDBuffer;


public interface IVirtualDisk {
	
	/*
	 * Start an asynchronous request to the underlying device/disk/volume.   
	 *  -- buf is an DBuffer object that needs to be read/write from/to the volume
	 *  -- operation is either READ or WRITE
	 */
	public void startRequest(AbstractDBuffer buf, DiskOperationType operation)
			throws IllegalArgumentException, IOException;
}
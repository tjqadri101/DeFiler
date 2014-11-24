package virtualdisk;

import common.Constants.DiskOperationType;
import dblockcache.DBuffer;
/*Data structure to model an incoming request to the VirtualDisk from a DBuffer*/
public class Request {
	public DBuffer _buffer = null;
	public DiskOperationType _operation = null;
	public Request(DBuffer buffer, DiskOperationType operation) {
		_buffer = buffer;
		_operation = operation;
	}
}

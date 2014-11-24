
package dblockcache;
import java.util.*;

public class DBufferCache extends AbstractDBufferCache{
	/*
	 * Constructor: allocates a cacheSize number of cache blocks, each
	 * containing BLOCK-size bytes data, in memory
	 */
	private Queue<DBuffer> buffersInCache;
	//private Map<Integer,DBuffer> cacheBlocks;
	int maxBlocksInCache;
	public DBufferCache(int cacheSize) {
		super(cacheSize);
		// TODO Auto-generated constructor stub
		buffersInCache=new LinkedList<DBuffer>();
		//cacheBlocks = new Hashtable<Integer,DBuffer>();
	}
	/*
	 * Get buffer for block specified by blockID. The buffer is "held" until the
	 * caller releases it. A "held" buffer cannot be evicted: its block ID
	 * cannot change.
	 */
	@Override
	public DBuffer getBlock(int blockID) {
		// TODO Auto-generated method stub
		int length=0;
		if (blockID<0) return null;
		/*if(cacheBlocks.containsKey(blockID)){
			
			return cacheBlocks.get(blockID);
		}
		if(cacheBlocks.size()==maxBlocksInCache){
			evict();
		}*/
		
		for(DBuffer b: buffersInCache){
			length++;
			if (b.getBlockID()==blockID){
				//also move it in front of the queue,it's in the back right now
				buffersInCache.remove(b);
				buffersInCache.add(b);
				b.setHeld(true);
				return b;
			}
		}
		if (length==maxBlocksInCache) evict();
		DBuffer newBuffer = new DBuffer(blockID);
		newBuffer.startFetch();
		return newBuffer;
	}
	public void evict(){
		for(DBuffer b:buffersInCache){
			if (!b.isBusy()){
				buffersInCache.remove(b);
				b.startPush();
			}
		}
	}
	/* Release the buffer so that others waiting on it can use it */
	@Override
	public void releaseBlock(DBuffer buf) {
		// TODO Auto-generated method stub
		buf.setHeld(false);
	}
	/*
	 * sync() writes back all dirty blocks to the volume and wait for completion.
	 * The sync() method should maintain clean block copies in DBufferCache.
	 */
	@Override
	public void sync() {
		// TODO Auto-generated method stub
		for(DBuffer b:buffersInCache){
			if(!b.checkClean()){
				b.startPush();
				b.waitClean();
			}
		}
		//now it should wait to complete
	}

}


package dblockcache;
import java.util.*;
import common.Constants;
import virtualdisk.VirtualDisk;
public class DBufferCache extends AbstractDBufferCache{
	/*
	 * Constructor: allocates a cacheSize number of cache blocks, each
	 * containing BLOCK-size bytes data, in memory
	 */
	private Queue<DBuffer> buffersInCache;
	//private Map<Integer,DBuffer> cacheBlocks;
	int maxBlocksInCache;
	VirtualDisk myDisk;
	public DBufferCache(int cacheSize,VirtualDisk disk) {
		super(cacheSize);
		// TODO Auto-generated constructor stub
		buffersInCache=new LinkedList<DBuffer>();
		myDisk=disk;
		maxBlocksInCache = Constants.NUM_OF_CACHE_BLOCKS;
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
			//System.out.println("The blockId from cache is "+b.getBlockID());
			if (b.getBlockID()==blockID){
				//also move it in front of the queue,it's in the back right now
				//System.out.println("found a block in the cache");
				buffersInCache.remove(b);
				buffersInCache.add(b);
				b.setHeld(true);
				return b;
			}
		}
		if (length==maxBlocksInCache) evict();
		DBuffer newBuffer = new DBuffer(blockID,myDisk);
		buffersInCache.add(newBuffer);
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

/**********************************************
 * Please DO NOT MODIFY the format of this file
 **********************************************/

/*************************
 * Team Info & Time spent
 *************************/

	Name1: Talal Javed Qadri 	
	NetId1: tq4	 	
	Time spent: 20 hours 	

	Name2: Negatu Asmamaw 	
	NetId2: nna4	 	
	Time spent: 20 hours 	 

	Name3: Alexandru Milu 	
	NetId3: aam47	 	
	Time spent: 20 hours 	

/******************
 * Files to submit
 ******************/

	lab4.jar // An executable jar including all the source files and test cases.
	README	// This file filled with the lab implementation details
        DeFiler.log   // (optional) auto-generated log on execution of jar file

/************************
 * Implementation details
 *************************/

/* 
 * This section should contain the implementation details and a overview of the
 * results. You are required to provide a good README document along with the
 * implementation details. In particular, you can pseudocode to describe your
 * implementation details where necessary. However that does not mean to
 * copy/paste your Java code. Rather, provide clear and concise text/pseudocode
 * describing the primary algorithms (for e.g., scheduling choices you used)
 * and any special data structures in your implementation. We expect the design
 * and implementation details to be 3-4 pages. A plain textfile is encouraged.
 * However, a pdf is acceptable. No other forms are permitted.
 *
 * In case of lab is limited in some functionality, you should provide the
 * details to maximize your partial credit.  
 * */
 
 Test Program. 
 When someone runs a test program, he must construct a virtualdisk, then construct
 a cache and then a DFS, in this order. They might do it by calling these methods(this part of
 code is in our example test program as well):
VirtualDisk disk = new VirtualDisk();
DBufferCache cache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS, disk);
Thread diskThread = new Thread(disk);
start diskthread;
DFS dfs = new DFS(cache);
dfs.init();

Make sure you create the diskthread and run it before dfs.init() is called, otherwise, dfs.init
would try to read from disk but there would be no thread to complete that request.



VirtualDisk. This class takes the requests it gets from the layer above and puts them on a queue.
Then, it uses a thread to handle the requests in the order they were received. After the request is
complete, it calls the i/o complete method that signals the Dbuffer that the i/o is done.  

DBlockCache. For the DBufferCache, we implement the LRU policy using a queue. Every time a cache 
block is used we move it to the back of the queue. We always evict the first block that is not
busy. A block is busy if it is pinned or held. It is pinned when it is used for I/O with the 
virtualdisk and it is held whenever it interacts with the DFS layer.  
When we evict a block from the cache, we write the contents of that block to the virtual disk.
We chose not to have a hashmap that assigns to every block a dbuffer because it is not  
necessary. It does not save us time because even if we would get the cache block assigned to
the block in the disk in ~constant time we would still spend O(n) time to find the block in the 
evict queue, remove it and add back to the back. 

For startFetch in the Dbuffer class, we make sure that we make pinned=true (I/O is going on) and 
that valid=false until the i/O complete returns. Then we call disk.startRequest, which puts the 
request on a queue. For startPush, we do something similar by making pinned=true (I/O is going on)
and then call disk.startRequest. The checkValid and checkClean are trivial methods. WaitVaild and
waitClean are also pretty easy, they wait for the i/o to complete and then return from wait. The 
read and write methods are both synchronized methods, so no two threads access them at the same 
time. They both use a loop to copy the values from the dbuffer into the buffer (or vice versa).

DFS. 
Our DFS layer offers an API for our testProgram to create files, write to them, read from them and destroy them when done. 
Our DFS layer makes an extensive use different Queues, mainly LinkedList, to track its main data structures. 
These main data structures are myDFileIDs, myInodes, myBlockIDs. These queues keep track of the dfile ids, the block Ids and the inodes that haven't been used by the layer yet.
Once they are used/allocated, we move them to other corresponding queues named dfiles and inodes.
This lists get updated during Create, Read, Write, and destroy operations on files. 
The inodes contain the main information about a dfile. This metadata information include its size, its dfid, its blockMap. 
We are assuming that dfiles and inodes are one to one mapped. 
The largest write to a file updates the blockMap and the size of the file. The dfid is bestowed upon creation. 

Our code works for a new VDF file as well as an old VDF. It successfully maps the stored inodes from an old VDF to our DFS layer during DFS init().
The test programs creates four UserThreads that create DFiles, write to the DFiles, and potentially read from the DFiles (can be commented out in UserThread class)
It also creates four reader threads that take in a DFileID and read 1 block of the corresponding file. The data read is also printed. Finally, the utilized inodes
are also printed out. The program then calls sync() and exits. 

/************************
 * Feedback on the lab
 ************************/

/*
 * Any comments/questions/suggestions/experiences that you would help us to
 * improve the lab.
 * */
This was a useful lab for learning about file systems. 
/************************
 * References
 ************************/

/*
 * List of collaborators involved including any online references/citations.
 * */
We didn't use any online references other than piazza and some java documentation.
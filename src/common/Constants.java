package common;

/*
 * This class contains the global constants used in DFS
 */

public class Constants {

	/* The below constants indicate that we have approximately 268 MB of
	 * disk space with 67 MB of memory cache; a block can hold upto 32 inodes and
	 * the maximum file size is constrained to be 500 blocks. These are compile
	 * time constants and can be changed during evaluation.  Your implementation
	 * should be free of any hard-coded constants.  
	 */
	public static final	int	INTEGER_SIZE = 4; //Number of bytes in an int
	public static final int NUM_OF_BLOCKS = 262144; // 2^18
	public static final int BLOCK_SIZE = 1024; // 1kB
	public static final int MAX_FILE_BLOCKS = 123;
	public static final int BLOCK_MAP_SIZE = MAX_FILE_BLOCKS*4;
	public static final int INODE_SIZE =  BLOCK_MAP_SIZE + 5*INTEGER_SIZE; //inode size should be a multiplicative factor of BLOCK_SIZE
																		   //this ensures that a block can contain 1 or more complete inodes
	public static final int NUM_OF_CACHE_BLOCKS = 65536; // 2^16
	public static final int MAX_FILE_SIZE = BLOCK_SIZE*MAX_FILE_BLOCKS; // Constraint on the max file size
	public static final int MAX_DFILES = 512; // For recylcing DFileIDs
	public static final int HEADER_BLOCK_ID = 0; //id for block 0 of VDF
	/* DStore Operation types */
	public enum DiskOperationType {
		READ, WRITE
	};

	/* Virtual disk file/store name */
	public static final String vdiskName = "DSTORE.dat";
}

class DiskTester {
	public static void main(String[] args) {
		int blockSize = Library.getDiskBlockSize();
		byte[] buffer = new byte[blockSize];
                for (int j = 0; j < blockSize; j++) {
                buffer[j] = 'A';
                }
                int blockCount = Library.getDiskBlockCount();
                for (int i = 0; i < blockCount; i++) {
                    Library.writeDiskBlock(i,buffer);
                    Library.writeDiskBlock(blockCount-1-i,buffer);
                }
                
                buffer = new byte[blockSize];
		Library.readDiskBlock(1, buffer);
                
		Library.readDiskBlock(1, buffer);
                
                Library.output(new String(buffer) + "\n");
	}
}
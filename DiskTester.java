class DiskTester {
	public static void main(String[] args) {
		int blockSize = Library.getDiskBlockSize();
		byte[] buffer = new byte[blockSize];
                int i = 0;
                for (int j = 0; j < blockSize; j++) {
                buffer[j] = 'A';
                }
                Library.writeDiskBlock(2, buffer);
                Library.writeDiskBlock(8, buffer);
                //Thread.sleep(500);
                Library.writeDiskBlock(4, buffer);
                Library.writeDiskBlock(6, buffer);
                Library.writeDiskBlock(5, buffer);
                Library.writeDiskBlock(2, buffer);
                Library.writeDiskBlock(6,buffer);
                
                buffer = new byte[blockSize];
		Library.readDiskBlock(1, buffer);
                
		Library.readDiskBlock(1, buffer);
                
                Library.output(new String(buffer) + "\n");
	}
}
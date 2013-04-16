/**
 * Disk Tester
 * @author Zach Souser
 * @author Alan Peters
 */
class DiskTester {
    public static void main(String[] args) {
        int test = Integer.parseInt(args[0]);
        int blockSize = Library.getDiskBlockSize();
        int diskSize = Library.getDiskBlockCount();
        int cacheSize = Library.getDiskCacheSize();
        byte[] buffer = new byte[blockSize];
        byte[] readIn = new byte[blockSize];
        switch(test){
        case 1:
            for(int i = 0;i < diskSize;i++){
                for(int j = 0; j < blockSize; j++){
                    buffer[j]= (byte)(char)('A' + i);
                }   
                Library.writeDiskBlock(i, buffer);
            }
            for(int i = 0; i< diskSize; i++){
                Library.readDiskBlock(i, readIn);
                Library.output(new String(readIn) + "; \n");
            }
            break;
        case 2:
            for (int i =0; i < 2*diskSize; i++){
                for (int j = 0; j < blockSize; j++ ){
                    buffer[j] = (byte)(char)(i%26 +'A');
                }
                Library.writeDiskBlock((int)Math.round(Math.random()*(diskSize-1)),buffer);
                Library.readDiskBlock((int)Math.round(Math.random()*(diskSize-1)), readIn);
                Library.output(new String(readIn) + "\n");
            }
            Library.output(diskSize*2 + " total read operations requested \n");
            Library.output(diskSize*2 + " total write operations requested \n");
            break;
        case 3:
            for (int i =0; i < 2*diskSize; i++){
                for (int j = 0; j < blockSize; j++ ){
                    buffer[j] = (byte)(char)(i%26 +'A');
                }
                int block = Math.random() > .9 ? (int)(Math.random() * diskSize) :(int)( Math.random() * cacheSize);
                Library.writeDiskBlock(block, buffer);
                block = Math.random() > .9 ? (int)(Math.random() * diskSize) :(int)( Math.random() * cacheSize);
                Library.readDiskBlock(block, readIn);
                Library.output(new String(readIn) + "\n");
            }
            Library.output(diskSize*2 + " total read operations requested \n");
            Library.output(diskSize*2 + " total write operations requested \n");
            break;
        }
    }
}
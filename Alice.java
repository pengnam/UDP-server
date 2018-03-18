/*
Name: Ng Peng Nam, Sean
Student number: A0164710M
Is this a group submission (no)?

*/


// Please DO NOT copy from the Internet (or anywhere else)
// Instead, if you see nice code somewhere try to understand it.
//
// After understanding the code, put it away, do not look at it,
// and write your own code.
// Subsequent exercises will build on the knowledge that
// you gain during this exercise. Possibly also the exam.
//
// We will check for plagiarism. Please be extra careful and
// do not share solutions with your friends.
//
// Good practices include
// (1) Discussion of general approaches to solve the problem
//     excluding detailed design discussions and code reviews.
// (2) Hints about which classes to use
// (3) High level UML diagrams
//
// Bad practices include (but are not limited to)
// (1) Passing your solution to your friends
// (2) Uploading your solution to the Internet including
//     public repositories
// (3) Passing almost complete skeleton codes to your friends
// (4) Coding the solution for your friend
// (5) Sharing the screen with a friend during coding
// (6) Sharing notes
//
// If you want to solve this assignment in a group,
// you are free to do so, but declare it as group work above.




import java.net.*;
import java.nio.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.util.zip.*;

//Alice can be seen as the server
class Alice {
  private int seqNum = 0;
  private DatagramSocket socket;

  public static void main(String[] args) throws Exception {
    // Do not modify this method
    if (args.length != 3) {
      System.out.println("Usage: java Alice <path/filename> <unreliNetPort> <rcvFileName>");
      System.exit(1);
    }
    new Alice(args[0], Integer.parseInt(args[1]), args[2]);
  }

  public Alice(String fileToSend, int port, String filenameAtBob) throws Exception {
    //Initialise parameters
    int MSS = 1024;
    int portNo = 5678;
    socket = new DatagramSocket(portNo);
    InetAddress address = InetAddress.getByName("localhost");
    InputStream input = getFileInputStream(fileToSend);

    while (input) {//Check if input stream is empty
      
      int seqNum = wait(socket);
      
      
    }

    

    
    
  }
  /**
   * Wait for response with socket
   * @param socket socket to wait for response in
   */
  private int wait(DatagramSocket socket) {
    byte[] rcvBuffer = new byte[1024];
    DatagramPacket rcvedPkt = new DatagramPacket(rcvBuffer, rcvBuffer.length);
    byte[] allData = null;
    byte[] givenChecksum = null;
    byte[] data = null;

    try {
      socket.receive(rcvedPkt);//Wait here until packet is received
    
      allData = rcvedPkt.getData();
      givenChecksum = Arrays.copyOfRange(allData,0,8);
      data = Arrays.copyOfRange(allData,8,allData.length);
    
      boolean val = validateChecksum(givenChecksum, data);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    //HACK: convert bytearr to string before converting it to int
    String str = new String(data);
    return Integer.parseInt(str);
  }

  
  /**
   * Form the input stream of the data to be sent
   * @param fileName file name of the item to be sent
   */
  private InputStream getFileStream(String fileName) {
    File initialFile = new File(fileName);
    InputStream result = null;
    try {
      result = new FileInputStream(initialFile);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return result;
  }

  /**
   * Wrapper with checksum
   * @param data byte array to be sent
   */
  private byte[] wrapWithChecksum(byte[] data) {

    //cs calculates checksum
    Checksum cs = new CRC32();
    cs.update(data);
    
    //b is checksum in bytes
    byte b[] = new byte[8];
    ByteBuffer buf = ByteBuffer.wrap(b);
    buf.putLong(cs.getValue());

    return concatenate(b,data);
  }
  /**
   * Checks the checksum given checksum and data
   * Pre-condition: 
   * @param givenChecksum the given checksum in packet
   * @param data data in packet
   */
  private boolean validateChecksum(byte[] givenChecksum, byte[] data) {
    Checksum cs = new CRC32();
    cs.update(data);
    byte calcChecksum[] = new byte[8];
    ByteBuffer buf = ByteBuffer.wrap(calcChecksum);
    buf.putLong(cs.getValue());
    
    return Arrays.equals(givenChecksum, calcChecksum);
  }

  /**
   * Concatenates 2 byte[] into a single byte[]
   * This is a function provided for your convenience.
   * @param  buffer1 a byte array
   * @param  buffer2 another byte array
   * @return concatenation of the 2 buffers
   */
  private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
    byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
    System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
    System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
    return returnBuffer;
  }

}

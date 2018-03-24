/*
Name: Ng Peng Nam, Sean
Student number: A0164710M
Is this a group submission (no)?

*/

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

  public Alice(String fileToSend, int port, String filenameAtBob) throws Exception{
    //Initialise parameters
    int MSS = 1012;
    int portNo = 5678;
    socket = new DatagramSocket(portNo);
    InetAddress address = InetAddress.getByName("localhost");
    InputStream input = getFileStream(fileToSend);
 
    //Buffer byte array
    byte[] bytes;
    int bytesLeft = input.available();

    //1. Sending configuration details
    byte[] name = filenameAtBob.getBytes();
    ByteBuffer bb = ByteBuffer.allocate(name.length);
    bb.put(name);
    DatagramPacket first = Packet.generatePacket(seqNum,bb.array(),address,port);
    while (true) {
      try {
        System.out.println("Sending initial pkt");
        socket.send(first);
        int pktNum = waitAck(socket);
        if (seqNum != pktNum) {
          System.out.println("Wrong packet num");
          continue;
        }
        seqNum = (seqNum==1) ? 0 : 1;
        break;
      } catch (SocketTimeoutException e) {
        System.out.println("Socket timed out, resending");
        continue;
      } catch (Exception e){
        System.out.println("Misc Error Occured");
        System.err.println(e.getMessage());
        continue;
      }
    }
    

    //2. Sending file
    while (bytesLeft > 0) {//Check if input stream is empty
      System.out.println("=============================================");
      System.out.println("Preparing packet");
      System.out.println("Bytes left: "+ bytesLeft);

      bytes = (bytesLeft > MSS)? new byte[MSS]: new byte[bytesLeft];
      //Read input
      input.read(bytes);
      bytesLeft = input.available();

      DatagramPacket pkt = Packet.generatePacket(seqNum, bytes, address, port);
      //To keep trying to send packet if it is valid
      while (true) {
        try {
          System.out.println("Sending pkt" + seqNum);
          socket.send(pkt);
          int ackNum = waitAck(socket);
          if (seqNum != ackNum) {
            System.out.println("Wrong packet num");
            System.out.println("Seqnum: " + seqNum);
            System.out.println("AckNum: " + ackNum);
            continue;
          }
          seqNum = (seqNum==1) ? 0 : 1;
          break;
        } catch (SocketTimeoutException e) {
          System.out.println("Socket timed out, resending");
          continue;
        } catch (Exception e){
          System.err.println(e.getMessage());
          continue;
        }
      }

    }



  }

  /**
   * Wait for response with socket
   * @param socket socket to wait for response in
   */
  private int waitAck(DatagramSocket socket) throws Exception{
    System.out.println("Waiting for ACK");
    byte[] rcvBuffer = new byte[1024];
    DatagramPacket rcvedPkt = new DatagramPacket(rcvBuffer, rcvBuffer.length);
  
    //Wait for packet to be received
    socket.setSoTimeout(100);
    socket.receive(rcvedPkt);//Wait here until packet is received

    Packet pkt = Packet.parsePacket(rcvedPkt);
    System.out.println("Received ack" + pkt.getSeqNum());
    if (!pkt.validateChecksum())
      throw new Exception("Checksum failed");
    return pkt.getSeqNum();
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


}

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


class Bob {
  private int seqNum = 0;
  private DatagramSocket socket;

  public static void main(String[] args) throws Exception {
    // Do not modify this method
    if (args.length != 1) {
      System.out.println("Usage: java Bob <port>");
      System.exit(1);
    }
    new Bob(Integer.parseInt(args[0]));
  }

  public Bob(int port) throws Exception {
    System.out.println("Bob on port: " + port);
    DatagramSocket socket = new DatagramSocket(port);
    byte[] data = wait(socket);
    String fileName = new String(data);
    DataOutputStream output = new DataOutputStream(new FileOutputStream(new File(fileName)));
    while (true) {
      System.out.println("++++++++++++++++++++++++++++++++++");
      System.out.println("Waiting for packet");
      data = wait(socket);
      if (data!=null) {
        output.write(data);
      }
    }
  }
  /**
   * Wait for response with socket
   * @param socket socket to wait for response in
   * @return byte array of result, null if data should NOT be written
   */
  private byte[] wait(DatagramSocket socket) throws IOException{
    System.out.println("Waiting for packet");
    System.out.println("Current seqNum = " + seqNum);
    byte[] rcvBuffer = new byte[1024];
    DatagramPacket rcvedPkt = new DatagramPacket(rcvBuffer, rcvBuffer.length);


    //Receiving packet
    socket.receive(rcvedPkt);//Wait here until packet is received
    Packet pkt = Packet.parsePacket(rcvedPkt);
    if (!pkt.validateChecksum()){
      System.out.println("Packet checksum failed, ignoring packet");
      return null;
    }
    if (pkt.getSeqNum() != seqNum) {
      System.out.println("Received resent packet");
      sendAck(socket,rcvedPkt, pkt);
      return null;
    }
    //Send ACK
    sendAck(socket,rcvedPkt, pkt);
    //Toggle seqNum
    seqNum = (seqNum==1) ? 0 : 1;
    return pkt.getData();
  }

  /**
   * Sends an ACK packet to sender
   * @param pkt Datagram packet that stores address and socket to return to
   * @param socket socket to send with
   */
  public void sendAck(DatagramSocket socket, DatagramPacket datPack, Packet pkt) throws IOException{
    ByteBuffer bb = ByteBuffer.allocate(4);
    bb.putInt(pkt.getSeqNum());
    System.out.println("Sending ACK" + pkt.getSeqNum());
    DatagramPacket ack = Packet.generatePacket(pkt.getSeqNum(), bb.array(), datPack.getAddress(), datPack.getPort());
    socket.send(ack);

  }
}

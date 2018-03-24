/*
Name: YOUR_NAME_HERE
Student number: YOUR_STUDENT_NO_HERE
Is this a group submission (yes/no)?

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/



import java.net.*;
import java.nio.*;
import java.util.zip.CRC32;

/**
 * Structure of packet:
 * Checksum (8 bytes)
 * Seqnum (4 bytes)
 */
class Packet {
  long givenChecksum; //Only for parsed packets
  int seqNum;
  byte[] data;
  
  /**
   * To use when creating packets
   */
  Packet(int seqNum, byte[] data) {
    this.seqNum = seqNum;
    this.data = data;
  }

  Packet(long checksum, int seqNum, byte[] data) {
    this.givenChecksum = checksum;
    this.seqNum = seqNum;
    this.data = data;
  }

  /**
   * Parses a datagram packet
   * Allows easy access to data
   * @param pkt
   */
  static Packet parsePacket(DatagramPacket pkt) {
      ByteBuffer bb = ByteBuffer.wrap(pkt.getData());
      //get checksum
      long givenChecksum = bb.getLong();
      //get seqNum
      int seqNum = bb.getInt();
      //get data
      byte[] data = new byte[pkt.getLength() - 12];
      bb.get(data);
      return new Packet(givenChecksum, seqNum, data);
  }
  /**
   * Creates datagram packet
   * Pre-condition: bytes is not null
   */
  static DatagramPacket generatePacket(int seqNum, byte[] bytes, InetAddress address, int port) {
    Packet pkt = new Packet(seqNum,bytes);
    ByteBuffer bb = ByteBuffer.allocate(4+8+pkt.data.length);
    //cs calculates checksum
    CRC32 cs = new CRC32();
    cs.update(pkt.seqNum);
    cs.update(pkt.data);

    bb.putLong(cs.getValue());
    bb.putInt(pkt.seqNum);
    bb.put(pkt.data);
    byte[] data = bb.array();
    return new DatagramPacket(data,data.length, address, port);
  }
  /**
   * Creates ack packet
   */
  static DatagramPacket generateAck(int seqNum, InetAddress address, int port) {
    Packet pkt = new Packet(seqNum,null);
    ByteBuffer bb = ByteBuffer.allocate(4+8);
    //cs calculates checksum
    CRC32 cs = new CRC32();
    cs.update(pkt.seqNum);

    bb.putLong(cs.getValue());
    bb.putInt(pkt.seqNum);
    byte[] data = bb.array();
    return new DatagramPacket(data,data.length, address, port);
  }

  /**
   * GETTERS
   */
  public byte[] getData() {
    return this.data;
  }
  public int getSeqNum() {
    return this.seqNum;
  }


  /**
   * Checks the checksum given checksum and data
   */
  public boolean validateChecksum() {
    CRC32 cs = new CRC32();
    cs.update(this.seqNum);
    cs.update(this.data);
    /**
    byte calcChecksum[] = new byte[8];
    ByteBuffer buf = ByteBuffer.wrap(calcChecksum);
    buf.putLong(cs.getValue());
    */
    System.out.println("Checksum status: " + (cs.getValue() == this.givenChecksum));
    return cs.getValue() == this.givenChecksum;  
  }

}

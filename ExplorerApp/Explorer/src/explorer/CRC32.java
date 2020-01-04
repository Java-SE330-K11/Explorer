/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author DungHK
 */
public class CRC32 {
    public static long checksumInputStream(String filepath) throws IOException {
 
    InputStream inputStreamn = new FileInputStream(filepath);
 
    java.util.zip.CRC32 crc = new java.util.zip.CRC32();
 
 
  int cnt;
 
   
 
  while ((cnt = inputStreamn.read()) != -1) {
 
 
crc.update(cnt);
 
  }
 
  return crc.getValue();
    }
 
    public static long checksumBufferedInputStream(String filepath) throws IOException {
 
  InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
 
   
 
  java.util.zip.CRC32 crc = new java.util.zip.CRC32();
 
 
  int cnt;
 
   
 
  while ((cnt = inputStream.read()) != -1) {
 
 
crc.update(cnt);
 
  }
 
  return crc.getValue();
    }
 
    public static long checksumRandomAccessFile(String filepath) throws IOException {
 
  RandomAccessFile randAccfile = new RandomAccessFile(filepath, "r");
 
  long length = randAccfile.length();
 
  java.util.zip.CRC32 crc = new java.util.zip.CRC32();
 
 
  for (long i = 0; i < length; i++) {
 
 
randAccfile.seek(i);
 
 
int cnt = randAccfile.readByte();
 
 
crc.update(cnt);
 
  }
 
  return crc.getValue();
    }
 
    public static long checksumMappedFile(String filepath) throws IOException {
 
  FileInputStream inputStream = new FileInputStream(filepath);
 
  FileChannel fileChannel = inputStream.getChannel();
 
 
  int len = (int) fileChannel.size();
 
 
  MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, len);
 
 
  java.util.zip.CRC32 crc = new java.util.zip.CRC32();
 
 
  for (int cnt = 0; cnt < len; cnt++) {
 
 
 
 
 
int i = buffer.get(cnt);
 
 
crc.update(i);
 
  }
 
  return crc.getValue();
    }
}

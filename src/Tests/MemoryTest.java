/*
 * Author: Christopher Madrigal
 * Date: 4 April 2019
 * References: None
 */

package Tests;

import System.RandomAccessMemory;

/**
 * Runs a test of the RAM memory module, allocating up to 2^16 bytes of memory
 * and accessing through each possible address
 */
public class MemoryTest {
  public static void main( String[] args ) {
    RandomAccessMemory mem;
    long time;
    int size;

    for( int numBytes = 1; numBytes <= 65536; numBytes++ ) {

      System.out.println( "-- Allocating " + numBytes + " bytes of memory --");
      mem = new RandomAccessMemory( numBytes );
      size = mem.getSize();

      System.out.println( "Writing to all " + numBytes + " bytes of memory...");
      time = System.currentTimeMillis();
      for( int addr = 0; addr < size; addr++ ) {
        mem.writeByte( addr, ( addr & 0xFF ) );
      }
      time = System.currentTimeMillis() - time;
      System.out.println( "Writing cycle took " + time + "ms" );

      System.out.println( "Reading from all " + numBytes + " bytes of memory." +
                          ".." );
      time = System.currentTimeMillis();
      for( int addr = 0; addr < size; addr++ ) {
        assert( mem.readByte( addr ) == ( addr & 0xFF ) );
      }
      time = System.currentTimeMillis() - time;
      System.out.println( "Reading cycle took " + time + "ms" );
      System.out.println();
    }
  }
}

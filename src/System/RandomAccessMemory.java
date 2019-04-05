/*
 * Author: Christopher Madrigal
 * Date: 5 April 2019
 * References: None
 */

package System;

import java.io.*;

/**
 * A device that provides contiguous RAM, starting at address 0.
 * It can address as much memory as the user specifies, up to 2,147,483,647
 * bytes (or about 2GB)
 * It must provide at least one byte of memory.
 */
public class RandomAccessMemory implements Memory {

  //max memory addressable
  public static final int MAX_MEMORY = Integer.MAX_VALUE;
  private byte[] memory;

  /**
   * Constructs the memory module.
   * This will allocate as much memory as you specify, as much as about 2GB.
   * Be mindful of this.
   * @param amount The amount of memory to allocate.
   * @throws IllegalArgumentException If amount is less than 1
   */
  public RandomAccessMemory( int amount ) throws IllegalArgumentException {

    //no less than 1 byte
    if( amount < 1 ) {
      throw new IllegalArgumentException( "Must address at least 1 byte." );
    }

    //create array
    memory = new byte[amount];
  }

  @Override
  public boolean validAddress( long address ) {
    return ( address >= 0x00000000 ) && ( address <= memory.length - 1 );
  }

  @Override
  public int readByte( long address ) throws IllegalArgumentException {
    if( validAddress( address ) ) {
      int val = memory[(int)address] & 0xFF;
      System.out.println( "System.Memory - Read from 0x" +
                          Long.toHexString( address ).toUpperCase() + " : 0x" +
                          Integer.toHexString( val ).toUpperCase() );
      return val;
    }
    else {
      throw new IllegalArgumentException( Long.toHexString( address ) +
                                          " exceeds bounds. Max address is " +
                                          Long.toHexString(
                                            memory.length - 1 ) );
    }
  }

  @Override
  public void writeByte( long address, int value )
    throws IllegalArgumentException {
    if( validAddress( address ) ) {
      value &= 0xFF;
      memory[(int)address] = (byte)value;
      System.out.println( "System.Memory - Write to 0x" +
                          Long.toHexString( address ).toUpperCase() + " : 0x" +
                          Integer.toHexString( value ).toUpperCase() );
    }
    else {
      throw new IllegalArgumentException( Long.toHexString( address ) +
                                          " exceeds bounds. Max address is " +
                                          Long.toHexString(
                                            memory.length - 1 ) );
    }
  }

  @Override
  public void dump( File file ) throws IOException {
    FileOutputStream fos = new FileOutputStream( file );
    fos.write( memory );
    fos.close();
  }

  @Override
  public void load( File file ) throws IOException {
    FileInputStream fis = new FileInputStream( file );
    fis.read( memory );
    fis.close();
  }

  @Override
  public int getSize() {
    return memory.length;
  }
}

/*
 * Author: Christopher Madrigal
 * Date: 3 April 2019
 * References: None
 */

package System;

import java.io.*;

/**
 * A device that purely provides contiguous memory.
 * It must provide at least one byte of memory.
 */
public interface Memory extends BusDevice {

  /**
   * Whether the address is valid for the memory module
   * @param address The address to check
   * @return true if the address is usable, false otherwise
   */
  boolean validAddress( long address );

  /**
   * Reads out an integer whose lowest byte is the byte value at the address
   * @param address The address from which to read
   * @return The integer containing the byte value
   * @throws IllegalArgumentException If the address is not valid
   * @throws UnsupportedOperationException If the memory module cannot be
   * read from
   */
  int readByte( long address ) throws IllegalArgumentException;

  /**
   * Writes the lowest byte of the integer value to the address
   * @param address The address at which to write
   * @param value An integer containing the byte value
   * @throws IllegalArgumentException If the address is not valid
   * @throws UnsupportedOperationException If the memory module cannot be
   * written to
   */
  void writeByte( long address, int value ) throws IllegalArgumentException;

  /**
   * Dumps all available memory to the file
   * @param file The file to overwrite
   * @throws IOException if the file could not be written to
   */
  void dump( File file ) throws IOException;

  /**
   * Writes as much of the file to memory as possible
   * @param file The file to read
   * @throws IOException if the file could not be read from or does not exist
   */
  void load( File file ) throws IOException;

  /**
   * The number of bytes allocated by this memory module
   * @return The number of bytes allocated by this memory module
   */
  int getSize();
}

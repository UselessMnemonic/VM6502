/*
 * Author: Christopher Madrigal
 * Date: 5 April 2019
 * References: None
 */

package System;

import java.io.*;

/**
 * A device that provides contiguous R0M, starting at address 0.
 * It can address as much memory as the user specifies, up to 2,147,483,647
 * bytes (or about 2GB)
 * It must provide at least one byte of memory.
 * While loads can be performed, writes are not available to buses.
 */
public class ReadOnlyMemory extends RandomAccessMemory {

  /**
   * Constructs the memory module.
   * This will allocate as much memory as you specify, as much as about 2GB.
   * Be mindful of this.
   * @param amount The amount of memory to allocate.
   * @param rom The file containing the entire ROM map.
   * @throws IllegalArgumentException If amount is less than 1
   * @throws IOException If the rom file could not be read from or does not
   * exist
   */
  public ReadOnlyMemory( int amount, File rom ) throws IllegalArgumentException,
                                                       IOException {
    super( amount );
    load( rom );
  }

  @Override
  public void writeByte( long address, int value ) {
    throw new UnsupportedOperationException( "ROM cannot be written to" );
  }
}

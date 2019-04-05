/*
 * Author: Christopher Madrigal
 * Date: 2 April 2019
 * References: None
 */

package System;

import java.util.HashMap;

/**
 * A 16-bit Data and 32-bit Address bus through which devices can communicate.
 * In the interest of expandability, this bus can address 2^32 locations. The
 * upper limit is for the programmer to decide.
 */
public class Bus {

  //Addressing limits
  private static final long MIN_ADDRESS = 0x00000000L;
  private static final long MAX_ADDRESS = 0xFFFFFFFFL;

  //The System.Bus maps addresses to devices
  private HashMap< Long, BusDevice > respondents;

  /**
   * Constructor for the bus.
   */
  public Bus() {
    respondents = new HashMap< Long, BusDevice >();
  }

  /**
   * Connects the device to the bus using the range of addresses provided.
   * @param device the Device to connect
   * @param address the address that the device will respond to
   * @throws IllegalArgumentException if the address exceeds the addressing
   * limits
   */
  public void connectDevice( BusDevice device, long address )
    throws IllegalArgumentException {

    //check for proper address
    if( inBounds( address ) ) {
      respondents.put( address, device );
    }
    else {
      throw new IllegalArgumentException(
        "Address " + Long.toHexString( address ) + " exceeds the bounds [" +
        Long.toHexString( MIN_ADDRESS ) + ", " +
        Long.toHexString( MAX_ADDRESS ) + " ]" );
    }
  }

  /**
   * Returns the byte mapped to the given address.
   * @param address The address at which the device is mapped
   * @return An integer containing the byte value
   * @throws InaddressableException If the address does not map to any known
   * device
   * @throws IllegalArgumentException If the address falls outside of the
   * valid address range
   */
  public int readByte( long address ) throws IllegalArgumentException,
                                             InaddressableException {
    //check for proper address
    if( inBounds( address ) ) {

      //grab the respondent
      BusDevice target = respondents.get( address );

      //check it exists
      if( target == null ) {
        throw new InaddressableException( address );
      }

      //return value from target
      return target.readByte( address );
    }
    else {
      throw new IllegalArgumentException(
        "Address " + Long.toHexString( address ) + " exceeds the bounds [" +
        Long.toHexString( MIN_ADDRESS ) + ", " + Long.toHexString( MAX_ADDRESS )
        + " ]" );
    }
  }

  /**
   * Writes the byte to the given address. The value will be truncated to its
   * byte value
   * @param address The address at which the device is mapped
   * @param value And integer containing the byte value
   * @throws InaddressableException If the address does not map to any known
   * device
   * @throws IllegalArgumentException If the address falls outside of the
   * valid address range
   */
  public void writeByte( long address, int value )
    throws IllegalArgumentException, InaddressableException {

    //check for proper address
    if( inBounds( address ) ) {

      //grab the respondent
      BusDevice target = respondents.get( address );

      //check it exists
      if( target == null ) {
        throw new InaddressableException( address );
      }

      //write value to target
      target.writeByte( address, value );
    }
    else {
      throw new IllegalArgumentException(
        "Address " + Long.toHexString( address ) + " exceeds the bounds [" +
        Long.toHexString( MIN_ADDRESS ) + ", " + Long.toHexString( MAX_ADDRESS )
        + " ]" );
    }
  }

  /**
   * Whether the address is a valid location for the bus
   * @param address The address to verify
   * @return True if the address is in [0x00000000, 0xFFFFFFFF], false otherwise
   */
  public static boolean inBounds( long address ) {
    return ( MIN_ADDRESS <= address ) && ( address <= MAX_ADDRESS );
  }
}

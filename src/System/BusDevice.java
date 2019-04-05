/*
 * Author: Christopher Madrigal
 * Date: 2 April 2019
 * References: None
 */

package System;

/**
 * An interface for devices that rely on a bus for communication.
 */
public interface BusDevice {
  /**
   * Returns the byte mapped to the given address.
   * @param address The address at which the device is mapped
   * @return An integer containing the byte value
   */
  public int readByte( long address );

  /**
   * Maps the byte to the given address.
   * @param address The address at which the device is mapped
   * @param value And integer containing the byte value
   */
  public void writeByte( long address, int value );
}

/*
 * Author: Christopher Madrigal
 * Date: 4 April 2019
 * References: None
 */

package Tests;

import System.Bus;
import System.BusDevice;
import System.InaddressableException;

/**
 * Implements the System.BusDevice interface to reflect the reads and writes on the
 * bus. Addresses 0 to 0xFFFF are tested.
 */
public class BusTest implements BusDevice {

  @Override
  public int readByte( long address ) {
    System.out.println( "Device read from " + Long.toHexString( address ) );
    return (byte)address;
  }

  @Override
  public void writeByte( long address, int value ) {
    System.out.println( "Device write to " + Long.toHexString( address ));
  }

  public static void main( String args[] ) throws InaddressableException {

    //construct bus and the test device
    Bus bus = new Bus();
    BusTest test = new BusTest();

    //test device will take up all the bus space
    for( long addr = 0; addr <= 0xFFFF; addr++ ) {
      bus.connectDevice( test, addr );
    }

    for( long addr = 0; addr <= 0xFFFF; addr++ ) {
      //write to all 2^16 addresses
      bus.writeByte( addr, 0 );
    }

    for( long addr = 0; addr <= 0xFFFF; addr++ ) {
      //read from all 2^16 addresses
      assert( bus.readByte( addr ) == (byte)addr);
    }
  }
}

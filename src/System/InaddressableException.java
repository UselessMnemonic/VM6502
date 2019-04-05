/*
 * Author: Christopher Madrigal
 * Date: 1 April 2019
 * References:
 *   http://archive.6502.org/books/mcs6500_family_hardware_manual.pdf
 */

package System;

/**
 * An exception thrown by buses when an address is valid, but does not map to
 * a valid source of data.
 */
public class InaddressableException extends Exception {
  public InaddressableException( long address ) {
    super( "The address " + Long.toHexString( address ) + " does not map to " +
           "any data." );
  }
}

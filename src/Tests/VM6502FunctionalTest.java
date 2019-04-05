/*
 * Author: Christopher Madrigal
 * Date: 1 April 2019
 * References:
 *   http://archive.6502.org/books/mcs6500_family_hardware_manual.pdf
 */

package Tests;

import System.RandomAccessMemory;
import System.Bus;
import System.InaddressableException;
import System.CPU6502;

import java.io.File;
import java.io.IOException;

/**
 * <p>A test of the NMOS 6502. Currently the test fills Zero-Page with 1's, and
 * the stack with 2's.</p>
 * <p>You can check the validity of the program by checking the dump of
 * memory "dump"</p>
 */
public class VM6502FunctionalTest {

  private static final String STATS_FMT = "CPU Stats:\n"+
                                          "    R_A = 0x%02x\n" +
                                          "    R_X = 0x%02x\n" +
                                          "    R_Y = 0x%02x\n" +
                                          "    R_SP = 0x%02x\n" +
                                          "    Flags = %s\n";

  public static void main( String[] args ) throws InaddressableException,
                                                  IOException {

    //create memory
    RandomAccessMemory mem = new RandomAccessMemory( 65536 );

    //create bus
    Bus bus = new Bus();

    //connect memory to bus
    for( int addr = 0; addr <= 0xFFFF; addr++ ) {
      bus.connectDevice( mem, addr );
    }

    //create CPU
    CPU6502 cpu = new CPU6502( bus );

    //reset vector -> 0x0200
    mem.writeByte( cpu.getRESTVector(), 0x00 );
    mem.writeByte( cpu.getRESTVector() + 1, 0x02 );

    /* setup program to write zero-page full of 1's
     * 0x0200: LDX #$FF   ;X = 0xFF
     * 0x0202: LDA #$01   ;A = 1
     * 0x0204: STA $00,X  ;*X = A
     * 0x0206: DEX        ;X--
     * 0x0207: BNE #$FD   ;if X != 0, PC -= 3
     * 0x0209: LDX #$FF   ;X = 0xFF
     * 0x020B: LDA #$02   ;A = 2
     * 0x020D: PHA        ;push A
     * 0x020E: DEX        ;X--
     * 0x020F: BNE #$FE   ;if X != 0, PC -= 2
     * 0x0212: BEQ #$00   ;halt
     */

    mem.writeByte( 0x0200, 0xA2 ); //LDX #$FF
    mem.writeByte( 0x0201, 0xFF );

    mem.writeByte( 0x0202, 0xA9 ); //LDA #$01
    mem.writeByte( 0x0203, 0x01 );

    mem.writeByte( 0x0204, 0x95 ); //STA $00,X
    mem.writeByte( 0x0205, 0x00 );

    mem.writeByte( 0x0206, 0xCA ); //DEX

    mem.writeByte( 0x0207, 0xD0 ); //BNE -5
    mem.writeByte( 0x0208, 0xFB );

    mem.writeByte( 0x0209, 0xA2 ); //LDX #$FF
    mem.writeByte( 0x020A, 0xFF );

    mem.writeByte( 0x020B, 0xA9 ); //LDA #$02
    mem.writeByte( 0x020C, 0x02 );

    mem.writeByte( 0x020D, 0x48 ); //PHA

    mem.writeByte( 0x020E, 0xca ); //DEX

    mem.writeByte( 0x020F, 0xD0 ); //BNE -4
    mem.writeByte( 0x0210, 0xFC );

    mem.writeByte( 0x0211, 0xF0 ); //BEQ -2
    mem.writeByte( 0x0212, 0xFE );

    //reset condition
    cpu.reset();

    //File for output
    File output = new File( "dump" );

    //loop forever, at 1 kHz
    long start = System.currentTimeMillis();
    while( true ) {
      if( ( System.currentTimeMillis() - start ) > 10 ) {
        start = System.currentTimeMillis();
        cpu.step();
        mem.dump( output );
        System.out.format( STATS_FMT, cpu.getAccumulator(), cpu.getXRegister(),
                           cpu.getYRegister(),
                           cpu.getStackPointer(),
                           String.format( "%8s",
                                          Integer.toBinaryString(
                                            cpu.getStatusRegister() ) )
                                 .replace(' ', '0') );
      }
    }
  }
}
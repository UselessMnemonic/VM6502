/*
 * Author: Christopher Madrigal
 * Date: 19 March 2019
 * References:
 *   http://archive.6502.org/books/mcs6500_family_hardware_manual.pdf
 *   http://nparker.llx.com/a2/opcodes.html
 */

package System;

/**
 * <p>This is the definition of the heart and soul of this project--the NMOS
 * 6502, and some of its revisions. It is the brain of the machine and must be
 * able to execute any program designed for it exactly as specification
 * intended.</p>
 * <p>This device should be able to emulate the following in the respective
 * level of compatibility:</p>
 * <ul>
 * <li>6502 - ?%</li>
 * <li>65C02 - 0%</li>
 * <li>65C816 - 0%</li>
 * <li>M65816 (custom set for this project) - 0%</li>
 * </ul>
 * <p>To determine the best hardware revision to use, the user may manually
 * specify the desired CPU version.</p>
 * <p>No support for hardware bugs is planned solely on the principle that they
 * can vary between manufactures. It should be possible to manually select a
 * mode within the input program using operations in the M65816 custom
 * revision.</p>
 * <p>The CPU exposes a few sets of lines, each serving critical
 * processes.</p>
 * <b>Interrupt Logic</b>
 * <p>The CPU runs three lines to external logic as part of its interrupt
 * logic.</p>
 * <ul>
 * <li>RES - Assumed to be set low while the system is powering on. When
 * this line receives a signal, the processor will begin program
 * execution. The interrupt vectors should be manually set before the CPU
 * runs any program!</li>
 * <li>IRQ - Interrupts the processor anytime it is set low, and only when the
 * internal Interrupt Disable flag is set to 0.</li>
 * <li>NMI - Edge-sensitive. Instead of being programmatically handled,
 * interrupts on this line are always polled only when it is first set high,
 * then low.
 * </li>
 * </ul>
 * <br>
 * <b>Data System.Bus</b>
 * <p>The CPU has 8 lines for each bit of data. Collectively, they will be
 * referred to as just "Data." </p>
 * <b>Address System.Bus</b>
 * <p>The CPU also has 16 lines for each bit of addressing. Collectively, they
 * will be referred to as just "Addressing"</p>
 * <br>
 * <table summary="Describes the addressing modes of each operation.">
 * <caption><b>Opcode Matrix</b></caption>
 * <tbody>
 * <tr>
 * <th rowspan="2">High nibble</th>
 * <th colspan="12">Low nibble</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <th>1</th>
 * <th>2</th>
 * <th>4</th>
 * <th>5</th>
 * <th>6</th>
 * <th>8</th>
 * <th>9</th>
 * <th>A</th>
 * <th>C</th>
 * <th>D</th>
 * <th>E</th>
 * </tr>
 * <tr>
 * <th>0</th>
 * <td bgcolor="#e0e0e0">BRK</td>
 * <td bgcolor="#ffe0ff">ORA (<i>ind</i>,X)</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">ORA <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">ASL <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">PHP</td>
 * <td bgcolor="#e0ffe0">ORA #</td>
 * <td bgcolor="#e0e0e0">ASL A</td>
 * <td></td>
 * <td bgcolor="#e0ffff">ORA <i>abs</i></td>
 * <td bgcolor="#e0ffff">ASL <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>1</th>
 * <td bgcolor="#ffffe0">BPL <i>rel</i></td>
 * <td bgcolor="#ffe0ff">ORA (<i>ind</i>),Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">ORA <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">ASL <i>zpg</i>,X</td>
 * <td bgcolor="#e0e0e0">CLC</td>
 * <td bgcolor="#e0ffff">ORA <i>abs</i>,Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#e0ffff">ORA <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">ASL <i>abs</i>,X</td>
 * </tr>
 * <tr>
 * <th>2</th>
 * <td bgcolor="#e0ffff">JSR <i>abs</i></td>
 * <td bgcolor="#ffe0ff">AND (<i>ind</i>,X)</td>
 * <td></td>
 * <td bgcolor="#ffe0e0">BIT <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">AND <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">ROL <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">PLP</td>
 * <td bgcolor="#e0ffe0">AND #</td>
 * <td bgcolor="#e0e0e0">ROL A</td>
 * <td bgcolor="#e0ffff">BIT <i>abs</i></td>
 * <td bgcolor="#e0ffff">AND <i>abs</i></td>
 * <td bgcolor="#e0ffff">ROL <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>3</th>
 * <td bgcolor="#ffffe0">BMI <i>rel</i></td>
 * <td bgcolor="#ffe0ff">AND (<i>ind</i>),Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">AND <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">ROL <i>zpg</i>,X</td>
 * <td bgcolor="#e0e0e0">SEC</td>
 * <td bgcolor="#e0ffff">AND <i>abs</i>,Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#e0ffff">AND <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">ROL <i>abs</i>,X</td>
 * </tr>
 * <tr>
 * <th>4</th>
 * <td bgcolor="#e0e0e0">RTI</td>
 * <td bgcolor="#ffe0ff">EOR (<i>ind</i>,X)</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">EOR <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">LSR <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">PHA</td>
 * <td bgcolor="#e0ffe0">EOR #</td>
 * <td bgcolor="#e0e0e0">LSR A</td>
 * <td bgcolor="#e0ffff">JMP <i>abs</i></td>
 * <td bgcolor="#e0ffff">EOR <i>abs</i></td>
 * <td bgcolor="#e0ffff">LSR <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>5</th>
 * <td bgcolor="#ffffe0">BVC <i>rel</i></td>
 * <td bgcolor="#ffe0ff">EOR (<i>ind</i>),Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">EOR <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">LSR <i>zpg</i>,X</td>
 * <td bgcolor="#e0e0e0">CLI</td>
 * <td bgcolor="#e0ffff">EOR <i>abs</i>,Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#e0ffff">EOR <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">LSR <i>abs</i>,X</td>
 * </tr>
 * <tr>
 * <th>6</th>
 * <td bgcolor="#e0e0e0">RTS</td>
 * <td bgcolor="#ffe0ff">ADC (<i>ind</i>,X)</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">ADC <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">ROR <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">PLA</td>
 * <td bgcolor="#e0ffe0">ADC #</td>
 * <td bgcolor="#e0e0e0">ROR A</td>
 * <td bgcolor="#ffe0ff">JMP (<i>ind</i>)</td>
 * <td bgcolor="#e0ffff">ADC <i>abs</i></td>
 * <td bgcolor="#e0ffff">ROR <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>7</th>
 * <td bgcolor="#ffffe0">BVS <i>rel</i></td>
 * <td bgcolor="#ffe0ff">ADC (<i>ind</i>),Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">ADC <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">ROR <i>zpg</i>,X</td>
 * <td bgcolor="#e0e0e0">SEI</td>
 * <td bgcolor="#e0ffff">ADC <i>abs</i>,Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#e0ffff">ADC <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">ROR <i>abs</i>,X</td>
 * </tr>
 * <tr>
 * <th>8</th>
 * <td></td>
 * <td bgcolor="#ffe0ff">STA (<i>ind</i>,X)</td>
 * <td></td>
 * <td bgcolor="#ffe0e0">STY <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">STA <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">STX <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">DEY</td>
 * <td></td>
 * <td bgcolor="#e0e0e0">TXA</td>
 * <td bgcolor="#e0ffff">STY <i>abs</i></td>
 * <td bgcolor="#e0ffff">STA <i>abs</i></td>
 * <td bgcolor="#e0ffff">STX <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>9</th>
 * <td bgcolor="#ffffe0">BCC <i>rel</i></td>
 * <td bgcolor="#ffe0ff">STA (<i>ind</i>),Y</td>
 * <td></td>
 * <td bgcolor="#ffe0e0">STY <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">STA <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">STX <i>zpg</i>,Y</td>
 * <td bgcolor="#e0e0e0">TYA</td>
 * <td bgcolor="#e0ffff">STA <i>abs</i>,Y</td>
 * <td bgcolor="#e0e0e0">TXS</td>
 * <td></td>
 * <td bgcolor="#e0ffff">STA <i>abs</i>,X</td>
 * <td></td>
 * </tr>
 * <tr>
 * <th>A</th>
 * <td bgcolor="#e0ffe0">LDY #</td>
 * <td bgcolor="#ffe0ff">LDA (<i>ind</i>,X)</td>
 * <td bgcolor="#e0ffe0">LDX #</td>
 * <td bgcolor="#ffe0e0">LDY <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">LDA <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">LDX <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">TAY</td>
 * <td bgcolor="#e0ffe0">LDA #</td>
 * <td bgcolor="#e0e0e0">TAX</td>
 * <td bgcolor="#e0ffff">LDY <i>abs</i></td>
 * <td bgcolor="#e0ffff">LDA <i>abs</i></td>
 * <td bgcolor="#e0ffff">LDX <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>B</th>
 * <td bgcolor="#ffffe0">BCS <i>rel</i></td>
 * <td bgcolor="#ffe0ff">LDA (<i>ind</i>),Y</td>
 * <td></td>
 * <td bgcolor="#ffe0e0">LDY <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">LDA <i>zpg</i>,Y</td>
 * <td bgcolor="#ffe0e0">LDX <i>zpg</i>,Y</td>
 * <td bgcolor="#e0e0e0">CLV</td>
 * <td bgcolor="#e0ffff">LDA <i>abs</i>,Y</td>
 * <td bgcolor="#e0e0e0">TSX</td>
 * <td bgcolor="#e0ffff">LDY <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">LDA <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">LDX <i>abs</i>,Y</td>
 * </tr>
 * <tr>
 * <th>C</th>
 * <td bgcolor="#e0ffe0">CPY #</td>
 * <td bgcolor="#ffe0ff">CMP (<i>ind</i>,X)</td>
 * <td></td>
 * <td bgcolor="#ffe0e0">CPY <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">CMP <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">DEC <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">INY</td>
 * <td bgcolor="#e0ffe0">CMP #</td>
 * <td bgcolor="#e0e0e0">DEX</td>
 * <td bgcolor="#e0ffff">CPY <i>abs</i></td>
 * <td bgcolor="#e0ffff">CMP <i>abs</i></td>
 * <td bgcolor="#e0ffff">DEC <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>D</th>
 * <td bgcolor="#ffffe0">BNE <i>rel</i></td>
 * <td bgcolor="#ffe0ff">CMP (<i>ind</i>),Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">CMP <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">DEC <i>zpg</i>,X</td>
 * <td bgcolor="#e0e0e0">CLD</td>
 * <td bgcolor="#e0ffff">CMP <i>abs</i>,Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#e0ffff">CMP <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">DEC <i>abs</i>,X</td>
 * </tr>
 * <tr>
 * <th>E</th>
 * <td bgcolor="#e0ffe0">CPX #</td>
 * <td bgcolor="#ffe0ff">SBC (<i>ind</i>,X)</td>
 * <td></td>
 * <td bgcolor="#ffe0e0">CPX <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">SBC <i>zpg</i></td>
 * <td bgcolor="#ffe0e0">INC <i>zpg</i></td>
 * <td bgcolor="#e0e0e0">INX</td>
 * <td bgcolor="#e0ffe0">SBC #</td>
 * <td bgcolor="#e0e0e0">NOP</td>
 * <td bgcolor="#e0ffff">CPX <i>abs</i></td>
 * <td bgcolor="#e0ffff">SBC <i>abs</i></td>
 * <td bgcolor="#e0ffff">INC <i>abs</i></td>
 * </tr>
 * <tr>
 * <th>F</th>
 * <td bgcolor="#ffffe0">BEQ <i>rel</i></td>
 * <td bgcolor="#ffe0ff">SBC (<i>ind</i>),Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#ffe0e0">SBC <i>zpg</i>,X</td>
 * <td bgcolor="#ffe0e0">INC <i>zpg</i>,X</td>
 * <td bgcolor="#e0e0e0">SED</td>
 * <td bgcolor="#e0ffff">SBC <i>abs</i>,Y</td>
 * <td></td>
 * <td></td>
 * <td bgcolor="#e0ffff">SBC <i>abs</i>,X</td>
 * <td bgcolor="#e0ffff">INC <i>abs</i>,X</td>
 * </tr>
 * </tbody>
 * </table>
 * <b>Addressing Modes</b>
 * <ul>
 * <li><b>Implied</b> - The operand is implied</li>
 * <li><b>Immediate</b> - The operand is a literal value</li>
 * <li><b>Accumulator</b> - The operand is in the Accumulator</li>
 * <li><b>Relative</b> - The operand is some 8-bit signed offset from the
 * current PC</li>
 * <li><b>Absolute</b> - The operand is some 16-bit address in memory;
 * 0xHHLL</li>
 * <li><b>Zero-Paged</b> - The operand is some 8-bit address at page 0;
 * 0x00LL</li>
 * <li><b>Indirect</b> - The operand is some 16-bit pointer, pointing to
 * a 16-bit address</li>
 * <li><b>Absolute Indexed</b> - The operand is some 16-bit address in
 * memory, plus the value stored in either the X or Y registers</li>
 * <li><b>Zero-Page Indexed</b> - The same as Absolute Indexed, except
 * the effective address is limited to page 0</li>
 * <li><b>Indexed Indirect</b> - The operand is some 8-bit base address,
 * added with the value in the X register</li>
 * <li><b>Indirect Indexed</b> - The operand is some 8-bit pointer into
 * page 0, whose <i>target</i> value is added with the value in the Y
 * register.</li>
 * </ul>
 */

public class CPU6502 {

  //Debugging strings
  private String opcodeName;
  private int opcodeArgument;
  private String opcodeFormat;

  private static final String IMM_FMT   = "%s #$%02x\n";
  private static final String ZPG_FMT   = "%s $%02x\n";
  private static final String ZPG_X_FMT = "%s $%02x,X\n";
  private static final String ZPG_Y_FMT = "%s $%02x,Y\n";
  private static final String ABS_FMT   = "%s $%04x\n";
  private static final String ABS_X_FMT = "%s $%04x,X\n";
  private static final String ABS_Y_FMT = "%s $%04x,Y\n";
  private static final String IND_FMT   = "%s ($%04x)\n";
  private static final String IND_X_FMT = "%s ($%02x,X)\n";
  private static final String IND_Y_FMT = "%s ($%02x),Y\n";
  private static final String REL_FMT   = "%s %+d\n";
  private static final String A_FMT     = "%s A\n";
  private static final String IMP_FMT   = "%s\n";
  private static final String INVALID_FMT = "%s 0x%02x\n";

  /* Interrupt Vector Pointers
   * These memory locations are reserved for the interrupt vectors, pointers
   * to code for dealing with each interrupt. The memory at these addresses
   * contain the low bytes of each pointer value, followed by the high bytes.
   *
   * 0xFFFA : NMI Low
   * 0xFFFB : NMI High
   * 0xFFFC : RESET Low
   * 0xFFFD : RESET High
   * 0xFFFE : IRQ Low
   * 0xFFFF : IRQ High
   */
  private static final int V_NMI = 0xFFFA;
  private static final int V_RESET = 0xFFFC;
  private static final int V_IRQ = 0xFFFE;

  /* Registers
   * The CPU has the following registers:
   * - Accumulator, 8 bits
   * - X and Y indexing registers, 8 bits
   * - Stack Pointer register, 8 bits
   * - Program Counter register, 16 bits
   * - processor Status register, 8 bits
   */
  private int R_A;
  private int R_X;
  private int R_Y;
  private int R_SP;
  private int R_PC;
  private int R_S;

  /* Status Register
   * The status register on the MOS6502 is organized as follows:
   *
   *    N | V | ? | B | D | I | Z | C
   *
   * N : Negative arithmetic result
   * V : oVerflow following arithmetic operation
   * ? : Unused, reserved.
   * B : Break, or Subroutine
   * D : Decimal arithmetic enabled
   * I : Interrupt ongoing
   * Z : Zero arithmetic result
   * C : Carry following arithmetic operation
   */
  public static final int F_CARRY = 0x01;
  public static final int F_ZERO = 0x02;
  public static final int F_IRQ = 0x04;
  public static final int F_DECIMAL = 0x08;
  public static final int F_BREAK = 0x10;
  public static final int F_OVERFLOW = 0x40;
  public static final int F_NEG = 0x80;

  /* Stack Range
   * The stack lives in memory page 1. That is, it lives between
   * 0x0100 and 0x01FF. Only the low byte changes because the stack
   * pointer is 8 bits.
   */
  private static final int STACK_START = 0x0100;

  /* Pointer Arithmetic
   * Since the 6502 can address 2^16 memory locations, and can hold 8-bit
   * values, but is emulated using integers, we need masks to ensure arithmetic
   * is confined to the first 16 or 8 least-significant bytes.
   */
  private static final int MASK_8 = 0x000000FF;
  private static final int MASK_16 = 0x0000FFFF;
  private static final int NEG_BIT_8 = 0x00000080;
  private static final int NEG_BIT_16 = 0x00008000;

  //R/W, NMI, IRQ and RESET Lines
  boolean nmi;
  boolean irq;
  boolean reset;

  //Working variables to minimize bus reads
  private int opcode;
  private int effectiveAddress;

  //CPU needs a System.Bus that can address to data
  private Bus bus;

  /**
   * <p>
   * The constructor for the CPU, which requires a System.Bus on which to
   * perform IO
   * </p>
   * <p>
   * Upon startup, it is assumed only the IRQ line is set high, and all other
   * lines are set low.
   * </p>
   */
  public CPU6502( Bus bus ) {
    this.bus = bus;
    nmi = false;
    irq = false; //debugging, set back to true
    reset = false;
  }

  /**
   * Resets program execution starting from where the reset vector points.
   * @throws InaddressableException If the Reset Vector Address is not
   * reachable from the bus
   */
  public void reset() throws InaddressableException {
    //set the program counter to the reset vector's value
    R_PC = readWordLE( V_RESET );
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * <p>Performs an operation, updating internal values as needed.</p>
   * <ul>
   * <li>First, it fetches the opcode and decodes it.</li>
   * <li>Then, it loads in any operands.</li>
   * <li>Finally, it sets the program counter to the address of the next
   * opcode.</li>
   * </ul>
   * <p>For interrupts, NMI's are handled before software IRQ's.</p>
   * @throws InaddressableException If the CPU attempts to read from a bus
   * address that is not mapped to any known device.
   */
  public void step() throws InaddressableException {

    //Test for NMI
    if( nmi ) {
      processNMI();
    }

    //Test for IRQ and flag
    if( irq & !flagSet( F_IRQ ) ) {
      processIRQ( R_PC );
    }

    System.out.println( "6502 - Start Step, PC @ 0x" +
                        Integer.toHexString( R_PC ) );

    //Fetch Opcode from bus. Address is in PC counter.
    opcode = bus.readByte( R_PC );

    //Decode and fetch operands. Calculates an effective address.
    switch( opcode ) {
      case 0x00: //BRK
        opcodeName = "BRK";
        opcodeFormat = IMP_FMT;
        BRK();
        break;
      case 0x01: //ORA (ind,X)
        opcodeName = "ORA";
        handleIdxInd();
        ORA();
        break;
      case 0x05: //ORA zpg
        opcodeName = "ORA";
        handleZpg();
        ORA();
        break;
      case 0x06: //ASL zpg
        opcodeName = "ASL";
        handleZpg();
        ASL();
        break;
      case 0x08: //PHP
        opcodeName = "PHP";
        opcodeFormat = IMP_FMT;
        PHP();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x09: //ORA #
        opcodeName = "ORA";
        handleImm();
        ORA();
        break;
      case 0x0A: //ASL A
        opcodeName = "ASL";
        opcodeFormat = A_FMT;
        ASL_A();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x0D: //ORA abs
        opcodeName = "ORA";
        handleAbs();
        ORA();
        break;
      case 0x0E: //ASL abs
        opcodeName = "ASL";
        handleAbs();
        ASL();
        break;

      case 0x10: //BPL rel
        opcodeName = "BPL";
        handleRel();
        BPL();
        break;
      case 0x11: //ORA (ind),Y
        opcodeName = "ORA";
        handleIndIdx();
        ORA();
        break;
      case 0x15: //ORA zpg,X
        opcodeName = "ORA";
        handleZpgX();
        ORA();
        break;
      case 0x16: //ASL zpg,X
        opcodeName = "ASL";
        handleZpgX();
        ASL();
        break;
      case 0x18: //CLC
        opcodeName = "CLC";
        opcodeFormat = IMP_FMT;
        CLC();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x19: //ORA abs,Y
        opcodeName = "ORA";
        handleAbsY();
        ORA();
        break;
      case 0x1D: //ORA abs,X
        opcodeName = "ORA";
        handleAbsX();
        ORA();
        break;
      case 0x1E: //ASL abs,X
        opcodeName = "ASL";
        handleAbsX();
        ASL();
        break;

      case 0x20: //JSR abs
        opcodeName = "JSR";
        handleAbs();
        JSR();
        break;
      case 0x21: //AND (ind,x)
        opcodeName = "AND";
        handleIdxInd();
        AND();
        break;
      case 0x24: //BIT zpg
        opcodeName = "BIT";
        handleZpg();
        BIT();
        break;
      case 0x25: //AND zpg
        opcodeName = "AND";
        handleZpg();
        AND();
        break;
      case 0x26: //ROL zpg
        opcodeName = "ROL";
        handleZpg();
        ROL();
        break;
      case 0x28: //PLP
        opcodeName = "PLP";
        opcodeFormat = IMP_FMT;
        PLP();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x29: //AND #
        opcodeName = "AND";
        handleImm();
        AND();
        break;
      case 0x2A: //ROL A
        opcodeName = "ROL";
        opcodeFormat = A_FMT;
        ROL_A();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x2C: //BIT abs
        opcodeName = "BIT";
        handleAbs();
        BIT();
        break;
      case 0x2D: //AND abs
        opcodeName = "AND";
        handleAbs();
        AND();
        break;
      case 0x2E: //ROL abs
        opcodeName = "ROL";
        handleAbs();
        ROL();
        break;

      case 0x30: //BMI rel
        opcodeName = "BMI";
        handleRel();
        BMI();
        break;
      case 0x31: //AND (ind),Y
        opcodeName = "AND";
        handleIndIdx();
        AND();
        break;
      case 0x35: //AND zpg,X
        opcodeName = "AND";
        handleZpgX();
        AND();
        break;
      case 0x36: //ROL zpg,X
        opcodeName = "ROL";
        handleZpgX();
        ROL();
        break;
      case 0x38: //SEC
        opcodeName = "SEC";
        opcodeFormat = IMP_FMT;
        SEC();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x39: //AND abs,Y
        opcodeName = "AND";
        handleAbsY();
        AND();
        break;
      case 0x3D: //AND abs,X
        opcodeName = "AND";
        handleAbsX();
        AND();
        break;
      case 0x3E: //ROL abs,X
        opcodeName = "ROL";
        handleAbsX();
        ROL();
        break;

      case 0x40: //RTI
        opcodeName = "RTI";
        opcodeFormat = IMP_FMT;
        RTI();
        break;
      case 0x41: //EOR (ind,x)
        opcodeName = "EOR";
        handleIdxInd();
        EOR();
        break;
      case 0x45: //EOR zpg
        opcodeName = "EOR";
        handleZpg();
        EOR();
        break;
      case 0x46: //LSR zpg
        opcodeName = "LSR";
        handleZpg();
        LSR();
        break;
      case 0x48: //PHA
        opcodeName = "PHA";
        PHA();
        opcodeFormat = IMP_FMT;
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x49: //EOR #
        opcodeName = "EOR";
        handleImm();
        EOR();
        break;
      case 0x4A: //LSR A
        opcodeName = "LSR";
        opcodeFormat = A_FMT;
        LSR_A();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x4C: //JMP abs
        opcodeName = "JMP";
        handleAbs();
        JMP();
        break;
      case 0x4D: //EOR abs
        opcodeName = "EOR";
        handleAbs();
        EOR();
        break;
      case 0x4E: //LSR abs
        opcodeName = "LSR";
        handleAbs();
        LSR();
        break;

      case 0x50: //BVC rel
        opcodeName = "BVC";
        handleRel();
        BVC();
        break;
      case 0x51: //EOR (ind),Y
        opcodeName = "EOR";
        handleIndIdx();
        EOR();
        break;
      case 0x55: //EOR zpg,X
        opcodeName = "EOR";
        handleZpgX();
        EOR();
        break;
      case 0x56: //LSR zpg,X
        opcodeName = "LSR";
        handleZpgX();
        LSR();
        break;
      case 0x58: //CLI
        opcodeName = "CLI";
        opcodeFormat = IMP_FMT;
        CLI();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x59: //EOR abs,Y
        opcodeName = "EOR";
        handleAbsY();
        EOR();
        break;
      case 0x5D: //EOR abs,X
        opcodeName = "EOR";
        handleAbsX();
        EOR();
        break;
      case 0x5E: //LSR abs,X
        opcodeName = "LSR";
        handleAbsX();
        LSR();
        break;

      case 0x60: //RTS
        opcodeName = "RTS";
        opcodeFormat = IMP_FMT;
        RTS();
        break;
      case 0x61: //ADC (ind,X)
        opcodeName = "ADC";
        handleIdxInd();
        ADC();
        break;
      case 0x65: //ADC zpg
        opcodeName = "ADC";
        handleZpg();
        ADC();
        break;
      case 0x66: //ROR zpg
        opcodeName = "ROR";
        handleZpg();
        ROR();
        break;
      case 0x68: //PLA
        opcodeName = "PLA";
        opcodeFormat = IMP_FMT;
        PLA();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x69: //ADC #
        opcodeName = "ADC";
        handleImm();
        ADC();
        break;
      case 0x6A: //ROR A
        opcodeName = "ROR";
        opcodeFormat = A_FMT;
        ROR_A();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x6C: //JMP (ind)
        opcodeName = "JMP";
        handleInd();
        JMP();
        break;
      case 0x6D: //ADC abs
        opcodeName = "ADC";
        handleAbs();
        ADC();
        break;
      case 0x6E: //ROR abs
        opcodeName = "ROR";
        handleAbs();
        ROR();
        break;

      case 0x70: //BVS rel
        opcodeName = "BVS";
        handleRel();
        BVS();
        break;
      case 0x71: //ADC (ind),Y
        opcodeName = "ADC";
        handleIndIdx();
        ADC();
        break;
      case 0x75: //ADC zpg,X
        opcodeName = "ADC";
        handleZpgX();
        ADC();
        break;
      case 0x76: //ROR zpg,X
        opcodeName = "ROR";
        handleZpgX();
        ROR();
        break;
      case 0x78: //SEI
        opcodeName = "SEI";
        opcodeFormat = IMP_FMT;
        SEI();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x79: //ADC abs,Y
        opcodeName = "ADC";
        handleAbsY();
        ADC();
        break;
      case 0x7D: //ADC abs,X
        opcodeName = "ADC";
        handleAbsX();
        ADC();
        break;
      case 0x7E: //ROR abs,X
        opcodeName = "ROR";
        handleAbsX();
        ROR();
        break;

      case 0x81: //STA (ind,X)
        opcodeName = "STA";
        handleIdxInd();
        STA();
        break;
      case 0x84: //STY zpg
        opcodeName = "STY";
        handleZpg();
        STY();
        break;
      case 0x85: //STA zpg
        opcodeName = "STA";
        handleZpg();
        STA();
        break;
      case 0x86: //STX zpg
        opcodeName = "STX";
        handleZpg();
        STX();
        break;
      case 0x88: //DEY
        opcodeName = "DEY";
        opcodeFormat = IMP_FMT;
        DEY();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x8A: //TXA
        opcodeName = "TXA";
        opcodeFormat = IMP_FMT;
        TXA();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x8C: //STY abs
        opcodeName = "STY";
        handleAbs();
        STY();
        break;
      case 0x8D: //STA abs
        opcodeName = "STA";
        handleAbs();
        STA();
        break;
      case 0x8E: //STX abs
        opcodeName = "STX";
        handleAbs();
        STX();
        break;

      case 0x90: //BCC rel
        opcodeName = "BCC";
        handleRel();
        BCC();
        break;
      case 0x91: //STA (ind),Y
        opcodeName = "STA";
        handleIndIdx();
        STA();
        break;
      case 0x94: //STY zpg,X
        opcodeName = "STY";
        handleZpgX();
        STY();
        break;
      case 0x95: //STA zpg,X
        opcodeName = "STA";
        handleZpgX();
        STA();
        break;
      case 0x96: //STX zpg,Y
        opcodeName = "STX";
        handleZpgY();
        STX();
        break;
      case 0x98: //TYA
        opcodeName = "TYA";
        opcodeFormat = IMP_FMT;
        TYA();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x99: //STA abs,Y
        opcodeName = "STA";
        handleAbsY();
        STA();
        break;
      case 0x9A: //TXS
        opcodeName = "TXS";
        opcodeFormat = IMP_FMT;
        TXS();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0x9D: //STA abs,X
        opcodeName = "STA";
        handleAbsX();
        STA();
        break;

      case 0xA0: //LDY #
        opcodeName = "LDY";
        handleImm();
        LDY();
        break;
      case 0xA1: //LDA (ind,X)
        opcodeName = "LDA";
        handleIdxInd();
        LDA();
        break;
      case 0xA2: //LDX #
        opcodeName = "LDX";
        handleImm();
        LDX();
        break;
      case 0xA4: //LDY zpg
        opcodeName = "LDY";
        handleZpg();
        LDY();
        break;
      case 0xA5: //LDA zpg
        opcodeName = "LDA";
        handleZpg();
        LDA();
        break;
      case 0xA6: //LDX zpg
        opcodeName = "LDX";
        handleZpg();
        LDX();
        break;
      case 0xA8: //TAY
        opcodeName = "TAY";
        opcodeFormat = IMP_FMT;
        TAY();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xA9: //LDA #
        opcodeName = "LDA";
        handleImm();
        LDA();
        break;
      case 0xAA: //TAX
        opcodeName = "TAX";
        opcodeFormat = IMP_FMT;
        TAX();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xAC: //LDY abs
        opcodeName = "LDY";
        handleAbs();
        LDY();
        break;
      case 0xAD: //LDA abs
        opcodeName = "LDA";
        handleAbs();
        LDA();
        break;
      case 0xAE: //LDX abs
        opcodeName = "LDX";
        handleAbs();
        LDX();
        break;

      case 0xB0: //BCS rel
        opcodeName = "BCS";
        handleRel();
        BCS();
        break;
      case 0xB1: //LDA (ind),Y
        opcodeName = "LDA";
        handleIndIdx();
        LDA();
        break;
      case 0xB4: //LDY zpg,X
        opcodeName = "LDY";
        handleZpgX();
        LDY();
        break;
      case 0xB5: //LDA zpg,Y
        opcodeName = "LDA";
        handleZpgY();
        LDA();
        break;
      case 0xB6: //LDX zpg,Y
        opcodeName = "LDX";
        handleZpgY();
        LDX();
        break;
      case 0xB8: //CLV
        opcodeName = "CLV";
        opcodeFormat = IMP_FMT;
        CLV();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xB9: //LDA abs,Y
        opcodeName = "LDA";
        handleAbsY();
        LDA();
        break;
      case 0xBA: //TSX
        opcodeName = "TSX";
        opcodeFormat = IMP_FMT;
        TSX();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xBC: //LDY abs,X
        opcodeName = "LDY";
        handleAbsX();
        LDY();
        break;
      case 0xBD: //LDA abs,X
        opcodeName = "LDA";
        handleAbsX();
        LDA();
        break;
      case 0xBE: //LDX abs,Y
        opcodeName = "LDX";
        handleAbsY();
        LDX();
        break;

      case 0xC0: //CPY #
        opcodeName = "CPY";
        handleImm();
        CPY();
        break;
      case 0xC1: //CMP (ind,X)
        opcodeName = "CMP";
        handleIdxInd();
        CMP();
        break;
      case 0xC4: //CPY zpg
        opcodeName = "CPY";
        handleZpg();
        CPY();
        break;
      case 0xC5: //CMP zpg
        opcodeName = "CMP";
        handleZpg();
        CMP();
        break;
      case 0xC6: //DEC zpg
        opcodeName = "DEC";
        handleZpg();
        DEC();
        break;
      case 0xC8: //INY
        opcodeName = "INY";
        opcodeFormat = IMP_FMT;
        INY();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xC9: //CMP #
        opcodeName = "CMP";
        handleImm();
        CMP();
        break;
      case 0xCA: //DEX
        opcodeName = "DEX";
        opcodeFormat = IMP_FMT;
        DEX();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xCC: //CPY abs
        opcodeName = "CPY";
        handleAbs();
        CPY();
        break;
      case 0xCD: //CMP abs
        opcodeName = "CMP";
        handleAbs();
        CMP();
        break;
      case 0xCE: //DEC abs
        opcodeName = "DEC";
        handleAbs();
        DEC();
        break;

      case 0xD0: //BNE rel
        opcodeName = "BNE";
        handleRel();
        BNE();
        break;
      case 0xD1: //CMP (ind),Y
        opcodeName = "CMP";
        handleIndIdx();
        CMP();
        break;
      case 0xD5: //CMP zpg,X
        opcodeName = "CMP";
        handleZpgX();
        CMP();
        break;
      case 0xD6: //DEC zpg,X
        opcodeName = "DEC";
        handleZpgX();
        DEC();
        break;
      case 0xD8: //CLD
        opcodeName = "CLD";
        opcodeFormat = IMP_FMT;
        CLD();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xD9: //CMP abs,Y
        opcodeName = "CMP";
        handleAbsY();
        CMP();
        break;
      case 0xDD: //CMP abs,X
        opcodeName = "CMP";
        handleAbsX();
        CMP();
        break;
      case 0xDE: //DEC abs,X
        opcodeName = "DEC";
        handleAbsX();
        DEC();
        break;

      case 0xE0: //CPX #
        opcodeName = "CPX";
        handleImm();
        CPX();
        break;
      case 0xE1: //SBC (ind,X)
        opcodeName = "SBC";
        handleIdxInd();
        SBC();
        break;
      case 0xE4: //CPX zpg
        opcodeName = "CPX";
        handleZpg();
        CPX();
        break;
      case 0xE5: //SBC zpg
        opcodeName = "SBC";
        handleZpg();
        SBC();
        break;
      case 0xE6: //INC zpg
        opcodeName = "INC";
        handleZpg();
        INC();
        break;
      case 0xE8: //INX
        opcodeName = "INX";
        opcodeFormat = IMP_FMT;
        INX();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xE9: //SBC #
        opcodeName = "SBC";
        handleImm();
        SBC();
        break;
      case 0xEA: //NOP
        opcodeName = "NOP";
        opcodeFormat = IMP_FMT;
        /*
         * Explicit NOP
         */
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xEC: //CPX abs
        opcodeName = "CPX";
        handleAbs();
        CPX();
        break;
      case 0xED: //SBC abs
        opcodeName = "SBC";
        handleAbs();
        SBC();
        break;
      case 0xEE: //INC abs
        opcodeName = "INC";
        handleAbs();
        INC();
        break;

      case 0xF0: //BEQ rel
        opcodeName = "BEQ";
        handleRel();
        BEQ();
        break;
      case 0xF1: //SBC (ind),Y
        opcodeName = "SBC";
        handleIndIdx();
        SBC();
        break;
      case 0xF5: //SBC zpg,X
        opcodeName = "SBC";
        handleZpgX();
        SBC();
        break;
      case 0xF6: //INC zpg,X
        opcodeName = "INC";
        handleZpgX();
        INC();
        break;
      case 0xF8: //SED
        opcodeName = "SED";
        opcodeFormat = IMP_FMT;
        SED();
        R_PC = increment16( R_PC );
        System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
        break;
      case 0xF9: //SBC abs,Y
        opcodeName = "SBC";
        handleAbsY();
        SBC();
        break;
      case 0xFD: //SBC abs,X
        opcodeName = "SBC";
        handleAbsX();
        SBC();
        break;
      case 0xFE: //INC abs,X
        opcodeName = "INC";
        handleAbsX();
        INC();
        break;

      default: //Invalid opcode trap
        handleBadOpcode();
        opcodeName = "Unknown";
        opcodeArgument = opcode;
        opcodeFormat = INVALID_FMT;
        break;
    }

    System.out.printf( "6502 - Disassembly: " + opcodeFormat, opcodeName,
                       opcodeArgument );
  }

  private void wip() {
    switch( opcode ) {
      /* Single byte instructions */
      case 0x18: //CLC
      case 0xD8: //CLD
      case 0x58: //CLI
      case 0xB8: //CLV
      case 0xCA: //DEX
      case 0x88: //DEY
      case 0xE8: //INX
      case 0xC8: //INY
      case 0xEA: //NOP
      case 0x38: //SEC
      case 0xF8: //SED
      case 0x78: //SEI
      case 0xAA: //TAX
      case 0xA8: //TAY
      case 0xBA: //TSX
      case 0x8A: //TXA
      case 0x9A: //TXS
      case 0x98: //TYA

      case 0x0A: //ASL A
      case 0x4A: //LSR A
      case 0x2A: //ROL A
      case 0x6A: //ROR A

      /* Internal execution on memory */
      case 0x69: //ADC #
      case 0x29: //AND #
      case 0xC9: //CMP #
      case 0xE0: //CPX #
      case 0xC0: //CPY #
      case 0x49: //EOR #
      case 0xA9: //LDA #
      case 0xA2: //LDX #
      case 0xA0: //LDY #
      case 0x09: //ORA #
      case 0xE9: //SBC #

      case 0x65: //ADC zpg
      case 0x25: //AND zpg
      case 0x24: //BIT zpg
      case 0xC5: //CMP zpg
      case 0xE4: //CPX zpg
      case 0xC4: //CPY zpg
      case 0x45: //EOR zpg
      case 0xA5: //LDA zpg
      case 0xA6: //LDX zpg
      case 0xA4: //LDY zpg
      case 0x05: //ORA zpg
      case 0xE5: //SBC zpg

      case 0x6D: //ADC abs
      case 0x2D: //AND abs
      case 0x2C: //BIT abs
      case 0xCD: //CMP abs
      case 0xEC: //CPX abs
      case 0xCC: //CPY abs
      case 0x4D: //EOR abs
      case 0xAD: //LDA abs
      case 0xAE: //LDX abs
      case 0xAC: //LDY abs
      case 0x0D: //ORA abs
      case 0xED: //SBC abs

      case 0x61: //ADC (ind,X)
      case 0x21: //AND (ind,X)
      case 0xC1: //CMP (ind,X)
      case 0x41: //EOR (ind,X)
      case 0xA1: //LDA (ind,X)
      case 0x01: //ORA (ind,X)
      case 0xE1: //SBC (ind,X)

      case 0x7D: //ADC abs,X
      case 0x79: //ADC abs,Y
      case 0x3D: //AND abs,X
      case 0x39: //AND abs,Y
      case 0xDD: //CMP abs,X
      case 0xD9: //CMP abs,Y
      case 0x5D: //EOR abs,X
      case 0x59: //EOR abs,Y
      case 0xBD: //LDA abs,X
      case 0xB9: //LDA abs,Y
      case 0xBE: //LDX abs,Y
      case 0xBC: //LDY abs,X
      case 0x1D: //ORA abs,X
      case 0x19: //ORA abs,Y
      case 0xFD: //SBC abs,X
      case 0xF9: //SBC abs,Y

      case 0x75: //ADC zpg,X
      case 0x35: //AND zpg,X
      case 0xD5: //CMP zpg,X
      case 0x55: //EOR zpg,X
      case 0xB5: //LDA zpg,Y
      case 0xB6: //LDX zpg,Y
      case 0xB4: //LDY zpg,X
      case 0x15: //ORA zpg,X
      case 0xF5: //SBC zpg,X

      case 0x71: //ADC (ind),Y
      case 0x31: //AND (ind),Y
      case 0xD1: //CMP (ind),Y
      case 0x51: //EOR (ind),Y
      case 0xB1: //LDA (ind),Y
      case 0x11: //ORA (ind),Y
      case 0xF1: //SBC (ind),Y

      //Store operations
      case 0x85: //STA zpg
      case 0x86: //STX zpg
      case 0x84: //STY zpg

      case 0x8D: //STA abs
      case 0x8E: //STX abs
      case 0x8C: //STY abs

      case 0x81: //STA (ind,X)

      case 0x9D: //STA abs,X
      case 0x99: //STA abs,Y

      case 0x95: //STA zpg,X
      case 0x96: //STX zpg,Y
      case 0x94: //STY zpg,X

      case 0x91: //STA (ind),Y

      /* Read-Modify-Write Operations */
      case 0x06: //ASL zpg
      case 0xC6: //DEC zpg
      case 0xE6: //INC zpg
      case 0x46: //LSR zpg
      case 0x66: //ROR zpg
      case 0x26: //ROL zpg

      case 0x0E: //ASL abs
      case 0xCE: //DEC abs
      case 0xEE: //INC abs
      case 0x4E: //LSR abs
      case 0x6E: //ROR abs
      case 0x2E: //ROL abs

      case 0x16: //ASL zpg,X
      case 0xD6: //DEC zpg,X
      case 0xF6: //INC zpg,X
      case 0x56: //LSR zpg,X
      case 0x76: //ROR zpg,X
      case 0x36: //ROL zpg,X

      case 0x1E: //ASL abs,X
      case 0xDE: //DEC abs,X
      case 0xFE: //INC abs,X
      case 0x5E: //LSR abs,X
      case 0x7E: //ROR abs,X
      case 0x3E: //ROL abs,X

      //Push
      case 0x08: //PHP
      case 0x48: //PHA

      //Pull
      case 0x68: //PLA
      case 0x28: //PLP

      //Jump
      case 0x4C: //JMP abs

      case 0x6C: //JMP (ind)

      //Branching
      case 0x90: //BCC rel
      case 0xB0: //BCS rel
      case 0xF0: //BEQ rel
      case 0x30: //BMI rel
      case 0xD0: //BNE rel
      case 0x10: //BPL rel
      case 0x50: //BVC rel
      case 0x70: //BVS rel

      //Misc.
      case 0x20: //JSR abs

      case 0x00: //BRK

      case 0x40: //RTI

      case 0x60: //RTS

      default: //Invalid opcode trap
        handleBadOpcode();
        break;
    }
  }

  /**
   * Calculates the target address for the immediate operand (PC + 1), and
   * points the PC to the next opcode (PC + 2).
   * @throws InaddressableException If PC + 1 is not reachable from the bus
   */
  private void handleImm() throws  InaddressableException {

    opcodeFormat = IMM_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //wastes a bus read...
    opcodeArgument = bus.readByte( effectiveAddress );

    System.out.println( "6502 - Imm @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address using the value of the immediate operand
   * as an offset from the PC, and points the PC to the next opcode (PC + 2).
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleRel() throws InaddressableException {

    opcodeFormat = REL_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    //now contains signed 8-bit offset
    effectiveAddress = bus.readByte( effectiveAddress );

    effectiveAddress = extendSign8( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //relative value is PC + offset
    effectiveAddress += R_PC;
    effectiveAddress &= MASK_16;

    System.out.println( "6502 - Rel @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address using the little-endian value of the
   * immediate operand as the address 0xHHLL, and points the PC to the next
   * opcode (PC + 3)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleAbs() throws InaddressableException {

    opcodeFormat = ABS_FMT;

    //absolute value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //load the value, our absolute address
    effectiveAddress = readWordLE( effectiveAddress );

    opcodeArgument = effectiveAddress;

    System.out.println( "6502 - Abs @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 3;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value at the address pointed
   * to by the immediate operand, and points the PC to the next opcode (PC + 3)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleInd() throws InaddressableException {

    opcodeFormat = IND_FMT;

    //absolute value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //load the value, our absolute address
    effectiveAddress = readWordLE( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //now contains the address pointed to by the immediate value
    effectiveAddress = readWordLE( effectiveAddress );

    System.out.println( "6502 - Ind @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 3;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand as the low byte of the address 0x00LL, and points the PC to the
   * next opcode (PC + 2)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleZpg() throws InaddressableException {

    opcodeFormat = ZPG_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //now contains address in page 0
    effectiveAddress = bus.readByte( effectiveAddress );

    opcodeArgument = effectiveAddress;

    System.out.println( "6502 - Zpg @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand plus the value in the X register, and points the PC to the next
   * opcode (PC + 3)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleAbsX() throws InaddressableException {

    opcodeFormat = ABS_X_FMT;

    //absolute value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //load the value, our absolute address
    effectiveAddress = readWordLE( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //offset by X
    effectiveAddress += R_X;
    effectiveAddress &= MASK_16;

    System.out.println( "6502 - Abs,X @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 3;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand plus the value in the Y register, and points the PC to the next
   * opcode (PC + 3)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleAbsY() throws InaddressableException {

    opcodeFormat = ABS_Y_FMT;

    //absolute value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //load the value, our absolute address
    effectiveAddress = readWordLE( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //offset by Y
    effectiveAddress += R_Y;
    effectiveAddress &= MASK_16;

    System.out.println( "6502 - Abs,Y @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 3;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand as the low byte in the address 0x00LL, plus the value in the X
   * register, and points the PC to the next opcode (PC + 2)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleZpgX() throws InaddressableException {

    opcodeFormat = ZPG_X_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //now contains address in page 0
    effectiveAddress = bus.readByte( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //offset by X
    effectiveAddress += R_X;
    effectiveAddress &= MASK_8;

    System.out.println( "6502 - Zpg,X @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand as the low byte in the address 0x00LL, plus the value in the Y
   * register, and points the PC to the next opcode (PC + 2)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleZpgY() throws InaddressableException {

    opcodeFormat = ZPG_Y_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //now contains address in page 0
    effectiveAddress = bus.readByte( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //offset by Y
    effectiveAddress += R_Y;
    effectiveAddress &= MASK_8;

    System.out.println( "6502 - Zpg,Y @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand as the low byte in the address 0x00LL plus the value in the X
   * register, then gets the value at that address (0x00LL + X), and points the
   * PC to the next opcode (PC + 2)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleIdxInd() throws InaddressableException {

    opcodeFormat = IND_X_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //now contains address in page 0
    effectiveAddress = bus.readByte( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //offset by X
    effectiveAddress += R_X;
    effectiveAddress &= MASK_8;

    //now contains the address at the calculated address
    effectiveAddress = readWordLE( effectiveAddress );

    System.out.println( "6502 - (Ind,X) @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Calculates the target address by using the value of the immediate
   * operand as the low byte in the address 0x00LL, gets the value at
   * that address, then adds the value in the Y register, and points the PC
   * to the next opcode (PC + 2)
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void handleIndIdx() throws InaddressableException {

    opcodeFormat = IND_Y_FMT;

    //immediate value is at PC + 1
    effectiveAddress = increment16( R_PC );

    //now contains address in page 0
    effectiveAddress = bus.readByte( effectiveAddress );

    opcodeArgument = effectiveAddress;

    //now contains the address at the calculated address
    effectiveAddress = readWordLE( effectiveAddress );

    //offset by Y
    effectiveAddress += R_Y;
    effectiveAddress &= MASK_16;

    System.out.println( "6502 - (Ind),Y @ 0x" +
                        Integer.toHexString( effectiveAddress ) );

    //set PC to address of next opcode
    R_PC += 2;
    R_PC &= MASK_16;

    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Performs an Add with Carry. The algorithm is taken from
   * http://6502.org/tutorials/decimal_mode.html
   * Flags affected: N Z C V
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void ADC() throws InaddressableException {

    int A = R_A;
    int AL;
    int S1, S2;
    int B = bus.readByte( effectiveAddress );
    int C = flagSet( F_CARRY ) ? 1 : 0;

    //Decimal mode
    if( flagSet( F_DECIMAL ) ) {

      //Calculate AL
      AL = ( A & 0x0F ) + ( B & 0x0F ) + C;
      if( AL >= 0x0A ) {
        AL = ( ( AL + 0x06 ) & 0x0F ) + 0x10;
      }

      //Calculate Sequence 1 sum
      S1 = ( A & 0xF0 ) + ( B & 0xF0 ) + AL;
      if( S1 >= 0xA0 ) {
        S1 += 0x60;
      }

      //Calculate Sequence 2 sum
      A = extendSign8( A );
      B = extendSign8( B );
      AL = extendSign8( AL );
      S2 = ( A & 0xF0 ) + ( B & 0xF0 ) + AL;

      R_A = S1 & MASK_8;
      setFlag( F_CARRY, S1 >= 0x100 );
      setFlag( F_NEG, isNegative8( S2 ) );
      setFlag( F_OVERFLOW, S2 < -128 || S2 > 127 );
    }

    //Binary mode
    else {
      S1 = A + B + C;
      R_A = S1 & MASK_8;
      setFlag( F_CARRY, S1 >= 0x100 );
      setFlag( F_NEG, isNegative8( S1 ) );
      setFlag( F_OVERFLOW, isNegative8( ~( A ^ B ) & ( A ^ S1 ) ) );
    }

    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Performs bitwise AND with the accumulator
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void AND() throws InaddressableException {
    int B = bus.readByte( effectiveAddress );
    R_A &= B;
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Performs ASL
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void ASL() throws InaddressableException {
    int B = bus.readByte( effectiveAddress );
    B <<= 1;
    R_A = B & MASK_8;

    setFlag( F_CARRY, ( B & 0x100 ) == 0x100 );
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Performs ASL with the accumulator
   * Flags affected: N Z C
   */
  private void ASL_A() {
    int B = R_A;
    B <<= 1;
    R_A = B & MASK_8;

    setFlag( F_CARRY, ( B & 0x100 ) == 0x100 );
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Perform a branch, if Carry bit is clear
   */
  private void BCC() {
    doBranch( F_CARRY, false, effectiveAddress );
  }

  /**
   * Perform a branch, if Carry bit is set
   */
  private void BCS() {
    doBranch( F_CARRY, true, effectiveAddress );
  }

  /**
   * Perform a branch, if Zero bit is set
   */
  private void BEQ() {
    doBranch( F_ZERO, true, effectiveAddress );
  }

  /**
   * Perform a bit test
   * Flags affected: N Z V
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void BIT() throws InaddressableException {
    int B = bus.readByte( effectiveAddress );
    setFlag( F_ZERO, ( B & R_A ) == 0 );
    setFlag( F_NEG, isNegative8( B ) );
    setFlag( F_OVERFLOW, ( B & 0x40 ) == 0x40 );
  }

  /**
   * Perform a branch, if Negative bit is set
   */
  private void BMI() {
    doBranch( F_NEG, true, effectiveAddress );
  }

  /**
   * Perform a branch, if Zero bit is clear
   */
  private void BNE() {
    doBranch( F_ZERO, false, effectiveAddress );
  }

  /**
   * Perform a branch, if Negative bit is clear
   */
  private void BPL() {
    doBranch( F_NEG, false, effectiveAddress );
  }

  /**
   * Perform a non-maskable interrupt
   * Flags affected: B
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void BRK() throws InaddressableException {
    push( highByte( R_PC ) ); //push Hi
    push( lowByte( R_PC ) ); //push PC Lo
    setFlag( F_BREAK, true );
    push( R_S ); //push status
    R_PC = readWordLE( V_IRQ );
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Perform a branch, if Overflow bit is clear
   */
  private void BVC() {
    doBranch( F_OVERFLOW, false, effectiveAddress );
  }

  /**
   * Perform a branch, if Overflow bit is set
   */
  private void BVS() {
    doBranch( F_OVERFLOW, true, effectiveAddress );
  }

  /**
   * Clear the Carry bit
   * Flags affected: C
   */
  private void CLC() {
    setFlag( F_CARRY, false );
  }

  /**
   * Clear the Decimal bit
   * Flags affected: D
   */
  private void CLD() {
    setFlag( F_DECIMAL, false );
  }

  /**
   * Clear the Interrupt Disable bit
   * Flags affected: I
   */
  private void CLI() {
    setFlag( F_IRQ, false );
  }

  /**
   * Clear the Overflow bit
   * Flags affected: V
   */
  private void CLV() {
    setFlag( F_OVERFLOW, false );
  }

  /**
   * Compare with the Accumulator
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void CMP() throws InaddressableException {
    compare( R_A, bus.readByte( effectiveAddress ) );
  }

  /**
   * Compare with the X Register
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void CPX() throws InaddressableException {
    compare( R_X, bus.readByte( effectiveAddress ) );
  }

  /**
   * Compare with the Y Register
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void CPY() throws InaddressableException {
    compare( R_Y, bus.readByte( effectiveAddress ) );
  }

  /**
   * Decrement a value.
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void DEC() throws InaddressableException {
    int R = bus.readByte( effectiveAddress );
    R = decrement8( R );
    bus.writeByte( effectiveAddress, R );
    setFlag( F_NEG, isNegative8( R ) );
    setFlag( F_ZERO, R == 0 );
  }

  /**
   * Decrement the X register.
   * Flags affected: N Z
   */
  private void DEX() {
    R_X = decrement8( R_X );
    setFlag( F_NEG, isNegative8( R_X ) );
    setFlag( F_ZERO, R_X == 0 );
  }

  /**
   * Decrement the Y register.
   * Flags affected: N Z
   */
  private void DEY() {
    R_Y = decrement8( R_Y );
    setFlag( F_NEG, isNegative8( R_Y ) );
    setFlag( F_ZERO, R_Y == 0 );
  }

  /**
   * Perform XOR with accumulator
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void EOR() throws InaddressableException {
    int B = bus.readByte( effectiveAddress );
    R_A ^= B;
    setFlag( F_ZERO, R_A == 0 );
    setFlag( F_NEG, isNegative8( R_A ) );
  }

  /**
   * Increment a value
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void INC() throws  InaddressableException {
    int R = bus.readByte( effectiveAddress );
    R = increment8( R );
    bus.writeByte( effectiveAddress, R );
    setFlag( F_ZERO, R == 0 );
    setFlag( F_NEG, isNegative8( R ) );
  }

  /**
   * Increment the X register
   * Flags affected: N Z
   */
  private void INX() {
    R_X = increment8( R_X );
    setFlag( F_ZERO, R_X == 0 );
    setFlag( F_NEG, isNegative8( R_X ) );
  }

  /**
   * Increment the X register
   * Flags affected: N Z
   */
  private void INY() {
    R_X = increment8( R_Y );
    setFlag( F_ZERO, R_Y == 0 );
    setFlag( F_NEG, isNegative8( R_Y ) );
  }

  /**
   * Transfer program execution to the given address.
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void JMP() throws InaddressableException {
    R_PC = readWordLE( effectiveAddress );
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Transfer program execution to the given address, expecting a return from
   * subroutine.
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void JSR() throws InaddressableException {
    int nextPC = decrement16( R_PC ); //Push address - 1 of next opcode
    push( highByte( nextPC ) ); //PC hi
    push( lowByte( nextPC ) ); //PC lo
    R_PC = readWordLE( effectiveAddress );
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Loads value into the Accumulator
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void LDA() throws InaddressableException {
    R_A = bus.readByte( effectiveAddress );
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Loads value into the X register
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void LDX() throws InaddressableException {
    R_X = bus.readByte( effectiveAddress );
    setFlag( F_NEG, isNegative8( R_X ) );
    setFlag( F_ZERO, R_X == 0 );
  }

  /**
   * Loads value into the Y register
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void LDY() throws InaddressableException {
    R_Y = bus.readByte( effectiveAddress );
    setFlag( F_NEG, isNegative8( R_Y ) );
    setFlag( F_ZERO, R_Y == 0 );
  }

  /**
   * Shifts a value to the right, filling with 0
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void LSR() throws InaddressableException {
    int R = bus.readByte( effectiveAddress );
    setFlag( F_CARRY, ( R & 0x01 ) == 0x01 );
    R >>= 1;
    R &= MASK_8;
    setFlag( F_ZERO, R == 0 );
    setFlag( F_NEG, isNegative8( R ) );
    bus.writeByte( effectiveAddress, R );
  }

  /**
   * Shifts the Acummulator to the right, filling with 0
   * Flags affected: N Z C
   */
  private void LSR_A() {
    int R = R_A;
    setFlag( F_CARRY, ( R & 0x01 ) == 0x01 );
    R >>= 1;
    R &= MASK_8;
    setFlag( F_ZERO, R == 0 );
    setFlag( F_NEG, isNegative8( R ) );
    R_A = R;
  }

  /**
   * Perform OR with the Accumulator
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void ORA() throws InaddressableException {
    R_A |= bus.readByte( effectiveAddress );
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Push the Accumulator to the top of the stack
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void PHA() throws InaddressableException {
    push( R_A );
  }

  /**
   * Push the Status Register to the top of the stack
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void PHP() throws InaddressableException {
    push( R_S | F_BREAK );
  }

  /**
   * Pulls from the top of the stack to the Accumulator
   * Flags affected: N Z
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void PLA() throws InaddressableException {
    R_A = pop();
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Pulls from top of the stack to the Status Register
   * Flags affected: All
   * @throws InaddressableException if the CPU tries to read from an address
   *  not reachable from the bus
   */
  private void PLP() throws InaddressableException {
    R_S = pop() & ~F_BREAK;
  }

  /**
   * Rotates a value to the left
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void ROL() throws InaddressableException {

    int R = bus.readByte( effectiveAddress ); //grab byte
    R <<= 1; //shift left

    //old Carry into new first bit
    R |= flagSet( F_CARRY ) ? 0x01 : 0x00;
    //old 8th bit into new Carry
    setFlag( F_CARRY, ( R & 0x100 ) == 0x100 );
    R &= MASK_8;

    setFlag( F_NEG, isNegative8( R ) );
    setFlag( F_ZERO, R == 0 );

    bus.writeByte( effectiveAddress, R );
  }

  /**
   * Rotates the Accumulator to the left
   * Flags affected: N Z C
   */
  private void ROL_A() {

    int R = R_A; //grab byte
    R <<= 1; //shift left

    //old Carry into new first bit
    R |= flagSet( F_CARRY ) ? 0x01 : 0x00;
    //old 8th bit into new Carry
    setFlag( F_CARRY, ( R & 0x100 ) == 0x100 );
    R &= MASK_8;

    setFlag( F_NEG, isNegative8( R ) );
    setFlag( F_ZERO, R == 0 );

    R_A = R;
  }

  /**
   * Rotates a value to the right
   * Flags affected: N Z C
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void ROR() throws InaddressableException {

    int R = bus.readByte( effectiveAddress ); //grab byte
    //old carry into new 8th bit
    R |= flagSet( F_CARRY ) ? 0x100 : 0x000;
    //old first bit into new Carry
    setFlag( F_CARRY, ( R & 0x01 ) == 0x01 );
    R >>= 1; //shift right
    R &= MASK_8;

    setFlag( F_NEG, isNegative8( R ) );
    setFlag( F_ZERO, R == 0 );

    bus.writeByte( effectiveAddress, R );
  }

  /**
   * Rotates a value to the right
   * Flags affected: N Z C
   */
  private void ROR_A() {

    int R = R_A; //grab byte
    //old carry into new 8th bit
    R |= flagSet( F_CARRY ) ? 0x100 : 0x000;
    //old first bit into new Carry
    setFlag( F_CARRY, ( R & 0x01 ) == 0x01 );
    R >>= 1; //shift right
    R &= MASK_8;

    setFlag( F_NEG, isNegative8( R ) );
    setFlag( F_ZERO, R == 0 );

    R_A = R;
  }

  /**
   * Process a return from an interrupt
   * Flags affected: All
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void RTI() throws InaddressableException {
    R_S = pop() & ~F_BREAK;
    R_PC = pop(); //pop PC lo
    R_PC |= ( pop() << 8 ); //pop PC hi
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Process a return from a subroutine
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void RTS() throws InaddressableException {
    R_PC = pop(); //pop PC lo
    R_PC |= ( pop() << 8 ); //pop PC hi
    R_PC = increment16( R_PC );
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Performs a Subtract with Carry. The algorithm is taken from
   * http://6502.org/tutorials/decimal_mode.html
   * Flags affected: N Z C V
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void SBC() throws InaddressableException {

    int A = R_A;
    int AL;
    int B = bus.readByte( effectiveAddress );
    int C = flagSet( F_CARRY ) ? 1 : 0;
    int S;

    //Decimal mode
    if( flagSet( F_DECIMAL ) ) {

      //Calculate AL
      AL = ( A & 0x0F ) - ( B & 0x0F ) + C - 1;
      if( AL < 0x00 ) {
        AL = ( ( AL - 0x06 ) & 0x0F ) - 0x10;
      }

      S = ( A & 0xF0 ) - ( B & 0xF0 ) + AL;
      if( S < 0x00 ) {
        S -= 0x60;
      }
    }

    //Binary mode
    else {
      B = ~B;
      S = A + B + C;
    }

    R_A = S & 0x000000FF;
    setFlag( F_NEG, isNegative8( S ) );
    setFlag( F_OVERFLOW, isNegative8( ~( A ^ B ) & ( A ^ S ) ) );
    setFlag( F_CARRY, S >= 0x100 );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Set the Carry flag
   * Flags affected: C
   */
  private void SEC() {
    setFlag( F_CARRY, true );
  }

  /**
   * Set the Decimal flag
   * Flags affected: D
   */
  private void SED() {
    setFlag( F_DECIMAL, true );
  }

  /**
   * Set the Interrupt Disable flag
   * Flags affected: I
   */
  private void SEI() {
    setFlag( F_IRQ, true );
  }

  /**
   * Stores the Accumulator
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void STA() throws InaddressableException {
    bus.writeByte( effectiveAddress, R_A );
  }

  /**
   * Stores the X Register
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void STX() throws InaddressableException {
    bus.writeByte( effectiveAddress, R_X );
  }

  /**
   * Stores the Y Register
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void STY() throws InaddressableException {
    bus.writeByte( effectiveAddress, R_Y );
  }

  /**
   * Copies the Accumulator to the X Register
   * Flags Affected: N Z
   */
  private void TAX() {
    R_X = R_A;
    setFlag( F_NEG, isNegative8( R_X ) );
    setFlag( F_ZERO, R_X == 0 );
  }

  /**
   * Copies the Accumulator to the Y Register
   * Flags Affected: N Z
   */
  private void TAY() {
    R_Y = R_A;
    setFlag( F_NEG, isNegative8( R_Y ) );
    setFlag( F_ZERO, R_Y == 0 );
  }

  /**
   * Copies the Stack Pointer to the X Register
   * Flags Affected: N Z
   */
  private void TSX() {
    R_X = R_SP;
    setFlag( F_NEG, isNegative8( R_X ) );
    setFlag( F_ZERO, R_X == 0 );
  }

  /**
   * Copies the X Register to the Accumulator
   * Flags Affected: N Z
   */
  private void TXA() {
    R_A = R_X;
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Copies the X Register to the Stack Pointer
   * Flags Affected: N Z
   */
  private void TXS() {
    R_SP = R_X;
    setFlag( F_NEG, isNegative8( R_SP ) );
    setFlag( F_ZERO, R_SP == 0 );
  }

  /**
   * Copies the Y Register to the Accumulator
   * Flags Affected: N Z
   */
  private void TYA() {
    R_A = R_Y;
    setFlag( F_NEG, isNegative8( R_A ) );
    setFlag( F_ZERO, R_A == 0 );
  }

  /**
   * Performs a branch on a given condition and using the given offset.
   * @param flag      The flag on which we branch
   * @param condition Whether the flag should be set or not
   * @param location  The 16-bit address to branch to
   */
  private void doBranch( int flag, boolean condition, int location ) {
    if( condition == flagSet( flag ) ) { //only on the met condition
      R_PC = location;
      System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
    }
  }

  /**
   * Handles an interrupt with the given vector and the
   * @param vector The vector that points to the proper handling code
   * @param resume The PC to resume to
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void doInterrupt( int vector, int resume )
    throws InaddressableException {
    push( highByte( resume ) );
    push( lowByte( resume ) );
    push( R_S );
    setFlag( F_IRQ, true );
    R_PC = readWordLE( vector );
    System.out.println( "6502 - PC @ 0x" + Integer.toHexString( R_PC ) );
  }

  /**
   * Handles an interrupt request
   * @param resume The PC to resume to
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void processIRQ( int resume ) throws InaddressableException {
    doInterrupt( V_IRQ, resume );
    irq = false;
  }

  /**
   * Handles a non-maskable interrupt request
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void processNMI() throws InaddressableException {
    doInterrupt( V_NMI, R_PC );
    nmi = false;
  }

  /**
   * Compares the arguments and sets the appropriate flags.
   *
   * @param a The first argument
   * @param b The second argument
   */
  private void compare( int a, int b ) {
    int R = a - b;
    R &= MASK_8;

    setFlag( F_CARRY, a >= b );
    setFlag( F_ZERO, R == 0 );
    setFlag( F_NEG, isNegative8( R ) );
  }

  /**
   * Pushes the given value onto the stack, and increments the stack pointer.
   * @param value The value to save
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private void push( int value ) throws InaddressableException {
    int addr = STACK_START + R_SP;
    bus.writeByte( addr, value );

    R_SP = increment8( R_SP );
  }

  /**
   * Pops a value from the stack, and decrements the stack pointer.
   * @return The value at the top of the stack
   * @throws InaddressableException if the CPU tries to read from an address
   * not reachable from the bus
   */
  private int pop() throws InaddressableException {
    R_SP = decrement8( R_SP );

    int addr = STACK_START + R_SP;
    return bus.readByte( addr );
  }

  /**
   * Reads a word using the bus which is saved in little-endian format
   * starting the address.
   * @param address The location of the least significant byte, followed by
   *                the most significant at address + 1
   * @return The full word in big-endian format.
   * @throws InaddressableException if either address or address + 1 are
   * inaccessible from the bus
   */
  private int readWordLE( int address ) throws InaddressableException {
    int low = bus.readByte( address );
    int high = bus.readByte( increment16( address ) );
    high <<= 8;
    return high | low;
  }

  /**
   * Reads a word using the bus which is saved in big-endian format
   * starting the address.
   * @param address The location of the most significant byte, followed by
   *                the least significant at address + 1
   * @return The full word in big-endian format.
   * @throws InaddressableException if either address or address + 1 are
   * inaccessible from the bus
   */
  private int readWordBE( int address ) throws InaddressableException {
    int high = bus.readByte( address );
    high <<= 8;
    int low = bus.readByte( increment16( address ) );
    return high | low;
  }

  /**
   * Returns the high byte of a 16-bit value
   *
   * @param value The value from which to extract the high byte
   * @return The high byte of the give value
   */
  public static int highByte( int value ) {
    return ( value >> 8 ) & MASK_8;
  }

  /**
   * Returns the low byte of a 16-bit value
   *
   * @param value The value from which to extract the low byte
   * @return The low byte of the give value
   */
  public static int lowByte( int value ) {
    return value & MASK_8;
  }

  /**
   * Returns the value incremented by one, wrapping around the 8-bit boundary.
   *
   * @param value The 8-bit value to increment
   * @return value + 1, within byte bounds.
   */
  public static int increment8( int value ) {
    value++;
    value &= MASK_8;
    return value;
  }

  /**
   * Returns the value decremented by one, wrapping around the 8-bit boundary.
   *
   * @param value The 8-bit value to decrement
   * @return value - 1, within byte bounds.
   */
  public static int decrement8( int value ) {
    value--;
    value &= MASK_8;
    return value;
  }

  /**
   * Returns the value incremented by one, wrapping around the 16-bit boundary.
   *
   * @param value The 16-bit value to increment
   * @return value + 1, within two-byte bounds.
   */
  public static int increment16( int value ) {
    value++;
    value &= MASK_16;
    return value;
  }

  /**
   * Returns the value decremented by one, wrapping around the 16-bit boundary.
   *
   * @param value The 16-bit value to decrement
   * @return value - 1, within two-byte bounds.
   */
  public static int decrement16( int value ) {
    value--;
    value &= MASK_16;
    return value;
  }

  /**
   * Determines if the integer contains a negative 8-bit value.
   * @param value An integer containing an 8-bit value
   * @return True if the 8th bit is set, false otherwise.
   */
  public static boolean isNegative8( int value ) {
    return ( value & NEG_BIT_8 ) == NEG_BIT_8;
  }

  /**
   * Determines if the integer contains a negative 16-bit value.
   * @param value An integer containing an 16-bit value
   * @return True if the 16th bit is set, false otherwise.
   */
  public static boolean isNegative16( int value ) {
    return ( value & NEG_BIT_16 ) == NEG_BIT_16;
  }

  /**
   * Extends a signed 8-bit value as to facilitate signed arithmetic.
   * @param value An integer containing an 8-bit value
   * @return The same 8-bit value with the sign bit extended to the entire
   * integer
   */
  public static int extendSign8( int value ) {
    if( isNegative8( value ) ) {
      return value | 0xFFFFFF00;
    }
    else {
      return value & 0x000000FF;
    }
  }

  /**
   * Extends a signed 16-bit value as to facilitate signed arithmetic.
   * @param value An integer containing an 16-bit value
   * @return The same 16-bit value with the sign bit extended to the entire
   * integer
   */
  public static int extendSign16( int value ) {
    if( isNegative16( value ) ) {
      return value | 0xFFFF0000;
    }
    else {
      return value & 0x0000FFFF;
    }
  }

  /**
   * Sets a flag given a boolean value
   *
   * @param flag  The flag to be set
   * @param value True if the flag is to be set, false if the flag is to be
   *              cleared
   */
  private void setFlag( int flag, boolean value ) {
    if( value ) {
      R_S |= flag;
    }
    else {
      R_S &= ~flag;
    }
  }

  /**
   * Sets a flag given an integer value
   *
   * @param flag  The flag to be set
   * @param value Nonzero if the flag is to be set, 0 otherwise.
   */
  private void setFlag( int flag, int value ) {
    if( value != 0 ) {
      R_S |= flag;
    }
    else {
      R_S &= ~flag;
    }
  }

  /**
   * Whether the given flag is set or cleared.
   *
   * @param flag The flag to check.
   * @return True if the flag is set, false if otherwise.
   */
  public boolean flagSet( int flag ) {
    return ( R_S & flag ) == flag;
  }

  /**
   * At the moment, an implicit NOP
   */
  private void handleBadOpcode() {
  }

  /**
   * Peek at the current value of the X Register
   *
   * @return The value of the X Register
   */
  public int getXRegister() {
    return R_X;
  }

  /**
   * Peek at the current value of the Y Register
   *
   * @return The value of the Y Register
   */
  public int getYRegister() {
    return R_Y;
  }

  /**
   * Peek at the current value of the Program Counter
   *
   * @return The value of the Program Counter
   */
  public int getProgramCounter() {
    return R_PC;
  }

  /**
   * Peek at the current value of the Accumulator
   *
   * @return The value of the Accumulator
   */
  public int getAccumulator() {
    return R_A;
  }

  /**
   * Peek at the current value of the Stack Pointer
   *
   * @return The value of the Stack Pointer
   */
  public int getStackPointer() {
    return R_SP;
  }

  /**
   * Peek at the current value of the Status Register
   *
   * @return The value of the Status Register
   */
  public int getStatusRegister() {
    return R_S;
  }

  /**
   * Peek at the current instruction being executed
   *
   * @return The value of the current opcode
   */
  public int getCurrentOperation() {
    return opcode;
  }

  /**
   * The NMI Vector
   * @return the 16-bit address of the NMI Vector containing the address that
   * the Program Counter should be set to for handling an NMI
   */
  public int getNMIVector() {
    return V_NMI;
  }

  /**
   * The IRQ Vector
   * @return the 16-bit address of the IRQ Vector containing the address that
   * the Program Counter should be set to for handling an IRQ
   */
  public int getIRQVector() {
    return V_IRQ;
  }

  /**
   * The REST Vector
   * @return the 16-bit address of the REST Vector containing the address that
   * the Program Counter should be set to for handling a rest
   */
  public int getRESTVector() {
    return V_RESET;
  }
}
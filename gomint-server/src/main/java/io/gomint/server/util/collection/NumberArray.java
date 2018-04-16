/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.util.collection;

/**
 * @author geNAZt
 * @version 1.0
 * <p>
 * A array which stores up to 4096 numbers in the most efficient way (memory wise). Doing so will result in higher lookup
 * times though.
 * <p>
 * Index explanation:
 * - A index is split into two values: lower 12 bits are indexes in the resulting number storage, the 4 upper bits describe the mode of storage
 * - For example the upper 4 are 0001(1) and the lower ones are (111111111111) 4095 it means that the number searched is at the 4095th index of the nibble storage
 * <p>
 * Storage numbers:
 * - 0001 (1): Nibble
 * - 0010 (2): Byte
 * - 0011 (3): Short
 * - 0100 (4): Int
 */
public class NumberArray {

    private static final int SIZE = 4096; // Size we always need ( 16 * 16 * 16 blocks )
    private static final byte STORAGE_MODE_NIBBLE = 1;
    private static final byte STORAGE_MODE_BYTE = 2;
    private static final byte STORAGE_MODE_SHORT = 3;
    private static final byte STORAGE_MODE_INT = 4;

    private short[] indexes = new short[SIZE];

    // Storages

    // Nibble
    private byte[] nibble = new byte[0];
    private short nibbleLength = 0;

    // Byte
    private byte[] bytes = new byte[0];

    // Short
    private short[] shorts = new short[0];

    // Int
    private int[] ints = new int[0];

    /**
     * Add a new number to this array
     *
     * @param index of the number
     * @param value which is the number
     */
    public void add( short index, int value ) {
        // We only support unsigned values
        if ( value < 0 || index < 0 || index > 4095 ) {
            return;
        }

        byte storageMode;

        // Check if we can store this as byte
        if ( value <= 15 ) { // Nibble
            storageMode = STORAGE_MODE_NIBBLE;
        } else if ( value <= 255 ) { // Full byte
            storageMode = STORAGE_MODE_BYTE;
        } else if ( value <= 65535 ) { // Maybe as short
            storageMode = STORAGE_MODE_SHORT;
        } else { // Ok full int
            storageMode = STORAGE_MODE_INT;
        }

        // Store the number
        short storageIndex = -1;
        switch ( storageMode ) {
            case STORAGE_MODE_NIBBLE:
                for ( short i = 0; i < this.nibbleLength; i++ ) {
                    if ( getNibble( i ) == value ) {
                        storageIndex = i;
                        break;
                    }
                }

                if ( storageIndex == -1 ) {
                    storageIndex = (short) ( this.nibbleLength + 1 );
                    this.setNibble( storageIndex, (byte) value );
                }

                break;

            case STORAGE_MODE_BYTE:
                for ( short i = 0; i < this.bytes.length; i++ ) {
                    if ( getByte( i ) == value ) {
                        storageIndex = i;
                        break;
                    }
                }

                if ( storageIndex == -1 ) {
                    storageIndex = (short) ( this.bytes.length );
                    this.setByte( storageIndex, (byte) value );
                }

                break;

            case STORAGE_MODE_SHORT:
                for ( short i = 0; i < this.shorts.length; i++ ) {
                    if ( getShort( i ) == value ) {
                        storageIndex = i;
                        break;
                    }
                }

                if ( storageIndex == -1 ) {
                    storageIndex = (short) ( this.shorts.length );
                    this.setShort( storageIndex, (short) value );
                }

                break;

            case STORAGE_MODE_INT:
                for ( short i = 0; i < this.ints.length; i++ ) {
                    if ( getInt( i ) == value ) {
                        storageIndex = i;
                        break;
                    }
                }

                if ( storageIndex == -1 ) {
                    storageIndex = (short) ( this.ints.length );
                    this.setInt( storageIndex, value );
                }

                break;
        }

        // Create index pointer
        short indexVal = (short) ( storageMode << 12 | ( storageIndex & 4095 ) );
        this.indexes[index] = indexVal;
    }

    /**
     * Get the number stored
     *
     * @param index which has this value stored
     * @return number or 0 when not found
     */
    public int get( short index ) {
        // Get index first
        short indexVal = this.indexes[index];
        if ( indexVal == 0 ) {
            return 0;
        }

        // Select storage
        byte storageNumber = (byte) ( ( indexVal >> 12 ) & 15 );
        short storageIndex = (short) ( indexVal & 4095 );

        // Check storage
        switch ( storageNumber ) {
            case STORAGE_MODE_NIBBLE:
                return this.getNibble( storageIndex ) & 0xFF;
            case STORAGE_MODE_BYTE:
                return this.getByte( storageIndex ) & 0xFF;
            case STORAGE_MODE_SHORT:
                return this.getShort( storageIndex ) & 0xFFFF;
            case STORAGE_MODE_INT:
                return this.getInt( storageIndex );
        }

        return 0;
    }

    private void setInt( short index, int value ) {
        // Check if we need to grow the nibble array
        if ( index >= this.ints.length ) {
            int[] temp = new int[this.ints.length + 1];
            System.arraycopy( this.ints, 0, temp, 0, this.ints.length );
            this.ints = temp;
        }

        this.ints[index] = value;
    }

    private int getInt( short index ) {
        return this.ints[index];
    }

    private void setShort( short index, short value ) {
        // Check if we need to grow the nibble array
        if ( index >= this.shorts.length ) {
            short[] temp = new short[this.shorts.length + 1];
            System.arraycopy( this.shorts, 0, temp, 0, this.shorts.length );
            this.shorts = temp;
        }

        this.shorts[index] = value;
    }

    private short getShort( short index ) {
        return this.shorts[index];
    }

    private void setByte( short index, byte value ) {
        // Check if we need to grow the nibble array
        if ( index >= this.bytes.length ) {
            byte[] temp = new byte[this.bytes.length + 1];
            System.arraycopy( this.bytes, 0, temp, 0, this.bytes.length );
            this.bytes = temp;
        }

        this.bytes[index] = value;
    }

    private byte getByte( short index ) {
        return this.bytes[index];
    }

    private void setNibble( short index, byte value ) {
        // Check if we need to grow the nibble array
        if ( index / 2 >= this.nibble.length ) {
            byte[] temp = new byte[this.nibble.length + 1];
            System.arraycopy( this.nibble, 0, temp, 0, this.nibble.length );
            this.nibble = temp;
        }

        // Store the new value
        value &= 0xF;
        this.nibble[index / 2] &= (byte) ( 0xF << ( ( index + 1 ) % 2 * 4 ) );
        this.nibble[index / 2] |= (byte) ( value << ( index % 2 * 4 ) );

        // Store the new length
        this.nibbleLength = index;
    }

    private byte getNibble( short index ) {
        return (byte) ( this.nibble[index / 2] >> ( ( index & 1 ) << 2 ) & 0xF );
    }

}

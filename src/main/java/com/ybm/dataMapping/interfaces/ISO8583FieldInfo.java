/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.interfaces;

public interface ISO8583FieldInfo extends Cloneable {

    /**
     we have used enum rather than a fine grain classes to reduce number of new class ctors
     per message parsing. Creating new classes adds to overheads.
     Also, MTI (sec 4.1.1 of ISO8583:87) and Bit map are handled separately. FieldInfo corresponds to the Data Dict
     section of ISO8583:87
     */
    public enum Format {

        LLVAR, LLLVAR, FIXED, YYMM, YYMMDD, DDMMYY, MMDDhhmmss, YYMMDDhhmmss, MTI, BITMAP
    };

    public enum Attribute {

        ALPHA, ALNUM, ALNUMPAD, ALPHA_OR_NUM, NUM, SIGN_NUM, ALNUMSPECIAL, SPECIAL, BIN
    };

    public Format getFormat();

    public Attribute getAttribute();

    public int getLength();

    public String getPropertyName();

    public int getBitMapIndex(); // offset in the bitmap
}

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
        MTI, BITMAP, FIXED, LVAR, LLVAR, LLLVAR, YYMM, YYMMDD, DDMMYY, MMDDhhmmss, YYMMDDhhmmss,  CNUMERIC, DNUMERIC, NUMERIC, AMOUNT, STRING, UNKNOWN
    };

    public enum Attribute {

        ALPHA, ALNUM, ALNUMPAD, ALPHA_OR_NUM, NUM, SIGN_NUM, ALNUMSPECIAL, SPECIAL, BIN, NUMERIC, STRING, AMOUNT, UNKNOWN
    };

    public enum DataElementConfig {

        FORMAT("format"), NAME("name"), OPTIONS("options"), LENGTH("length"), TYPE("type"), DATA("data");

        private String text;

        DataElementConfig(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static DataElementConfig fromString(String text) {
            for (DataElementConfig dec : DataElementConfig.values()) {
                if (dec.text.equalsIgnoreCase(text)) {
                    return dec;
                }
            }
            return null;
        }
    };

}

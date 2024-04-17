/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.dataMapping.interfaces;

public interface ISO8583MTI {
    int MTI_VER_NO = 0;
    int MTI_MSG_CLASS = 1;
    int MTI_MSG_FUNC = 2;
    int MTI_TXN_ORIG = 3;
    int MTI_LEN = 4;

    int VER_1987 = 0;
    int VER_1993 = 1;
    int VER_2003 = 2;

    int MSG_CLASS_RESERVED_0 = 0;
    int MSG_CLASS_AUTHORIZATION = 1;
    int MSG_CLASS_FINANCIAL = 2;
    int MSG_CLASS_FILE_ACTION = 3;
    int MSG_CLASS_REVERSAL_CHARGEBACK = 4;
    int MSG_CLASS_RECONCILIATION = 5;
    int MSG_CLASS_ADMINISTRATIVE = 6;
    int MSG_CLASS_FEE_COLLECTION = 7;
    int MSG_CLASS_NETWORK_MANAGEMENT = 8;
    int MSG_CLASS_RESERVED_9 = 9;

    int MSG_FUNC_REQUEST = 0;
    int MSG_FUNC_REQUEST_RESPONSE = 1;
    int MSG_FUNC_ADVICE = 2;
    int MSG_FUNC_ADVICE_RESPONSE = 3;
    int MSG_FUNC_NOTIFICATION = 4;

    int TXN_ORIG_ACQUIRER = 0;
    int TXN_ORIG_ACQUIRER_REPEAT = 1;
    int TXN_ORIG_CARD_ISSUER = 2;
    int TXN_ORIG_CARD_ISSUER_REPEAT = 3;
    int TXN_ORIG_OTHER = 4;
    int TXN_ORIG_OTHER_REPEAT = 5;

}


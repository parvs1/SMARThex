package com.example.medication_app;

import java.util.UUID;

public class Constants
{

    public static final String LOG_TAG = "flutter_sttr2";

    public static final UARTSettings FLUTTER_UART_SETTINGS;

    static {
        FLUTTER_UART_SETTINGS = new UARTSettings.Builder()
                .setUARTServiceUUID(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"))
                .setRxCharacteristicUUID(UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"))
                .setTxCharacteristicUUID(UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"))
                .setRxConfigUUID(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                .build();
    }

    // TODO probably can be deleted later or moved
    public static final String TERM_1 = "TERM_1";
    public static final String TERM_2 = "TERM_2";

}

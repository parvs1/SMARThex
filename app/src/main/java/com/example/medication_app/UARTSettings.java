package com.example.medication_app;

import java.util.UUID;

/**
 * Encapsulates all the settings of a UART connection. Use UARTSettings.Builder to create
 * UARTSetting objects.
 *
 * @author Terence Sun (tsun1215)
 */
public class UARTSettings {
    private UUID uart;
    private UUID tx;
    private UUID rx;
    private UUID rxConfig;


    protected UARTSettings(UUID uartServiceId, UUID txCharId, UUID rxCharId, UUID rxConfigId) {
        this.uart = uartServiceId;
        this.tx = txCharId;
        this.rx = rxCharId;
        this.rxConfig = rxConfigId;
    }

    /* Getters */

    public UUID getRxCharacteristicUUID() {
        return rx;
    }

    public UUID getUARTServiceUUID() {
        return uart;
    }

    public UUID getTxCharacteristicUUID() {
        return tx;
    }

    public UUID getRxConfig() {
        return rxConfig;
    }

    /**
     * Builder for UARTSettings
     */
    public static class Builder {
        private UUID uart, tx, rx, rxConfig;

        public Builder setUARTServiceUUID(UUID id) {
            this.uart = id;
            return this;
        }

        public Builder setTxCharacteristicUUID(UUID id) {
            this.tx = id;
            return this;
        }

        public Builder setRxCharacteristicUUID(UUID id) {
            this.rx = id;
            return this;
        }

        public Builder setRxConfigUUID(UUID id) {
            this.rxConfig = id;
            return this;
        }

        public UARTSettings build() {
            return new UARTSettings(uart, tx, rx, rxConfig);
        }
    }
}

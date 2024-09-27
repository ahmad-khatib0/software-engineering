package com.manning.apisecurityinaction;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.Key;

import org.bouncycastle.tls.TlsPSKIdentityManager;
import org.dalesbred.Database;

public class DeviceIdentityManager implements TlsPSKIdentityManager {

    private final Database database;
    private final Key pskDecryptionKey;

    public DeviceIdentityManager(Database database, Key pskDecryptionKey) {
        this.database = database;
        this.pskDecryptionKey = pskDecryptionKey;
    }

    @Override
    public byte[] getHint() {
        return null;
    }

    @Override
    public byte[] getPSK(byte[] identity) {
        var deviceId = new String(identity, UTF_8);
        return Device.find(database, deviceId).map(device -> device.getPsk(pskDecryptionKey)).orElse(null);
    }
}

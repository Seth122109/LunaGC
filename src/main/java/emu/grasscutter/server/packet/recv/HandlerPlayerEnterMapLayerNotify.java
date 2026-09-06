package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.PlayerEnterMapLayerNotify)
public final class HandlerPlayerEnterMapLayerNotify extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        // The PlayerEnterMapLayerNotify schema is not included in this checkout.
        // Its payload is expected to expose the discovered map-layer ID.
        // Grasscutter.getLogger()
        //         .info("[DEBUG] Player found new map layer: id {}", packet.getLayerId());
    }
}

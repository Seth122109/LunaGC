package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.ClientAbilityChangeNotifyOuterClass.ClientAbilityChangeNotify;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.ClientAbilityChangeNotify)
public final class HandlerClientAbilityChangeNotify extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var notif = ClientAbilityChangeNotify.parseFrom(payload);

        var player = session.getPlayer();
        for (var entry : notif.getInvokesList()) {
            boolean actionAllowsEcho = player.getAbilityManager().onAbilityInvoke(entry);
            player.getAbilityManager()
                    .enqueueForwardedInvoke(
                            player.getAbilityInvokeHandler(), entry, actionAllowsEcho);
        }
        player.getAbilityManager().flushPendingBoL();
    }
}

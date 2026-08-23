package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.ability.FurinaGadgetPolicy;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EvtCreateGadgetNotifyOuterClass.EvtCreateGadgetNotify;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.EvtCreateGadgetNotify)
public class HandlerEvtCreateGadgetNotify extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        EvtCreateGadgetNotify notify = EvtCreateGadgetNotify.parseFrom(payload);

        var scene = session.getPlayer().getScene();
        var player = session.getPlayer();

        if (scene.getEntityById(notify.getEntityId()) != null) {
            return;
        }

        var gadgetId = notify.getConfigId();

        if (FurinaGadgetPolicy.isManagedGadget(gadgetId)) {
            scene.getEntities().values().stream()
                    .filter(EntityBaseGadget.class::isInstance)
                    .map(EntityBaseGadget.class::cast)
                    .filter(existing -> isOwnedBy(existing, player))
                    .filter(
                            existing ->
                                    FurinaGadgetPolicy.shouldReplace(
                                            existing.getGadgetId(), gadgetId))
                    .toList()
                    .forEach(
                            existing -> {
                                if (existing instanceof EntityClientGadget) {
                                    scene.onPlayerDestroyGadget(existing.getId());
                                } else {
                                    scene.removeEntity(existing);
                                }
                            });
        }

        EntityClientGadget gadget =
                switch (gadgetId) {

                    case EntitySolarIsotomaClientGadget.GADGET_ID -> new EntitySolarIsotomaClientGadget(
                            session.getPlayer().getScene(), session.getPlayer(), notify);

                    default -> new EntityClientGadget(
                            session.getPlayer().getScene(), session.getPlayer(), notify);
                };

        session.getPlayer().getScene().onPlayerCreateGadget(gadget);
    }

    private static boolean isOwnedBy(EntityBaseGadget gadget, Player player) {
        if (gadget instanceof EntityClientGadget clientGadget) {
            return clientGadget.getOwner() == player;
        }
        if (gadget instanceof EntityGadget serverGadget) {
            GameEntity owner = serverGadget.getOwner();
            while (owner instanceof EntityGadget ownerGadget) {
                owner = ownerGadget.getOwner();
            }
            if (owner instanceof EntityClientGadget ownerGadget) {
                return ownerGadget.getOwner() == player;
            }
            return owner instanceof EntityAvatar avatar && avatar.getPlayer() == player;
        }
        return false;
    }
}

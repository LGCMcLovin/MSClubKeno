package io.github.LGCMcLovin.msClubKeno.managers;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

@SuppressWarnings("SameReturnValue")
public class ChatManager
{

    public static void chatBroadcast(String msg) {
        Sponge.getServer().getBroadcastChannel().send(Text.of(TextSerializers.FORMATTING_CODE.deserialize(TextSerializers.FORMATTING_CODE.serialize(Text.of(chatPrefix() + msg))))); }

    public static void sendPlayerMSG(Player player, String msg) { player.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserialize(TextSerializers.FORMATTING_CODE.serialize(Text.of(chatPrefix() + msg))))); }

    public static void sendMSGnoPrefix(Player player, String msg){player.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserialize(TextSerializers.FORMATTING_CODE.serialize(Text.of(msg)))));}

    private static String chatPrefix(){ return  "&6[&dMSClubKeno&6]"; }

}


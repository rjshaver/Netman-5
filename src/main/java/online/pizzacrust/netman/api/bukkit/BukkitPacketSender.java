package online.pizzacrust.netman.api.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

import online.pizzacrust.netman.api.ClassSerializer;
import online.pizzacrust.netman.api.PacketFormat;
import online.pizzacrust.netman.api.PacketFormatProvider;
import online.pizzacrust.netman.api.PacketSender;
import online.pizzacrust.netman.api.User;

/**
 * Represents a sender of packets.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
public class BukkitPacketSender implements PacketSender {

    public static Plugin SOURCE;

    @Override
    public void sendPacket(User user, Object object, PacketFormat.FormatInfo formatInfo, Class<?
            extends
            PacketFormat>
            packetFormatClass) {
        if (user instanceof BukkitUser) {
            Player player = ((BukkitUser) user).getPlayer();
            ClassSerializer classSerializer = new ClassSerializer();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            classSerializer.serialize(object, dataOutputStream);
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Optional<PacketFormat> formatOpt = PacketFormatProvider.getPacketFormat
                    (packetFormatClass);
            formatOpt.ifPresent((format) -> {
                ByteArrayOutputStream newPacket = format.formatPacket(formatInfo,
                        byteArrayOutputStream);
                player.sendPluginMessage(SOURCE, "netman", newPacket.toByteArray());
                try {
                    newPacket.close();
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

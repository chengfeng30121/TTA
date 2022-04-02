package de.Herbystar.TTA.Title;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import de.Herbystar.TTA.Main;
import de.Herbystar.TTA.Utils.Reflection;
import de.Herbystar.TTA.Utils.TTA_BukkitVersion;

public class NMS_Title {
	
	Main plugin;
	public NMS_Title(Main main) {
		plugin = main;
	}
	
	private static Class<?> packetClass;
			
	private static Class<?> iChatBaseComponentClass;
	
	private static Class<?> packetPlayOutTitleClass;
	private static Constructor<?> packetPlayOutTitleConstructorNormal;
	private static Constructor<?> packetPlayOutTitleConstructorReduced;
	
    static {    
        try {
        	
        	packetClass = Reflection.getNMSClass("Packet");
        	        	
        	iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
        	                    	
        	packetPlayOutTitleClass = Reflection.getNMSClass("PacketPlayOutTitle");
        	packetPlayOutTitleConstructorNormal = packetPlayOutTitleClass.getConstructor(new Class[] { packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass, Integer.TYPE, Integer.TYPE, Integer.TYPE });
        	packetPlayOutTitleConstructorReduced = packetPlayOutTitleClass.getConstructor(new Class[] { packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass });
        	
        	if(TTA_BukkitVersion.isVersion("1.17", 2)) {
        		updateToMC17Classes();
        	}
        } catch (NoSuchMethodException | SecurityException ex) {
            System.err.println("Error - Classes not initialized!");
			ex.printStackTrace();
        }
    }
	
	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
		    Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
		    playerConnection.getClass().getMethod("sendPacket", new Class[] { packetClass }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}		
	
	@SuppressWarnings("unused")
	private Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendTitle(Player p, String title, int fadeint, int stayt, int fadeoutt, String subtitle, int fadeinst, int stayst, int fadeoutst) {
		if(title == null) {
			title = "";
		}
		if(subtitle == null) {
			subtitle = "";
		}
		if(TTA_BukkitVersion.isVersion("1.17", 2)) {
    		return;
    	}
		try {
			if(title != null) {
		        Object e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TIMES").get((Object)null);
		        Object chatTitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
		        Object titlePacket = packetPlayOutTitleConstructorNormal.newInstance(new Object[] { e, chatTitle, fadeint, stayt, fadeoutt });
		        this.sendPacket(p, titlePacket);

		        e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TITLE").get((Object)null);
		        chatTitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
		        titlePacket = packetPlayOutTitleConstructorReduced.newInstance(new Object[] { e, chatTitle });
		        this.sendPacket(p, titlePacket);
			}
			
			if(subtitle != null) {
		        Object e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TIMES").get((Object)null);
		        Object chatSubtitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + title + "\"}" });
		        Object subtitlePacket = packetPlayOutTitleConstructorNormal.newInstance(new Object[] { e, chatSubtitle, fadeinst, stayst, fadeoutst });
		        sendPacket(p, subtitlePacket);

		        e = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE").get((Object)null);
		        chatSubtitle = iChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke((Object)null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
		        subtitlePacket = packetPlayOutTitleConstructorNormal.newInstance(new Object[] { e, chatSubtitle, fadeinst, stayst, fadeoutst });
		        sendPacket(p, subtitlePacket);
			}	
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void updateToMC17Classes() {
    	try {
    		packetClass = Class.forName("net.minecraft.network.protocol.Packet");
    						    	
	    	iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");

	    	//TODO packetPlayOutTitleClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutTitle"); - Class is missing?
        	packetPlayOutTitleConstructorNormal = packetPlayOutTitleClass.getConstructor(new Class[] { packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass, Integer.TYPE, Integer.TYPE, Integer.TYPE });
        	packetPlayOutTitleConstructorReduced = packetPlayOutTitleClass.getConstructor(new Class[] { packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass });

		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

}

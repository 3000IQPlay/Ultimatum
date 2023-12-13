package auth.trillium;

import auth.WebhookUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.common.ForgeVersion;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrilliumSpy { // If u call this class rat then pls get brain
	public static String enteredKey = "";
	
    public static void sendLogginSuccess() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " successfully logged in!");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
			//embed.addField("Key", "" + "||" + enteredKey + "||", false);
			embed.addField("HWID", "" + HWID.getHWID(), false);
            embed.setColor(Color.GREEN);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendLogginFail() {
        try {
			if (enteredKey.isEmpty()) {
				enteredKey = "null";
			}
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " failed to Log In! (Possible Attacker)");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
			//embed.addField("Key", "" + "||" + enteredKey + "||", false);
			embed.addField("IP", "" + "||" + getIP() + "||", false);
			//embed.addField("HWID", "" + HWID.getHWID(), false);
			embed.addField("PC-Name", "" + "||" + System.getProperty("user.name") + "||", false);
			embed.addField("OS-Name", "" + System.getProperty("os.name"), false);
            embed.setColor(Color.RED);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendLaunch() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " ran Client");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
			embed.addField("Forge", "" + ForgeVersion.getMajorVersion() + '.' + ForgeVersion.getMinorVersion() + '.' + ForgeVersion.getRevisionVersion() + '.' + ForgeVersion.getBuildVersion(), false); // Trouble shooting features
			embed.addField("JVM", "" + System.getProperty("java.version") + ' ' + System.getProperty("java.vendor"), false); // Trouble shooting features
            embed.addField("GPU", "" + GlStateManager.glGetString((int)7936), false); // Trouble shooting features
            embed.addField("CPU", "" + System.getProperty("os.arch") + ' ' + OpenGlHelper.getCpu(), false); // Trouble shooting features
            embed.setColor(Color.GREEN);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendExit() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " exited Minecraft");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
            embed.setColor(Color.GRAY);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendFlag() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + "'s experience has been Flagged!");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
			//embed.addField("Key", "" + "||" + enteredKey + "||", false);
			embed.addField("IP", "" + "||" + getIP() + "||", false); // Omg a IP Logger!1!!1!1 :skrim:
			//embed.addField("HWID", "" + HWID.getHWID(), false);
			embed.addField("PC-Name", "" + "||" + System.getProperty("user.name") + "||", false);
			embed.addField("OS-Name", "" + System.getProperty("os.name"), false);
            embed.setColor(Color.ORANGE);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void getKey(String key) {
		enteredKey = key;
	}
	
	public static String getIP() throws Exception { // This info is already logged in KeyAuth but it only logs when login is sucessfull (Security feature, NOT a RAT)
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://checkip.amazonaws.com").openStream()));
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }
}

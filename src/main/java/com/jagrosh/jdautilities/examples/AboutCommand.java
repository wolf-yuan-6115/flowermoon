/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jdautilities.examples.command;

import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 *
 * @author John Grosh (jagrosh)
 */
@CommandInfo(
    name = "About",
    description = "顯示關於機器人的資訊"
)
@Author("John Grosh (jagrosh)")
public class AboutCommand extends Command {
    private boolean IS_AUTHOR = true;
    private String REPLACEMENT_ICON = "+";
    private final Color color;
    private final String description;
    private final Permission[] perms;
    private String oauthLink;
    private final String[] features;
    
    public AboutCommand(Color color, String description, String[] features, Permission... perms)
    {
        this.color = color;
        this.description = description;
        this.features = features;
        this.name = "about";
        this.help = "顯示關於機器人的資訊";
        this.guildOnly = false;
        this.perms = perms;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }
    
    public void setIsAuthor(boolean value)
    {
        this.IS_AUTHOR = value;
    }
    
    public void setReplacementCharacter(String value)
    {
        this.REPLACEMENT_ICON = value;
    }
    
    @Override
    protected void execute(CommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("Could not generate invite link ", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : color);
        builder.setAuthor("All about " + event.getSelfUser().getName() + "!", null, event.getSelfUser().getAvatarUrl());
        boolean join = !(event.getClient().getServerInvite() == null || event.getClient().getServerInvite().isEmpty());
        boolean inv = !oauthLink.isEmpty();
        String invline = "\n" + (join ? "Join my server [`here`](" + event.getClient().getServerInvite() + ")" : (inv ? "Please " : "")) 
                + (inv ? (join ? ", or " : "") + "[`invite`](" + oauthLink + ") me to your server" : "") + "!";
        String author = event.getJDA().getUserById(event.getClient().getOwnerId())==null ? "<@" + event.getClient().getOwnerId()+">" 
                : event.getJDA().getUserById(event.getClient().getOwnerId()).getName();
        StringBuilder descr = new StringBuilder().append("你好! 我是 **").append(event.getSelfUser().getName()).append("**, ")
                .append(description).append("\n我").append(IS_AUTHOR ? "使用Java寫成" : "的擁有者是").append(" **")
                .append(author).append("** 正在使用 " + JDAUtilitiesInfo.AUTHOR + "的 [指令擴充](" + JDAUtilitiesInfo.GITHUB + ") (")
                .append(JDAUtilitiesInfo.VERSION).append(") 以及 [JDA 程式庫](https://github.com/DV8FromTheWorld/JDA) (")
                .append(JDAInfo.VERSION).append(")\n輸入 `").append(event.getClient().getTextualPrefix()).append(event.getClient().getHelpWord())
                .append("` 來看見我的指令!").append(join || inv ? invline : "").append("\n\n我擁有這些功能: ```css");
        for (String feature : features)
            descr.append("\n").append(event.getClient().getSuccess().startsWith("<") ? REPLACEMENT_ICON : event.getClient().getSuccess()).append(" ").append(feature);
        descr.append(" ```");
        builder.setDescription(descr);
        if (event.getJDA().getShardInfo() == null)
        {
            builder.addField("狀態", event.getJDA().getGuilds().size() + " 個伺服器\n1 個分片", true);
            builder.addField("使用者", event.getJDA().getUsers().size() + " 個用戶\n" + event.getJDA().getGuilds().stream().mapToInt(g -> g.getMembers().size()).sum() + " total", true);
            builder.addField("頻道", event.getJDA().getTextChannels().size() + " 個文字頻道\n" + event.getJDA().getVoiceChannels().size() + " 個語音頻道", true);
        }
        else
        {
            builder.addField("狀態", (event.getClient()).getTotalGuilds() + " 個伺服器\n於分片 " + (event.getJDA().getShardInfo().getShardId() + 1) 
                    + "/" + event.getJDA().getShardInfo().getShardTotal() + "上", true);
            builder.addField("此分片有", event.getJDA().getUsers().size() + " 個使用者\n" + event.getJDA().getGuilds().size() + " 個伺服器", true);
            builder.addField("", event.getJDA().getTextChannels().size() + " 個文字頻道\n" + event.getJDA().getVoiceChannels().size() + " 個語音頻道", true);
        }
        builder.setFooter("最後一次重新啟動", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }
    
}

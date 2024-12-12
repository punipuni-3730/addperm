package prj.salmon.addparm;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Add_parm extends JavaPlugin {

    @Override
    public void onEnable() {
        // コマンドの登録
        getCommand("addbuilder").setExecutor(new AddBuilderCommand());
    }

    public static class AddBuilderCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length != 1) {
                sender.sendMessage("使用方法: /addbuilder <MCID>");
                return true;
            }

            String targetName = args[0];

            // LuckPerms APIを取得
            LuckPerms luckPerms;
            try {
                luckPerms = LuckPermsProvider.get();
            } catch (IllegalStateException e) {
                sender.sendMessage("LuckPerms API のロードに失敗しました。");
                return true;
            }

            // オフラインまたはオンラインプレイヤーのUUIDを取得
            UUID targetUUID = null;
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer != null) {
                targetUUID = targetPlayer.getUniqueId();
            } else {
                targetUUID = Bukkit.getOfflinePlayer(targetName).getUniqueId();
            }

            if (targetUUID == null) {
                sender.sendMessage("指定されたプレイヤーのUUIDを取得できませんでした。");
                return true;
            }

            // ユーザー情報を取得または作成
            luckPerms.getUserManager().loadUser(targetUUID).thenAcceptAsync(user -> {
                if (user == null) {
                    sender.sendMessage("指定されたプレイヤーのデータを取得できませんでした。");
                    return;
                }

                // グループ「builder」を追加
                Node builderGroupNode = Node.builder("group.builder").build();
                user.data().add(builderGroupNode);
                luckPerms.getUserManager().saveUser(user);

                sender.sendMessage(targetName + " に builder グループを追加しました。");
            });

            return true;
        }
    }
}

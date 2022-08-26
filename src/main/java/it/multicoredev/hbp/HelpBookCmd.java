package it.multicoredev.hbp;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mbcore.spigot.util.TabCompleterUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Copyright © 2022 by Lorenzo Magni
 * This file is part of HelpBookPlus.
 * HelpBookPlus is under "The 3-Clause BSD License", you can find a copy <a href="https://opensource.org/licenses/BSD-3-Clause">here</a>.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
public class HelpBookCmd implements CommandExecutor, TabCompleter {
    private final HelpBookPlus plugin;

    public HelpBookCmd(HelpBookPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("helpbookplus.use")) {
            Chat.send(plugin.config().insufficientPerms, sender);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("helpbookplus.reload")) {
                Chat.send(plugin.config().insufficientPerms, sender);
                return true;
            }

            plugin.onDisable();
            plugin.onEnable();
            Chat.send(plugin.config().reloaded, sender);

            return true;
        }

        Book book;
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                Chat.send(plugin.config().notPlayer, sender);
                return true;
            }

            target = (Player) sender;
            book = plugin.getBook(plugin.config().defBook);

            if (preventAction(target, book)) return true;
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                Chat.send(plugin.config().notPlayer, sender);
                return true;
            }

            target = (Player) sender;
            book = plugin.getBook(args[0]);

            if (preventAction(target, book)) return true;
        } else {
            if (!sender.hasPermission("helpbookplus.others")) {
                Chat.send(plugin.config().insufficientPerms, sender);
                return true;
            }

            book = plugin.getBook(args[0]);
            target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                Chat.send(plugin.config().playerNotFound, sender);
                return true;
            }
        }

        if (book == null) {
            Chat.send(plugin.config().unknownBook, sender);
            return true;
        }

        if (book.needPermission() && !sender.hasPermission("helpbookplus.book." + book.getId())) {
            Chat.send(plugin.config().insufficientPerms, sender);
            return true;
        }

        if (!target.getInventory().addItem(book.getBook()).isEmpty()) {
            Chat.send(plugin.config().insufficientSpace, sender);
            return true;
        }

        return true;
    }

    private boolean preventAction(Player player, Book book) {
        if (book == null) return false;

        if (!plugin.getSpamList().containsKey(player)) {
            Map<String, Date> map = new HashMap<>();
            map.put(book.getId(), new Date());

            plugin.getSpamList().put(player, map);
        } else {
            Map<String, Date> map = plugin.getSpamList().get(player);

            if (!map.containsKey(book.getId())) {
                map.put(book.getId(), new Date());
                plugin.getSpamList().put(player, map);
            } else {
                if (new Date().getTime() - map.get(book.getId()).getTime() < plugin.config().minutes * 60 * 1000) {
                    Chat.send(plugin.config().timeout.replace("{time}", String.valueOf(plugin.config().minutes)), player);
                    return true;
                } else {
                    map.put(book.getId(), new Date());
                    plugin.getSpamList().put(player, map);
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("helpbookplus.use")) return null;
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("helpbookplus.reload")) completions.add("reload");

            plugin.books()
                    .stream()
                    .filter(book -> !book.needPermission() || sender.hasPermission("helpbookplus.book." + book.getId()))
                    .map(Book::getId)
                    .forEach(completions::add);

            return TabCompleterUtil.getCompletions(args[0], completions);
        } else if (args.length == 2) {
            if (!sender.hasPermission("helpbookplus.others")) return null;
            return TabCompleterUtil.getPlayers(args[1], sender.hasPermission("pv.see"));
        }

        return null;
    }
}

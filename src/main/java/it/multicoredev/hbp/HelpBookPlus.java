package it.multicoredev.hbp;

import it.multicoredev.mbcore.spigot.Chat;
import it.multicoredev.mclib.json.GsonHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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
public class HelpBookPlus extends JavaPlugin {
    private static final GsonHelper gson = new GsonHelper();
    private Config config;
    private final File booksDir = new File(getDataFolder(), "books");
    private final List<Book> books = new ArrayList<>();
    private final Map<Player, Map<String, Date>> spamList = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
                if (!getDataFolder().mkdirs()) throw new IOException("Could not create data folder.");
            }

            if (!booksDir.exists() || !booksDir.isDirectory()) {
                if (!booksDir.mkdirs()) throw new IOException("Could not create books folder.");
            }

            config = gson.autoload(new File(getDataFolder(), "config.json"), new Config().init(), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            onDisable();
            return;
        }

        loadBooks();

        HelpBookCmd cmd = new HelpBookCmd(this);
        getCommand("helpbook").setExecutor(cmd);
        getCommand("helpbook").setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        books.clear();
        spamList.clear();
    }

    public Config config() {
        return config;
    }

    public List<Book> books() {
        return books;
    }

    public Book getBook(String id) {
        for (Book book : books) {
            if (book.getId().equals(id)) return book;
        }
        return null;
    }

    public Map<Player, Map<String, Date>> getSpamList() {
        return spamList;
    }

    private void loadBooks() {
        File[] files = booksDir.listFiles();
        if (files == null || files.length == 0) return;

        for (File file : files) {
            if (!file.isFile()) continue;
            if (!file.getName().toLowerCase().endsWith(".json")) return;

            try {
                Book book = gson.load(file, Book.class);
                if (book == null || book.getId() == null || book.getId().trim().isEmpty()) continue;
                books.add(book);
            } catch (IOException e) {
                Chat.warning(e.getMessage());
            }
        }

        Chat.info("&bLoaded &e" + books.size() + " &bbooks.");
    }
}

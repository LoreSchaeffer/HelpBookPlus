package it.multicoredev.hbp;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

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
public class Config extends JsonConfig {
    @SerializedName("insufficient_permissions")
    public String insufficientPerms;
    @SerializedName("incorrect_usage")
    public String icorrectUsage;
    @SerializedName("unknown_book")
    public String unknownBook;
    @SerializedName("player_not_found")
    public String playerNotFound;
    @SerializedName("not_player")
    public String notPlayer;
    @SerializedName("insufficient_space")
    public String insufficientSpace;
    public String timeout;
    public String reloaded;

    @SerializedName("def_book")
    public String defBook;
    public Integer minutes;

    @Override
    public Config init() {
        if (insufficientPerms == null) insufficientPerms = "&cYou do not have permission to use this command.";
        if (icorrectUsage == null) icorrectUsage = "&cIncorrect usage.";
        if (unknownBook == null) unknownBook = "&cUnknown book.";
        if (playerNotFound == null) playerNotFound = "&cPlayer not found.";
        if (notPlayer == null) notPlayer = "&cYou are not a player.";
        if (insufficientSpace == null) insufficientSpace = "&cYou do not have enough space in your inventory.";
        if (reloaded == null) reloaded = "&aHelpBookPlus reloaded.";
        if (timeout == null) timeout = "&cYou can get this book once every {time} minutes.";
        if (defBook == null) defBook = "default";
        if (minutes == null) minutes = 5;

        return this;
    }
}

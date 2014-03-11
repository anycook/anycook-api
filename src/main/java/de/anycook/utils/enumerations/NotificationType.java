/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.utils.enumerations;

public enum NotificationType {
    RECIPEACTIVATION,
    DISCUSSION,
    TAGACCEPTED,
    TAGDENIED,
    NEWSLETTER,
    DISCUSSIONANSWER,
    TASTES,
    RESETPASSWORD,
    ACCOUNTACTIVATION,
    NEWMESSAGE,
    NEWMAIL;

    @Override
    public String toString() {
        switch (this){
            case RECIPEACTIVATION:
                return "recipeActivation";
            case DISCUSSION:
                return "discussion";
            case DISCUSSIONANSWER:
                return "discussionAnswer";
            case TAGACCEPTED:
                return "tagAccepted";
            case TAGDENIED:
                return "tagDenied";
            case ACCOUNTACTIVATION:
                return "accountActivation";
            case RESETPASSWORD:
                return "resetPassword";
            case TASTES:
                return "tastes";
            case NEWSLETTER:
                return "newsletter";
            case NEWMESSAGE:
                return "newMessage";
            case NEWMAIL:
                return "newMail";
        }
        return null;
    }


}

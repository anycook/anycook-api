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
    RECIPE_ACTIVATION,
    DISCUSSION,
    TAG_ACCEPTED,
    TAG_DENIED,
    NEWSLETTER,
    DISCUSSION_ANSWER,
    TASTES,
    RESET_PASSWORD,
    ACCOUNT_ACTIVATION,
    NEW_MESSAGE,
    NEW_MAIL,
    ADMIN_NEW_VERSION;

    @Override
    public String toString() {
        switch (this){
            case RECIPE_ACTIVATION:
                return "recipeActivation";
            case DISCUSSION:
                return "discussion";
            case DISCUSSION_ANSWER:
                return "discussionAnswer";
            case TAG_ACCEPTED:
                return "tagAccepted";
            case TAG_DENIED:
                return "tagDenied";
            case ACCOUNT_ACTIVATION:
                return "accountActivation";
            case RESET_PASSWORD:
                return "resetPassword";
            case TASTES:
                return "tastes";
            case NEWSLETTER:
                return "newsletter";
            case NEW_MESSAGE:
                return "newMessage";
            case NEW_MAIL:
                return "newMail";
            case ADMIN_NEW_VERSION:
                return "adminNewVersion";
        }
        return null;
    }


}

package de.anycook.mailprovider;

import de.anycook.db.mysql.DBMailProvider;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public final class MailProviders {

    private MailProviders() {

    }

    public static List<MailProvider> getMailProviders() throws SQLException {
        DBMailProvider dbMailProvider = new DBMailProvider();
        return dbMailProvider.getMailProviders();
    }

    public static MailProvider getMailProviderForDomain(String domain) throws SQLException,
            DBMailProvider.ProviderNotFoundException {
        try (DBMailProvider dbmailprovider = new DBMailProvider()) {
            return dbmailprovider.getMailProviderByDomain(domain);
        }
    }

    public static MailProvider getMailProvider(String shortName) throws SQLException,
        DBMailProvider.ProviderNotFoundException {
        try (DBMailProvider dbMailProvider = new DBMailProvider()) {
            return dbMailProvider.getMailProvider(shortName);
        }
    }

    public static void updateMailProvider(String shortName, MailProvider mailProvider) throws SQLException {
        try (DBMailProvider dbMailProvider = new DBMailProvider()) {
            if (!dbMailProvider.checkMailProvider(shortName)) dbMailProvider.addMailProvider(mailProvider);
            else dbMailProvider.updateMailProvider(shortName, mailProvider);
        }
    }

    public static void deleteMailProvider(String shortName) throws SQLException {
        try (DBMailProvider dbMailProvider = new DBMailProvider()) {
            dbMailProvider.deleteMailProvider(shortName);
        }
    }
}

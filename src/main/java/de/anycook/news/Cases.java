package de.anycook.news;

import de.anycook.db.mysql.DBLive;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class Cases {
    private Cases() {}
    
    public static List<Case> getCases() throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            return dbLive.getCases();
        }
    }

    public static Case getCase(String name) throws SQLException, DBLive.CaseNotFoundException {
        try (DBLive dbLive = new DBLive()) {
            return dbLive.getCase(name);
        }
    }

    public static String newCase(Case cAse) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            dbLive.newCase(cAse.getName(), cAse.getSyntax());
            return cAse.getName();
        }
    }

    public static void updateCase(String name, String syntax) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            if (!dbLive.checkCase(name))  dbLive.newCase(name, syntax);
            else dbLive.updateCase(name, syntax);
        }
    }

    public static void deleteCase(String name) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            dbLive.deleteCase(name);
        }
    }
}

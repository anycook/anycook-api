package de.anycook.news;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class Case {
    private String name;
    private String syntax;

    public Case() {
        this.name = null;
        this.syntax = null;
    }

    public Case(String name, String syntax) {
        this.name = name;
        this.syntax = syntax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }
}

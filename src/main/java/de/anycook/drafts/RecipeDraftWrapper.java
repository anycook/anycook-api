package de.anycook.drafts;

public class RecipeDraftWrapper {
    private String id;
    private RecipeDraft recipeDraft;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecipeDraft getRecipeDraft() {
        return recipeDraft;
    }

    public void setRecipeDraft(RecipeDraft recipeDraft) {
        this.recipeDraft = recipeDraft;
    }
}

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

package de.anycook.db.lucene;

import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.recipe.Recipe;
import de.anycook.recipe.step.Step;
import de.anycook.recipe.step.Steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class FulltextIndex {

    private static FulltextIndex singleton = null;

    private final Logger logger;
    private final Analyzer analyzer = new NGramAnalyzer();
    private final Directory index;
    //private final IndexWriterConfig indexWriterConfig;


    private FulltextIndex() throws IOException {
        logger = LogManager.getLogger(getClass());
        String indexPath = Configuration.getInstance().getFullTextIndexPath();
        index = new NIOFSDirectory(Paths.get(indexPath));
    }

    public static FulltextIndex init() throws IOException {
        if (singleton == null) {
            singleton = new FulltextIndex();
            LogManager.getLogger(FulltextIndex.class).info("created new instance of FulltextIndex");
        }

        return singleton;
    }

    private IndexWriterConfig createIndexWriterConfig() {
        return new IndexWriterConfig(analyzer);
    }


    public void addRecipe(String recipeName)
            throws SQLException, DBRecipe.RecipeNotFoundException, IOException {
        recipeName = recipeName.toLowerCase(Locale.GERMAN);
        if (checkIfRecipeExists(recipeName)) {
            removeRecipe(recipeName);
        }

        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            Recipe recipe = dbGetRecipe.get(recipeName);

            int id = dbGetRecipe.getActiveIdfromRecipe(recipeName);

            String date = DateTools.dateToString(new Date(), Resolution.DAY);

            List<Step> steps = Steps.loadRecipeSteps(recipeName);
            StringBuilder stepText = new StringBuilder();
            for (Step step : steps) {
                stepText.append(" ").append(step.getText());
            }

            try (IndexWriter writer = new IndexWriter(index, createIndexWriterConfig())) {
                Document doc = new Document();
                doc.add(new TextField("title", recipe.getName(), Field.Store.YES));
                doc.add(new TextField("description", recipe.getDescription() == null ? "" : recipe
                        .getDescription(),
                                      Field.Store.YES));
                doc.add(new TextField("steps", stepText.toString(), Field.Store.YES));
                doc.add(new IntField("version_id", id, Field.Store.YES));
                doc.add(new TextField("date", date, Field.Store.YES));

                writer.addDocument(doc);
                writer.commit();

                logger.info("added " + recipeName + " to index");

            } catch (CorruptIndexException | LockObtainFailedException e) {
                throw new IOException(e);
            }
        }
    }

    public void clearIndex() throws IOException {
        try (IndexWriter writer = new IndexWriter(index, createIndexWriterConfig())) {
            writer.deleteAll();
            writer.commit();
            logger.info("cleared index");
        } catch (CorruptIndexException e) {
            throw new IOException(e);
        }
    }

    public void addAllRecipes() throws SQLException, IOException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe();
             IndexWriter writer = new IndexWriter(index, createIndexWriterConfig())) {
            writer.deleteAll();
            for (String recipeName : dbGetRecipe.getAllActiveRecipeNames()) {
                try {
                    Document document = generateRecipeDoc(recipeName);
                    writer.addDocument(document);
                } catch (DBRecipe.RecipeNotFoundException e) {
                    //nope
                }
            }
            writer.commit();
            logger.info("successfully build fulltext index");
        }
    }

    public Document generateRecipeDoc(String recipeName)
            throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            Recipe recipe = dbGetRecipe.get(recipeName);

            List<Step> steps = Steps.loadRecipeSteps(recipeName);
            StringBuilder stepText = new StringBuilder();
            for (Step step : steps) {
                stepText.append(" ").append(step.getText());
            }

            int id = dbGetRecipe.getActiveIdfromRecipe(recipeName);
            String date = DateTools.dateToString(new Date(), Resolution.DAY);

            Document doc = new Document();
            doc.add(new TextField("title", recipe.getName(), Field.Store.YES));
            doc.add(new TextField("description",
                                  recipe.getDescription() == null ? "" : recipe.getDescription(),
                                  Field.Store.YES));
            doc.add(new TextField("steps", stepText.toString(), Field.Store.YES));
            doc.add(new IntField("version_id", id, Field.Store.YES));
            doc.add(new TextField("date", date, Field.Store.YES));

            return doc;
        }

    }

    public void removeRecipe(String recipeName) throws IOException {

        try (IndexWriter writer = new IndexWriter(index, createIndexWriterConfig())) {
            writer.deleteDocuments(new Term("title", recipeName));
            writer.commit();

            logger.info("removed " + recipeName + " from index");
        } catch (CorruptIndexException e) {
            throw new IOException(e);
        }
    }

    private boolean checkIfRecipeExists(String recipeName) throws IOException {
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            MultiFieldQueryParser.parse(new String[]{recipeName}, new String[]{"title"}, analyzer);
            Query query = new QueryParser("title", analyzer).parse(recipeName);
            TopDocs topdocs = searcher.search(query, 1);
            if (topdocs.totalHits > 0) {
                return true;
            }
        } catch (CorruptIndexException | ParseException e) {
            throw new IOException(e);
        }

        return false;
    }

    public Set<String> search(String q) throws IOException {
        Set<String> recipes = new LinkedHashSet<>();
        String fields[] = new String[]{"description", "steps"};
        logger.debug(String.format("searching for %s", q));

        try (IndexReader reader = DirectoryReader.open(index)) {
            int hitsPerPage = 1000;
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new MultiFieldQueryParser(fields, analyzer).parse(q);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, null);
            searcher.search(query, collector);

            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            for (ScoreDoc hit : hits) {
                Document d = searcher.doc(hit.doc);
                recipes.add(d.get("title"));
            }

        } catch (CorruptIndexException | ParseException e) {
            logger.error(e);
        }

        logger.debug(String.format("found %d results", recipes.size()));
        return recipes;
    }

    public Set<String> search(Set<String> queries) throws IOException {
        Set<String> recipeNames = new HashSet<>();
        for (String term : queries) {
            recipeNames.addAll(search(term));
        }

        return recipeNames;
    }
}

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

package de.anycook.api.filter.xss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.filterchain.Filter;

/**
 * Servlet Filter implementation class XSSFilter
 */

@PreMatching
public class XSSFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        cleanHeaders(requestContext.getHeaders());
        cleanParams(requestContext);
    }

    private void cleanHeaders(MultivaluedMap<String, String> headers) {
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            String key = header.getKey();
            List<String> values = header.getValue();

            List<String> cleanValues = new ArrayList<>();
            for (String value : values) {
                cleanValues.add(cleanXSS(value));
            }

            headers.put(key, cleanValues);
        }
    }

    private void cleanParams(ContainerRequestContext requestContext) {
        UriBuilder builder = requestContext.getUriInfo().getRequestUriBuilder();
        MultivaluedMap<String, String> queries = requestContext.getUriInfo().getQueryParameters();

        for (Map.Entry<String, List<String>> query : queries.entrySet()) {
            String key = query.getKey();
            List<String> values = query.getValue();

            List<String> xssValues = new ArrayList<String>();
            for (String value : values) {
                xssValues.add(cleanXSS(value));
            }

            int size = xssValues.size();
            builder.replaceQueryParam(key);

            if (size == 1) {
                builder.replaceQueryParam(key, xssValues.get(0));
            } else if (size > 1) {
                builder.replaceQueryParam(key, xssValues.toArray());
            }
        }

        requestContext.setRequestUri(builder.build());
    }

    private String cleanXSS(String value) {
        //value = value.replaceAll("<.*script", "");
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        //value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        //value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return value;
    }
}

package org.segrada.search;

import java.util.Map;

/**
 * Copyright 2015 Maximilian Kalus [segrada@auxnet.de]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Search engine interface
 */
public interface SearchEngine {
	/**
	 * index new document in search engine
	 *
	 * @param id        of document e.g. db id
	 * @param className full classname referenced - may be a dummy
	 * @param title     title of document, if any (should be ranked very high)
	 * @param subTitles subtitles of document, if any (should be ranked relatively high)
	 * @param content   text content of document, if any (plain text)
	 * @param tagIds    ids of tags the indexed element belongs to
	 * @param weight    weight of document, might be dependent of class or user definition
	 * @return
	 */
	boolean index(String id, String className, String title, String subTitles, String content, String[] tagIds, float weight);

	/**
	 * Do a search
	 *
	 * @param searchTerm term(s) to search for
	 * @param filters    possible filters to search in, e.g. class names, etc.
	 * @return
	 */
	SearchResult search(String searchTerm, Map<String, String> filters);

	/**
	 * Remove entity from index
	 *
	 * @param id of document e.g. db id
	 */
	void remove(String id);

	/**
	 * Find entry by id an return all data in search engine - used for testing
	 *
	 * @param id of saved entry
	 * @return single search hit or null
	 */
	SearchHit getById(String id);

	/**
	 * completely clear index
	 */
	void clearAllIndexes();
}
package org.segrada.search;

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
 * Representation of a single search hit
 */
public class SearchHit {
	protected String id;

	protected String className;

	protected String title;

	protected String subTitles;

	protected String[] tagIds;

	/**
	 * should contain highlight fragments
	 */
	protected String[] highlightText;

	protected float relevance;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitles() {
		return subTitles;
	}

	public void setSubTitles(String subTitles) {
		this.subTitles = subTitles;
	}

	public String[] getTagIds() {
		return tagIds;
	}

	public void setTagIds(String[] tagIds) {
		this.tagIds = tagIds;
	}

	public String[] getHighlightText() {
		return highlightText;
	}

	public void setHighlightText(String[] highlightText) {
		this.highlightText = highlightText;
	}

	public float getRelevance() {
		return relevance;
	}

	public void setRelevance(float relevance) {
		this.relevance = relevance;
	}
}
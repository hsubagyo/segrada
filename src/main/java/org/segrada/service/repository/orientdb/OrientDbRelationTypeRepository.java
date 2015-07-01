package org.segrada.service.repository.orientdb;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.TermQuery;
import org.segrada.model.RelationType;
import org.segrada.model.prototype.IRelationType;
import org.segrada.model.prototype.ITag;
import org.segrada.model.prototype.SegradaAnnotatedEntity;
import org.segrada.service.repository.RelationRepository;
import org.segrada.service.repository.RelationTypeRepository;
import org.segrada.service.repository.TagRepository;
import org.segrada.service.repository.orientdb.base.AbstractColoredOrientDbRepository;
import org.segrada.service.repository.orientdb.factory.OrientDbRepositoryFactory;
import org.segrada.service.util.PaginationInfo;
import org.segrada.util.OrientStringEscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Copyright 2015 Maximilian Kalus [segrada@auxnet.de]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * OrientDb Relation type Repository
 */
public class OrientDbRelationTypeRepository extends AbstractColoredOrientDbRepository<IRelationType> implements RelationTypeRepository {
	private static final Logger logger = LoggerFactory.getLogger(OrientDbRelationTypeRepository.class);

	/**
	 * Constructor
	 */
	@Inject
	public OrientDbRelationTypeRepository(OrientDbRepositoryFactory repositoryFactory) {
		super(repositoryFactory);
	}

	@Override
	public String getModelClassName() {
		return "RelationType";
	}

	@Override
	public IRelationType convertToEntity(ODocument document) {
		RelationType relationType = new RelationType();

		relationType.setFromTitle(document.field("fromTitle", String.class));
		relationType.setToTitle(document.field("toTitle", String.class));
		relationType.setDescription(document.field("description", String.class));
		relationType.setDescriptionMarkup(document.field("descriptionMarkup", String.class));

		// populate with data
		populateEntityWithBaseData(document, relationType);
		populateEntityWithCreatedModified(document, relationType);
		populateEntityWithColored(document, relationType);

		// set tags
		relationType.setTags(lazyLoadTags(relationType));

		return relationType;
	}

	@Override
	public ODocument convertToDocument(IRelationType entity) {
		ODocument document = createOrLoadDocument(entity);

		// populate with data
		document.field("fromTitle", entity.getFromTitle())
				.field("toTitle", entity.getToTitle())
				.field("description", entity.getDescription())
				.field("descriptionMarkup", entity.getDescriptionMarkup());

		// populate with data
		populateODocumentWithCreatedModified(document, (RelationType) entity);
		populateODocumentWithColored(document, (RelationType) entity);

		return document;
	}

	@Override
	protected String getDefaultOrder(boolean addOrderBy) {
		return (addOrderBy?" ORDER BY":"").concat(" fromTitle ASC, toTitle ASC");
	}

	@Override
	public List<IRelationType> findBySearchTerm(String term, int maximum, boolean returnWithoutTerm) {
		List<IRelationType> hits = new LinkedList<>();

		// empty search term and returnWithoutTerm false
		if (!returnWithoutTerm && (term == null || term.equals(""))) return hits;

		initDb();

		// search for term
		List<ODocument> result;
		if (term != null && term.length() > 0) {
			// execute query
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select * from RelationType where " + createSearchTermFullText(term) + " LIMIT " + maximum);
			result = db.command(query).execute();
		} else { // no term, just find top X entries
			// execute query
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select * from RelationType " + getDefaultOrder(true) + " LIMIT " + maximum);
			result = db.command(query).execute();
		}

		// browse entities
		for (ODocument document : result) {
			hits.add(convertToEntity(document));
		}

		return hits;
	}

	@Override
	public PaginationInfo<IRelationType> paginate(int page, int entriesPerPage, Map<String, Object> filters) {
		// avoid NPEs
		if (filters == null) filters = new HashMap<>();

		// aggregate filters
		List<String> constraints = new LinkedList<>();
		// search term
		if (filters.get("search") != null) {
			constraints.add(createSearchTermFullText((String) filters.get("search")));
		}
		// location
		// TODO search locations and contain
		// period
		//TODO minJD/maxJD
		// tags
		if (filters.get("tags") != null) {
			StringBuilder sb = new StringBuilder(" in('IsTagOf').title IN [ ");
			boolean first = true;
			for (String tag : (String[]) filters.get("tags")) {
				if (first) first = false;
				else sb.append(",");
				sb.append("'").append(OrientStringEscape.escapeOrientSql(tag)).append("'");
			}

			constraints.add(sb.append("]").toString());
		}

		// let helper do most of the work
		return super.paginate(page, entriesPerPage, constraints);
	}

	/**
	 * create search term for full text search
	 * @param term term(s) to search for
	 * @return search term part
	 */
	private String createSearchTermFullText(String term) {
		// create query term for lucene full text search
		StringBuilder sb = new StringBuilder(" [fromTitle, toTitle] LUCENE ");
		boolean first = true;
		for (String termPart : term.toLowerCase().split("\\s+")) {
			if (termPart.contains(":")) termPart = "\"" + termPart + "\"";
			else termPart = QueryParserUtil.escape(termPart);
			if (termPart.contains(".")) termPart = "\"" + termPart + "\"";
			else if (!termPart.startsWith("\"") || !termPart.endsWith("\"")) termPart += "*";
			TermQuery termQuery1 = new TermQuery(new Term("fromTitle", termPart));
			TermQuery termQuery2 = new TermQuery(new Term("toTitle", termPart));
			if (first) first = false;
			else sb.append(" AND [fromTitle, toTitle] LUCENE ");
			sb.append("(").append(termQuery1.toString()).append(" OR ").append(termQuery2.toString()).append(")");
		}

		return sb.toString();
	}

	@Override
	protected IRelationType processAfterSaving(ODocument updated, IRelationType entity) {
		super.processAfterSaving(updated, entity);

		// connect tags
		updateEntityTags(entity);

		return entity;
	}

	@Override
	public boolean delete(IRelationType entity) {
		if (super.delete(entity)) {
			// delete connected relations
			RelationRepository relationRepository = repositoryFactory.produceRepository(OrientDbRelationRepository.class);
			if (relationRepository == null) {
				logger.error("Could not produce RelationRepository while deleting relation type.");
				return false;
			}
			relationRepository.deleteByRelationType(entity);

			return true;
		}
		return false;
	}
}

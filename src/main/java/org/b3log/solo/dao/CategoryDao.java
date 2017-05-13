/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.dao;

import java.text.Collator;
import java.util.Collections;
import java.util.List;

import org.b3log.solo.Keys;
import org.b3log.solo.frame.repository.FilterOperator;
import org.b3log.solo.frame.repository.PropertyFilter;
import org.b3log.solo.frame.repository.Query;
import org.b3log.solo.frame.repository.RepositoryException;
import org.b3log.solo.frame.repository.SortDirection;
import org.b3log.solo.model.Category;
import org.b3log.solo.model.Tag;
import org.b3log.solo.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

/**
 * Category repository.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.2, Apr 19, 2017
 * @since 2.0.0
 */
@Repository
public class CategoryDao extends AbstractBlogDao {

	@Override
	public String getTableNamePostfix() {
		return Category.CATEGORY;
	}

	public JSONObject getByTitle(final String categoryTitle) throws RepositoryException {
		final Query query = new Query()
				.setFilter(new PropertyFilter(Category.CATEGORY_TITLE, FilterOperator.EQUAL, categoryTitle))
				.setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	public JSONObject getByURI(final String categoryURI) throws RepositoryException {
		final Query query = new Query()
				.setFilter(new PropertyFilter(Category.CATEGORY_URI, FilterOperator.EQUAL, categoryURI))
				.setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	public int getMaxOrder() throws RepositoryException {
		final Query query = new Query();

		query.addSort(Category.CATEGORY_ORDER, SortDirection.DESCENDING);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return -1;
		}

		return array.optJSONObject(0).optInt(Category.CATEGORY_ORDER);
	}

	public JSONObject getByOrder(final int order) throws RepositoryException {
		final Query query = new Query();

		query.setFilter(new PropertyFilter(Category.CATEGORY_ORDER, FilterOperator.EQUAL, order));

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (0 == array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	public List<JSONObject> getMostUsedCategories(final int num) throws RepositoryException {
		final Query query = new Query().addSort(Category.CATEGORY_ORDER, SortDirection.ASCENDING).setCurrentPageNum(1)
				.setPageSize(num).setPageCount(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		final List<JSONObject> ret = CollectionUtils.jsonArrayToList(array);
		sortJSONCategoryList(ret);

		return ret;
	}

	public JSONObject getUpper(final String id) throws RepositoryException {
		final JSONObject category = get(id);

		if (null == category) {
			return null;
		}

		final Query query = new Query();

		query.setFilter(new PropertyFilter(Category.CATEGORY_ORDER, FilterOperator.LESS_THAN,
				category.optInt(Category.CATEGORY_ORDER))).addSort(Category.CATEGORY_ORDER, SortDirection.DESCENDING);
		query.setCurrentPageNum(1);
		query.setPageSize(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (1 != array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	public JSONObject getUnder(final String id) throws RepositoryException {
		final JSONObject category = get(id);

		if (null == category) {
			return null;
		}

		final Query query = new Query();

		query.setFilter(new PropertyFilter(Category.CATEGORY_ORDER, FilterOperator.GREATER_THAN,
				category.optInt(Category.CATEGORY_ORDER))).addSort(Category.CATEGORY_ORDER, SortDirection.ASCENDING);
		query.setCurrentPageNum(1);
		query.setPageSize(1);

		final JSONObject result = get(query);
		final JSONArray array = result.optJSONArray(Keys.RESULTS);

		if (1 != array.length()) {
			return null;
		}

		return array.optJSONObject(0);
	}

	private void sortJSONCategoryList(final List<JSONObject> tagJoList) {
		Collections.sort(tagJoList, (o1, o2) -> Collator.getInstance(java.util.Locale.CHINA)
				.compare(o1.optString(Tag.TAG_TITLE), o2.optString(Tag.TAG_TITLE)));
	}
}

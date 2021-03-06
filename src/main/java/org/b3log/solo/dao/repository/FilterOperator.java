/*
 * Copyright (c) 2017, cxy7.com
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
package org.b3log.solo.dao.repository;

/**
 * Filter operator.
 *
 * @author <a href="http://cxy7.com">XyCai</a>
 * @version 1.1.1.1, Jun 25, 2016
 * @see Filter
 */
public enum FilterOperator {

	/**
	 * Less than.
	 */
	LESS_THAN,
	/**
	 * Less than or equal.
	 */
	LESS_THAN_OR_EQUAL,
	/**
	 * Greater than.
	 */
	GREATER_THAN,
	/**
	 * Grater than or equal.
	 */
	GREATER_THAN_OR_EQUAL,
	/**
	 * Equal.
	 */
	EQUAL,
	/**
	 * Not equal.
	 */
	NOT_EQUAL,
	/**
	 * In.
	 */
	IN,
	/**
	 * Like.
	 *
	 * <p>
	 * <b>Note</b>: This operation just support JDBC repository.
	 * </p>
	 */
	LIKE,
	/**
	 * Not like.
	 *
	 * <p>
	 * <b>Note</b>: This operation just support JDBC repository.
	 * </p>
	 */
	NOT_LIKE,
}

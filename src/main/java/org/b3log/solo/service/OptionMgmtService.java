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
package org.b3log.solo.service;

import org.b3log.solo.Keys;
import org.b3log.solo.dao.OptionDao;
import org.b3log.solo.model.Option;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Option management service.
 *
 * @author <a href="http://cxy7.com">XyCai</a>
 * @version 1.0.0.0, Apr 16, 2013
 * @since 0.6.0
 */
@Service
public class OptionMgmtService {

	/**
	 * Option repository.
	 */
	@Autowired
	private OptionDao optionRepository;

	/**
	 * Adds or updates the specified option.
	 * 
	 * @param option
	 *            the specified option
	 * @return option id
	 * @throws ServiceException
	 */
	public String addOrUpdateOption(final JSONObject option) throws ServiceException {
		// final Transaction transaction = optionRepository.beginTransaction();

		try {
			String id = option.optString(Keys.OBJECT_ID);

			if (StringUtils.isBlank(id)) {
				id = optionRepository.add(option);
			} else {
				final JSONObject old = optionRepository.get(id);

				if (null == old) { // The id is specified by caller
					id = optionRepository.add(option);
				} else {
					old.put(Option.OPTION_CATEGORY, option.optString(Option.OPTION_CATEGORY));
					old.put(Option.OPTION_VALUE, option.optString(Option.OPTION_VALUE));

					optionRepository.update(id, old);
				}
			}

			// transaction.commit();

			return id;
		} catch (final Exception e) {
			// if (transaction.isActive()) {
			// transaction.rollback();
			// }

			throw new ServiceException(e);
		}
	}

	/**
	 * Removes the option specified by the given option id.
	 * 
	 * @param optionId
	 *            the given option id
	 * @throws ServiceException
	 *             service exception
	 */
	public void removeOption(final String optionId) throws ServiceException {
		// final Transaction transaction = optionRepository.beginTransaction();

		try {
			optionRepository.remove(optionId);

			// transaction.commit();
		} catch (final Exception e) {
			// if (transaction.isActive()) {
			// transaction.rollback();
			// }

			throw new ServiceException(e);
		}
	}

	/**
	 * Sets the option repository with the specified option repository.
	 * 
	 * @param optionRepository
	 *            the specified option repository
	 */
	public void setOptionRepository(final OptionDao optionRepository) {
		this.optionRepository = optionRepository;
	}
}

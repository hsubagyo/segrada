package org.segrada.service;

import org.segrada.model.UserGroup;
import org.segrada.model.prototype.IUserGroup;
import org.segrada.service.base.AbstractRepositoryService;
import org.segrada.service.repository.UserGroupRepository;
import org.segrada.service.repository.factory.RepositoryFactory;

import javax.inject.Inject;

/**
 * Copyright 2016 Maximilian Kalus [segrada@auxnet.de]
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
 * User Group service
 */
public class UserGroupService extends AbstractRepositoryService<IUserGroup, UserGroupRepository> {
	/**
	 * Constructor
	 */
	@Inject
	public UserGroupService(RepositoryFactory repositoryFactory) {
		super(repositoryFactory, UserGroupRepository.class);
	}

	@Override
	public IUserGroup createNewInstance() {
		return new UserGroup();
	}

	@Override
	public Class<IUserGroup> getModelClass() {
		return IUserGroup.class;
	}

	/**
	 * find special user group
	 * @param special type of group
	 * @return single user group or null
	 */
	public IUserGroup findSpecial(String special) {
		return repository.findSpecial(special);
	}
}

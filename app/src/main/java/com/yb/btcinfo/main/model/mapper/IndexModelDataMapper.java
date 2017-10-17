/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yb.btcinfo.main.model.mapper;

import com.yb.btcinfo.main.model.IndexDataModel;
import com.yb.btcinfo.main.model.UserModel;
import com.yb.btcinfo.repository.bean.User;
import com.yb.btcinfo.repository.entity.IndexEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Mapper class used to transform {@link com.yb.btcinfo.repository.entity.IndexEntity} (in the domain layer) to {@link UserModel} in the
 * presentation layer.
 */
public class IndexModelDataMapper {

  public IndexModelDataMapper() {}

  /**
   * Transform a {@link com.yb.btcinfo.repository.entity.IndexEntity} into an {@link com.yb.btcinfo.main.model.IndexDataModel}.
   *
   * @param index Object to be transformed.
   * @return {@link com.yb.btcinfo.main.model.IndexDataModel}.
   */
  public IndexDataModel transform(IndexEntity index) {
    if (index == null) {
      throw new IllegalArgumentException("Cannot transform a null value");
    }
    final IndexDataModel indexModel = new IndexDataModel();
    indexModel.setId(index.getId());
    indexModel.setPlatform(index.getPlatform());
    indexModel.setTime(index.getTime());
    indexModel.setTitle(index.getTitle());
    indexModel.setUrl(index.getUrl());
    indexModel.setLogourl(index.getLogourl());
    return indexModel;
  }

  /**
   * Transform a Collection of {@link User} into a Collection of {@link IndexDataModel}.
   *
   * @param indexEntities Objects to be transformed.
   * @return List of {@link IndexDataModel}.
   */
  public List<IndexDataModel> transform(Collection<IndexEntity> indexEntities) {
    List<IndexDataModel> userModelsCollection;

    if (indexEntities != null && !indexEntities.isEmpty()) {
      userModelsCollection = new ArrayList<>();
      for (IndexEntity indexEntity : indexEntities) {
        userModelsCollection.add(transform(indexEntity));
      }
    } else {
      userModelsCollection = Collections.emptyList();
    }

    return userModelsCollection;
  }
}

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
package com.yb.btcinfo.main.datamodel.mapper;

import com.yb.btcinfo.main.datamodel.PlatformModel;
import com.yb.btcinfo.main.datamodel.UserModel;
import com.yb.btcinfo.repository.entity.PlatformEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper class used to transform {@link com.yb.btcinfo.repository.entity.PlatformEntity} (in the domain layer) to {@link UserModel} in the
 * presentation layer.
 */
public class PlatformModelDataMapper {

  public PlatformModelDataMapper() {}

  /**
   * Transform a {@link com.yb.btcinfo.repository.entity.PlatformEntity} into an {@link com.yb.btcinfo.main.datamodel.PlatformModel}.
   *
   * @param entity Object to be transformed.
   * @return {@link com.yb.btcinfo.main.datamodel.PlatformModel}.
   */
  public PlatformModel transform(PlatformEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Cannot transform a null value");
    }
    final PlatformModel platformModel = new PlatformModel();
    platformModel.setId(entity.getId());
    platformModel.setName(entity.getName());
    platformModel.setLogourl(entity.getLogourl());
    return platformModel;
  }

  /**
   * Transform a Collection of {@link com.yb.btcinfo.repository.entity.PlatformEntity} into a Collection of {@link com.yb.btcinfo.main.datamodel.PlatformModel}.
   *
   * @param platformEntities Objects to be transformed.
   * @return List of {@link com.yb.btcinfo.main.datamodel.PlatformModel}.
   */
  public List<PlatformModel> transform(List<PlatformEntity> platformEntities) {
    List<PlatformModel> platformModels;

    if (platformEntities != null && !platformEntities.isEmpty()) {
      platformModels = new ArrayList<>();
      for (PlatformEntity entity : platformEntities) {
        platformModels.add(transform(entity));
      }
    } else {
      platformModels = Collections.emptyList();
    }

    return platformModels;
  }
}

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
package com.yb.btcinfo.platform.datamode.mapper;

import com.yb.btcinfo.platform.datamode.PlatformListMode;
import com.yb.btcinfo.repository.entity.PlatformListEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class PlatformListModelDataMapper {

  public PlatformListModelDataMapper() {}

  public PlatformListMode transform(PlatformListEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Cannot transform a null value");
    }
    final PlatformListMode platformModel = new PlatformListMode();
    platformModel.setId(entity.getId());
    platformModel.setTitle(entity.getTitle());
    platformModel.setLogourl(entity.getLogourl());
    platformModel.setTime(entity.getTime());
    platformModel.setUrl(entity.getUrl());
    platformModel.setPlatform(entity.getPlatform());
    return platformModel;
  }

  public List<PlatformListMode> transform(List<PlatformListEntity> platformEntities) {
    List<PlatformListMode> platformModels;

    if (platformEntities != null && !platformEntities.isEmpty()) {
      platformModels = new ArrayList<>();
      for (PlatformListEntity entity : platformEntities) {
        platformModels.add(transform(entity));
      }
    } else {
      platformModels = Collections.emptyList();
    }

    return platformModels;
  }
}

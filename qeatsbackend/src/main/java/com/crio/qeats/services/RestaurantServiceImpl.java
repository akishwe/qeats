/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;


  // COMPLETED: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
    double currentLat = getRestaurantsRequest.getLatitude();
    double currentLong = getRestaurantsRequest.getLongitude();

    Double servingRange = normalHoursServingRadiusInKms;
    if (isPeekHour(currentTime)) {
      servingRange = peakHoursServingRadiusInKms;
    }

    List<Restaurant> restaurants;
    restaurants = restaurantRepositoryService
        .findAllRestaurantsCloseBy(currentLat, currentLong, currentTime, servingRange);

    restaurants.forEach(restaurant -> {
      restaurant.setName(StringUtils.stripAccents(restaurant.getName()));
    });
    return new GetRestaurantsResponse(restaurants);
  }



  private boolean isPeekHour(LocalTime currentTime) {
    LocalTime s1 = LocalTime.of(8, 0);
    LocalTime e1 = LocalTime.of(10, 0);

    LocalTime s2 = LocalTime.of(13, 0);
    LocalTime e2 = LocalTime.of(14, 0);

    LocalTime s3 = LocalTime.of(19, 0);
    LocalTime e3 = LocalTime.of(21, 0);

    return (currentTime.isAfter(s1) && currentTime.isBefore(e1))
        || (currentTime.isAfter(s2) && currentTime.isBefore(e2))
        || (currentTime.isAfter(s3) && currentTime.isBefore(e3))
        || currentTime.equals(s1) || currentTime.equals(e1)
        || currentTime.equals(s2) || currentTime.equals(e2)
        || currentTime.equals(s3) || currentTime.equals(e3);
  }

 
}

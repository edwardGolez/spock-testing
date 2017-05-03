package com.synacy.lesson03.demo;

import com.synacy.lesson03.demo.domain.FinishedGood;

import java.util.Map;

public interface WarehouseService {
	void updateInventoryLevel(Map<FinishedGood, Integer> totalItemsShipped);
}

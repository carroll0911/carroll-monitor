package com.carroll.monitor.analyzer.service.impl;

import com.carroll.cache.RedisUtil;
import com.carroll.monitor.analyzer.dto.ChartSeriesItem;
import com.carroll.monitor.analyzer.model.ItemSummaryRecord;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.repository.ItemSummaryRecordRepository;
import com.carroll.monitor.analyzer.repository.MonitorItemRepository;
import com.carroll.monitor.analyzer.response.ItemSummary4LineResponse;
import com.carroll.monitor.analyzer.service.IItemSummaryRecordService;
import com.carroll.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: carroll
 * @date 2019/10/16
 *
 */
@Service
public class ItemSummaryRecordServiceImpl implements IItemSummaryRecordService {

    @Autowired
    private ItemSummaryRecordRepository itemSummaryRecordRepository;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MonitorItemRepository monitorItemRepository;
    @Value("${summary4LineTop:10}")
    private int summary4LineTop=10;

    @Override
    public ItemSummaryRecord find(String itemId, Date date) {
        ItemSummaryRecord record = findFromRedis(itemId, date);
        if (record != null) {
            return record;
        }
        return itemSummaryRecordRepository.findTopByItemIdAndDate(itemId, date);
    }

    @Override
    public ItemSummaryRecord findFromRedis(String itemId, Date date) {
        String key = String.format("ISR_%s_%s", itemId, DateUtils.getStrDateFormat(date, DateUtils.YMD_DATE_FORMAT));
        return (ItemSummaryRecord) redisUtil.get(key);
    }

    @Override
    public ItemSummaryRecord save(ItemSummaryRecord record) {
        String key = String.format("ISR_%s_%s", record.getItemId(), DateUtils.getStrDateFormat(record.getDate(), DateUtils.YMD_DATE_FORMAT));
        itemSummaryRecordRepository.save(record);
        redisUtil.set(key, record);
        return record;
    }

    @Override
    public ItemSummary4LineResponse getSummary4Line(String projectId, String type) {
//        List<String> itemIds = Arrays.asList("5d5f908d37941db84fe554f0", "5d3540f320577f25c0555330", "5d3540f620577f25c0555332");
        Date end = DateUtils.dayBegin(new Date());
        Map<String, List<ItemSummaryRecord>> allDataMap = getSummaryData4Line(projectId, type, DateUtils.addInteger(end, Calendar.DATE, -15), end);
//        List<ItemSummaryRecord> records = itemSummaryRecordRepository.findAllByItemIdInAndDateBetween(itemIds, DateUtils.addInteger(end, Calendar.DATE, -15), end);
        ItemSummary4LineResponse response = new ItemSummary4LineResponse();
        response.setDatas(new ArrayList<>());
        response.setDates(new ArrayList<>());
        response.setItemNames(new ArrayList<>());
        if (CollectionUtils.isEmpty(allDataMap)) {
            return response;
        }
        for (int i = 15; i > 0; i--) {
            response.getDates().add(DateUtils.addInteger(end, Calendar.DATE, 0 - i));
        }
        MonitorItem monitorItem = null;
        ChartSeriesItem dto = null;
        Map<String, Double> dataMap = new HashMap<>();
        for (Map.Entry<String, List<ItemSummaryRecord>> e : allDataMap.entrySet()) {
            monitorItem = monitorItemRepository.findOne(e.getKey());
            if (monitorItem == null) {
                continue;
            }
            response.getItemNames().add(monitorItem.getName());
            for (ItemSummaryRecord record : e.getValue()) {
                dataMap.put(String.format("%s#%s", monitorItem.getName(), DateUtils.getStrDateFormat(record.getDate(), DateUtils.YMD_DATE_FORMAT)), record.getFailPer());
            }
        }

        Double data = null;
        for (String itemName : response.getItemNames()) {
            dto = new ChartSeriesItem();
            dto.setType("line");
            dto.setName(itemName);
            dto.setData(new ArrayList<>());
            for (Date date : response.getDates()) {
                data = dataMap.get(String.format("%s#%s", itemName, DateUtils.getStrDateFormat(date, DateUtils.YMD_DATE_FORMAT)));
                dto.getData().add(Arrays.asList(date, data == null ? 0 : Math.floor(data * 10000) / 100));
            }
            response.getDatas().add(dto);
        }
        return response;
    }

    private Map<String, List<ItemSummaryRecord>> getSummaryData4Line(String projectId, String type, Date start, Date end) {
        List<MonitorItem> items = monitorItemRepository.findByProjectId(projectId);
        if (CollectionUtils.isEmpty(items)) {
            return null;
        }
        List<String> itemIds = new ArrayList<>();
        items.forEach(item -> {
            itemIds.add(item.getId());
        });
        List<ItemSummaryRecord> allData = itemSummaryRecordRepository.findAllByItemIdInAndDateBetween(itemIds, start, end);
        if (CollectionUtils.isEmpty(allData)) {
            return null;
        }
        Map<String, List<ItemSummaryRecord>> itemDataMap = allData.stream().collect(Collectors.groupingBy(ItemSummaryRecord::getItemId));
        Map<String, List<ItemSummaryRecord>> result = new HashMap<>();
        List<ItemStep> itemSteps = new ArrayList<>();
        itemDataMap.forEach((k, v) -> {
            ItemStep itemStep = caculate(v, type);
            if (itemStep != null) {
                itemStep.setItemId(k);
                itemSteps.add(itemStep);
            }
        });
        Collections.sort(itemSteps);
        Collections.reverse(itemSteps);
        int size = itemSteps.size();
        size = size > summary4LineTop ? summary4LineTop : size;
        String key = null;
        for (int i = 0; i < size; i++) {
            key = itemSteps.get(i).getItemId();
            result.put(key, itemDataMap.get(key));
        }
        return result;
    }

    private ItemStep caculate(List<ItemSummaryRecord> data, String type) {
        ItemSummaryRecord maxItem = null;
        ItemSummaryRecord minItem = null;
        Double maxData = null;
        Double minData = null;
        Double currData = null;
        for (ItemSummaryRecord item : data) {
            currData = item.getValue(type);
            if (maxItem == null) {
                maxItem = item;
            }
            if (minItem == null) {
                minItem = item;
            }
            if (minItem != null) {
                minData = minItem.getValue(type);
            }
            if (maxItem != null) {
                maxData = maxItem.getValue(type);
            }
            if (currData != null && minData > currData) {
                minItem = item;
            }
            if (currData != null && maxData < currData) {
                maxItem = item;
            }
        }
        if (minItem != null) {
            minData = minItem.getValue(type);
        }
        if (maxItem != null) {
            maxData = maxItem.getValue(type);
        }
        if (maxData != null && minData != null && !maxData.equals(minData)) {
            return new ItemStep(null, maxData - minData);
        }
        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class ItemStep implements Comparable<ItemStep> {
        private String itemId;
        private double step;

        @Override
        public int compareTo(ItemStep o) {
            return this.step > o.step ? 1 : (this.step == o.step ? 0 : -1);
        }
    }

}

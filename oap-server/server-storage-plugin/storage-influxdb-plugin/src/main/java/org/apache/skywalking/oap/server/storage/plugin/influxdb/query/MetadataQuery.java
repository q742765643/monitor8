/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.storage.plugin.influxdb.query;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.core.analysis.NodeType;
import org.apache.skywalking.oap.server.core.analysis.TimeBucket;
import org.apache.skywalking.oap.server.core.analysis.manual.endpoint.EndpointTraffic;
import org.apache.skywalking.oap.server.core.analysis.manual.instance.InstanceTraffic;
import org.apache.skywalking.oap.server.core.analysis.manual.service.ServiceTraffic;
import org.apache.skywalking.oap.server.core.query.enumeration.Language;
import org.apache.skywalking.oap.server.core.query.type.Attribute;
import org.apache.skywalking.oap.server.core.query.type.Database;
import org.apache.skywalking.oap.server.core.query.type.Endpoint;
import org.apache.skywalking.oap.server.core.query.type.Service;
import org.apache.skywalking.oap.server.core.query.type.ServiceInstance;
import org.apache.skywalking.oap.server.core.storage.query.IMetadataQueryDAO;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.InfluxClient;
import org.apache.skywalking.oap.server.storage.plugin.influxdb.InfluxConstants;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.querybuilder.SelectQueryImpl;
import org.influxdb.querybuilder.SelectSubQueryImpl;
import org.influxdb.querybuilder.WhereQueryImpl;
import org.influxdb.querybuilder.WhereSubQueryImpl;

import static org.apache.skywalking.oap.server.storage.plugin.influxdb.InfluxConstants.ID_COLUMN;
import static org.apache.skywalking.oap.server.storage.plugin.influxdb.InfluxConstants.NAME;
import static org.apache.skywalking.oap.server.storage.plugin.influxdb.InfluxConstants.TagName;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.contains;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.eq;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.gte;
import static org.influxdb.querybuilder.BuiltQuery.QueryBuilder.select;

@RequiredArgsConstructor
@Slf4j
public class MetadataQuery implements IMetadataQueryDAO {
    private static final Gson GSON = new Gson();
    private final InfluxClient client;

    @Override
    public List<Service> getAllServices(final long startTimestamp, final long endTimestamp) throws IOException {
        SelectSubQueryImpl<SelectQueryImpl> subQuery = select()
            .fromSubQuery(client.getDatabase())
            .column(ID_COLUMN).column(NAME)
            .from(ServiceTraffic.INDEX_NAME)
            .where(eq(InfluxConstants.TagName.NODE_TYPE, String.valueOf(NodeType.Normal.value())))
            .groupBy(TagName.NAME, TagName.NODE_TYPE);
        SelectQueryImpl query = select(ID_COLUMN, NAME).from(client.getDatabase());
        query.setSubQuery(subQuery);
        return buildServices(query);
    }

    @Override
    public List<Service> getAllBrowserServices(long startTimestamp, long endTimestamp) throws IOException {
        WhereQueryImpl<SelectQueryImpl> query = select(ID_COLUMN, NAME)
            .from(client.getDatabase(), ServiceTraffic.INDEX_NAME)
            .where(eq(InfluxConstants.TagName.NODE_TYPE, String.valueOf(NodeType.Normal.value())));
        return buildServices(query);
    }

    @Override
    public List<Database> getAllDatabases() throws IOException {
        SelectSubQueryImpl<SelectQueryImpl> subQuery = select()
            .fromSubQuery(client.getDatabase())
            .column(ID_COLUMN).column(NAME)
            .from(ServiceTraffic.INDEX_NAME)
            .where(eq(InfluxConstants.TagName.NODE_TYPE, NodeType.Database.value()))
            .groupBy(TagName.NAME, TagName.NODE_TYPE);
        SelectQueryImpl query = select(ID_COLUMN, NAME).from(client.getDatabase());
        query.setSubQuery(subQuery);
        QueryResult.Series series = client.queryForSingleSeries(query);
        if (log.isDebugEnabled()) {
            log.debug("SQL: {} result: {}", query.getCommand(), series);
        }

        List<Database> databases = Lists.newArrayList();
        if (Objects.nonNull(series)) {
            for (List<Object> values : series.getValues()) {
                Database database = new Database();
                database.setId((String) values.get(1));
                database.setName((String) values.get(2));
                databases.add(database);
            }
        }
        return databases;
    }

    @Override
    public List<Service> searchServices(long startTimestamp, long endTimestamp, String keyword) throws IOException {
        WhereSubQueryImpl<SelectSubQueryImpl<SelectQueryImpl>, SelectQueryImpl> subQuery = select()
            .fromSubQuery(client.getDatabase())
            .column(ID_COLUMN)
            .column(NAME)
            .from(ServiceTraffic.INDEX_NAME)
            .where(eq(InfluxConstants.TagName.NODE_TYPE, String.valueOf(NodeType.Normal.value())));
        if (!Strings.isNullOrEmpty(keyword)) {
            subQuery.and(contains(ServiceTraffic.NAME, keyword));
        }
        subQuery.groupBy(TagName.NAME, TagName.NODE_TYPE);

        SelectQueryImpl query = select(ID_COLUMN, NAME).from(client.getDatabase());
        query.setSubQuery(subQuery);
        return buildServices(query);
    }

    @Override
    public Service searchService(String serviceCode) throws IOException {
        WhereQueryImpl<SelectQueryImpl> query = select(ID_COLUMN, NAME)
            .from(client.getDatabase(), ServiceTraffic.INDEX_NAME)
            .where(eq(InfluxConstants.TagName.NODE_TYPE, String.valueOf(NodeType.Normal.value())));
        query.and(eq(ServiceTraffic.NODE_TYPE, serviceCode));
        return buildServices(query).get(0);
    }

    @Override
    public List<Endpoint> searchEndpoint(final String keyword,
                                         final String serviceId,
                                         final int limit) throws IOException {
        WhereSubQueryImpl<SelectSubQueryImpl<SelectQueryImpl>, SelectQueryImpl> subQuery = select()
            .fromSubQuery(client.getDatabase())
            .column(ID_COLUMN)
            .column(NAME)
            .from(EndpointTraffic.INDEX_NAME)
            .where(eq(InfluxConstants.TagName.SERVICE_ID, String.valueOf(serviceId)));
        if (!Strings.isNullOrEmpty(keyword)) {
            subQuery.where(contains(EndpointTraffic.NAME, keyword.replaceAll("/", "\\\\/")));
        }
        subQuery.groupBy(TagName.NAME, TagName.SERVICE_ID);
        SelectQueryImpl query = select(ID_COLUMN, NAME)
            .from(client.getDatabase());
        query.setSubQuery(subQuery);
        query.limit(limit);

        final QueryResult.Series series = client.queryForSingleSeries(query);
        if (log.isDebugEnabled()) {
            log.debug("SQL: {} result: {}", query.getCommand(), series);
        }

        List<Endpoint> list = new ArrayList<>(limit);
        if (series != null) {
            series.getValues().forEach(values -> {
                Endpoint endpoint = new Endpoint();
                endpoint.setId((String) values.get(1));
                endpoint.setName((String) values.get(2));
                list.add(endpoint);
            });
        }
        return list;
    }

    @Override
    public List<ServiceInstance> getServiceInstances(final long startTimestamp,
                                                     final long endTimestamp,
                                                     final String serviceId) throws IOException {
        final long minuteTimeBucket = TimeBucket.getMinuteTimeBucket(startTimestamp);

        SelectSubQueryImpl<SelectQueryImpl> subQuery = select()
            .fromSubQuery(client.getDatabase())
            .column(ID_COLUMN).column(NAME).column(InstanceTraffic.PROPERTIES)
            .from(InstanceTraffic.INDEX_NAME)
            .where()
            .and(gte(InstanceTraffic.LAST_PING_TIME_BUCKET, minuteTimeBucket))
            .and(eq(InfluxConstants.TagName.SERVICE_ID, serviceId))
            .groupBy(TagName.NAME, TagName.SERVICE_ID);

        SelectQueryImpl query = select().column(ID_COLUMN)
                                        .column(NAME)
                                        .column(InstanceTraffic.PROPERTIES)
                                        .from(client.getDatabase(), InstanceTraffic.INDEX_NAME);
        query.setSubQuery(subQuery);

        QueryResult.Series series = client.queryForSingleSeries(query);
        if (log.isDebugEnabled()) {
            log.debug("SQL: {} result: {}", query.getCommand(), series);
        }

        if (Objects.isNull(series)) {
            return Collections.EMPTY_LIST;
        }

        List<List<Object>> result = series.getValues();
        List<ServiceInstance> instances = Lists.newArrayList();
        for (List<Object> values : result) {
            ServiceInstance serviceInstance = new ServiceInstance();

            serviceInstance.setId((String) values.get(1));
            serviceInstance.setName((String) values.get(2));
            serviceInstance.setInstanceUUID(serviceInstance.getId());

            String propertiesString = (String) values.get(3);
            if (!Strings.isNullOrEmpty(propertiesString)) {
                JsonObject properties = GSON.fromJson(propertiesString, JsonObject.class);
                for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
                    String key = property.getKey();
                    String value = property.getValue().getAsString();
                    if (key.equals(InstanceTraffic.PropertyUtil.LANGUAGE)) {
                        serviceInstance.setLanguage(Language.value(value));
                    } else {
                        serviceInstance.getAttributes().add(new Attribute(key, value));
                    }

                }
            } else {
                serviceInstance.setLanguage(Language.UNKNOWN);
            }
            instances.add(serviceInstance);
        }
        return instances;
    }

    private List<Service> buildServices(Query query) throws IOException {
        QueryResult.Series series = client.queryForSingleSeries(query);
        if (log.isDebugEnabled()) {
            log.debug("SQL: {} result: {}", query.getCommand(), series);
        }

        ArrayList<Service> services = Lists.newArrayList();
        if (Objects.nonNull(series)) {
            for (List<Object> values : series.getValues()) {
                Service service = new Service();
                service.setId((String) values.get(1));
                service.setName((String) values.get(2));
                services.add(service);
            }
        }
        return services;
    }
}

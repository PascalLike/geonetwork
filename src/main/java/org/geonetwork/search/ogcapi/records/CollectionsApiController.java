/*
 * (c) 2003 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license,
 * available at the root application directory.
 */

package org.geonetwork.search.ogcapi.records;

import static org.geonetwork.index.model.record.IndexRecordFieldNames.CommonField.DEFAULT_TEXT;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import jakarta.annotation.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.geonetwork.domain.Source;
import org.geonetwork.index.IndexClient;
import org.geonetwork.index.model.record.IndexRecord;
import org.geonetwork.repository.SourceRepository;
import org.geonetwork.search.ogcapi.records.generated.CollectionsApi;
import org.geonetwork.search.ogcapi.records.generated.model.CollectionDto;
import org.geonetwork.search.ogcapi.records.generated.model.GetCollections200ResponseDto;
import org.geonetwork.search.ogcapi.records.generated.model.GetRecords200ResponseDto;
import org.geonetwork.search.ogcapi.records.generated.model.GetRecordsBboxDto;
import org.geonetwork.search.ogcapi.records.generated.model.RecordGeoJSONDto;
import org.geonetwork.search.ogcapi.records.generated.model.RecordGeoJSONPropertiesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Generated(
    value = "org.openapitools.codegen.languages.SpringCodegen",
    date = "2024-07-03T16:51:11.791047085+02:00[Europe/Paris]",
    comments = "Generator version: 7.6.0")
@Controller
@RequestMapping("${openapi.oGCAPIRecordsPart1Core.base-path:/data}")
public class CollectionsApiController implements CollectionsApi {

  private final IndexClient client;
  private final SourceRepository sourceRepository;

  @Autowired
  public CollectionsApiController(IndexClient client, SourceRepository sourceRepository) {
    this.sourceRepository = sourceRepository;
    this.client = client;
  }

  @Override
  public ResponseEntity<GetCollections200ResponseDto> getCollections() {
    List<Source> sourceList = sourceRepository.findAll();
    GetCollections200ResponseDto collections =
        GetCollections200ResponseDto.builder()
            .collections(
                sourceList.stream()
                    .map(
                        source ->
                            CollectionDto.builder()
                                .id(source.getUuid())
                                .title(source.getName())
                                .build())
                    .collect(Collectors.toUnmodifiableList()))
            .build();
    return ResponseEntity.ok(collections);
  }

  @SneakyThrows
  @Override
  public ResponseEntity<GetRecords200ResponseDto> getRecords(
      String catalogId,
      GetRecordsBboxDto bbox,
      String datetime,
      Integer limit,
      List<String> q,
      List<String> type,
      List<String> externalId,
      List<String> ids,
      List<String> sortby) {

    SearchResponse<IndexRecord> searchResponse =
        client
            .getEsClient()
            .search(
                s -> {
                  s.index("gn-records");
                  if (limit != null) {
                    s.size(limit);
                  }
                  if (q != null && StringUtils.isNoneEmpty(q.getFirst())) {
                    s.q(q.getFirst());
                  }
                  return s;
                },
                IndexRecord.class);

    return ResponseEntity.ok(
        GetRecords200ResponseDto.builder()
            .numberMatched(Math.toIntExact(searchResponse.hits().total().value()))
            .numberReturned(searchResponse.hits().hits().size())
            .features(
                searchResponse.hits().hits().stream()
                    .map(
                        h -> {
                          var title =
                              Optional.ofNullable(h.source().getResourceTitle())
                                  .map(t -> t.get(DEFAULT_TEXT))
                                  .orElse("");
                          var description =
                              Optional.ofNullable(h.source().getResourceAbstract())
                                  .map(a -> a.get(DEFAULT_TEXT))
                                  .orElse("");
                          var keywords =
                              Optional.ofNullable(h.source().getTag())
                                  .orElseGet(ArrayList::new)
                                  .stream()
                                  .flatMap(Stream::ofNullable)
                                  .map(tag -> tag.get(DEFAULT_TEXT))
                                  .toList();

                          return RecordGeoJSONDto.builder()
                              .id(h.id())
                              .properties(
                                  RecordGeoJSONPropertiesDto.builder()
                                      .title(title)
                                      .description(description)
                                      .keywords(keywords)
                                      .build())
                              .build();
                        })
                    .toList())
            .build());
  }
}

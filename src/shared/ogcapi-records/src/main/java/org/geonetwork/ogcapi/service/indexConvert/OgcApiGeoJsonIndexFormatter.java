/*
 * (c) 2003 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license,
 * available at the root application directory.
 */
package org.geonetwork.ogcapi.service.indexConvert;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import lombok.AllArgsConstructor;
import org.geonetwork.formatting.processor.IndexFormatterProcessor;
import org.geonetwork.index.model.record.IndexRecord;
import org.springframework.stereotype.Component;

/** simple wrapper around OgcApiGeoJsonIndexFormatter so it can be used as an output format. */
@Component
@AllArgsConstructor
public class OgcApiGeoJsonIndexFormatter implements IndexFormatterProcessor {

    private final OgcApiGeoJsonConverter ogcApiGeoJsonConverter;
    private final ObjectMapper objectMapper;

    @Override
    public String getId() {
        return "OgcApiGeoJson";
    }

    @Override
    public void process(IndexRecord indexRecord, OutputStream out) throws IOException {
        String lang = "eng";
        var result = ogcApiGeoJsonConverter.convert(indexRecord, lang);
        objectMapper.writeValue(out, result);
    }
}

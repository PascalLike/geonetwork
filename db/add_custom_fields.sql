BEGIN;

-- 1. reportingObligations (EEA thesaurus: Eionet reporting obligations, #255)
INSERT INTO ogcapi_field_mapping
    (config_id, sort_order, ogc_property, elastic_property, index_record_property,
     type_override, is_sortable, is_queryable, add_property_to_output)
SELECT 'property-mapping',
       (SELECT COALESCE(MAX(sort_order), -1) + 1 FROM ogcapi_field_mapping WHERE config_id = 'property-mapping'),
       'reportingObligations', 'th_rod-eionet-europa-eu.default',
       'keywordByThesaurus.["th_rod-eionet-europa-eu"].[*].properties.["default"]',
       'LIST_STRING', false, true, true
WHERE EXISTS (SELECT 1 FROM ogcapi_property_mapping WHERE id = 'property-mapping')
  AND NOT EXISTS (SELECT 1 FROM ogcapi_field_mapping
                  WHERE config_id = 'property-mapping' AND ogc_property = 'reportingObligations');

INSERT INTO ogcapi_facet_config
    (field_mapping_id, sort_order, facet_name, facet_type, bucket_sorting, bucket_sorting_direction,
     bucket_count, minimum_document_count)
SELECT fm.id, 0, 'reportingObligations', 'TERM', 'COUNT', 'DESCENDING', 150, 1
FROM ogcapi_field_mapping fm
WHERE fm.config_id = 'property-mapping' AND fm.ogc_property = 'reportingObligations'
  AND NOT EXISTS (SELECT 1 FROM ogcapi_facet_config fc
                  WHERE fc.field_mapping_id = fm.id AND fc.facet_name = 'reportingObligations');

-- 2. eeaTopics (EEA thesaurus: EEA topics, #256)
INSERT INTO ogcapi_field_mapping
    (config_id, sort_order, ogc_property, elastic_property, index_record_property,
     type_override, is_sortable, is_queryable, add_property_to_output)
SELECT 'property-mapping',
       (SELECT COALESCE(MAX(sort_order), -1) + 1 FROM ogcapi_field_mapping WHERE config_id = 'property-mapping'),
       'eeaTopics', 'th_eea-topics.default',
       'keywordByThesaurus.["th_eea-topics"].[*].properties.["default"]',
       'LIST_STRING', false, true, true
WHERE EXISTS (SELECT 1 FROM ogcapi_property_mapping WHERE id = 'property-mapping')
  AND NOT EXISTS (SELECT 1 FROM ogcapi_field_mapping
                  WHERE config_id = 'property-mapping' AND ogc_property = 'eeaTopics');

INSERT INTO ogcapi_facet_config
    (field_mapping_id, sort_order, facet_name, facet_type, bucket_sorting, bucket_sorting_direction,
     bucket_count, minimum_document_count)
SELECT fm.id, 0, 'eeaTopics', 'TERM', 'COUNT', 'DESCENDING', 50, 1
FROM ogcapi_field_mapping fm
WHERE fm.config_id = 'property-mapping' AND fm.ogc_property = 'eeaTopics'
  AND NOT EXISTS (SELECT 1 FROM ogcapi_facet_config fc
                  WHERE fc.field_mapping_id = fm.id AND fc.facet_name = 'eeaTopics');

-- 3. gemetKeywords (GEMET hierarchy: group^theme^term paths, #257)
INSERT INTO ogcapi_field_mapping
    (config_id, sort_order, ogc_property, elastic_property, index_record_property,
     type_override, is_sortable, is_queryable, add_property_to_output)
SELECT 'property-mapping',
       (SELECT COALESCE(MAX(sort_order), -1) + 1 FROM ogcapi_field_mapping WHERE config_id = 'property-mapping'),
       'gemetKeywords', 'th_gemet_tree.default',
       'keywordHierarchyByThesaurus.["th_gemet_tree"].defaultTexts',
       'LIST_STRING', false, true, true
WHERE EXISTS (SELECT 1 FROM ogcapi_property_mapping WHERE id = 'property-mapping')
  AND NOT EXISTS (SELECT 1 FROM ogcapi_field_mapping
                  WHERE config_id = 'property-mapping' AND ogc_property = 'gemetKeywords');

INSERT INTO ogcapi_facet_config
    (field_mapping_id, sort_order, facet_name, facet_type, bucket_sorting, bucket_sorting_direction,
     bucket_count, minimum_document_count)
SELECT fm.id, 0, 'gemetKeywords', 'TERM', 'COUNT', 'DESCENDING', 500, 1
FROM ogcapi_field_mapping fm
WHERE fm.config_id = 'property-mapping' AND fm.ogc_property = 'gemetKeywords'
  AND NOT EXISTS (SELECT 1 FROM ogcapi_facet_config fc
                  WHERE fc.field_mapping_id = fm.id AND fc.facet_name = 'gemetKeywords');

-- 4. eeaBusinessDomain #237
INSERT INTO ogcapi_field_mapping
    (config_id, sort_order, ogc_property, elastic_property, index_record_property,
     type_override, is_sortable, is_queryable, add_property_to_output)
SELECT 'property-mapping', (SELECT COALESCE(MAX(sort_order), -1) + 1 FROM ogcapi_field_mapping WHERE config_id = 'property-mapping'), 'eeaBusinessDomain', 'th_eea-business-domains_tree.default',
       'keywordHierarchyByThesaurus.["th_eea-business-domains_tree"].defaultTexts',
       'LIST_STRING', false, true, true
WHERE EXISTS (SELECT 1 FROM ogcapi_property_mapping WHERE id = 'property-mapping')
  AND NOT EXISTS (SELECT 1 FROM ogcapi_field_mapping
                  WHERE config_id = 'property-mapping' AND ogc_property = 'eeaBusinessDomain');

INSERT INTO ogcapi_facet_config (field_mapping_id, sort_order, facet_name, facet_type, bucket_count)
SELECT fm.id, 0, 'eeaBusinessDomain', 'TERM', 50
FROM ogcapi_field_mapping fm
WHERE fm.config_id = 'property-mapping' AND fm.ogc_property = 'eeaBusinessDomain'
  AND NOT EXISTS (SELECT 1 FROM ogcapi_facet_config fc
                  WHERE fc.field_mapping_id = fm.id AND fc.facet_name = 'eeaBusinessDomain');

-- Check result
SELECT fm.sort_order, fm.ogc_property, fm.elastic_property, fm.index_record_property,
       fm.type_override, fm.is_sortable, fm.is_queryable,
       fc.facet_name, fc.bucket_count
FROM ogcapi_field_mapping fm
LEFT JOIN ogcapi_facet_config fc ON fc.field_mapping_id = fm.id
WHERE fm.config_id = 'property-mapping'
  AND fm.ogc_property IN ('reportingObligations', 'eeaTopics', 'gemetKeywords', 'eeaBusinessDomain')
ORDER BY fm.sort_order;

COMMIT;

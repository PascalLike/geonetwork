package org.geonetwork.formatting;

import java.io.OutputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.geonetwork.domain.Metadata;
import org.geonetwork.metadata.IMetadataAccessManager;
import org.geonetwork.metadata.IMetadataManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/** High level api for The FormatterApi. */
@Component
@RequiredArgsConstructor
public class FormatterApi {

    private final IMetadataManager metadataManager;
    private final IMetadataAccessManager metadataAccessManager;
    private final FormatterFactory formatterFactory;

    List<String> getRecordFormatterList(String metadataUuid) throws Exception {
        return getRecordFormatterList(metadataUuid, true);
    }

    List<String> getRecordFormatterList(String metadataUuid, boolean approved) throws Exception {
        Metadata metadata = metadataManager.findMetadataByUuid(metadataUuid, approved);

        if (!metadataAccessManager.canView(metadata.getId())) {
            throw new AccessDeniedException("User is not permitted to access this resource");
        }

        return formatterFactory.getAvailableFormatters(metadata);
    }

    public void getRecordFormattedBy(
            String metadataUuid, final String formatterId, boolean approved, OutputStream outputStream)
            throws Exception {

        Metadata metadata = metadataManager.findMetadataByUuid(metadataUuid, approved);

        //        String language;
        //        if (StringUtils.isNotEmpty(iso3lang)) {
        //            if (PARAM_LANGUAGE_ALL_VALUES.equalsIgnoreCase(iso3lang)) {
        //                language = iso3lang;
        // TODO           } else if (languageUtils.getUiLanguages().contains(iso3lang)) {
        //                language = isoLanguagesMapper.iso639_2T_to_iso639_2B(iso3lang);
        //            } else {
        //                language = languageUtils.getDefaultUiLanguage();
        //            }
        //        } else {
        //                        language = isoLanguagesMapper.iso639_2T_to_iso639_2B(locale.getISO3Language());
        //        }

        // TODO      Boolean hideWithheld = !context.getBean(AccessManager.class).canEdit(context,
        // String.valueOf(metadata.getId()));

        //  TODO Cache?

        Formatter formatter = formatterFactory.getFormatter(metadata, formatterId);
        formatter.format(metadata, formatterId, outputStream);
    }
}

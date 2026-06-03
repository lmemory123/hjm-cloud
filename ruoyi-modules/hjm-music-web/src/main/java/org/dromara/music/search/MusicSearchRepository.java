package org.dromara.music.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momao.valkey.adapter.BaseValkeyRepository;
import com.momao.valkey.adapter.ValkeyClientRouting;
import com.momao.valkey.adapter.observability.ValkeyObservationInvoker;
import com.momao.valkey.adapter.observability.ValkeyUpdateMetricsRecorder;
import org.springframework.stereotype.Repository;

@Repository
public class MusicSearchRepository extends BaseValkeyRepository<MusicSearchDocument> {

    public MusicSearchRepository(
        ValkeyClientRouting clientRouting,
        ObjectMapper objectMapper,
        ValkeyObservationInvoker observationInvoker,
        ValkeyUpdateMetricsRecorder updateMetricsRecorder
    ) {
        super(MusicSearchDocumentQuery.METADATA, clientRouting, MusicSearchDocument.class, objectMapper, observationInvoker, updateMetricsRecorder);
    }

    public void save(MusicSearchDocument document) {
        save(document.getId(), document);
    }
}

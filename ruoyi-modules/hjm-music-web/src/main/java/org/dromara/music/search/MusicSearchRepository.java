package org.dromara.music.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momao.valkey.adapter.BaseValkeyRepository;
import com.momao.valkey.adapter.ValkeyClientRouting;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "valkey.query", name = "enabled", havingValue = "true")
public class MusicSearchRepository extends BaseValkeyRepository<MusicSearchDocument> {

    public MusicSearchRepository(ValkeyClientRouting clientRouting, ObjectMapper objectMapper) {
        super(MusicSearchDocumentQuery.METADATA, clientRouting, MusicSearchDocument.class, objectMapper);
    }
}

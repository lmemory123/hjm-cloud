package org.dromara.music.search;

import com.momao.valkey.adapter.ValkeyClientRouting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = MusicValkeySearchTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.main.web-application-type=none",
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.config.import=",
        "valkey.query.enabled=true",
        "valkey.query.use-tls=false",
        "valkey.query.connection-timeout=5000",
        "valkey.query.request-timeout=5000",
        "valkey.query.password=memory1314",
        "valkey.query.mode=standalone",
        "valkey.query.standalone.nodes[0].host=14.103.130.141",
        "valkey.query.standalone.nodes[0].port=15053"
    }
)
@Tag("dev")
class MusicValkeySearchIntegrationTest {

    @Autowired
    private MusicSearchRepository musicSearchRepository;

    @Autowired
    private ValkeyClientRouting clientRouting;

    @Test
    void shouldCreateMusicIndexAndQueryByGeneratedDsl() {
        String id = "music-it-" + System.currentTimeMillis();
        String title = "valkey-it-title-" + id;
        String creatorName = "copilot-" + id;
        String producerMark = "official" + System.currentTimeMillis();

        String indexResult = musicSearchRepository.checkAndCreateIndex();
        assertNotNull(indexResult);
        assertEquals("idx:music", musicSearchRepository.getIndexName());

        MusicSearchDocument document = new MusicSearchDocument();
        document.setId(id);
        document.setTitle(title);
        document.setSubtitle("Boot4 集成验证");
        document.setOriginalTitle("Original " + id);
        document.setCreatorName(creatorName);
        document.setProducerMark(producerMark);
        document.setDuration(215L);
        document.setBpm(128L);
        document.setPublishTime(System.currentTimeMillis());
        document.setPlayCount(1024L);
        document.setLikeCount(256L);
        document.setAuditStatus("approved");
        document.setIsPublic("Y");
        document.setTags(List.of("电子", "测试", "集成"));

        musicSearchRepository.save(id, document);

        Object rawJson = runRawCommand("JSON.GET", "music:" + id, "$");
        assertNotNull(rawJson);

        long rawSearchCount = awaitAtLeastOne(() -> extractSearchCount(runRawCommand(
            "FT.SEARCH",
            "idx:music",
            "@producer_mark:{" + producerMark + "}",
            "LIMIT",
            "0",
            "1"
        )));
        assertTrue(rawSearchCount >= 1, "raw FT.SEARCH did not find the saved document");

        MusicSearchDocumentQuery q = new MusicSearchDocumentQuery();

        MusicSearchDocument loaded = awaitNotNull(() -> musicSearchRepository.one(
            q.producerMark.eq(producerMark)
                .and(q.auditStatus.eq("approved"))
                .and(q.tags.contains("电子"))
        ));

        assertNotNull(loaded);
        assertEquals(id, loaded.getId());
        assertEquals(title, loaded.getTitle());

        long matchedCount = awaitAtLeastOne(() -> musicSearchRepository.count(
            q.producerMark.eq(producerMark)
                .and(q.playCount.gte(1000L))
                .and(q.isPublic.eq("Y"))
        ));
        assertTrue(matchedCount >= 1);

        List<MusicSearchDocument> documents = awaitNotNull(() -> {
            List<MusicSearchDocument> result = musicSearchRepository.list(
                q.tags.contains("测试")
                    .and(q.isPublic.eq("Y"))
            );
            return result.stream().anyMatch(item -> title.equals(item.getTitle())) ? result : null;
        });
        assertFalse(documents.isEmpty());
        assertTrue(documents.stream().anyMatch(item -> id.equals(item.getId())));
    }

    private static <T> T awaitNotNull(Supplier<T> supplier) {
        T result = null;
        for (int attempt = 0; attempt < 20; attempt++) {
            result = supplier.get();
            if (result != null) {
                return result;
            }
            sleepQuietly();
        }
        return result;
    }

    private static long awaitAtLeastOne(LongSupplier supplier) {
        long matchedCount = 0L;
        for (int attempt = 0; attempt < 20; attempt++) {
            matchedCount = supplier.getAsLong();
            if (matchedCount >= 1L) {
                return matchedCount;
            }
            sleepQuietly();
        }
        return matchedCount;
    }

    private static void sleepQuietly() {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for Valkey indexing", ex);
        }
    }

    private Object runRawCommand(String... args) {
        try {
            return clientRouting.executeRead(args);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to execute raw Valkey command", ex);
        }
    }

    private static long extractSearchCount(Object response) {
        if (response instanceof Object[] values && values.length > 0 && values[0] instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }
}

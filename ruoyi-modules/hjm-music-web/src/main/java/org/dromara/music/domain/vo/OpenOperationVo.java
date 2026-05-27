package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenOperationVo {

    private List<Announcement> announcements = new ArrayList<>();

    private List<RecommendCard> recommendCards = new ArrayList<>();

    private IncentiveActivity incentiveActivity;

    @Data
    public static class Announcement {
        private String id;
        private String title;
        private String content;
        private String link;
        private String level;
    }

    @Data
    public static class RecommendCard {
        private String id;
        private String title;
        private String desc;
        private String link;
        private String icon;
    }

    @Data
    public static class IncentiveActivity {
        private String title;
        private String desc;
        private String rule;
        private String link;
        private Long poolAmount;
    }
}

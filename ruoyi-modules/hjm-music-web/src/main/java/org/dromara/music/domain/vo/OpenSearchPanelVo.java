package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class OpenSearchPanelVo {

    private String keyword;

    private String currentSort;

    private String recommendedSort;

    private String correctedKeyword;

    private String searchSummary;

    private List<String> synonymKeywords;

    private List<String> selectedTags;

    private Long total;

    private List<OpenSearchSortOptionVo> sortOptions;

    private List<OpenSearchFacetItemVo> matchedFields;

    private List<OpenSearchFacetItemVo> creators;

    private List<OpenSearchFacetItemVo> tags;

    private List<OpenSearchFacetItemVo> styleTags;

    private List<OpenSearchFacetItemVo> activeFilters;

    private List<String> hotKeywords;
}

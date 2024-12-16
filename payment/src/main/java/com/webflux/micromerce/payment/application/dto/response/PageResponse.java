package com.webflux.micromerce.payment.application.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;
    private PageInfo pageInfo;

    @Getter
    @Setter
    public static class PageInfo {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean isFirst;
        private boolean isLast;
        private boolean hasNext;
        private boolean hasPrevious;

        public static PageInfo from(Page<?> page) {
            PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNumber(page.getNumber());
            pageInfo.setPageSize(page.getSize());
            pageInfo.setTotalElements(page.getTotalElements());
            pageInfo.setTotalPages(page.getTotalPages());
            pageInfo.setFirst(page.isFirst());
            pageInfo.setLast(page.isLast());
            pageInfo.setHasNext(page.hasNext());
            pageInfo.setHasPrevious(page.hasPrevious());
            return pageInfo;
        }
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setPageInfo(PageInfo.from(page));
        return response;
    }
}
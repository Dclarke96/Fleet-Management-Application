package com.dylanclarke.FleetManagementApp.network;

import java.util.List;

/**
 * Generic paginated API response payload.
 *
 * @param <T> Type of content contained in the page
 */
public class PageResponse<T> {

    // Page content/results
    private List<T> content;

    // Current page index
    private int page;

    // Number of items per page
    private int size;

    // Total number of records available
    private long totalElements;

    /**
     * Returns the paginated content list.
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * Returns the current page index.
     */
    public int getPage() {
        return page;
    }

    /**
     * Returns the configured page size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the total number of available records.
     */
    public long getTotalElements() {
        return totalElements;
    }
}
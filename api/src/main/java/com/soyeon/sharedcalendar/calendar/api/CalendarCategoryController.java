package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarCategoryService;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarCategoryRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarCategoriesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendars/{calendarId}")
@RequiredArgsConstructor
@Tag(name = "Calendar Category", description = "캘린더에서 사용중인 카테고리 관련 API")
public class CalendarCategoryController {
    private final CalendarCategoryService calendarCategoryService;

    @Operation(summary = "카테고리 조회", description = "캘린더 내 사용중인 카테고리를 조회한다.")
    @GetMapping("/categories")
    public List<CalendarCategoriesResponse> getCategories(@PathVariable Long calendarId) {
        return calendarCategoryService.getCategories(calendarId);
    }

    @Operation(summary = "카테고리 생성", description = "캘린더에서 사용할 카테고리를 생성한다.")
    @PostMapping("/categories")
    public void addCategory(@PathVariable Long calendarId, @RequestBody CalendarCategoryRequest request) {
        calendarCategoryService.addCategory(calendarId, request);
    }
}

package com.sparta.moit.domain.bookmark.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookMarkResponseDto {
    private final Long meetingId;
    private final Long memberId;

}

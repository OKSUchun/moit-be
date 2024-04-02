package com.sparta.moit.domain.meeting.service;

import com.sparta.moit.domain.meeting.dto.GetMeetingResponseDto;

import java.util.List;

public interface MeetingService {
    List<GetMeetingResponseDto> getMeetingList(List<Integer> careerTypes, List<Integer> skillTypes, String region1depthName, String region2depthName);

}

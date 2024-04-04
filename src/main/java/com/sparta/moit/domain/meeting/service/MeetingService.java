package com.sparta.moit.domain.meeting.service;

import com.sparta.moit.domain.meeting.dto.CreateMeetingRequestDto;
import com.sparta.moit.domain.meeting.dto.GetMeetingResponseDto;
import com.sparta.moit.domain.meeting.dto.UpdateMeetingRequestDto;
import com.sparta.moit.domain.member.entity.Member;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface MeetingService {
    List<GetMeetingResponseDto> getMeetingList(List<Integer> careerTypes, List<Integer> skillTypes, String region1depthName, String region2depthName);

    Long createMeeting(CreateMeetingRequestDto requestDto, Member member);

    Long updateMeeting(UpdateMeetingRequestDto requestDto, UserDetails userDetails, Long meetingId);
}
